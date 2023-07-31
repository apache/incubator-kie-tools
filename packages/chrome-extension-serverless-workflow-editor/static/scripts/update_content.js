// Updates the GitHub editor with the content on the `kogito-content` `pre` tag.
var kieTools_cm = document.querySelector(".cm-content").cmView.view;
kieTools_cm.dispatch({
  changes: {
    from: 0,
    to: kieTools_cm.state.doc.length,
    insert: `${document.getElementById("kogito-content").textContent ?? ""}`,
  },
});
