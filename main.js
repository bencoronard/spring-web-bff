import * as components from './modules/components.js';

const filesToUpload = [];
const filesToDownload = [];
const tasksToDownload = [];
const indicesToDownload = [];

const downloadButton = new components.DynamicButton(
  document.getElementById('downloadButton')
);
const downloadButtonHidable = new components.HidableElement(
  document.getElementById('downloadButton')
);
const resetButton = new components.DynamicButton(
  document.getElementById('resetButton')
);
const resetButtonHidable = new components.HidableElement(
  document.getElementById('resetButton')
);
const selectButton = new components.DynamicButton(
  document.getElementById('selectButton')
);
const selectButtonHidable = new components.HidableElement(
  document.getElementById('selectButton')
);
const submitButton = new components.DynamicButton(
  document.getElementById('submitButton')
);
const submitButtonHidable = new components.HidableElement(
  document.getElementById('submitButton')
);

const otherTools = new components.HidableElement(
  document.getElementById('other-tools')
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
          resetAppState();
          break;
        case '10':
          stageList.setStage(1);

          submitButton.enable();
          submitButtonHidable.show();

          selectButton.disable();
          selectButtonHidable.hide();

          fileOptions.show();
          break;
        case '01':
          stageList.setStage(2);

          submitButton.disable();
          submitButtonHidable.hide();

          resetButton.enable();
          downloadButton.enable();

          fileDropZone.disable();

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
//####################      BUTTON FUNCTIONS      #######################
//#######################################################################
function resetAppState() {
  stageList.setStage(0);

  selectButton.enable();
  selectButtonHidable.show();

  submitButton.disable();
  submitButtonHidable.hide();

  resetButton.disable();
  resetButtonHidable.hide();

  downloadButton.disable();
  downloadButtonHidable.hide();

  otherTools.hide();
  fileOptions.hide();
  fileDropZone.enable();

  filePreviews.clear();

  filesToUpload.splice(0);
  filesToDownload.splice(0);
  tasksToDownload.splice(0);
  indicesToDownload.splice(0);
  fileInput.value = '';

  statusMessage.update(``);
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

  submitButton.disable();

  statusMessage.update('⏳ Uploading files...');

  // Upload
  const tasksToPoll = await sendFiles(filesToUpload);

  // Render file downloads
  filePreviews.update(filesToDownload, {
    button: false,
    bar: false,
    text: true,
  });
  statusMessage.update('✅ Files uploaded');

  // Edit here
  const { tasks: tasksCompleted, index: indexToDownload } = await pollFiles(
    tasksToPoll
  );

  tasksCompleted.forEach((task, index) => {
    tasksToDownload.push(task);
    indicesToDownload.push(indexToDownload[index]);
  });

  downloadButtonHidable.show();
  resetButtonHidable.show();
  otherTools.show();

  if (!tasksCompleted.length) {
    downloadButton.disable();
    statusMessage.update('⛔️ Files not available to download');
  } else {
    statusMessage.update('🚀 Files available to download');
  }
}

async function handleDownload() {
  downloadButton.disable();
  resetButton.disable();
  // try {}catch(errpr){}
  await downloadFiles(tasksToDownload);
  downloadButton.enable();
  resetButton.enable();
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
      createUploadTask(file, progresses[index], buttons[index], statuses[index])
    );
  });

  const uploadResults = await Promise.allSettled(uploadTasks);
  uploadResults.forEach((result, index) => {
    if (result.status === 'fulfilled') {
      filesToDownload.push(filesToUpload[index].name);
      tasksToPoll.push(result.value);
    }
  });
  filesToUpload.splice(0);
  return tasksToPoll;
}

async function pollFiles(tasks) {
  const statuses = Array.from(filePreviews.pointer.querySelectorAll('span'));

  const pollTasks = [];
  const tasksCompleted = [];

  tasks.forEach((task, index) => {
    pollTasks.push(createPollingTask(task, statuses[index]));
  });

  const pollResults = await Promise.allSettled(pollTasks);

  const indexToRemove = [];
  const indexToDownload = [];

  pollResults.forEach((result, index) => {
    if (result.status === 'fulfilled') {
      tasksCompleted.push(result.value);
      indexToDownload.push(index);
    } else {
      indexToRemove.push(index);
    }
  });
  // Here
  indexToRemove
    .sort((a, b) => b - a)
    .forEach((index) => {
      filesToDownload.splice(index, 1);
    });

  return { tasks: tasksCompleted, index: indexToDownload };
}

