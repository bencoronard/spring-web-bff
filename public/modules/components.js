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

export class PreviewListInteractive extends PreviewList {
  constructor(htmlElement, statePointer) {
    super(htmlElement);
    this.statePointer = statePointer;
  }

  update(items, options) {
    this.clear();
    items.forEach((item) => {
      const li = document.createElement('li');
      li.classList.add(`${this.pointer.id}-item`);
      li.textContent = item;
      this.pointer.appendChild(li);
      if (options.bar) {
        this.addProgressBar(li);
      }
      if (options.text) {
        this.addStatusText(li);
      }
      if (options.button) {
        this.addButton(li);
      }
    });
  }

  addButton(element) {
    const deleteButton = document.createElement('button');
    deleteButton.type = 'button';
    deleteButton.classList.add(`${this.pointer.id}-item-delBtn`);
    deleteButton.textContent = 'x';
    deleteButton.addEventListener('click', () => {
      const index = Array.from(this.pointer.children).indexOf(element);
      this.pointer.removeChild(element);
      this.statePointer.splice(index, 1);
    });
    element.appendChild(deleteButton);
  }

  addProgressBar(element) {
    const progressBar = document.createElement('progress');
    progressBar.value = 0;
    progressBar.max = 100;
    progressBar.classList.add(`${this.pointer.id}-item-progBar`);
    progressBar.classList.add(`hidden`);
    element.appendChild(progressBar);
  }

  addStatusText(element) {
    const statusText = document.createElement('span');
    statusText.classList.add(`${this.pointer.id}-item-statTxt`);
    statusText.classList.add(`hidden`);
    element.appendChild(statusText);
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
    this.pointer.classList.remove('lowlight');

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

    this.pointer.addEventListener('drop', this.dropHandle);
  }

  disable() {
    this.pointer.removeEventListener('drop', this.dropHandle);
    this.pointer.classList.add('lowlight');
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
    this.pointer.children[index].classList.add('colored');
  }
  removeColor(index) {
    this.pointer.children[index].classList.remove('colored');
  }

  setStage(stage) {
    this.reset();
    const children = this.pointer.children;
    const limit = Math.min(stage, this.numChild - 1);
    for (let i = 1; i <= limit; i++) {
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

export class HidableElement {
  constructor(htmlElement) {
    this.pointer = htmlElement;
  }

  hide() {
    if (!this.pointer.classList.contains('hidden')) {
      this.pointer.classList.add('hidden');
    }
  }

  show() {
    if (this.pointer.classList.contains('hidden')) {
      this.pointer.classList.remove('hidden');
    }
  }
}

export class JsonForm {
  constructor(htmlElement) {
    this.pointer = htmlElement;
    this.inputs = htmlElement.querySelectorAll('input');
    this.init();
  }

  init() {
    this.inputs.forEach((input, index) => {
      input.addEventListener('keydown', (event) => {
        if (event.key === 'Enter') {
          event.preventDefault();

          if (input.type === 'checkbox') {
            input.checked = !input.checked;
          }

          const nextIndex = index + 1;
          if (nextIndex < this.inputs.length) {
            this.inputs[nextIndex].focus();
          } else {
            const submitButton = this.pointer.querySelector(
              'button[type="submit"]'
            );
            submitButton.focus();
          }
        }
      });
    });
  }

  getValues() {
    const formValues = {};
    this.inputs.forEach((input) => {
      formValues[input.name] =
        input.type === 'checkbox' ? input.checked : input.value;
    });
    return formValues;
  }
}
