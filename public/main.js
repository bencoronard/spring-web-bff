import * as components from './modules/components.js';

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

async function handleSubmit(event) {
  event.preventDefault();

  // Disable buttons
  submitButton.disable();
  fileOptions.hide();
  selectButton.disable();
  resetButton.disable();
  statusMessage.update('⏳ Uploading files...');
  // Upload
  await sendFiles(filesToUpload);
}

async function sendFiles(files) {
  const progresses = Array.from(
    filePreviews.pointer.querySelectorAll('progress')
  );
  const buttons = Array.from(filePreviews.pointer.querySelectorAll('button'));

  const filePromises = [];
  files.forEach((file, index) => {
    filePromises.push(
      createFileUploadTask(file, progresses[index], buttons[index])
    );
  });

  const responses = await Promise.allSettled(filePromises);

  // Handle app state change
  filesToUpload.forEach((file) => {
    filesToDownload.push(file);
  });
  filesToUpload.splice(0);
  filePreviews.update(
    filesToDownload.map((file) => file.name + ' is ready.'),
    { button: false, bar: false }
  );
  statusMessage.update('✅ Success');
  resetButton.enable();

  // Log response
  responses.forEach((response) => {
    if (response.status === 'fulfilled') {
      display.innerText += response.value + '\n';
    } else {
      display.innerText += response.reason + '\n';
    }
  });
}

function createFileUploadTask(data, progress, button) {
  return new Promise((resolve, reject) => {
    const deleteButton = new components.HidableElement(button);
    const progressBar = new components.ProgressBar(progress);
    const progressBarHidable = new components.HidableElement(progress);

    const url = 'https://filetools13.pdf24.org/client.php?action=upload';
    const method = 'POST';
    const xhr = new XMLHttpRequest();

    const formData = new FormData();
    // Add the file to FormData
    formData.append('file', data);

    // Configure XMLHttpRequest
    xhr.open(method, url);

    xhr.onloadstart = () => {
      progressBarHidable.show();
      deleteButton.hide();
    };

    xhr.upload.onprogress = (event) => {
      progressBar.update(event.loaded / event.total);
    };

    xhr.onload = () => {
      progressBarHidable.hide();
    };

    xhr.onloadend = () => {
      if (xhr.status === 200) {
        resolve(xhr.responseText);
      } else {
        reject('Error:' + xhr.status);
      }
    };

    xhr.onerror = () => {
      reject('Network error while uploading file:', file.name);
    };

    // Send request
    xhr.send(formData);
  });
}

// function createFilePollingTask(data) {
//   return new Promise((resolve, reject) => {
//     const pollInterval = 2000;
//     const pollTimeout = 5 * pollInterval;
//     const url = 'https://httpbin.org/post';
//     const method = 'GET';
//     const xhr = new XMLHttpRequest();

//     xhr.addEventListener('loadend', () => {
//       if (xhr.status < 300 && xhr.status >= 200) {
//         const fileStatus = JSON.parse(xhr.responseText);
//         if (fileStatus === 'DONE') {
//           clearInterval(polling);
//           clearTimeout(timeout);
//           resolve(xhr.responseText);
//         }
//       } else {
//         clearInterval(polling);
//         clearTimeout(timeout);
//         reject(xhr.status + '::' + xhr.statusText);
//       }
//     });

//     xhr.open(method, url);
//     xhr.setRequestHeader('Content-Type', 'multipart/form-data');

//     const timeout = setTimeout(() => {
//       clearInterval(polling);
//       reject('Timed out');
//     }, pollTimeout);

//     const polling = setInterval(() => {
//       xhr.send(data);
//     }, pollInterval);
//   });
// }
