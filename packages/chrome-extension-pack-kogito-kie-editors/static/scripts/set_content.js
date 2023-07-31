// gets the initial content and sets in a pre tag
var kieTools_cmInstance = document.querySelector(".cm-content").cmView;
var kieTools_content = kieTools_cmInstance.view.state.doc.toString();
var kieTools_existingElement = document.getElementById("kie-tools__initial-content") ?? null;
if (kieTools_existingElement) {
  kieTools_existingElement.remove();
}
var kieTools_newElement = document.createElement("pre");
kieTools_newElement.id = "kie-tools__initial-content";
kieTools_newElement.style.display = "none";
kieTools_newElement.textContent = kieTools_content;
document.body.appendChild(kieTools_newElement);
