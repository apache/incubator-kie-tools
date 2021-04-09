/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
         * Returns DOM Element for button which open new file.
         */
         newButtonPMML(): Chainable<JQuery<HTMLBodyElement>>;

         /**
          * Return DOM Element for button which upload new file.
          */
         uploadButtonPMML(): Chainable<JQuery<HTMLBodyElement>>;

         /**
          * Return DOM Element for button which open Data Dictionary Editor.
          */
         buttonDataDictionary(): Chainable<JQuery<HTMLBodyElement>>;

         /**
          * Return DOM Element for button which open Mining Schema Editor.
          */

         buttonMiningSchema(): Chainable<JQuery<HTMLBodyElement>>;

         /**
          * Return DOM Element for button which open Outputs Editor.
          */

         buttonOutputs(): Chainable<JQuery<HTMLBodyElement>>;

         /**
          * Return DOM Element for button which invoke Undo operation.
          */
         buttonUndo(): Chainable<JQuery<HTMLBodyElement>>;

         /**
          * Return DOM Element for button which invoke Redo operation.
          */

         buttonRedo(): Chainable<JQuery<HTMLBodyElement>>;

         /**
          * Return DOM Element for button which open PMML Source Editor.
          */
         buttonPMML(): Chainable<JQuery<HTMLBodyElement>>;

         /**
          * Return DOM Element for button which validate PMML.
          */
         buttonValidation(): Chainable<JQuery<HTMLBodyElement>>;
    }
}

Cypress.Commands.add("newButtonPMML", () => {
    return cy.get("[data-ouia-component-id='OUIA-Generated-Button-link-1']");
});

Cypress.Commands.add("uploadButtonPMML", () => {
    return cy.get("label.pf-c-button");
});

Cypress.Commands.add("buttonDataDictionary", () => {
    return cy.get("[data-title='DataDictionary']");
});

Cypress.Commands.add("buttonMiningSchema", () => {
    return cy.get("[data-title='MiningSchema']");
});

Cypress.Commands.add("buttonOutputs", () => {
    return cy.get("[data-title='Outputs']");
});

Cypress.Commands.add("buttonUndo", () => {
    return cy.get("[data-ouia-component-id='OUIA-Generated-Button-primary-1']");
});

Cypress.Commands.add("buttonRedo", () => {
    return cy.get("[data-ouia-component-id='OUIA-Generated-Button-secondary-1']");
});

Cypress.Commands.add("buttonPMML", () => {
    return cy.get("[data-ouia-component-id='OUIA-Generated-Button-secondary-2']");
});

Cypress.Commands.add("buttonValidation", () => {
    return cy.get("[data-ouia-component-id='OUIA-Generated-Button-secondary-3']");
});
