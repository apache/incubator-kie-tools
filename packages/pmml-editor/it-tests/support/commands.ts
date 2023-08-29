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

Cypress.Commands.add("newButtonPMML", () => {
  return cy.get("[data-ouia-component-id='new-button']");
});

Cypress.Commands.add("uploadButtonPMML", () => {
  return cy.get("[data-ouia-component-id='upload-button']");
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
  return cy.get("[data-ouia-component-id='undo-button']");
});

Cypress.Commands.add("buttonRedo", () => {
  return cy.get("[data-ouia-component-id='redo-button']");
});

Cypress.Commands.add("buttonPMML", () => {
  return cy.get("[data-ouia-component-id='pmml-button']");
});

Cypress.Commands.add("buttonValidation", () => {
  return cy.get("[data-ouia-component-id='validate-button']");
});

Cypress.Commands.add("ouiaId", (valueStr) => {
  return cy.get(`[data-ouia-component-id='${valueStr}']`);
});

Cypress.Commands.add("ouiaType", (valueStr) => {
  return cy.get(`[data-ouia-component-type='${valueStr}']`);
});

Cypress.Commands.add("ouia", (id, type) => {
  return cy.get(`[data-ouia-component-id='${id}'][data-ouia-component-type='${type}']`);
});

Cypress.Commands.add("editorShouldContains", (fileName) => {
  cy.fixture(fileName).then(($fileContent) => {
    const text = $fileContent.toString().replaceAll("\n", "").replaceAll("\r", "");
    cy.ouiaType("source-code").should("to.have.text", text);
  });
});
