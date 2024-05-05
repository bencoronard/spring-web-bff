export class JsonForm {
  constructor(htmlElement) {
    this.pointer = htmlElement;
    this.inputArray = Array.from(htmlElement.querySelectorAll("input"));
  }

  getValues() {
    const formValues = {};
    this.inputArray.forEach((input) => {
      formValues[input.name] = input.value;
    });
    console.log(formValues);
  }
}
