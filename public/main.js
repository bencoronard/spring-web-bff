import * as components from './modules/components.js';

const filesToUpload = [];
const filesToDownload = [];
const filesNotCompleted = [];

const display = document.getElementById('display');
const success = display.querySelector('.success');
const failure = display.querySelector('.failure');

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
//###################     INITIALIZATIONS    ############################
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
//###################      APP STATE FUNCTIONS      #####################
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

  success.innerText = '';
  failure.innerText = '';
}
function handleDownload() {
  console.log('Downloading...');
}
function handleFileUpload(uploadedFiles) {
  try {
    extractFiles(uploadedFiles);
  } catch (error) {
    statusMessage.update(error.message);
  }
  // Initialize uploaded file previews
  filePreviews.update(
    filesToUpload.map((file) => file.name),
    { button: true, bar: true, text: true }
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

async function handleSubmit(event) {
  event.preventDefault();

  // Disable buttons
  submitButton.disable();
  fileOptions.hide();
  selectButton.disable();
  resetButton.disable();
  statusMessage.update('⏳ Uploading files...');

  // Upload
  const tasksToPoll = await sendFiles(filesToUpload);

  // Render file downloads
  filePreviews.update(filesToDownload, {
    button: false,
    bar: false,
    text: true,
  });
  statusMessage.update('✅ Success');
  resetButton.enable();

  await pollFiles(tasksToPoll);
}

//#######################################################################
//##################       NETWORK FUNCTIONS      #######################
//#######################################################################

async function sendFiles(files) {
  const progresses = Array.from(
    filePreviews.pointer.querySelectorAll('progress')
  );
  const buttons = Array.from(filePreviews.pointer.querySelectorAll('button'));
  const statuses = Array.from(filePreviews.pointer.querySelectorAll('span'));

  const uploadTasks = [];
  const tasksToPoll = [];

  files.forEach((file, index) => {
    uploadTasks.push(
      createFileUploadTask(
        file,
        progresses[index],
        buttons[index],
        statuses[index]
      )
    );
  });

  const uploadResults = await Promise.allSettled(uploadTasks);
  uploadResults.forEach((result, index) => {
    if (result.status === 'fulfilled') {
      filesToDownload.push(filesToUpload[index].name);
      tasksToPoll.push(result.value);
    } else {
      filesNotCompleted.push(filesToUpload[index].name);
    }
  });
  filesToUpload.splice(0);
  return tasksToPoll;
}

async function pollFiles(tasks) {
  const statuses = Array.from(filePreviews.pointer.querySelectorAll('span'));
  const pollTasks = [];

  tasks.forEach((task, index) => {
    pollTasks.push(createFilePollingTask(task, statuses[index]));
  });

  const pollResults = await Promise.allSettled(pollTasks);
  pollResults.forEach((result) => {
    if (result.status === 'fulfilled') {
      success.innerText += result.value + '\n';
    } else {
      failure.innerText += result.reason + '\n';
    }
  });
}

//#######################################################################
function createFileUploadTask(file, progress, button, status) {
  return new Promise((resolve, reject) => {
    const deleteButton = new components.HidableElement(button);
    const progressBar = new components.ProgressBar(progress);
    const progressBarHidable = new components.HidableElement(progress);
    const statusText = new components.StatusText(status);
    const statusTextHidable = new components.HidableElement(status);

    const formData = new FormData();
    formData.append('file', file);

    const url = 'https://filetools13.pdf24.org/client.php?action=upload';
    const method = 'POST';
    const xhr = new XMLHttpRequest();

    // Configure XMLHttpRequest
    xhr.open(method, url);

    xhr.onloadstart = () => {
      progressBarHidable.show();
      deleteButton.hide();
    };

    xhr.upload.onprogress = (event) => {
      progressBar.update(event.loaded / event.total);
    };

    xhr.onerror = () => {
      reject('Network error while uploading file:', file.name);
    };

    xhr.onload = async () => {
      progressBarHidable.hide();
      statusTextHidable.show();
      if (xhr.status < 300 && xhr.status >= 200) {
        statusText.update('✔️ uploaded');
        // Parse response object
        const response = JSON.parse(xhr.responseText);
        // Start file compression job
        try {
          const anotherResponse = await signalTaskStart(response);
          statusText.update('♻️ compressing...');
          resolve(anotherResponse);
        } catch (error) {
          statusText.update('⭕️ failed to start compression');
          reject('Error:' + error.message);
        }
      } else {
        statusText.update('❌ fail to upload');
        reject('Error:' + xhr.status);
      }
    };

    // xhr.onloadend = () => {};

    // Send request
    xhr.send(formData);
  });
}

function createFilePollingTask(data, status) {
  return new Promise((resolve, reject) => {
    const statusText = new components.StatusText(status);
    const statusTextHidable = new components.HidableElement(status);

    const pollInterval = 2000;
    const pollTimeout = 5 * pollInterval;

    const url = `https://filetools13.pdf24.org/client.php?action=getStatus&jobId=${data.jobId}`;
    resolve(url);
    // const method = 'GET';
    // const xhr = new XMLHttpRequest();

    // xhr.addEventListener('loadend', () => {
    //   if (xhr.status < 300 && xhr.status >= 200) {
    //     const fileStatus = JSON.parse(xhr.responseText);
    //     if (fileStatus === 'DONE') {
    //       clearInterval(polling);
    //       clearTimeout(timeout);
    //       resolve(xhr.responseText);
    //     }
    //   } else {
    //     clearInterval(polling);
    //     clearTimeout(timeout);
    //     reject(xhr.status + '::' + xhr.statusText);
    //   }
    // });

    // xhr.open(method, url);
    // xhr.setRequestHeader('Content-Type', 'multipart/form-data');

    // const timeout = setTimeout(() => {
    //   clearInterval(polling);
    //   reject('Timed out');
    // }, pollTimeout);

    // const polling = setInterval(() => {
    //   xhr.send(data);
    // }, pollInterval);
  });
}
//#######################################################################
function signalTaskStart(data) {
  return new Promise((resolve, reject) => {
    const options = formJSON.getValues();
    const payload = JSON.stringify({
      files: data,
      dpi: parseInt(options.dpi),
      imageQuality: parseInt(options.imgQuality),
      mode: 'normal',
      colorModel: options.grayScale ? 'gray' : '',
    });

    const url = 'https://filetools13.pdf24.org/client.php?action=compressPdf';
    const method = 'POST';
    const xhr = new XMLHttpRequest();

    // Configure XMLHttpRequest
    xhr.open(method, url);
    xhr.setRequestHeader('Content-Type', 'application/json; charset=UTF-8');

    xhr.onerror = () => {
      reject('Network error while trying to start file compression');
      // throw new Error('unable to start compression');
    };

    xhr.onload = () => {
      if (xhr.status < 300 && xhr.status >= 200) {
        const response = JSON.parse(xhr.responseText);
        resolve(response);
      } else {
        reject('unable to start compression');
      }
    };

    // Send request
    xhr.send(payload);
  });
}
