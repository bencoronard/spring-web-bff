import { JsonForm } from "./modules/JsonForm.js";
import {
  StatusText,
  ProgressBar,
  DynamicButton,
  DropZone,
  PreviewListInteractive,
  ChromaticList,
  HidableElement,
} from "./modules/components.js";

const filesToUpload = [];
const filesToDownload = [];

const downloadButton = new DynamicButton(
  document.getElementById("downloadButton")
);
const resetButton = new DynamicButton(document.getElementById("resetButton"));
const selectButton = new DynamicButton(document.getElementById("selectButton"));
const submitButton = new DynamicButton(document.getElementById("submitButton"));

const fileDropZone = new DropZone(
  document.getElementById("dropZone"),
  (event) => {
    handleFileUpload(event.dataTransfer.files);
  }
);
const filePreviews = new PreviewListInteractive(
  document.getElementById("filePreviews"),
  filesToUpload
);

const statusMessage = new StatusText(document.getElementById("statusMessage"));
const progressBar = new ProgressBar(document.querySelector("progress"));
const stageList = new ChromaticList(document.getElementById("stageList"));

const form = document.querySelector("form");
const formJSON = new JsonForm(form);

const fileInput = document.getElementById("fileInput");
const fileOptions = new HidableElement(document.getElementById("options"));

selectButton.addClickHandle(() => {
  fileInput.click();
});
downloadButton.addClickHandle(handleDownload);

form.addEventListener("submit", handleSubmit);
form.addEventListener("reset", resetFormState);
form.addEventListener("change", () => {
  formJSON.getValues();
  console.log(filesToUpload);
});

fileOptions.pointer.addEventListener("keydown", (event) => {
  if (event.key === "Enter") {
    event.preventDefault();
  }
});

fileInput.addEventListener("change", () => {
  handleFileUpload(fileInput.files);
});

resetFormState();

const appStateTracker = new MutationObserver((mutationsList) => {
  for (let mutation of mutationsList) {
    if (mutation.type === "childList") {
      const a = filesToUpload.length > 0 ? 1 : 0;
      const b = filesToDownload.length > 0 ? 1 : 0;
      switch (`${a}${b}`) {
        case "00":
          submitButton.disable();
          resetButton.disable();
          stageList.setStage(0);
          fileOptions.hide();
          break;
        case "10":
          submitButton.enable();
          resetButton.enable();
          stageList.setStage(1);
          fileOptions.show();
          break;
        case "01":
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
function resetFormState() {
  filesToUpload.splice(0);
  filesToDownload.splice(0);
  fileInput.value = "";

  filePreviews.clear();
  selectButton.enable();
  submitButton.disable();
  resetButton.disable();
  downloadButton.disable();
  fileDropZone.enable();
  stageList.reset();
  fileOptions.hide();

  statusMessage.update(`🤷‍♂ Nothing's uploaded`);
}

function handleFileUpload(uploadedFiles) {
  try {
    extractFiles(uploadedFiles);
  } catch (err) {
    statusMessage.update(err.message);
  }
  filePreviews.update(
    filesToUpload.map((file) => file.name),
    true
  );
  submitButton.enable();
}

function handleDownload() {
  console.log("Downloading...");
}

function handleSubmit(event) {
  event.preventDefault();

  // sendFiles(filesToUpload);
  mockSending(filesToUpload);
}

function extractFiles(fileList) {
  const allowedTypes = ["application/pdf"];
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
      `❌ Could not upload following files: ${invalidFiles.join(", ")}`
    );
  }
}

function mockSending(filesToUpload) {
  submitButton.disable();
  fileOptions.hide();
  statusMessage.update("⏳ Uploading...");

  let progress = 0;

  setTimeout(() => {
    filesToUpload.forEach((file) => {
      filesToDownload.push(file);
    });
    filesToUpload.splice(0);

    statusMessage.update("✅ Success");
    progressBar.update(0);
    submitButton.enable();

    filePreviews.update(
      filesToDownload.map((file) => file.name + " is ready."),
      false
    );

    clearInterval(progressBarUpdateTimer);
  }, 3250);

  const progressBarUpdateTimer = setInterval(() => {
    progress += 0.35;
    progressBar.update(progress);
  }, 1000);
}

function sendFiles(files) {
  statusMessage.update("⏳ Pending...");
  const url = "https://httpbin.org/post";
  const method = "POST";
  const xhr = new XMLHttpRequest();

  const data = new FormData();
  for (const file of files) {
    data.append("file", file);
  }

  xhr.addEventListener("loadend", () => {
    if (xhr.status === 200) {
      filesToUpload.forEach((file) => {
        filesToDownload.push(file);
      });
      filesToUpload.splice(0);
      filePreviews.update(
        filesToDownload.map((file) => file.name + " is ready."),
        false
      );
      statusMessage.update("✅ Success");
    } else {
      statusMessage.update("❌ Error");
    }
    progressBar.update(0);
  });

  xhr.upload.addEventListener("progress", (event) => {
    statusMessage.update(`⏳ Uploaded ${event.loaded} bytes of ${event.total}`);
    progressBar.update(event.loaded / event.total);
  });

  xhr.open(method, url);
  xhr.send(data);
}
