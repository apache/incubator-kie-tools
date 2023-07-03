// gets the initial content and sets in a pre tag
const kieTools_cmInstance = document.querySelector(".cm-content").cmView;
const kieTools_content = kieTools_cmInstance.view.state.doc.toString();
const kieTools_newElement = document.createElement("pre");
kieTools_newElement.id = "kie-tools__initial-content";
kieTools_newElement.style.display = "none";
kieTools_newElement.textContent = kieTools_content;
document.body.appendChild(kieTools_newElement);
