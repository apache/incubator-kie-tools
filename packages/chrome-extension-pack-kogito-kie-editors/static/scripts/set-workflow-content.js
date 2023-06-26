// gets the initial workflow json/yaml and sets in a pre tag
const cmInstance = document.querySelector(".cm-content").cmView;
const workflowContent = cmInstance.view.state.doc.toString();
const newElement = document.createElement("pre");
newElement.id = "workflow-content";
newElement.style.display = "none";
newElement.textContent = workflowContent;
document.body.appendChild(newElement);
