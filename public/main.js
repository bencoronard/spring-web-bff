import { DropZone } from './modules/DropZone.mjs';
import { PreviewList } from './modules/PreviewList.mjs';
import { ProgressBar } from './modules/ProgressBar.mjs';
import { StatusText } from './modules/StatusText.mjs';

const form = document.querySelector('form');
const fileInput = document.getElementById('fileInput');

const submitButton = document.getElementById('submitButton');

const fileDropZone = new DropZone(document.getElementById('dropZone'));
const fileUploads = new PreviewList(document.getElementById('fileUploads'));
const filePreviews = new PreviewList(document.getElementById('filePreviews'));
const statusMessage = new StatusText(document.getElementById('statusMessage'));
const progressBar = new ProgressBar(document.querySelector('progress'));

let filesToUpload = [];

fileDropZone.enable().addDropHandle(handleDrop);
form.addEventListener('submit', handleSubmit);
fileInput.addEventListener('change', handleInputChange);

function handleSubmit(event) {
  event.preventDefault();
  showPendingState();
  sendFiles(fileInput.files);
}

function assertFilesValid(fileList) {
  const allowedTypes = ['application/pdf'];

  for (const file of fileList) {
    if (!allowedTypes.includes(file.type)) {
      throw new Error(`❌ File "${file.name}" could not be uploaded.`);
    }
  }
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
  filePreviews.render(fileNames);
  submitButton.disabled = false;
}

function resetFormState() {
  submitButton.disabled = true;
  statusMessage.update(`🤷‍♂ Nothing's uploaded`);
}

function showPendingState() {
  submitButton.disabled = true;
  statusMessage.update('⏳ Pending...');
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
  showPendingState();
  sendFiles(fileList);
}

function sendFiles(files) {
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
      fileUploads.render(fileNames);
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
