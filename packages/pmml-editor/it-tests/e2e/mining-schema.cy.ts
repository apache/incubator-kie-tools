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

describe("Mining Schema Test", () => {
  beforeEach(() => {
    cy.visit(`http://localhost:${buildEnv.pmmlEditor.dev.port}/`);
  });

  /**
   * Mining schema cannot be defined without any Data Field in the Data Dictionary.
   */
  it("No DD defined", () => {
    cy.newButtonPMML().click();
    cy.buttonMiningSchema().click();

    cy.ouiaId("mining-toolbar")
      .should("be.visible")
      .within(() => {
        cy.ouiaId("select-mining-field")
          .find("input#select-mining-field-select-multi-typeahead-typeahead")
          .should("be.disabled");
        cy.ouiaId("select-mining-field").find("button#select-mining-field").should("be.disabled");
        cy.ouiaId("add-mining-field").should("be.disabled");
        cy.ouiaId("add-all-fields").should("be.disabled");
      });
    cy.ouiaId("mining-schema-no-data-fields-title").should("be.visible").should("have.text", "No Data Fields defined");
  });

  it("Define all Fields", () => {
    cy.newButtonPMML().click();
    cy.buttonDataDictionary().click();

    cy.ouiaId("add-data-type").click();
    cy.ouiaId("dd-types-list")
      .should("be.visible")
      .within(() => {
        cy.ouiaType("field-name").find("input").type("test1");
        cy.ouiaType("field-type").find("button").click();
        cy.ouiaType("select-option").contains("integer").click();
        cy.ouiaType("field-optype").find("button").click();
        cy.ouiaType("select-option").contains("ordinal").click();
      });
    cy.ouiaId("dd-toolbar").click();

    cy.ouiaId("add-data-type").click();
    cy.ouiaId("dd-types-list")
      .should("be.visible")
      .within(() => {
        cy.ouiaType("field-name").find("input").type("test2");
        cy.ouiaType("field-type").find("button").click();
        cy.ouiaType("select-option").contains("integer").click();
        cy.ouiaType("field-optype").find("button").click();
        cy.ouiaType("select-option").contains("ordinal").click();
      });
    cy.ouiaId("dd-toolbar").click();
    cy.get("button[data-title='DataDictionaryModalClose']").click();

    cy.buttonMiningSchema().click();
    cy.ouiaId("add-all-fields").click();

    cy.ouiaType("mining-schema-list")
      .find("[data-ouia-component-type='mining-field-row']")
      .should(($item) => {
        expect($item).to.have.length(2);
        expect($item[0]).to.have.text("test1");
        expect($item[1]).to.have.text("test2");
      });
    cy.ouiaId("editor-close").click();
  });

  it("Define field", () => {
    cy.newButtonPMML().click();
    cy.buttonDataDictionary().click();

    cy.ouiaId("add-data-type").click();
    cy.ouiaId("dd-types-list")
      .should("be.visible")
      .within(() => {
        cy.ouiaType("field-name").find("input").type("test1");
        cy.ouiaType("field-type").find("button").click();
        cy.ouiaType("select-option").contains("integer").click();
        cy.ouiaType("field-optype").find("button").click();
        cy.ouiaType("select-option").contains("ordinal").click();
      });
    cy.ouiaId("dd-toolbar").click();

    cy.ouiaId("add-data-type").click();
    cy.ouiaId("dd-types-list")
      .should("be.visible")
      .within(() => {
        cy.ouiaType("field-name").find("input").type("test2");
        cy.ouiaType("field-type").find("button").click();
        cy.ouiaType("select-option").contains("integer").click();
        cy.ouiaType("field-optype").find("button").click();
        cy.ouiaType("select-option").contains("ordinal").click();
      });
    cy.ouiaId("dd-toolbar").click();

    cy.get("button[data-title='DataDictionaryModalClose']").click();

    cy.buttonMiningSchema().click();
    cy.ouiaId("mining-toolbar").within(() => {
      cy.ouiaId("select-mining-field").find("input").click();
      cy.ouiaId("select-mining-field")
        .find("ul button")
        .should(($options) => {
          expect($options).to.have.length(2);
          expect($options[0]).to.have.text("test1");
          expect($options[0]).to.not.have.class("pf-m-disabled");
          expect($options[1]).to.have.text("test2");
          expect($options[1]).to.not.have.class("pf-m-disabled");
          $options[0].click();
        });
      cy.ouiaId("add-mining-field").click();
    });

    cy.ouiaType("mining-schema-list")
      .find("[data-ouia-component-type='mining-field-row']")
      .should(($item) => {
        expect($item).to.have.length(1);
        expect($item[0]).to.have.text("test1");
      });

    cy.ouiaId("mining-toolbar").within(() => {
      cy.ouiaId("select-mining-field").find("input").click();
      cy.ouiaId("select-mining-field")
        .find("ul button")
        .should(($options) => {
          expect($options).to.have.length(2);
          expect($options[0]).to.have.text("test1");
          expect($options[0]).to.have.class("pf-m-disabled");
          expect($options[1]).to.have.text("test2");
          expect($options[1]).to.not.have.class("pf-m-disabled");
          $options[1].click();
        });
      cy.ouiaId("add-mining-field").click();
    });

    cy.ouiaType("mining-schema-list")
      .find("[data-ouia-component-type='mining-field-row']")
      .should(($item) => {
        expect($item).to.have.length(2);
        expect($item[0]).to.have.text("test1");
        expect($item[1]).to.have.text("test2");
      });

    cy.ouiaId("editor-close").click();
  });

  describe("Use predefined data type", () => {
    beforeEach(() => {
      cy.fixture("simple-predicate.pmml").then((fileContent) => {
        cy.get("[data-ouia-component-id='upload-button']+input").should("not.be.visible").attachFile({
          fileContent: fileContent.toString(),
          fileName: "simple-predicate.pmml",
          mimeType: "text/plain",
          encoding: "utf-8",
        });
      });
    });

    /**
     * Mining Schema was defined correctly and unfortunately Data Filed was deleted.
     */
    it("Delete Data Field when Mining schema was defined", () => {
      cy.buttonDataDictionary().click();
      cy.ouiaType("dd-type-item")
        .first()
        .focus()
        .within(() => {
          cy.ouiaId("delete-field").click();
        });
      cy.get("button[data-title='DataDictionaryModalClose']").click();

      cy.buttonMiningSchema().click();
      cy.ouiaId("mining-toolbar")
        .should("be.visible")
        .within(() => {
          cy.ouiaId("select-mining-field")
            .find("input#select-mining-field-select-multi-typeahead-typeahead")
            .should("be.disabled");
          cy.ouiaId("select-mining-field").find("button#select-mining-field").should("be.disabled");
          cy.ouiaId("add-mining-field").should("be.disabled");
          cy.ouiaId("add-all-fields").should("be.disabled");
        });
      cy.ouiaId("validation-container")
        .find("[data-ouia-component-type='PF4/Alert']")
        .first()
        .should("be.visible")
        .should("contain", "Some items are invalid and need attention.");

      cy.ouiaId("test").then(() => {
        cy.ouiaId("validation-issue").should("be.visible");
      });

      cy.ouiaId("editor-close").click();

      /*
       * Recover from validation error.
       */
      cy.buttonDataDictionary().click();
      cy.ouiaId("add-data-type").click();
      cy.ouiaId("dd-types-list")
        .should("be.visible")
        .within(() => {
          cy.ouiaType("field-name").find("input").type("test");
          cy.ouiaType("field-type").find("button").click();
          cy.ouiaType("select-option").contains("integer").click();
          cy.ouiaType("field-optype").find("button").click();
          cy.ouiaType("select-option").contains("ordinal").click();
        });
      cy.ouiaId("dd-toolbar").click();
      cy.get("button[data-title='DataDictionaryModalClose']").click();

      cy.buttonMiningSchema().click();

      cy.ouiaType("mining-schema-list")
        .find("[data-ouia-component-type='mining-field-row']")
        .should(($item) => {
          expect($item).to.have.length(1);
          expect($item[0]).to.have.text("test");
        });

      cy.ouiaId("validation-container").should("not.exist");

      cy.ouiaId("editor-close").click();
    });

    it("Delete Mining Field", () => {
      cy.buttonMiningSchema().click();
      cy.ouiaType("mining-schema-list")
        .find("[data-ouia-component-id='test']")
        .focus()
        .find("[data-ouia-component-id='delete-field']")
        .click();

      cy.ouiaId("no-mining-fields").should("be.visible").should("contain.text", "No Mining Fields found");
      cy.ouiaId("editor-close").click();
    });

    it("Set and unset 'Usage Type' - check supported options", () => {
      /* PMML standard defines these options for the Usage Type:
       * active - default
       * predicated
       * supplementary
       * group
       * order
       * frequencyWeight
       * analysisWeight
       */
      cy.buttonMiningSchema().click();
      cy.ouiaId("test").click();
      cy.ouiaId("test").find("[data-ouia-component-id='edit-properties']").click();
      cy.ouiaId("usageType").click();
      cy.get("ul#usageType li button").then(($options) => {
        expect($options).have.length(9);
        expect($options[0]).have.text("");
        expect($options[1]).have.text("active");
        expect($options[2]).have.text("predicted");
        expect($options[3]).have.text("target");
        expect($options[4]).have.text("supplementary");
        expect($options[5]).have.text("group");
        expect($options[6]).have.text("order");
        expect($options[7]).have.text("frequencyWeight");
        expect($options[8]).have.text("analysisWeight");

        expect($options[0]).not.have.class("pf-m-disabled");
        expect($options[1]).not.have.class("pf-m-disabled");
        expect($options[2]).not.have.class("pf-m-disabled");
        expect($options[3]).not.have.class("pf-m-disabled");
        expect($options[4]).not.have.class("pf-m-disabled");
        expect($options[5]).not.have.class("pf-m-disabled");
        expect($options[6]).not.have.class("pf-m-disabled");
        expect($options[7]).not.have.class("pf-m-disabled");
        expect($options[8]).not.have.class("pf-m-disabled");
        $options[8].click();
      });
      cy.ouiaId("back-to-ms-overview").click();
      cy.ouiaType("editor-container").click();
      cy.ouiaId("test").then(() => {
        cy.ouiaType("mining-label").then(($labels) => {
          expect($labels).to.have.length(1);
          expect($labels[0]).to.have.text("Usage Type:\u00A0analysisWeight");
        });
      });

      /*
       * Unset property
       */
      cy.ouiaId("test").click();
      cy.ouiaId("test").find("[data-ouia-component-id='edit-properties']").click();
      cy.ouiaId("usageType").click();
      cy.get("ul#usageType li button").first().click();
      cy.ouiaId("back-to-ms-overview").click();
      cy.ouiaType("editor-container").click();
      cy.ouiaId("test").then(() => {
        cy.ouiaType("mining-label").should("not.exist");
      });
    });

    it("Rename Data Field", () => {
      const newName = "something";
      cy.buttonDataDictionary().click();
      cy.ouia("test", "dd-type-item").click();
      cy.ouia("test", "dd-type-item").then(() => {
        cy.ouiaType("field-name").find("input").clear().type(newName);
      });
      cy.ouiaId("dd-toolbar").click();
      cy.get("button[data-title='DataDictionaryModalClose']").click();

      cy.buttonMiningSchema().click();

      cy.ouiaType("mining-schema-list")
        .find("[data-ouia-component-type='mining-field-row']")
        .should(($item) => {
          expect($item).to.have.length(1);
          expect($item[0]).to.have.text(newName);
        });
    });

    describe("Set and unset 'Outliers'", () => {
      beforeEach(() => {
        cy.buttonMiningSchema().click();
        cy.ouiaId("test").click(); //new element with same id

        cy.ouiaId("test").find("[data-ouia-component-id='edit-properties']").click();
        cy.ouia("outliers", "PF4/Select").click();
      });

      it("supported options", () => {
        cy.get("ul#outliers li button").then(($options) => {
          expect($options).have.length(4);
          expect($options[0]).have.text("");
          expect($options[1]).have.text("asIs");
          expect($options[2]).have.text("asMissingValues");
          expect($options[3]).have.text("asExtremeValues");

          expect($options[0]).not.have.class("pf-m-disabled");
          expect($options[1]).not.have.class("pf-m-disabled");
          expect($options[2]).not.have.class("pf-m-disabled");
          expect($options[3]).not.have.class("pf-m-disabled");
        });
      });

      it("asIs", () => {
        cy.get("ul#outliers>li button ").contains("asIs").click();

        cy.ouiaId("low-value").should("have.attr", "disabled");
        cy.ouiaId("high-value").should("have.attr", "disabled");

        cy.ouiaId("back-to-ms-overview").click();
        cy.ouiaType("editor-container").click();
        cy.ouiaId("test").then(() => {
          cy.ouiaType("mining-label").then(($labels) => {
            expect($labels).to.have.length(1);
            expect($labels[0]).to.have.text("Outliers:\u00A0asIs");
          });
        });

        /*
         * Unset Outliers
         */

        cy.ouiaId("test").click();
        cy.ouiaId("test").find("[data-ouia-component-id='edit-properties']").click();
        cy.ouia("outliers", "PF4/Select").click();
        cy.get("ul#outliers>li button ").first().click();
        cy.ouiaId("back-to-ms-overview").click();
        cy.ouiaType("editor-container").click();
        cy.ouiaId("test").then(() => {
          cy.ouiaType("mining-label").should("not.exist");
        });
      });

      it("asMissingValues", () => {
        const lowValue = "-10";
        const highValue = "10";
        cy.get("ul#outliers>li button ").contains("asMissingValues").click();

        cy.ouiaId("low-value").should("not.have.attr", "disabled");
        cy.ouiaId("low-value").type(lowValue);
        cy.ouiaId("high-value").should("not.have.attr", "disabled");
        cy.ouiaId("high-value").type(highValue);

        cy.ouiaId("back-to-ms-overview").click();
        cy.ouiaType("editor-container").click();
        cy.ouiaId("test").then(() => {
          cy.ouiaType("mining-label").then(($labels) => {
            expect($labels).to.have.length(3);
            expect($labels[0]).to.have.text("Outliers:\u00A0asMissingValues");
            expect($labels[1]).to.have.text(`Low Value:\u00A0${lowValue}`);
            expect($labels[2]).to.have.text(`High Value:\u00A0${highValue}`);
          });
        });

        /*
         * Unset Outliers
         */

        cy.ouiaId("test").click();
        cy.ouiaId("test").find("[data-ouia-component-id='edit-properties']").click();
        cy.ouia("outliers", "PF4/Select").click();
        cy.get("ul#outliers>li button ").first().click();

        cy.ouiaId("low-value").should("have.attr", "disabled");
        cy.ouiaId("high-value").should("have.attr", "disabled");
        cy.ouiaId("back-to-ms-overview").click();

        cy.ouiaType("editor-container").click();
        cy.ouiaId("test").then(() => {
          cy.ouiaType("mining-label").should("not.exist");
        });
      });

      it("asExtremeValues", () => {
        const lowValue = "-10.8";
        const highValue = "10.1";
        cy.get("ul#outliers>li button ").contains("asExtremeValues").click();

        cy.ouiaId("low-value").should("not.have.attr", "disabled");
        cy.ouiaId("low-value").type(lowValue);
        cy.ouiaId("high-value").should("not.have.attr", "disabled");
        cy.ouiaId("high-value").type(highValue);

        cy.ouiaId("back-to-ms-overview").click();
        cy.ouiaType("editor-container").click();
        cy.ouiaId("test").then(() => {
          cy.ouiaType("mining-label").then(($labels) => {
            expect($labels).to.have.length(3);
            expect($labels[0]).to.have.text("Outliers:\u00A0asExtremeValues");
            expect($labels[1]).to.have.text(`Low Value:\u00A0${lowValue}`);
            expect($labels[2]).to.have.text(`High Value:\u00A0${highValue}`);
          });
        });

        /*
         * Unset Outliers
         */

        cy.ouiaId("test").click();
        cy.ouiaId("test").find("[data-ouia-component-id='edit-properties']").click();
        cy.ouia("outliers", "PF4/Select").click();
        cy.get("ul#outliers>li button ").first().click();

        cy.ouiaId("low-value").should("have.attr", "disabled");
        cy.ouiaId("high-value").should("have.attr", "disabled");
        cy.ouiaId("back-to-ms-overview").click();

        cy.ouiaType("editor-container").click();
        cy.ouiaId("test").then(() => {
          cy.ouiaType("mining-label").should("not.exist");
        });
      });
    });
  });
});
