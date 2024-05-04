import {
  StatusText,
  ProgressBar,
  PreviewList,
  DynamicButton,
  DropZone,
} from './modules/components.js';

const downloadButton = new DynamicButton(
  document.getElementById('downloadButton')
);
const resetButton = new DynamicButton(document.getElementById('resetButton'));
const submitButton = new DynamicButton(document.getElementById('submitButton'));
const fileDropZone = new DropZone(document.getElementById('dropZone'));
const fileUploads = new PreviewList(document.getElementById('fileUploads'));
const filePreviews = new PreviewList(document.getElementById('filePreviews'));
const statusMessage = new StatusText(document.getElementById('statusMessage'));
const progressBar = new ProgressBar(document.querySelector('progress'));

const form = document.querySelector('form');
const fileInput = document.getElementById('fileInput');
let filesToUpload = [];

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
}

function handleInputChange() {
  resetFormState();
  try {
    assertFilesValid(fileInput.files);
  } catch (err) {
    statusMessage.update(err.message);
    return;
  }
  const fileNames = Array.from(fileInput.files).map((file) => file.name);
  filePreviews.update(fileNames);
  submitButton.enable();
}

function handleDownload() {
  console.log('Downloading...');
}

function handleSubmit(event) {
  event.preventDefault();
  sendFiles(fileInput.files);
}

function handleDrop(event) {
  const fileList = event.dataTransfer.files;
  resetFormState();
  try {
    assertFilesValid(fileList);
  } catch (err) {
    statusMessage.update(err.message);
    return;
  }
  fileInput.files = fileList;
  const fileNames = Array.from(fileList).map((file) => file.name);
  filePreviews.update(fileNames);
  submitButton.enable();
  console.log(fileInput.files);
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
      statusMessage.update('✅ Success');
      const fileNames = Array.from(fileInput.files).map((file) => file.name);
      fileUploads.update(fileNames);
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

function assertFilesValid(fileList) {
  const allowedTypes = ['application/pdf'];

  for (const file of fileList) {
    if (!allowedTypes.includes(file.type)) {
      throw new Error(`❌ File "${file.name}" could not be uploaded.`);
    }
  }
}
