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

declare namespace Cypress {
  interface Chainable {
    /**
     * Get Kogito editor iframe.
     */
    getEditor(): Chainable<JQuery<HTMLBodyElement>>;

    /**
     * Wait until Kogito editor is loaded.
     */
    loadEditor(): void;

    /**
     * Search elements by data-ouia component attributes.
     * @param id string
     * @param opts optional - config object
     */
    ouiaId(id: string, opts?: Record<string, any>): Chainable<Element>;
  }
}

Cypress.Commands.add("getEditor", () => {
  cy.frameLoaded("iframe#kogito-iframe");
  return cy.iframe("iframe#kogito-iframe");
});

Cypress.Commands.add("loadEditor", () => {
  cy.getEditor().within(() => {
    cy.get("[data-testid='loading-screen-div']", { timeout: 15000 }).should("be.visible");
    cy.get("[data-testid='loading-screen-div']", { timeout: 60000 }).should("not.exist");
  });
});

Cypress.Commands.add("ouiaId", { prevSubject: "optional" }, (subject, id: string, options = {}) => {
  const idSelector = `[data-ouia-component-id='${id}']`;

  if (subject) {
    cy.wrap(subject, options).find(idSelector, options);
  } else {
    cy.get(idSelector, options);
  }
});
