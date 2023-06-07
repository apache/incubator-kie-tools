// enables the commit changes... button
document.querySelector(".CodeMirror-code").dispatchEvent(new Event("keypress"));

// Updates the GitHub editor with the content on the `kogito-content` `pre` tag.
document
  .querySelector(".CodeMirror")
  .CodeMirror.setValue(`${document.getElementById("kogito-content").textContent.trim() ?? ""}`);
