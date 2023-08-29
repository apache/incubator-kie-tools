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

import { env } from "../../env";
const buildEnv = env;

/**
 * This test suite work with attributes in Scorecard element
 * which are defined by PMML standard (see http://dmg.org/pmml/v4-1/Scorecard.html).
 *
 * Each attributes is set and unset.
 * Test asserts that the value of the attribute is saved (PMML code or model information).
 * Tests are focused on unset the value to be sure that all values are available and to prevent reggression:
 *    - PR #565
 */
describe("Scorecard Model Test", () => {
  beforeEach(() => {
    cy.visit(`http://localhost:${buildEnv.pmmlEditor.dev.port}/`);
    cy.newButtonPMML().click();
  });

  it("Set model name", () => {
    cy.ouiaId("model-name").click();
    cy.ouiaId("set-model-name").clear().type("Unique Scorecard Name");

    cy.ouiaType("filler").first().click();

    cy.ouiaId("model-name").should("be.visible").should("contain", "Unique Scorecard Name");
    cy.buttonPMML()
      .click()
      .ouiaType("source-code")
      .should("contain", '<Scorecard modelName="Unique Scorecard Name"')
      .ouiaId("pmml-modal-confirm")
      .click();
  });

  it("Cancel editing model name", () => {
    cy.ouiaId("model-name").click();
    cy.ouiaId("set-model-name").clear().type("Unique Scorecard Name");

    cy.ouiaType("filler").first().click();

    cy.ouiaId("model-name").click();
    cy.ouiaId("set-model-name").clear().type("Something{esc}");

    cy.ouiaId("model-name").should("be.visible").should("contain", "Unique Scorecard Name");
    cy.buttonPMML()
      .click()
      .ouiaType("source-code")
      .should("contain", '<Scorecard modelName="Unique Scorecard Name"')
      .ouiaId("pmml-modal-confirm")
      .click();
  });

  it("Attribute 'Is scorable' - set and unset", () => {
    cy.ouiaType("model-setup-overview").click();
    cy.ouiaType("edit-model-setup").should("be.visible");
    cy.ouiaId("is-scorable").find("input").should("be.checked");
    cy.ouiaId("is-scorable").click();
    cy.ouiaId("is-scorable").find("input").should("not.to.be.checked");

    cy.ouiaType("filler").first().click();

    cy.ouiaType("model-setup-overview")
      .find("[data-ouia-component-type=model-property]")
      .should(($label) => {
        expect($label).to.have.length(5);
        expect($label[0]).to.have.text("Is Scorable:\u00A0No");
        expect($label[1]).to.have.text("Function:\u00A0regression");
        expect($label[2]).to.have.text("Use Reason Codes:\u00A0Yes");
        expect($label[3]).to.have.text("Reason Code Algorithm:\u00A0pointsBelow");
        expect($label[4]).to.have.text("Baseline Method:\u00A0other");
      });

    cy.ouiaType("model-setup-overview")
      .find("[data-ouia-component-type=invalid-label]")
      .should(($label) => {
        expect($label).to.have.length(1);
        expect($label[0]).to.have.text("Baseline Score:\u00A0Missing");
      });

    cy.ouiaType("model-setup-overview").should("be.visible").click();
    cy.ouiaType("edit-model-setup").should("be.visible");
    cy.ouiaId("is-scorable").find("input").should("not.to.be.checked");
    cy.ouiaId("is-scorable").click();
    cy.ouiaId("is-scorable").find("input").should("be.checked");

    cy.ouiaType("filler").first().click();

    cy.ouiaType("model-setup-overview")
      .find("[data-ouia-component-type=model-property]")
      .should(($label) => {
        expect($label).to.have.length(5);
        expect($label[0]).to.have.text("Is Scorable:\u00A0Yes");
        expect($label[1]).to.have.text("Function:\u00A0regression");
        expect($label[2]).to.have.text("Use Reason Codes:\u00A0Yes");
        expect($label[3]).to.have.text("Reason Code Algorithm:\u00A0pointsBelow");
        expect($label[4]).to.have.text("Baseline Method:\u00A0other");
      });

    cy.ouiaType("model-setup-overview")
      .find("[data-ouia-component-type=invalid-label]")
      .should(($label) => {
        expect($label).to.have.length(1);
        expect($label[0]).to.have.text("Baseline Score:\u00A0Missing");
      });
  });
  it("Attribute 'Use Reason Codes' - set and unset", () => {
    cy.ouiaType("model-setup-overview").click();
    cy.ouiaType("edit-model-setup").should("be.visible");
    cy.ouiaId("use-reason-codes").find("input").should("be.checked");
    cy.ouiaId("use-reason-codes").click();
    cy.ouiaId("use-reason-codes").find("input").should("not.to.be.checked");

    cy.ouiaId("core-reasonCodeAlgorithm").find("button").should("be.disabled");
    cy.ouiaId("core-baselineMethod").find("button").should("be.disabled");

    cy.ouiaType("filler").first().click();

    cy.ouiaType("model-setup-overview")
      .find("[data-ouia-component-type=model-property]")
      .should(($label) => {
        expect($label).to.have.length(5);
        expect($label[0]).to.have.text("Is Scorable:\u00A0Yes");
        expect($label[1]).to.have.text("Function:\u00A0regression");
        expect($label[2]).to.have.text("Use Reason Codes:\u00A0No");
        expect($label[3]).to.have.text("Reason Code Algorithm:\u00A0pointsBelow");
        expect($label[4]).to.have.text("Baseline Method:\u00A0other");
      });

    cy.ouiaType("model-setup-overview").find("[data-ouia-component-type=invalid-label]").should("have.have.length", 0);

    cy.ouiaType("model-setup-overview").should("be.visible").click();
    cy.ouiaType("edit-model-setup").should("be.visible");
    cy.ouiaId("use-reason-codes").find("input").should("not.to.be.checked");
    cy.ouiaId("use-reason-codes").click();
    cy.ouiaId("use-reason-codes").find("input").should("be.checked");

    cy.ouiaId("core-reasonCodeAlgorithm").find("button").should("be.enabled");
    cy.ouiaId("core-baselineMethod").find("button").should("be.enabled");

    cy.ouiaType("filler").first().click();

    cy.ouiaType("model-setup-overview")
      .find("[data-ouia-component-type=model-property]")
      .should(($label) => {
        expect($label).to.have.length(5);
        expect($label[0]).to.have.text("Is Scorable:\u00A0Yes");
        expect($label[1]).to.have.text("Function:\u00A0regression");
        expect($label[2]).to.have.text("Use Reason Codes:\u00A0Yes");
        expect($label[3]).to.have.text("Reason Code Algorithm:\u00A0pointsBelow");
        expect($label[4]).to.have.text("Baseline Method:\u00A0other");
      });

    cy.ouiaType("model-setup-overview")
      .find("[data-ouia-component-type=invalid-label]")
      .should(($label) => {
        expect($label).to.have.length(1);
        expect($label[0]).to.have.text("Baseline Score:\u00A0Missing");
      });
  });

  it("Attribute 'Function' - contains 'regression' and is disabled", () => {
    /*
     * PMML definition marks the 'Function' attribute as required.
     * This editor should always show its value.
     * Allowed value of this attribute for scorecards is 'regression'.
     * See http://dmg.org/pmml/v4-4/Scorecard.html
     */

    cy.ouiaType("model-setup-overview").find(
      "[data-ouia-component-type=model-property]:contains('Function:\u00A0regression')"
    );

    cy.ouiaType("model-setup-overview").click();
    cy.ouiaType("edit-model-setup").should("be.visible");
    cy.ouiaId("core-functionName").find("button").should("be.disabled").should("contain", "regression");

    cy.ouiaType("filler").first().click();

    cy.ouiaType("model-setup-overview")
      .find("[data-ouia-component-type=model-property]")
      .should(($label) => {
        expect($label).to.have.length(5);
        expect($label[0]).to.have.text("Is Scorable:\u00A0Yes");
        expect($label[1]).to.have.text("Function:\u00A0regression");
        expect($label[2]).to.have.text("Use Reason Codes:\u00A0Yes");
        expect($label[3]).to.have.text("Reason Code Algorithm:\u00A0pointsBelow");
        expect($label[4]).to.have.text("Baseline Method:\u00A0other");
      });

    cy.ouiaType("model-setup-overview")
      .find("[data-ouia-component-type=invalid-label]")
      .should(($label) => {
        expect($label).to.have.length(1);
        expect($label[0]).to.have.text("Baseline Score:\u00A0Missing");
      });
  });

  it("Attribute 'Initial Score' - set and unset", () => {
    cy.ouiaType("model-setup-overview").click();
    cy.ouiaType("edit-model-setup").should("be.visible");
    cy.ouiaId("initial-score").type("-15.5");

    cy.ouiaType("filler").first().click();

    cy.ouiaType("model-setup-overview")
      .find("[data-ouia-component-type=model-property]")
      .should(($label) => {
        expect($label).to.have.length(6);
        expect($label[0]).to.have.text("Is Scorable:\u00A0Yes");
        expect($label[1]).to.have.text("Function:\u00A0regression");
        expect($label[2]).to.have.text("Initial Score:\u00A0-15.5");
        expect($label[3]).to.have.text("Use Reason Codes:\u00A0Yes");
        expect($label[4]).to.have.text("Reason Code Algorithm:\u00A0pointsBelow");
        expect($label[5]).to.have.text("Baseline Method:\u00A0other");
      });

    cy.ouiaType("model-setup-overview")
      .find("[data-ouia-component-type=invalid-label]")
      .should(($label) => {
        expect($label).to.have.length(1);
        expect($label[0]).to.have.text("Baseline Score:\u00A0Missing");
      });

    cy.ouiaType("model-setup-overview").click();
    cy.ouiaType("edit-model-setup").should("be.visible");
    cy.ouiaId("initial-score").clear();

    cy.ouiaType("filler").first().click();

    cy.ouiaType("model-setup-overview")
      .find("[data-ouia-component-type=model-property]")
      .should(($label) => {
        expect($label).to.have.length(5);
        expect($label[0]).to.have.text("Is Scorable:\u00A0Yes");
        expect($label[1]).to.have.text("Function:\u00A0regression");
        expect($label[2]).to.have.text("Use Reason Codes:\u00A0Yes");
        expect($label[3]).to.have.text("Reason Code Algorithm:\u00A0pointsBelow");
        expect($label[4]).to.have.text("Baseline Method:\u00A0other");
      });

    cy.ouiaType("model-setup-overview")
      .find("[data-ouia-component-type=invalid-label]")
      .should(($label) => {
        expect($label).to.have.length(1);
        expect($label[0]).to.have.text("Baseline Score:\u00A0Missing");
      });
  });

  it("Attribute 'Baseline score' - set and unset", () => {
    cy.ouiaType("model-setup-overview").click();
    cy.ouiaType("edit-model-setup").should("be.visible");
    cy.ouiaId("baseline-score").type("-15.5");

    cy.ouiaType("filler").first().click();

    cy.ouiaType("model-setup-overview")
      .find("[data-ouia-component-type=model-property]")
      .should(($label) => {
        expect($label).to.have.length(6);
        expect($label[0]).to.have.text("Is Scorable:\u00A0Yes");
        expect($label[1]).to.have.text("Function:\u00A0regression");
        expect($label[2]).to.have.text("Use Reason Codes:\u00A0Yes");
        expect($label[3]).to.have.text("Reason Code Algorithm:\u00A0pointsBelow");
        expect($label[4]).to.have.text("Baseline Score:\u00A0-15.5");
        expect($label[5]).to.have.text("Baseline Method:\u00A0other");
      });

    cy.ouiaType("model-setup-overview").find("[data-ouia-component-type=invalid-label]").should("have.length", 0);

    cy.ouiaType("model-setup-overview").click();
    cy.ouiaType("edit-model-setup").should("be.visible");
    cy.ouiaId("baseline-score").clear();

    cy.ouiaType("filler").first().click();

    cy.ouiaType("model-setup-overview")
      .find("[data-ouia-component-type=model-property]")
      .should(($label) => {
        expect($label).to.have.length(5);
        expect($label[0]).to.have.text("Is Scorable:\u00A0Yes");
        expect($label[1]).to.have.text("Function:\u00A0regression");
        expect($label[2]).to.have.text("Use Reason Codes:\u00A0Yes");
        expect($label[3]).to.have.text("Reason Code Algorithm:\u00A0pointsBelow");
        expect($label[4]).to.have.text("Baseline Method:\u00A0other");
      });

    cy.ouiaType("model-setup-overview")
      .find("[data-ouia-component-type=invalid-label]")
      .should(($label) => {
        expect($label).to.have.length(1);
        expect($label[0]).to.have.text("Baseline Score:\u00A0Missing");
      });
  });

  it("Attribute 'Algorithm' - set and unset", () => {
    cy.ouiaType("model-setup-overview").click();
    cy.ouiaType("edit-model-setup").should("be.visible");
    cy.ouiaId("algorithm").type("Test something");

    cy.ouiaType("filler").first().click();

    cy.ouiaType("model-setup-overview")
      .find("[data-ouia-component-type=model-property]")
      .should(($label) => {
        expect($label).to.have.length(6);
        expect($label[0]).to.have.text("Is Scorable:\u00A0Yes");
        expect($label[1]).to.have.text("Function:\u00A0regression");
        expect($label[2]).to.have.text("Algorithm:\u00A0Test something");
        expect($label[3]).to.have.text("Use Reason Codes:\u00A0Yes");
        expect($label[4]).to.have.text("Reason Code Algorithm:\u00A0pointsBelow");
        expect($label[5]).to.have.text("Baseline Method:\u00A0other");
      });

    cy.ouiaType("model-setup-overview")
      .find("[data-ouia-component-type=invalid-label]")
      .should(($label) => {
        expect($label).to.have.length(1);
        expect($label[0]).to.have.text("Baseline Score:\u00A0Missing");
      });

    cy.ouiaType("model-setup-overview").click();
    cy.ouiaType("edit-model-setup").should("be.visible");
    cy.ouiaId("algorithm").clear();

    cy.ouiaType("filler").first().click();

    cy.ouiaType("model-setup-overview")
      .find("[data-ouia-component-type=model-property]")
      .should(($label) => {
        expect($label).to.have.length(5);
        expect($label[0]).to.have.text("Is Scorable:\u00A0Yes");
        expect($label[1]).to.have.text("Function:\u00A0regression");
        expect($label[2]).to.have.text("Use Reason Codes:\u00A0Yes");
        expect($label[3]).to.have.text("Reason Code Algorithm:\u00A0pointsBelow");
        expect($label[4]).to.have.text("Baseline Method:\u00A0other");
      });

    cy.ouiaType("model-setup-overview")
      .find("[data-ouia-component-type=invalid-label]")
      .should(($label) => {
        expect($label).to.have.length(1);
        expect($label[0]).to.have.text("Baseline Score:\u00A0Missing");
      });
  });

  it("Attribute 'reasonCodeAlgorithm' - check options", () => {
    cy.ouiaType("model-setup-overview").click();
    cy.ouiaType("edit-model-setup").should("be.visible");
    cy.ouiaId("core-reasonCodeAlgorithm").click();
    cy.ouiaType("select-option").should(($opt) => {
      /*
       * These values are defined by PMML
       * See http://dmg.org/pmml/v4-4/Scorecard.html
       */
      expect($opt).to.have.length(2);
      expect($opt[0]).to.have.text("pointsAbove");
      expect($opt[1]).to.have.text("pointsBelow");
    });
    cy.ouiaType("select-option").contains("pointsAbove").click();

    cy.ouiaType("filler").first().click();

    cy.ouiaType("model-setup-overview")
      .find("[data-ouia-component-type=model-property]")
      .should(($label) => {
        expect($label).to.have.length(5);
        expect($label[0]).to.have.text("Is Scorable:\u00A0Yes");
        expect($label[1]).to.have.text("Function:\u00A0regression");
        expect($label[2]).to.have.text("Use Reason Codes:\u00A0Yes");
        expect($label[3]).to.have.text("Reason Code Algorithm:\u00A0pointsAbove");
        expect($label[4]).to.have.text("Baseline Method:\u00A0other");
      });

    cy.ouiaType("model-setup-overview")
      .find("[data-ouia-component-type=invalid-label]")
      .should(($label) => {
        expect($label).to.have.length(1);
        expect($label[0]).to.have.text("Baseline Score:\u00A0Missing");
      });

    cy.ouiaType("model-setup-overview").click();
    cy.ouiaType("edit-model-setup").should("be.visible");
    cy.ouiaId("core-reasonCodeAlgorithm").click();
    cy.ouiaType("select-option").contains("pointsBelow").click();

    cy.ouiaType("filler").first().click();

    cy.ouiaType("model-setup-overview")
      .find("[data-ouia-component-type=model-property]")
      .should(($label) => {
        expect($label).to.have.length(5);
        expect($label[0]).to.have.text("Is Scorable:\u00A0Yes");
        expect($label[1]).to.have.text("Function:\u00A0regression");
        expect($label[2]).to.have.text("Use Reason Codes:\u00A0Yes");
        expect($label[3]).to.have.text("Reason Code Algorithm:\u00A0pointsBelow");
        expect($label[4]).to.have.text("Baseline Method:\u00A0other");
      });

    cy.ouiaType("model-setup-overview")
      .find("[data-ouia-component-type=invalid-label]")
      .should(($label) => {
        expect($label).to.have.length(1);
        expect($label[0]).to.have.text("Baseline Score:\u00A0Missing");
      });
  });

  it("Attribute 'baselineMethod' - check options", () => {
    cy.ouiaType("model-setup-overview").click();
    cy.ouiaType("edit-model-setup").should("be.visible");
    cy.ouiaId("core-baselineMethod").click();
    cy.ouiaType("select-option").should(($opt) => {
      /*
       * These values are defined by PMML.
       * The selected value only describe type of scorecards.
       *
       * See http://dmg.org/pmml/v4-4/Scorecard.html
       */
      expect($opt).to.have.length(5);
      expect($opt[0]).to.have.text("max");
      expect($opt[1]).to.have.text("min");
      expect($opt[2]).to.have.text("mean");
      expect($opt[3]).to.have.text("neutral");
      expect($opt[4]).to.have.text("other");
    });
    cy.ouiaType("select-option").contains("min").click();

    cy.ouiaType("filler").first().click();

    cy.ouiaType("model-setup-overview")
      .find("[data-ouia-component-type=model-property]")
      .should(($label) => {
        expect($label).to.have.length(5);
        expect($label[0]).to.have.text("Is Scorable:\u00A0Yes");
        expect($label[1]).to.have.text("Function:\u00A0regression");
        expect($label[2]).to.have.text("Use Reason Codes:\u00A0Yes");
        expect($label[3]).to.have.text("Reason Code Algorithm:\u00A0pointsBelow");
        expect($label[4]).to.have.text("Baseline Method:\u00A0min");
      });

    cy.ouiaType("model-setup-overview")
      .find("[data-ouia-component-type=invalid-label]")
      .should(($label) => {
        expect($label).to.have.length(1);
        expect($label[0]).to.have.text("Baseline Score:\u00A0Missing");
      });

    cy.ouiaType("model-setup-overview").click();
    cy.ouiaType("edit-model-setup").should("be.visible");
    cy.ouiaId("core-baselineMethod").click();
    cy.ouiaType("select-option").contains("other").click();

    cy.ouiaType("filler").first().click();

    cy.ouiaType("model-setup-overview")
      .find("[data-ouia-component-type=model-property]")
      .should(($label) => {
        expect($label).to.have.length(5);
        expect($label[0]).to.have.text("Is Scorable:\u00A0Yes");
        expect($label[1]).to.have.text("Function:\u00A0regression");
        expect($label[2]).to.have.text("Use Reason Codes:\u00A0Yes");
        expect($label[3]).to.have.text("Reason Code Algorithm:\u00A0pointsBelow");
        expect($label[4]).to.have.text("Baseline Method:\u00A0other");
      });

    cy.ouiaType("model-setup-overview")
      .find("[data-ouia-component-type=invalid-label]")
      .should(($label) => {
        expect($label).to.have.length(1);
        expect($label[0]).to.have.text("Baseline Score:\u00A0Missing");
      });
  });
});
