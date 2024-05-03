const dropZone = document.getElementById("dropZone");
const dropMessage = document.getElementById("dropMessage");
const filePreviews = document.getElementById("filePreviews");
const fileInput = document.getElementById("fileInput");
const compressButton = document.getElementById("compressButton");
const uploadButton = document.getElementById("uploadButton");
const submitButton = document.getElementById("submitButton");
let filesToUpload = [];

fileInput.addEventListener("change", () => {
  handleFiles(fileInput.files);
});

dropZone.addEventListener("dragover", (e) => {
  e.stopPropagation();
  e.preventDefault();
  dropZone.classList.add("highlight");
});

dropZone.addEventListener("dragleave", () => {
  dropZone.classList.remove("highlight");
});

dropZone.addEventListener("drop", (e) => {
  e.stopPropagation();
  e.preventDefault();
  dropZone.classList.remove("highlight");
  const files = e.dataTransfer.files;
  handleFiles(files);
});

submitButton.addEventListener("click", sendFiles);

function handleFiles(files) {
  Array.from(files).forEach((file) => {
    const fileExists = filesToUpload.some(
      (existingFile) => existingFile.name === file.name
    );
    if (fileExists) {
      return;
    }

    if (file.name.toLowerCase().endsWith(".pdf")) {
      filesToUpload.push(file);

      const preview = document.createElement("div");
      preview.classList.add("file-preview");

      const desc = document.createElement("div");
      desc.classList.add("file-name");
      desc.innerText = `${file.name}`;

      const delBttn = document.createElement("button");
      delBttn.classList.add("delete-button");
      delBttn.innerText = "Delete";
      delBttn.addEventListener("click", (e) => {
        const indexToRemove = Array.from(filePreviews.children).indexOf(
          e.target.parentNode
        );
        filesToUpload.splice(indexToRemove, 1);
        console.log(filesToUpload);
        preview.remove();
        fileInput.value = "";
        if (filesToUpload.length !== 0) {
          uploadButton.style.display = "none";
        } else {
          uploadButton.style.display = "initial";
        }
      });

      preview.appendChild(desc);
      preview.appendChild(delBttn);
      filePreviews.appendChild(preview);

      if (filesToUpload.length !== 0) {
        uploadButton.style.display = "none";
      } else {
        uploadButton.style.display = "initial";
      }
    } else {
      alert("Please upload only PDF files.");
      fileInput.value = "";
    }
  });
}

async function sendFiles() {
  if (filesToUpload.length === 0) {
    alert("No files uploaded");
    return;
  }
  const formData = new FormData();
  filesToUpload.forEach((file) => {
    formData.append("files", file);
  });
  try {
    const response = await fetch("http://localhost:3000/api/uploads", {
      method: "POST",
      body: formData,
    });
    console.log(response);
    // const data = await response.json();
    // alert(response);
  } catch (error) {
    console.error("Error uploading file:", error);
    alert("Error uploading file.");
  }
}
