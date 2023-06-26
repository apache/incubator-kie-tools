// Updates the GitHub editor with the content on the `kogito-content` `pre` tag.
var cm = document.querySelector(".cm-content").cmView.view;
cm.dispatch({
  changes: {
    from: 0,
    to: cm.state.doc.length,
    insert: `${document.getElementById("kogito-content").textContent ?? ""}`,
  },
});