async function downloadFiles(tasks) {
  const statuses = Array.from(filePreviews.pointer.querySelectorAll('span'));

  const downloadTasks = [];
  tasks.forEach((task, index) => {
    downloadTasks.push(
      createDownloadTask(
        task,
        filesToDownload[index],
        statuses[indicesToDownload[index]]
      )
    );
  });

  await Promise.allSettled(downloadTasks);
}
//#######################################################################
function createUploadTask(file, progress, button, status) {
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
      statusText.update('⚠️ failed to upload');
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
          const ongoingTask = await signalTaskStart(response);
          statusText.update('♻️ starting');
          resolve(ongoingTask);
        } catch (error) {
          statusText.update('⭕️ failed to start compression');
          reject('Error: ' + error.message);
        }
      } else {
        statusText.update('❌ failed to upload');
        reject('Error: ' + xhr.status);
      }
    };

    // Send request
    xhr.send(formData);
  });
}

function createPollingTask(data, status) {
  return new Promise((resolve, reject) => {
    const statusText = new components.StatusText(status);
    const statusTextHidable = new components.HidableElement(status);

    const pollInterval = 1000;
    const pollTimeout = 5 * pollInterval;

    statusTextHidable.show();
    statusText.update('🟡 compressing');

    let randNum;

    const mockTimeout = setTimeout(() => {
      clearInterval(mockPolling);
      statusText.update('🟠 task took too long to complete');
      reject();
    }, pollTimeout);

    const mockPolling = setInterval(() => {
      randNum = Math.floor(Math.random() * 10) + 1;
      if (randNum >= 8) {
        clearTimeout(mockTimeout);
        clearInterval(mockPolling);
        statusText.update('🟢 ready to download');
        resolve(data);
      }
    }, pollInterval);

    // const url = `https://filetools13.pdf24.org/client.php?action=getStatus&jobId=${data.jobId}`;
    // const method = 'GET';
    // const xhr = new XMLHttpRequest();

    // Configure XMLHttpRequest
    // xhr.open(method, url);

    // xhr.onloadstart = () => {
    //   statusTextHidable.show();
    //   statusText.update('🟡 compressing');
    // };

    // xhr.onerror = () => {
    //   statusText.update('⚠️ polling failed');
    //   reject('Network error while tracking file status');
    // };

    // xhr.onload = () => {
    //   if (xhr.status < 300 && xhr.status >= 200) {
    //     const fileStatus = JSON.parse(xhr.responseText).status;
    //     if (fileStatus === 'done') {
    //       statusText.update('🟢 ready to download');
    //       resolve(data);
    //     }
    //   } else {
    //     statusText.update('❌ polling failed');
    //     reject('Error: ' + xhr.status);
    //   }
    // };

    // xhr.onloadend = () => {
    //   clearTimeout(timeout);
    //   clearInterval(polling);
    // };

    // const timeout = setTimeout(() => {
    //   clearInterval(polling);
    //   statusText.update('🔴 too long to complete');
    //   reject('Timed out');
    // }, pollTimeout);

    // const polling = setInterval(() => {
    //   xhr.send(data);
    // }, pollInterval);
  });
}

function createDownloadTask(data, fileName, status) {
  return new Promise((resolve, reject) => {
    const statusText = new components.StatusText(status);

    const url = `https://filetools13.pdf24.org/client.php?mode=download&action=downloadJobResult&jobId=${data.jobId}`;
    const method = 'GET';
    const xhr = new XMLHttpRequest();

    // Configure XMLHttpRequest
    xhr.open(method, url);
    xhr.responseType = 'blob';

    xhr.onloadstart = () => {
      statusText.update('📀 downloading');
    };

    xhr.upload.onprogress = (event) => {
      statusText.update(
        `📀 ${Math.floor(event.loaded / event.total) * 100}% downloaded`
      );
    };

    xhr.onerror = () => {
      statusText.update('⚠️ failed to download');
      reject('Network error while downloading file:', fileName);
    };

    xhr.onload = () => {
      if (xhr.status < 300 && xhr.status >= 200) {
        // Create a temporary anchor element to trigger the download
        const downloadUrl = window.URL.createObjectURL(xhr.response);
        const downloadLink = document.createElement('a');
        downloadLink.href = downloadUrl;
        downloadLink.download = fileName + '_compressed';
        document.body.appendChild(downloadLink);
        downloadLink.click();

        // Clean up after the download is complete
        document.body.removeChild(downloadLink);
        window.URL.revokeObjectURL(downloadUrl);

        statusText.update('💽 downloaded');
        resolve();
      } else {
        statusText.update('❌ failed to download');
        reject('Error: ' + xhr.status);
      }
    };

    // Send request
    xhr.send();
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
