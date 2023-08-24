/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
     * Move to position in textarea.
     * @param row destination row
     * @param column destination column
     */
    moveToPosition(row: number, column: number): Chainable<JQuery<HTMLBodyElement>>;

    /**
     * Search elements by data-ouia component attributes.
     * @param locator component type and component id according to OUIA specification
     * @param opts optional - config object
     */
    ouia<S = any>(locator: { ouiaType?: string; ouiaId?: string }, opts?: Record<string, any>): Chainable<S>;

    /**
     * Go to a link in the sidebar menu
     * @param locator component id according to OUIA specification
     */
    goToSidebarLink(locator: { ouiaId: string }): void;
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

Cypress.Commands.add("moveToPosition", { prevSubject: true }, (subject, row, column) => {
  // create path to destination row and column
  var path = "";
  for (var r = 0; r < row; r++) {
    path += "{downArrow}";
  }
  for (var c = 0; c < column; c++) {
    path += "{rightArrow}";
  }

  // move to the beginning of the textarea and move to the destination
  return cy.wrap(subject).type("{ctrl}{home}").type(path);
});

Cypress.Commands.add("ouia", { prevSubject: "optional" }, (subject, locator, options = {}) => {
  let selector = "";

  if (locator.ouiaId !== undefined && locator.ouiaId !== "") {
    selector = `[data-ouia-component-id=\"${locator.ouiaId}\"]`;
  }

  if (locator.ouiaType !== undefined && locator.ouiaType !== "") {
    selector = `[data-ouia-component-type=\"${locator.ouiaType}\"]` + selector;
  }

  if (subject) {
    cy.wrap(subject, options).find(selector, options);
  } else {
    cy.get(selector, options);
  }
});

Cypress.Commands.add("goToSidebarLink", { prevSubject: false }, (locator) => {
  cy.get("#page-sidebar").then((pageSidebar) => {
    if (!pageSidebar.is(":visible")) {
      cy.get("#nav-toggle").click();
    }
    cy.ouia({ ouiaId: locator.ouiaId }).click();
    cy.get("#nav-toggle").click();
  });
});
