// gets the initial content and sets in a pre tag
const cmInstance = document.querySelector(".cm-content").cmView;
const content = cmInstance.view.state.doc.toString();
const newElement = document.createElement("pre");
newElement.id = "kie-tools__initial-content";
newElement.style.display = "none";
newElement.textContent = content;
document.body.appendChild(newElement);
