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
     * Confirm 'Automatic Layout' dialogue.
     * Please notice, such dialogue appears only if <DMNDI/> tag is missing in the diagram.
     */
    confirmAutomaticLayoutDialogue(): void;

    /**
     * Search elements by data-ouia component attributes.
     * @param locator component type and component id according to OUIA specification
     * @param opts optional - config object
     */
    ouia(locator: { ouiaType?: string; ouiaId?: string }, opts?: Record<string, any>): Chainable<Element>;
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

Cypress.Commands.add("confirmAutomaticLayoutDialogue", () => {
  cy.getEditor().within(() => {
    cy.get("[data-testid='loading-screen-div']", { timeout: 15000 }).should("exist");
    cy.get(".spinner", { timeout: 15000 }).should("be.visible");
    cy.get(".modal-title").contains("Automatic Layout").should("be.visible");
    cy.get("[data-field='yes-button']").click();
    cy.get(".spinner", { timeout: 15000 }).should("not.exist");
    cy.get("[data-testid='loading-screen-div']", { timeout: 60000 }).should("not.exist");
  });
});

Cypress.Commands.add("ouia", { prevSubject: "optional" }, (subject, locator, options = {}) => {
  let selector = "";

  if (locator.ouiaId !== undefined && locator.ouiaId !== "") {
    selector = `[data-ouia-component-id='${locator.ouiaId}']`;
  }

  if (locator.ouiaType !== undefined && locator.ouiaType !== "") {
    selector = `[data-ouia-component-type='${locator.ouiaType}']` + selector;
  }

  if (subject) {
    cy.wrap(subject, options).find(selector, options);
  } else {
    cy.get(selector, options);
  }
});
