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
}
