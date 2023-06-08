// Updates the GitHub editor with the content on the `kogito-content` `pre` tag.
document
  .querySelector(".CodeMirror")
  .CodeMirror.setValue(`${document.getElementById("kogito-content").textContent ?? ""}`);

// Enables the commit changes... button
document.querySelector(".CodeMirror-code").dispatchEvent(new Event("keypress", { bubbles: true }));

// Undo the special character which gets added due to the keypress event.
document.querySelector(".CodeMirror").CodeMirror.undo();
