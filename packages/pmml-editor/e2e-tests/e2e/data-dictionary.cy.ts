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

describe("Data Dictionary Test", () => {
  beforeEach(() => {
    cy.visit(`http://localhost:${buildEnv.pmmlEditor.dev.port}/`);
  });

  it("Empty DD", () => {
    cy.newButtonPMML().click();
    cy.buttonDataDictionary().click();

    cy.ouiaId("dd-toolbar")
      .should("be.visible")
      .within(() => {
        cy.ouiaId("add-data-type").should("not.be.disabled").should("be.visible");
        cy.ouiaId("add-multiple-data-type").should("not.be.disabled").should("be.visible");
        cy.ouiaId("order-toggle").should("not.be.disabled").should("be.visible");
      });
    cy.ouiaId("no-data-fields-title").should("be.visible").should("have.text", "No Data Fields Defined");
  });

  it("Add multiple Fields", () => {
    cy.newButtonPMML().click();
    cy.buttonDataDictionary().click();

    cy.ouiaId("add-multiple-data-type").click();
    cy.ouiaId("multiple-data-types").should("be.visible").type("aaa\nbbb\n");
    cy.ouiaId("add-them").click();

    cy.ouiaType("dd-type-item").should("have.length", 2);
    cy.ouiaId("aaa", "dd-type-item")
      .should("be.visible")
      .within(() => {
        cy.ouia("string", "data-type-label").should("be.visible");
        cy.ouia("categorical", "data-optype-label").should("be.visible");
      });

    cy.ouiaId("bbb", "dd-type-item")
      .should("be.visible")
      .within(() => {
        cy.ouia("string", "data-type-label").should("be.visible");
        cy.ouia("categorical", "data-optype-label").should("be.visible");
      });
    cy.get("button[data-title='DataDictionaryModalClose']").click();

    cy.buttonPMML().click();
    cy.ouiaType("source-code")
      .should(
        "to.contain",
        '  <DataDictionary numberOfFields="2">' +
          '    <DataField name="aaa" optype="categorical" dataType="string"/>' +
          '    <DataField name="bbb" optype="categorical" dataType="string"/>' +
          "  </DataDictionary>"
      )
      .ouiaId("pmml-modal-confirm")
      .click();
    /*
     * Current implementation allows use string "test\ntest" without any warning
     * TODO: discuss this topic with developers
     */
  });

  it("Add a single data field", () => {
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
        cy.ouiaType("select-option").contains("double").click();
        cy.ouiaType("field-optype").find("button").click();
        cy.ouiaType("select-option").contains("ordinal").click();
      });
    cy.ouiaId("dd-toolbar").click();

    cy.get("button[data-title='DataDictionaryModalClose']").click();

    cy.buttonPMML().click();
    cy.ouiaType("source-code")
      .should(
        "to.contain",
        '  <DataDictionary numberOfFields="2">' +
          '    <DataField name="test1" optype="ordinal" dataType="integer"/>' +
          '    <DataField name="test2" optype="ordinal" dataType="double"/>' +
          "  </DataDictionary>"
      )
      .ouiaId("pmml-modal-confirm")
      .click();
  });

  describe("Use predefined data type", () => {
    beforeEach(() => {
      cy.fixture("empty-characteristics-DD-defined-types.pmml").then((fileContent) => {
        cy.get("[data-ouia-component-id='upload-button']+input").should("not.be.visible").attachFile({
          fileContent: fileContent.toString(),
          fileName: "simple-predicate.pmml",
          mimeType: "text/plain",
          encoding: "utf-8",
        });
      });
    });

    /**
     * Deletion of Data Field causes an valdation error.
     */
    it("Delete Data Field", () => {
      cy.buttonDataDictionary().click();
      cy.ouia("dt-boolean", "dd-type-item")
        .focus()
        .within(() => {
          cy.ouiaId("delete-field").click();
        });
      cy.ouiaType("dd-type-item").should("have.length", "5");
      cy.ouia("dt-boolean", "dd-type-item").should("not.exist");

      cy.get("button[data-title='DataDictionaryModalClose']").click();

      cy.buttonMiningSchema().click();
      cy.ouiaId("validation-container")
        .find("[data-ouia-component-type='PF4/Alert']")
        .first()
        .should("be.visible")
        .should("contain", "Some items are invalid and need attention.");

      cy.ouiaId("dt-boolean")
        .focus()
        .within(() => {
          cy.ouiaId("validation-issue").should("be.visible");
          cy.ouiaId("delete-field").click();
        });

      cy.ouiaId("validation-container").should("not.exist");

      cy.ouiaId("editor-close").click();
    });

    it("Reorder Data Fields", () => {
      cy.buttonDataDictionary().click();

      cy.ouiaId("order-toggle").should("have.text", "Order").click();
      cy.ouiaId("dt-boolean").find("[data-ouia-component-id='drag-it']").should("exist");
      //TODO: find a way how drag an item
      cy.ouiaId("order-toggle").should("have.text", "End Ordering").click();

      cy.get("button[data-title='DataDictionaryModalClose']").click();
    });

    it("Rename Data Field - unique", () => {
      cy.buttonDataDictionary().click();

      cy.ouia("dt-boolean", "dd-type-item")
        .click()
        .within(() => {
          cy.ouiaType("field-name").find("input").type("-new");
        });
      cy.ouiaId("dd-toolbar").click();

      cy.ouia("dt-boolean", "dd-type-item").should("not.exist");
      cy.ouia("dt-boolean-new", "dd-type-item").should("exist");

      cy.get("button[data-title='DataDictionaryModalClose']").click();
    });

    it("Rename Data Field - not unique", () => {
      cy.buttonDataDictionary().click();

      cy.ouia("dt-boolean", "dd-type-item")
        .click()
        .within(() => {
          cy.ouiaType("field-name").find("input").clear().type("dt-string");
          cy.ouiaType("field-name").find("div#name-helper").should("have.text", "Name is mandatory and must be unique");
        });
      cy.ouiaId("dd-toolbar").click();

      cy.ouia("dt-string", "dd-type-item").should("have.length", "1");
      cy.ouia("dt-boolean", "dd-type-item").should("exist");

      cy.get("button[data-title='DataDictionaryModalClose']").click();
    });

    it("Empty Data Field Name", () => {
      cy.buttonDataDictionary().click();

      cy.ouia("dt-boolean", "dd-type-item")
        .click()
        .within(() => {
          cy.ouiaType("field-name").find("input").clear();
          cy.ouiaType("field-name").find("div#name-helper").should("have.text", "Name is mandatory and must be unique");
        });
      cy.ouiaId("dd-toolbar").click();

      cy.ouia("dt-boolean", "dd-type-item").should("exist");

      cy.get("button[data-title='DataDictionaryModalClose']").click();
    });

    it("Set and unset 'Display Name'", () => {
      cy.buttonDataDictionary().click();

      cy.ouia("dt-int", "dd-type-item")
        .within(() => {
          cy.ouia("Display Name", "data-props-label").should("not.exist");
        })
        .click();

      /*
       * Set property
       */
      cy.ouia("edit-props", "link-label").click();
      cy.ouia("df-props", "editor-body").within(() => {
        cy.ouiaId("display-name").type("test input");
      });
      cy.ouiaId("back-to-DFs").click();

      cy.ouia("dt-int", "dd-type-item")
        .within(() => {
          cy.ouia("Display Name", "data-props-label")
            .should("exist")
            .should("to.have.text", "Display Name:\u00A0test input");
        })
        .click();

      /*
       * Unset property
       */
      cy.ouia("edit-props", "link-label").click();
      cy.ouia("df-props", "editor-body").within(() => {
        cy.ouiaId("display-name").clear();
      });
      cy.ouiaId("back-to-DFs").click();

      cy.ouia("dt-int", "dd-type-item").within(() => {
        cy.ouia("Display Name", "data-props-label").should("not.exist");
      });

      cy.get("button[data-title='DataDictionaryModalClose']").click();
    });

    describe("Set and unset 'Values'", () => {
      it("Definition of several values: Integer - Rating", () => {
        cy.buttonDataDictionary().click();

        cy.ouia("dt-int", "dd-type-item")
          .within(() => {
            cy.ouia("constraints", "data-props-label").should("not.exist");
          })
          .click();

        /*
         * Set property
         */
        cy.ouia("edit-props", "link-label").click();
        cy.ouia("df-props", "editor-body").within(() => {
          cy.ouiaId("constraints-type").find("button").click();
          cy.ouiaType("select-option").contains("Value").click();
          cy.ouiaId("val-0").find("input").type("1");
          cy.ouiaId("add-another-value").click();
          cy.ouiaId("val-1").find("input").type("2");
          cy.ouiaId("add-another-value").click();
          cy.ouiaId("val-2").find("input").type("3");
          cy.ouiaId("add-another-value").click();
          cy.ouiaId("val-3").find("input").type("4");
          cy.ouiaId("add-another-value").click();
          cy.ouiaId("val-4").find("input").type("5");
        });
        cy.ouiaId("back-to-DFs").click();
        cy.ouiaId("dd-toolbar").click();

        cy.ouia("dt-int", "dd-type-item")
          .within(() => {
            cy.ouia("constraints", "data-props-label")
              .should("exist")
              .should("to.have.text", 'Constraints:\u00A0"1", "2", "3", "4", "5"');
          })
          .click();

        /*
         * Unset property
         */
        cy.ouia("edit-props", "link-label").click();
        cy.ouia("df-props", "editor-body").within(() => {
          cy.ouiaId("val-0").find("button").click();
          cy.ouiaId("val-0").find("button").click();
          cy.ouiaId("val-0").find("button").click();
          cy.ouiaId("val-0").find("button").click();
          //PMML Spec says that there can be zero Values, but editor does not allow to delete the last item.
        });
        cy.ouiaId("back-to-DFs").click();

        cy.ouiaId("dd-toolbar").click();
        cy.ouia("dt-int", "dd-type-item").within(() => {
          cy.ouia("constraints", "data-props-label").should("exist").should("to.have.text", 'Constraints:\u00A0"5"');
        });
      });

      it("Missing Value: Double - not analyzed", () => {
        cy.buttonDataDictionary().click();

        cy.ouia("dt-double", "dd-type-item")
          .within(() => {
            cy.ouia("Missing Value", "data-props-label").should("not.exist");
          })
          .click();

        /*
         * Set property
         */
        cy.ouia("edit-props", "link-label").click();
        cy.ouia("df-props", "editor-body").within(() => {
          cy.ouiaId("missing-value").type("N.A.");
        });
        cy.ouiaId("back-to-DFs").click();

        cy.ouia("dt-double", "dd-type-item")
          .within(() => {
            cy.ouia("Missing Value", "data-props-label")
              .should("exist")
              .should("to.have.text", "Missing Value:\u00A0N.A.");
          })
          .click();

        /*
         * Unset property
         */
        cy.ouia("edit-props", "link-label").click();
        cy.ouia("df-props", "editor-body").within(() => {
          cy.ouiaId("missing-value").clear();
        });
        cy.ouiaId("back-to-DFs").click();

        cy.ouiaId("dd-toolbar").click();
        cy.ouia("dt-double", "dd-type-item").within(() => {
          cy.ouia("Missing Value", "data-props-label").should("not.exist");
        });
      });

      it("Invalid Value: String", () => {
        cy.buttonDataDictionary().click();

        cy.ouia("dt-string", "dd-type-item")
          .within(() => {
            cy.ouia("Invalid Value", "data-props-label").should("not.exist");
          })
          .click();

        /*
         * Set property
         */
        cy.ouia("edit-props", "link-label").click();
        cy.ouia("df-props", "editor-body").within(() => {
          cy.ouiaId("invalid-value").type("invalid");
        });
        cy.ouiaId("back-to-DFs").click();
        cy.ouiaId("dd-toolbar").click();

        cy.ouia("dt-string", "dd-type-item")
          .within(() => {
            cy.ouia("Invalid Value", "data-props-label")
              .should("exist")
              .should("to.have.text", "Invalid Value:\u00A0invalid");
            //XSD allows several invalid values. PMML editors allows just one.
          })
          .click();

        /*
         * Unset property
         */
        cy.ouia("edit-props", "link-label").click();
        cy.ouia("df-props", "editor-body").within(() => {
          cy.ouiaId("invalid-value").clear();
        });
        cy.ouiaId("back-to-DFs").click();

        cy.ouiaId("dd-toolbar").click();
        cy.ouia("dt-string", "dd-type-item").within(() => {
          cy.ouia("Invalid Value", "data-props-label").should("not.exist");
        });
      });
    });

    describe("Set and unset 'Interval'", () => {
      beforeEach(() => {
        cy.buttonDataDictionary().click();

        cy.ouia("dt-double", "dd-type-item")
          .within(() => {
            cy.ouia("constraints", "data-props-label").should("not.exist");
          })
          .click();
      });

      afterEach("Unset Property", () => {
        cy.ouia("dt-double", "dd-type-item").click();
        cy.ouia("edit-props", "link-label").click();
        cy.ouia("df-props", "editor-body").within(() => {
          cy.ouiaId("constraints-type").find("button").click();
          cy.ouiaType("select-option").contains("Select a type").click();
        });
        cy.ouiaId("back-to-DFs").click();

        cy.ouiaId("dd-toolbar").click();
        cy.ouia("dt-double", "dd-type-item").within(() => {
          cy.ouia("constraints", "data-props-label").should("not.exist");
        });
      });

      it("One closed interval", () => {
        /*
         * Set property
         */
        cy.ouia("edit-props", "link-label").click();
        cy.ouia("df-props", "editor-body").within(() => {
          cy.ouiaId("constraints-type").find("button").click();
          cy.ouiaType("select-option").contains("Interval").click();
          cy.ouiaId("interval-0").within(() => {
            cy.ouiaId("start-value").type("0");
            cy.ouiaId("end-value").type("10");
          });
        });
        cy.ouiaId("back-to-DFs").click();
        cy.ouiaId("dd-toolbar").click();

        cy.ouia("dt-double", "dd-type-item").within(() => {
          cy.ouia("constraints", "data-props-label")
            .should("exist")
            .should("to.have.text", "Constraints:\u00A0[0, 10]");
        });
      });

      it("One open interval", () => {
        /*
         * Set property
         */
        cy.ouia("edit-props", "link-label").click();
        cy.ouia("df-props", "editor-body").within(() => {
          cy.ouiaId("constraints-type").find("button").click();
          cy.ouiaType("select-option").contains("Interval").click();
          cy.ouiaId("interval-0").within(() => {
            cy.ouiaId("start-value").type("0");
            cy.ouiaId("end-value").type("10");
            cy.get("div#start-value-0-helper").should("not.exist");
            cy.ouiaId("is-start-included").uncheck();
            cy.ouiaId("is-end-included").uncheck();
          });
        });
        cy.ouiaId("back-to-DFs").click();

        cy.ouia("dt-double", "dd-type-item").within(() => {
          cy.ouia("constraints", "data-props-label")
            .should("exist")
            .should("to.have.text", "Constraints:\u00A0(0, 10)");
        });
      });

      it("Two intervals", () => {
        /*
         * Set property
         */
        cy.ouia("edit-props", "link-label").click();
        cy.ouia("df-props", "editor-body").within(() => {
          cy.ouiaId("constraints-type").find("button").click();
          cy.ouiaType("select-option").contains("Interval").click();
          cy.ouiaId("interval-0").within(() => {
            cy.ouiaId("start-value").type("0");
            cy.ouiaId("end-value").type("10");
            cy.get("div#start-value-0-helper").should("not.exist");
            cy.ouiaId("is-end-included").uncheck();
          });
          cy.ouiaId("add-another-interval").click();
          cy.ouiaId("interval-1").within(() => {
            cy.ouiaId("start-value").type("20");
            cy.ouiaId("end-value").type("30");
            cy.get("div#start-value-1-helper").should("not.exist");
            cy.ouiaId("is-start-included").uncheck();
          });
        });
        cy.ouiaId("back-to-DFs").click();

        cy.ouia("dt-double", "dd-type-item").within(() => {
          cy.ouia("constraints", "data-props-label")
            .should("exist")
            .should("to.have.text", "Constraints:\u00A0[0, 10) (20, 30]");
        });
      });
    });
  });
});
