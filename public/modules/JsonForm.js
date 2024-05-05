export class JsonForm {
  constructor(htmlElement) {
    this.pointer = htmlElement;
    this.inputs = htmlElement.querySelectorAll("input");
    this.init();
  }

  init() {
    this.inputs.forEach((input, index) => {
      input.addEventListener("keydown", (event) => {
        if (event.key === "Enter") {
          event.preventDefault();

          if (input.type === "checkbox") {
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
      formValues[input.name] = input.value;
    });
    return formValues;
  }
}
