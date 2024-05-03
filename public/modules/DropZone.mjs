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
