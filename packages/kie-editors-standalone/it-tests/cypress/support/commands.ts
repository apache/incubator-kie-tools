/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import "cypress-file-upload";
import "cypress-iframe";

Cypress.Commands.add("ouiaId", { prevSubject: "optional" }, (subject, type: string, id: string, options = {}) => {
  const typeSelector = type ? `[data-ouia-component-type='${type}']` : "";
  const idSelector = id ? `[data-ouia-component-id='${id}']` : "";

  if (subject) {
    cy.wrap(subject, options).find(typeSelector + idSelector, options);
  } else {
    cy.get(typeSelector + idSelector, options);
  }
});

Cypress.Commands.add("ouiaType", { prevSubject: "optional" }, (subject, type: string, options = {}) => {
  const typeSelector = type ? `[data-ouia-component-type='${type}']` : "";
  if (subject) {
    cy.wrap(subject, options).find(typeSelector, options);
  } else {
    cy.get(typeSelector, options);
  }
});

Cypress.Commands.add("loadEditors", (editorIds: string[], options?: Record<string, any>) => {
  const opts: Record<string, any> = { log: false, ...options };
  cy.get("div#root", opts)
    .should("exist")
    .within(opts, ($root) => {
      // cy.ouiaType("editor", opts).should("have.length", editorIds.length);
      for (const id in editorIds) {
        cy.ouiaId("editor", editorIds[id], opts).should("exist");
        cy.frameLoaded("div#" + editorIds[id] + " iframe", opts);
      }
      for (const id in editorIds) {
        cy.iframe("div#" + editorIds[id] + " iframe", opts)
          .find("[data-testid='loading-screen-div']", { timeout: 1000, ...opts })
          .should("be.visible");
      }
      for (const id in editorIds) {
        cy.iframe("div#" + editorIds[id] + " iframe", opts)
          .find(".kie-tools--keyboard-shortcuts-icon", { timeout: 120000, ...opts })
          .should("be.visible");
      }
    });

  // it takes a while for the editor to display everything correctly even after the loading screen disappears
  cy.wait(1000);

  Cypress.log({ name: "loadEditor", message: `Wait for editor '${editorIds}' to load.` });
});

Cypress.Commands.add("editor", (editorId: string, options?: Record<string, any>) => {
  cy.iframe("div#" + editorId + " iframe", { log: false });
  Cypress.log({ name: "editor", message: `Using editor ${editorId}` });
});

Cypress.Commands.add("uploadFile", (fileName: string, editorId: string) => {
  const noLogOpts = { log: false };
  cy.ouiaId("file-loader", editorId, noLogOpts).within(noLogOpts, ($loader) => {
    cy.ouiaType("file-upload-form", noLogOpts).within(noLogOpts, ($form) => {
      cy.get("input[type='file']", noLogOpts).attachFile(fileName);
      cy.get("button", noLogOpts).click();
    });
  }); // upload file from fixtures

  Cypress.log({ name: "upload file", message: `Uploading file ${fileName} for editor ${editorId}.` });
});

Cypress.Commands.add("viewFile", (fileName: string, editorId: string, options?: Record<string, any>) => {
  const opts = { ...{ log: false }, options };
  cy.ouiaId("file-loader", editorId, opts)
    .ouiaType("file-list", opts)
    .ouiaId("file-list-item", fileName, opts)
    .should("be.visible")
    .ouiaId("file-list-item-button", "view", opts)
    .should("be.visible")
    .click(); // choose file to view

  cy.loadEditors([editorId], options);

  Cypress.log({ name: "view file", message: `Viewing file ${fileName} in editor ${editorId}.` });
});
