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

describe("Dmn Editable - Expression.", () => {
  before("Visit page", () => {
    cy.visit("/dmn-editable");
    cy.loadEditors(["dmn-editable"]);
  });

  // https://issues.redhat.com/browse/KOGITO-6707
  it.skip("Test New Expression Editor Can Be Activated", () => {
    cy.editor("dmn-editable").find("[data-field='kie-palette']").should("be.visible");

    cy.editor("dmn-editable")
      .ouiaId("collapsed-docks-bar", "collapsed-docks-bar-W", { timeout: 10000 })
      .should("be.visible");

    cy.uploadFile("Traffic Violation.dmn", "dmn-editable");
    cy.viewFile("Traffic Violation.dmn", "dmn-editable");

    cy.editor("dmn-editable")
      .ouiaId("collapsed-docks-bar", "collapsed-docks-bar-W")
      .find("button")
      .first()
      .should("be.visible")
      .click(); // open DecisionNavigator

    cy.editor("dmn-editable")
      .ouiaId("expanded-docks-bar", "expanded-docks-bar-W")
      .should("be.visible")
      .within(($navigator) => {
        cy.get("[title='Should the driver be suspended?'] div span").contains("Context").click();
      });
    // turn on new editor
    cy.editor("dmn-editable").find("[data-field='beta-boxed-expression-toggle'] [data-field='try-it']").click();
    // check expressions content was displayed
    cy.editor("dmn-editable")
      .find("[data-ouia-component-id='expression-container']")
      .contains("Should the driver be suspended?")
      .should("be.visible");

    // decision table
    cy.editor("dmn-editable")
      .ouiaId("expanded-docks-bar", "expanded-docks-bar-W")
      .should("be.visible")
      .within(($navigator) => {
        cy.get("[title='Fine'] div span").contains("Decision Table").click();
      });
    // turn on new editor again
    cy.editor("dmn-editable").find("[data-field='beta-boxed-expression-toggle'] [data-field='try-it']").click();
    cy.editor("dmn-editable")
      .find("[data-ouia-component-id='expression-container']")
      .contains("Fine")
      .should("be.visible");
  });
});
