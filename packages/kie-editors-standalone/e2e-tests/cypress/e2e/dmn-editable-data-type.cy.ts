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

describe("Dmn Editable Data Type.", () => {
  before("Visit page", () => {
    cy.visit("/dmn-editable");
    cy.loadEditors(["dmn-editable"]);
  });

  it("Test Add New Data Type And Check Dirty Indicator", () => {
    cy.editor("dmn-editable").find("[data-field='kie-palette']").should("be.visible");

    cy.editor("dmn-editable").ouiaId("editor-nav-tab", "Data Types", { timeout: 10000 }).should("be.visible").click();

    cy.editor("dmn-editable").ouiaId("add-data-type-button", "first").should("be.visible").click();

    cy.editor("dmn-editable")
      .ouiaId("dmn-data-type-item", "Insert a name")
      .should("be.visible")
      .find("[data-type-field='save-button']")
      .click();

    cy.ouiaType("content-dirty").should("be.visible");
  });
});
