const form = document.querySelector("form");
const statusMessage = document.getElementById("statusMessage");
const submitButton = document.querySelector("button");
const fileInput = document.querySelector("input");

form.addEventListener("submit", handleSubmit);
fileInput.addEventListener("change", handleInputChange);

function handleSubmit(event) {
  event.preventDefault();
  uploadFiles();
}

function uploadFiles() {
  const url = "https://httpbin.org/post";
  const method = "POST";

  const xhr = new XMLHttpRequest();

  const data = new FormData(form);

  xhr.open(method, url);
  xhr.send(data);
}

function updateStatusMessage(text) {
  statusMessage.textContent = text;
}

function assertFilesValid(fileList) {
  const allowedTypes = ["application/pdf"];

  for (const file of fileList) {
    if (!allowedTypes.includes(file.type)) {
      throw new Error(`❌ File "${file.name}" could not be uploaded.`);
    }
  }
}

function handleInputChange() {
  try {
    assertFilesValid(fileInput.files);
  } catch (err) {
    updateStatusMessage(err.message);
    return;
  }
  submitButton.disabled = false;
}
