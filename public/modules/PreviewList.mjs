export class PreviewList {
  constructor(htmlElement) {
    this.pointer = htmlElement;
  }
  render(items) {
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
