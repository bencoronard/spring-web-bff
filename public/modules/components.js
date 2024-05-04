export class DynamicButton {
  constructor(htmlElement) {
    this.pointer = htmlElement;
  }
  enable() {
    this.pointer.disabled = false;
  }
  disable() {
    this.pointer.disabled = true;
  }
  addClickHandle(handle) {
    this.pointer.addEventListener("click", handle);
  }
}

export class PreviewList {
  constructor(htmlElement) {
    this.pointer = htmlElement;
  }
  update(items) {
    this.clear();
    items.forEach((item) => {
      const li = document.createElement("li");
      li.classList.add(`${this.pointer.id}-item`);
      li.textContent = item;
      this.pointer.appendChild(li);
    });
  }
  clear() {
    this.pointer.innerHTML = "";
  }
}

export class PreviewListInteractive extends PreviewList {
  constructor(htmlElement, statePointer) {
    super(htmlElement);
    this.statePointer = statePointer;
  }

  update(items, option) {
    this.clear();
    items.forEach((item) => {
      const li = document.createElement("li");
      li.classList.add(`${this.pointer.id}-item`);
      li.textContent = item;
      this.pointer.appendChild(li);
      if (option) {
        this.addButton(li);
      }
    });
  }

  addButton(element) {
    const deleteButton = document.createElement("button");
    deleteButton.classList.add(`${this.pointer.id}-item-delBtn`);
    deleteButton.textContent = "x";
    deleteButton.addEventListener("click", () => {
      const index = Array.from(this.pointer.children).indexOf(element);
      this.pointer.removeChild(element);
      this.statePointer.splice(index, 1);
    });
    element.appendChild(deleteButton);
  }
}

export class ProgressBar {
  constructor(htmlElement) {
    this.pointer = htmlElement;
  }
  update(value) {
    this.pointer.value = Math.round(value * 100);
  }
}

export class StatusText {
  constructor(htmlElement) {
    this.pointer = htmlElement;
  }
  update(text) {
    this.pointer.textContent = text;
  }
}

export class DropZone {
  constructor(htmlElement, dropHandle) {
    this.pointer = htmlElement;
    this.dropHandle = dropHandle;
  }

  enable() {
    this.pointer.classList.remove("lowlight");

    let dragEventCounter = 0;

    this.pointer.addEventListener("dragenter", (event) => {
      event.preventDefault();

      if (dragEventCounter === 0) {
        this.pointer.classList.add("highlight");
      }

      dragEventCounter += 1;
    });

    this.pointer.addEventListener("dragover", (event) => {
      event.preventDefault();

      if (dragEventCounter === 0) {
        dragEventCounter = 1;
      }
    });

    this.pointer.addEventListener("dragleave", (event) => {
      event.preventDefault();

      dragEventCounter -= 1;

      if (dragEventCounter <= 0) {
        dragEventCounter = 0;
        this.pointer.classList.remove("highlight");
      }
    });

    this.pointer.addEventListener("drop", (event) => {
      event.preventDefault();

      dragEventCounter = 0;
      this.pointer.classList.remove("highlight");
    });

    this.pointer.addEventListener("drop", this.dropHandle);
  }

  disable() {
    this.pointer.removeEventListener("drop", this.dropHandle);
    this.pointer.classList.add("lowlight");
  }
}

export class ChromaticList {
  constructor(htmlElement) {
    this.pointer = htmlElement;
    this.numChild = htmlElement.childElementCount;
    this.currentStage = 0;
    this.addColor(this.currentStage);
  }

  addColor(index) {
    this.pointer.children[index].classList.add("colored");
  }
  removeColor(index) {
    this.pointer.children[index].classList.remove("colored");
  }

  setStage(stage) {
    this.reset();
    const children = this.pointer.children;
    for (let i = 1; i <= stage; i++) {
      this.addColor(i);
    }
  }

  reset() {
    const children = this.pointer.children;
    for (let i = 1; i < this.numChild; i++) {
      this.removeColor(i);
    }
  }
}
