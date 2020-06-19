/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

/// <reference path="../../support/index.d.ts" />

context('Check sample models successfully created', () => {

  beforeEach(() => {
    // visit home page of the Online Editor
    cy.visit('/')
  })

  it('Try BPMN sample', () => {
    cy.trySampleModel('bpmn')

    cy.getDiagramEditorBody().within(($diagramEditor) => {
      cy.get('[data-title="Explore Diagram"]').click();
      cy.get('[data-field="explorerPanelBody"]')
        .contains("Process travelers")
    })
  })

  it('Try DMN sample', () => {
    cy.trySampleModel('dmn')

    cy.getDiagramEditorBody().within(($diagramEditor) => {
      cy.get('.fa-chevron-right').click();
      cy.get('li[data-i18n-prefix="DecisionNavigatorTreeView."]')
        .should("have.attr", "title", "loan_pre_qualification");
    })
  })
})