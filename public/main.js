import {
  StatusText,
  ProgressBar,
  DynamicButton,
  DropZone,
  PreviewListInteractive,
  ChromaticList,
  HidableElement,
  JsonForm,
} from './modules/components.js';

const display = document.getElementById('display');

const filesToUpload = [];
const filesToDownload = [];

const downloadButton = new DynamicButton(
  document.getElementById('downloadButton')
);
const resetButton = new DynamicButton(document.getElementById('resetButton'));
const selectButton = new DynamicButton(document.getElementById('selectButton'));
const submitButton = new DynamicButton(document.getElementById('submitButton'));

const fileDropZone = new DropZone(
  document.getElementById('dropZone'),
  (event) => {
    handleFileUpload(event.dataTransfer.files);
  }
);
const filePreviews = new PreviewListInteractive(
  document.getElementById('filePreviews'),
  filesToUpload
);

const statusMessage = new StatusText(document.getElementById('statusMessage'));
const stageList = new ChromaticList(document.getElementById('stageList'));

const formJSON = new JsonForm(document.querySelector('form'));

const fileInput = document.getElementById('fileInput');
const fileOptions = new HidableElement(document.getElementById('options'));

selectButton.addClickHandle(() => {
  fileInput.click();
});
downloadButton.addClickHandle(handleDownload);

formJSON.pointer.addEventListener('submit', handleSubmit);
formJSON.pointer.addEventListener('reset', resetAppState);
// formJSON.pointer.addEventListener("change", () => {
//   console.log(formJSON.getValues());
// });

fileInput.addEventListener('change', () => {
  handleFileUpload(fileInput.files);
});

resetAppState();

const appStateTracker = new MutationObserver((mutationsList) => {
  for (let mutation of mutationsList) {
    if (mutation.type === 'childList') {
      const a = filesToUpload.length > 0 ? 1 : 0;
      const b = filesToDownload.length > 0 ? 1 : 0;
      switch (`${a}${b}`) {
        case '00':
          submitButton.disable();
          resetButton.disable();
          stageList.setStage(0);
          fileOptions.hide();
          break;
        case '10':
          submitButton.enable();
          resetButton.enable();
          stageList.setStage(1);
          fileOptions.show();
          break;
        case '01':
          selectButton.disable();
          submitButton.disable();
          downloadButton.enable();
          fileDropZone.disable();
          stageList.setStage(2);
          fileOptions.hide();
          break;
      }
    }
  }
});
appStateTracker.observe(filePreviews.pointer, { childList: true });

// Functions
function resetAppState() {
  filesToUpload.splice(0);
  filesToDownload.splice(0);
  fileInput.value = '';

  filePreviews.clear();
  selectButton.enable();
  submitButton.disable();
  resetButton.disable();
  downloadButton.disable();
  fileDropZone.enable();
  stageList.reset();
  fileOptions.hide();

  statusMessage.update(`🤷‍♂ Nothing's uploaded`);

  display.innerText = '';
}

function handleFileUpload(uploadedFiles) {
  try {
    extractFiles(uploadedFiles);
  } catch (err) {
    statusMessage.update(err.message);
  }
  filePreviews.update(
    filesToUpload.map((file) => file.name),
    { button: true, bar: true }
  );
  submitButton.enable();
}

function handleDownload() {
  console.log('Downloading...');
}

function handleSubmit(event) {
  event.preventDefault();

  // sendFiles(filesToUpload);
  sendFilesMultiple(filesToUpload);
  // mockSending(filesToUpload);
}

function extractFiles(fileList) {
  const allowedTypes = ['application/pdf'];
  const existingFiles = filesToUpload.map((file) => file.name);
  const invalidFiles = [];
  for (const file of fileList) {
    if (!allowedTypes.includes(file.type)) {
      invalidFiles.push(file.name);
      continue;
    }
    if (!existingFiles.includes(file.name)) {
      filesToUpload.push(file);
    }
  }
  if (invalidFiles.length) {
    throw new Error(
      `❌ Could not upload following files: ${invalidFiles.join(', ')}`
    );
  }
}

function mockSending(filesToUpload) {
  submitButton.disable();
  fileOptions.hide();
  statusMessage.update('⏳ Uploading...');

  let progress = 0;

  setTimeout(() => {
    filesToUpload.forEach((file) => {
      filesToDownload.push(file);
    });
    filesToUpload.splice(0);

    statusMessage.update('✅ Success');
    progressBar.update(0);
    submitButton.enable();

    filePreviews.update(
      filesToDownload.map((file) => file.name + ' is ready.'),
      { button: false, bar: false }
    );

    clearInterval(progressBarUpdateTimer);
  }, 3250);

  const progressBarUpdateTimer = setInterval(() => {
    progress += 0.35;
    progressBar.update(progress);
  }, 1000);
}

