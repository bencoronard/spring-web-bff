const dropContainer = document.getElementById('dropContainer');
const dropMessage = document.getElementById('dropMessage');
const fileInput = document.getElementById('fileInput');
const uploadButton = document.getElementById('uploadButton');

dropContainer.addEventListener('dragover', (e) => {
  e.preventDefault();
  dropContainer.classList.add('highlight');
});

dropContainer.addEventListener('dragleave', () => {
  dropContainer.classList.remove('highlight');
});

dropContainer.addEventListener('drop', (e) => {
  e.preventDefault();
  dropContainer.classList.remove('highlight');

  const files = e.dataTransfer.files;
  handleFiles(files);
});

fileInput.addEventListener('change', () => {
  const files = fileInput.files;
  handleFiles(files);
});

uploadButton.addEventListener('click', () => {
  const files = fileInput.files;
  uploadFiles(files);
});

function handleFiles(files) {
  Array.from(files).forEach((file) => {
    const fileItem = document.createElement('div');
    fileItem.classList.add('file-item');
    fileItem.textContent = file.name;
    dropContainer.appendChild(fileItem);
  });
}

function uploadFiles(files) {
  const formData = new FormData();
  Array.from(files).forEach((file) => {
    formData.append('files', file);
  });

  fetch('http://localhost:3000/api/uploads', {
    method: 'POST',
    body: formData,
  })
    .then((response) => {
      if (response.ok) {
        alert('Files uploaded successfully!');
      } else {
        alert('Error uploading files.');
      }
    })
    .catch((error) => {
      console.error('Error uploading files:', error);
      alert('Error uploading files.');
    });
}
