import {
  StatusText,
  ProgressBar,
  PreviewList,
  DynamicButton,
  DropZone,
  PreviewListInteractive,
} from './modules/components.js';

const filesToUpload = [];
const filesToDownload = [];

const downloadButton = new DynamicButton(
  document.getElementById('downloadButton')
);

const resetButton = new DynamicButton(document.getElementById('resetButton'));
const submitButton = new DynamicButton(document.getElementById('submitButton'));
const fileDropZone = new DropZone(document.getElementById('dropZone'));
const fileUploads = new PreviewList(document.getElementById('fileUploads'));
const filePreviews = new PreviewListInteractive(
  document.getElementById('filePreviews'),
  filesToUpload
);
const statusMessage = new StatusText(document.getElementById('statusMessage'));
const progressBar = new ProgressBar(document.querySelector('progress'));

const form = document.querySelector('form');
const fileInput = document.getElementById('fileInput');

fileDropZone.enable().addDropHandle(handleDrop);
form.addEventListener('submit', handleSubmit);
form.addEventListener('reset', resetFormState);
fileInput.addEventListener('change', handleInputChange);
downloadButton.addClickHandle(handleDownload);
resetFormState();

// Functions

function resetFormState() {
  submitButton.disable();
  resetButton.disable();
  downloadButton.disable();
  statusMessage.update(`🤷‍♂ Nothing's uploaded`);
  filePreviews.clear();
  fileUploads.clear();
  filesToUpload.splice(0);
  filesToDownload.splice(0);
}

function handleInputChange() {
  handleFileUpload(fileInput.files);
}

function handleDrop(event) {
  handleFileUpload(event.dataTransfer.files);
}

function handleFileUpload(uploadedFiles) {
  try {
    extractFiles(uploadedFiles);
  } catch (err) {
    statusMessage.update(err.message);
  }
  filePreviews.update(filesToUpload.map((file) => file.name));
  submitButton.enable();
  console.log(filesToUpload);
}

function handleDownload() {
  console.log('Downloading...');
}

function handleSubmit(event) {
  event.preventDefault();
  sendFiles(filesToUpload);
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

function sendFiles(files) {
  statusMessage.update('⏳ Pending...');
  submitButton.disable();
  const url = 'https://httpbin.org/post';
  const method = 'POST';
  const xhr = new XMLHttpRequest();

  const data = new FormData();
  for (const file of files) {
    data.append('file', file);
  }

  xhr.addEventListener('loadend', () => {
    if (xhr.status === 200) {
      statusMessage.update('✅ Success');
      fileUploads.update(filesToUpload.map((file) => file.name));
      downloadButton.enable();
      resetButton.enable();
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

// Create a new instance of MutationObserver and define a callback function
const observer = new MutationObserver((mutationsList) => {
  // Loop through each mutation
  for (let mutation of mutationsList) {
    // Check if nodes were added or removed from the target node's children
    if (mutation.type === 'childList') {
      // Handle the changes here, for example:
      if (filesToUpload.length === 0) {
        submitButton.disable();
      } else {
        submitButton.enable();
      }
    }
  }
});

observer.observe(filePreviews.pointer, { childList: true });
