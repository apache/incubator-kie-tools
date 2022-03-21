/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

function newDiagram() {
  window.frames.editorFrame.contentWindow.gwtEditorBeans.get("DMNDiagramEditor").get().setContent("", "");
  resetOpenFile();
}

function resetOpenFile() {
  document.getElementById("open-file").value = "";
}

function openSample() {
  fetch("static/sample.dmn")
    .then((response) => {
      return response.text();
    })
    .then((content) => {
      window.frames.editorFrame.contentWindow.gwtEditorBeans
        .get("DMNDiagramEditor")
        .get()
        .setContent("sample.dmn", content);
      resetOpenFile();
    });
}

function loadDiagram(diagram) {
  window.frames.editorFrame.contentWindow.gwtEditorBeans.get("DMNDiagramEditor").get().setContent("", diagram);
}

function openFEELEditor() {
  window.frames.editorFrame.contentWindow.openFEELEditor();
}

function download() {
  window.frames.editorFrame.contentWindow.gwtEditorBeans
    .get("DMNDiagramEditor")
    .get()
    .getContent()
    .then(function (p) {
      const d = document.createElement("a");
      d.setAttribute("href", "data:text/xml;charset=utf-8," + encodeURIComponent(p));
      d.setAttribute("download", "diagram.dmn");
      d.style.display = "none";

      document.body.appendChild(d);
      d.click();
      document.body.removeChild(d);
    });
}

function undo() {
  window.frames.editorFrame.contentWindow.gwtEditorBeans.get("DMNDiagramEditor").get().undo();
}

function redo() {
  window.frames.editorFrame.contentWindow.gwtEditorBeans.get("DMNDiagramEditor").get().redo();
}

function search() {
  window.frames.editorFrame.contentWindow.gwtEditorBeans
    .get("DMNDiagramEditor")
    .get()
    .searchDomainObject(document.getElementById("input-search").value);
}

const openFile = (event) => {
  const input = event.target;
  const reader = new FileReader();
  reader.onload = function () {
    const diagram = reader.result;
    loadDiagram(diagram);
  };

  reader.readAsText(input.files[0]);
};
