/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

export function getGitHubEditor() {
  const realEditor = document.querySelector(".file-editor-textarea + .CodeMirror") as any;
  if (!realEditor) {
    throw new Error("GitHub editor was not found. GitHub must've change its DOM structure.");
  }
  return realEditor;
}

export function findContainers() {
  const githubEditorElement = document.querySelector(".file");
  githubEditorElement!
    .parentElement!.insertBefore(document.createElement("div"), githubEditorElement)
    .setAttribute("id", "kogito-iframe-container");

  return {
    iframe: document.getElementById("kogito-iframe-container")!,
    fullScreenButton: document.querySelector(".breadcrumb.d-flex.flex-items-center")!
  };
}
