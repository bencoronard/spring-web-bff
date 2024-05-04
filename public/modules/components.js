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
    this.pointer.addEventListener('click', handle);
  }
}

export class PreviewList {
  constructor(htmlElement) {
    this.pointer = htmlElement;
  }
  update(items) {
    this.clear();
    items.forEach((item) => {
      const li = document.createElement('li');
      li.classList.add(`${this.pointer.id}-item`);
      li.textContent = item;
      this.pointer.appendChild(li);
    });
  }
  clear() {
    this.pointer.innerHTML = '';
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
  constructor(htmlElement) {
    this.pointer = htmlElement;
  }

  enable() {
    let dragEventCounter = 0;

    this.pointer.addEventListener('dragenter', (event) => {
      event.preventDefault();

      if (dragEventCounter === 0) {
        this.pointer.classList.add('highlight');
      }

      dragEventCounter += 1;
    });

    this.pointer.addEventListener('dragover', (event) => {
      event.preventDefault();

      if (dragEventCounter === 0) {
        dragEventCounter = 1;
      }
    });

    this.pointer.addEventListener('dragleave', (event) => {
      event.preventDefault();

      dragEventCounter -= 1;

      if (dragEventCounter <= 0) {
        dragEventCounter = 0;
        this.pointer.classList.remove('highlight');
      }
    });

    this.pointer.addEventListener('drop', (event) => {
      event.preventDefault();

      dragEventCounter = 0;
      this.pointer.classList.remove('highlight');
    });
    return this;
  }

  disable() {
    const clonedElement = this.pointer.cloneNode(true);
    this.pointer.parentNode.replaceChild(clonedElement, this.pointer);
  }

  addDropHandle(handle) {
    this.pointer.addEventListener('drop', handle);
  }
}

// export class FileForm {
//   constructor(htmlElement) {
//     this.pointer = htmlElement;
//     this.stage = 1;
//   }
//   init() {
//     return this;
//   }
//   addSubmitHandle(handle) {
//     this.pointer.addEventListener('submit', handle);
//   }

//   reset(handle) {
//     handle();
//   }

//   submit() {}

//   getStage() {
//     return this.stage;
//   }
// }
