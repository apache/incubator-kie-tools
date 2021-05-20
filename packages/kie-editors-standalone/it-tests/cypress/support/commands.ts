/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import "cypress-file-upload";
import "cypress-iframe";

Cypress.Commands.add("ouiaId", { prevSubject: "optional" }, (subject, type: string, id: string, options = {}) => {
  let typeSelector = type ? `[data-ouia-component-type='${type}']` : "";
  let idSelector = id ? `[data-ouia-component-id='${id}']` : "";

  if (subject) {
    cy.wrap(subject, options).find(typeSelector + idSelector, options);
  } else {
    cy.get(typeSelector + idSelector, options);
  }
});

Cypress.Commands.add("ouiaType", { prevSubject: "optional" }, (subject, type: string, options = {}) => {
  let typeSelector = type ? `[data-ouia-component-type='${type}']` : "";
  if (subject) {
    cy.wrap(subject, options).find(typeSelector, options);
  } else {
    cy.get(typeSelector, options);
  }
});

const loadEditorInternal = (options: Record<string, any>, editorIds: string[]) => {
  const opts: Record<string, any> = { log: false, ...options };
  cy.get("div#root", opts)
    .should("exist")
    .within(opts, ($root) => {
      cy.ouiaType("editor", opts).should("have.length", editorIds.length);
      for (let i in editorIds) {
        cy.ouiaId("editor", editorIds[i], opts).should("exist");
        cy.frameLoaded("div#" + editorIds[i] + " iframe", opts);
      }
      for (let i in editorIds) {
        cy.iframe("div#" + editorIds[i] + " iframe", opts)
          .find("[data-testid='loading-screen-div']", {
            timeout: 1000,
            ...opts,
          })
          .should("be.visible");
      }
      for (let i in editorIds) {
        cy.iframe("div#" + editorIds[i] + " iframe", opts)
          .find(".kogito-tooling--keyboard-shortcuts-icon", {
            timeout: 120000,
            ...opts,
          })
          .should("be.visible");
      }
    });

  Cypress.log({ name: "loadEditor", message: `Wait for editor ${editorIds} to load.` });
};

Cypress.Commands.add("loadEditor", (editorId: string | string[], options?: Record<string, any>) => {
  loadEditorInternal(options, Array.isArray(editorId) ? editorId : [editorId]);
});

Cypress.Commands.add("editor", (editorId: string, options?: Record<string, any>) => {
  cy.iframe("div#" + editorId + " iframe", { log: false });
  Cypress.log({ name: "editor", message: `Using editor ${editorId}` });
});

Cypress.Commands.add("uploadFile", (fileName: string, componentId: string) => {
  const noLogOpts = { log: false };
  cy.ouiaId("file-loader", componentId, noLogOpts).within(noLogOpts, ($loader) => {
    cy.ouiaType("file-upload-form", noLogOpts).within(noLogOpts, ($form) => {
      cy.get("input[type='file']", noLogOpts).attachFile(fileName);
      cy.get("button", noLogOpts).click();
    });
  }); // upload file from fixtures

  Cypress.log({ name: "upload file", message: `Uploading file ${fileName} for editor ${componentId}.` });
});

Cypress.Commands.add("viewFile", (fileName: string, componentId: string, options?: Record<string, any>) => {
  const opts = { ...{ log: false }, options };
  cy.ouiaId("file-loader", componentId, opts)
    .ouiaType("file-list", opts)
    .ouiaId("file-list-item", fileName, opts)
    .should("be.visible")
    .ouiaId("file-list-item-button", "view", opts)
    .should("be.visible")
    .click(); // choose file to view
  Cypress.log({ name: "view file", message: `Viewing file ${fileName} in editor ${componentId}.` });
});