function sendFiles(files) {
  statusMessage.update('⏳ Pending...');
  const url = 'https://httpbin.org/post';
  const method = 'POST';
  const xhr = new XMLHttpRequest();

  const data = new FormData();
  for (const file of files) {
    data.append('file', file);
  }

  xhr.addEventListener('loadend', () => {
    if (xhr.status === 200) {
      filesToUpload.forEach((file) => {
        filesToDownload.push(file);
      });
      filesToUpload.splice(0);
      filePreviews.update(
        filesToDownload.map((file) => file.name + ' is ready.'),
        { button: false, bar: false }
      );
      statusMessage.update('✅ Success');
    } else {
      statusMessage.update('❌ Error');
    }
    progressBar.update(0);
  });

  xhr.upload.addEventListener('progress', (event) => {
    statusMessage.update(`⏳ Uploaded ${event.loaded} bytes of ${event.total}`);
    progressBar.update(event.loaded / event.total);
  });

  xhr.open(method, url);
  xhr.send(data);
}

async function sendFilesMultiple(files) {
  const filePromises = [];
  const progresses = Array.from(
    filePreviews.pointer.querySelectorAll('progress')
  );
  const buttons = Array.from(filePreviews.pointer.querySelectorAll('button'));
  files.forEach((file, index) => {
    const data = new FormData();
    data.append('file', file);
    filePromises.push(
      createFileUploadTask(data, progresses[index], buttons[index])
    );
  });
  const results = await Promise.allSettled(filePromises);

  // file polling

  filesToUpload.forEach((file) => {
    filesToDownload.push(file);
  });
  filesToUpload.splice(0);
  filePreviews.update(
    filesToDownload.map((file) => file.name + ' is ready.'),
    { button: false, bar: false }
  );
  statusMessage.update('✅ Success');

  results.forEach((result) => {
    display.innerText += result.status + ' | ';
  });
}

function createFileUploadTask(data, progress, button) {
  return new Promise((resolve, reject) => {
    const url = 'https://httpbin.org/post';
    const method = 'POST';
    const deleteButton = new HidableElement(button);
    const progressBar = new ProgressBar(progress);
    const progressBarHidable = new HidableElement(progress);
    const xhr = new XMLHttpRequest();

    xhr.addEventListener('loadstart', () => {
      submitButton.disable();
      selectButton.disable();
      resetButton.disable();
      statusMessage.update('⏳ Uploading files...');
      deleteButton.hide();
      progressBarHidable.show();
    });

    xhr.upload.addEventListener('progress', (event) => {
      progressBar.update(event.loaded / event.total);
    });

    xhr.addEventListener('load', () => {
      progressBarHidable.hide();
      resetButton.enable();
    });

    xhr.addEventListener('loadend', () => {
      if (xhr.status < 300 && xhr.status >= 200) {
        resolve(xhr.responseText);
      } else {
        reject(xhr.status + '::' + xhr.statusText);
      }
    });

    xhr.open(method, url);
    xhr.setRequestHeader('Content-Type', 'multipart/form-data');
    xhr.send(data);
  });
}

function createFilePollingTask(data) {
  return new Promise((resolve, reject) => {
    const pollInterval = 2000;
    const pollTimeout = 5 * pollInterval;
    const url = 'https://httpbin.org/post';
    const method = 'GET';
    const xhr = new XMLHttpRequest();

    xhr.addEventListener('loadend', () => {
      if (xhr.status < 300 && xhr.status >= 200) {
        const fileStatus = JSON.parse(xhr.responseText);
        if (fileStatus === 'DONE') {
          clearInterval(polling);
          clearTimeout(timeout);
          resolve(xhr.responseText);
        }
      } else {
        clearInterval(polling);
        clearTimeout(timeout);
        reject(xhr.status + '::' + xhr.statusText);
      }
    });

    xhr.open(method, url);
    xhr.setRequestHeader('Content-Type', 'multipart/form-data');

    const timeout = setTimeout(() => {
      clearInterval(polling);
      reject('Timed out');
    }, pollTimeout);

    const polling = setInterval(() => {
      xhr.send(data);
    }, pollInterval);

    // setInterval{} send get request periodically
    // setTimeOut{} clear interval after some time
  });
}
