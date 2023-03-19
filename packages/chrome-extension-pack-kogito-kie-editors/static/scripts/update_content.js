// Updates the GitHub editor with the content on the `kogito-content` `pre` tag.

document
  .querySelector(".file-editor-textarea + .CodeMirror")
  .CodeMirror.setValue(`${document.getElementById("kogito-content").textContent ?? ""}`);
