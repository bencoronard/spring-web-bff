import * as components from './modules/components.js';

//#######################################################################
//#######################################################################

const filesToUpload = [];
const filesToDownload = [];

const display = document.getElementById('display');

const downloadButton = new components.DynamicButton(
  document.getElementById('downloadButton')
);
const resetButton = new components.DynamicButton(
  document.getElementById('resetButton')
);
const selectButton = new components.DynamicButton(
  document.getElementById('selectButton')
);
const submitButton = new components.DynamicButton(
  document.getElementById('submitButton')
);

const fileDropZone = new components.DropZone(
  document.getElementById('dropZone'),
  (event) => {
    handleFileUpload(event.dataTransfer.files);
  }
);

const filePreviews = new components.PreviewListInteractive(
  document.getElementById('filePreviews'),
  filesToUpload
);

const statusMessage = new components.StatusText(
  document.getElementById('statusMessage')
);
const stageList = new components.ChromaticList(
  document.getElementById('stageList')
);

const formJSON = new components.JsonForm(document.querySelector('form'));

const fileInput = document.getElementById('fileInput');
const fileOptions = new components.HidableElement(
  document.getElementById('options')
);

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

//#######################################################################
//#######################################################################

selectButton.addClickHandle(() => {
  fileInput.click();
});
downloadButton.addClickHandle(handleDownload);

formJSON.pointer.addEventListener('submit', handleSubmit);
formJSON.pointer.addEventListener('reset', resetAppState);

fileInput.addEventListener('change', () => {
  handleFileUpload(fileInput.files);
});

resetAppState();

appStateTracker.observe(filePreviews.pointer, { childList: true });

//#######################################################################
//#######################################################################
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
function handleDownload() {
  console.log('Downloading...');
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

function handleSubmit(event) {
  event.preventDefault();

  // sendFiles(filesToUpload);
  sendMultipleFiles(filesToUpload);
  // sendFilesMultiple(filesToUpload);
}

function sendFiles(files) {
  statusMessage.update('⏳ Pending...');
  const url = 'https://filetools13.pdf24.org/client.php?action=upload';
  const method = 'POST';

  const data = new FormData();
  for (const file of files) {
    data.append('file', file);
  }

  const xhr = new XMLHttpRequest();
  xhr.open(method, url);

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
      display.innerText = xhr.responseText;
    } else {
      statusMessage.update('❌ Error');
      display.innerText = 'Error:' + xhr.status;
    }
  });

  xhr.upload.addEventListener('progress', (event) => {
    statusMessage.update(`⏳ Uploaded ${event.loaded} bytes of ${event.total}`);
  });

  xhr.send(data);
}

async function sendFilesMultiple(files) {
  const filePromises = [];
  const progresses = Array.from(
    filePreviews.pointer.querySelectorAll('progress')
  );
  const buttons = Array.from(filePreviews.pointer.querySelectorAll('button'));

  const formData = new FormData();
  for (const file of files) {
    formData.append('file', file);
  }
  console.log(formData);

  // files.forEach((file, index) => {
  //   const data = new FormData();
  //   data.append('file', file);
  //   console.log(data);
  //   // filePromises.push(
  //   //   createFileUploadTask(data, progresses[index], buttons[index])
  //   // );
  // });

  // const results = await Promise.allSettled(filePromises);

  // filesToUpload.forEach((file) => {
  //   filesToDownload.push(file);
  // });
  // filesToUpload.splice(0);
  // filePreviews.update(
  //   filesToDownload.map((file) => file.name + ' is ready.'),
  //   { button: false, bar: false }
  // );
  // statusMessage.update('✅ Success');

  // results.forEach((result) => {
  //   display.innerText += result.status + ' | ';
  // });
}

function sendMultipleFiles(files) {
  // Iterate through each file in the array
  files.forEach((file) => {
    console.log(file.name);
    const xhr = new XMLHttpRequest();
    const formData = new FormData();

    // Add the file to FormData
    formData.append('file', file);

    // Configure the XMLHttpRequest
    xhr.open('POST', 'https://filetools13.pdf24.org/client.php?action=upload');

    // Handle the response
    xhr.onload = function () {
      if (xhr.status === 200) {
        display.innerText += xhr.responseText;
      } else {
        display.innerText += 'Error:' + xhr.status;
      }
    };

    // Handle network errors
    xhr.onerror = function () {
      console.error('Network error while uploading file:', file.name);
    };

    // Send the request
    xhr.send(formData);
  });
}

function createFileUploadTask(data, progress, button) {
  return new Promise((resolve, reject) => {
    const url = 'https://httpbin.org/post';
    const method = 'POST';
    const deleteButton = new components.HidableElement(button);
    const progressBar = new components.ProgressBar(progress);
    const progressBarHidable = new components.HidableElement(progress);
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
  });
}
