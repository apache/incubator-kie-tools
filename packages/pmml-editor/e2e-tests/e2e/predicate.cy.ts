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

describe("Predicate Test", () => {
  beforeEach(() => {
    cy.visit(`http://localhost:${buildEnv.pmmlEditor.dev.port}/`);
  });

  it("Create simple predicate - same pmml as in fixture: simple-predicate.pmml", () => {
    cy.fixture("empty-characteristics-DD-defined.pmml").then((fileContent) => {
      cy.get("[data-ouia-component-id='upload-button']+input").should("not.be.visible").attachFile({
        fileContent: fileContent.toString(),
        fileName: "empty-characteristics-DD-defined.pmml",
        mimeType: "text/plain",
        encoding: "utf-8",
      });
    });

    cy.ouiaId("characteristics")
      .should("be.visible")
      .within(() => {
        cy.ouiaId("add-characteristic").click();

        cy.ouiaId("edit-characteristic").within(() => {
          cy.ouiaId("characteristic-name-input").type("{selectall}{del}Char1");
          cy.ouiaId("characteristic-reason-code-input").type("10");
          cy.ouiaId("characteristic-baseline-score-input").type("5");
          cy.ouiaId("add-attribute").click();
        });

        cy.ouiaId("edit-attribute").within(() => {
          cy.ouiaId("predicate").find("div:first").should("have.text", "1True").type("{selectall}{del}test>3");
          cy.ouiaId("attribute-partial-score").type("5");
        });
        cy.ouiaId("attribute-done").click();

        cy.ouiaType("filler").first().should("be.visible").click();
        cy.get("[data-ouia-component-type='characteristic-item']:contains('Char1')")
          .should("be.visible")
          .within(() => {
            cy.get("span.characteristic-list__item__label").should(($label) => {
              expect($label).to.have.length(3);
              expect($label[0]).to.have.text("Reason code:\u00A010");
              expect($label[1]).to.have.text("Baseline score:\u00A05");
              expect($label[2]).to.have.text("test > 3");
            });
            cy.get("span.attribute-list__item__label").should(($label) => {
              expect($label).to.have.length(1);
              expect($label[0]).to.have.text("Partial score:\u00A05");
            });
          });
      });
    cy.buttonPMML().click().editorShouldContains("simple-predicate.pmml").ouiaId("pmml-modal-confirm").click();
  });

  describe("Use predefined predicate", () => {
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

    it("Rename Data Field", () => {
      cy.buttonDataDictionary().click();
      cy.ouiaId("dd-types-list")
        .should("be.visible")
        .within(() => {
          cy.ouiaType("dd-type-item").contains("test").click();
          cy.ouiaType("field-name").find("input").type("{selectall}{del}count");
        });
      cy.ouiaId("dd-toolbar").click();
      cy.get("button[data-title='DataDictionaryModalClose']").click();

      cy.get("[data-ouia-component-type='characteristic-item']:contains('Char1')")
        .should("be.visible")
        .within(() => {
          cy.get("span.characteristic-list__item__label").should(($label) => {
            expect($label).to.have.length(3);
            expect($label[0]).to.have.text("Reason code:\u00A010");
            expect($label[1]).to.have.text("Baseline score:\u00A05");
            expect($label[2]).to.have.text("count > 3");
          });
          cy.get("span.attribute-list__item__label").should(($label) => {
            expect($label).to.have.length(1);
            expect($label[0]).to.have.text("Partial score:\u00A05");
          });
        });
    });

    it("Add Second Predicate", () => {
      cy.ouiaType("characteristic-item").contains("Char1").click();

      cy.ouiaId("edit-characteristic").within(() => {
        cy.ouiaId("add-attribute").click();
      });

      cy.ouiaId("edit-attribute").within(() => {
        cy.ouiaId("predicate").find("div:first").should("have.text", "1True").type("{selectall}{del}test<0");
        cy.ouiaId("attribute-partial-score").type("-5");
      });
      cy.ouiaId("attribute-done").click();

      cy.ouiaType("filler").first().should("be.visible").click();

      cy.get("[data-ouia-component-type='characteristic-item']:contains('Char1')")
        .should("be.visible")
        .within(() => {
          cy.get("span.characteristic-list__item__label").should(($label) => {
            expect($label).to.have.length(4);
            expect($label[0]).to.have.text("Reason code:\u00A010");
            expect($label[1]).to.have.text("Baseline score:\u00A05");
            expect($label[2]).to.have.text("test > 3");
            expect($label[3]).to.have.text("test < 0");
          });
          cy.get("span.attribute-list__item__label").should(($label) => {
            expect($label).to.have.length(2);
            expect($label[0]).to.have.text("Partial score:\u00A05");
            expect($label[1]).to.have.text("Partial score:\u00A0-5");
          });
        });
    });

    it("Check monaco-editor autocompletion", () => {
      cy.ouiaType("characteristic-item").contains("Char1").click();

      cy.ouiaId("edit-characteristic").within(() => {
        cy.ouiaId("add-attribute").click();
      });

      cy.ouiaId("edit-attribute").within(() => {
        cy.ouiaId("predicate").find("div:first").should("have.text", "1True").type("{selectall}{del}Fal");
        cy.get("div[class='monaco-list-rows']:contains('False')").should("be.visible").click();
        cy.get("div[class='monaco-list-rows']").should("not.be.visible");
        cy.ouiaId("predicate").find("div:first").contains("1False");
      });
    });

    it("Delete Predicate", () => {
      cy.ouiaType("characteristic-item").contains("Char1").click();

      cy.ouiaId("edit-characteristic").within(() => {
        cy.ouiaType("attribute-item")
          .first()
          //The "Attribute Item" needs the focus (or mouse hover) for the delete icon to be visible.
          .focus()
          .within(() => {
            cy.ouiaId("delete-attribute").click();
          });
      });

      cy.ouiaType("filler").first().should("be.visible").click();

      cy.get("[data-ouia-component-type='characteristic-item']:contains('Char1')")
        .should("be.visible")
        .within(() => {
          cy.get("span.characteristic-list__item__label").should(($label) => {
            expect($label).to.have.length(2);
            expect($label[0]).to.have.text("Reason code:\u00A010");
            expect($label[1]).to.have.text("Baseline score:\u00A05");
          });
          cy.get("span.attribute-list__item__label").should("not.exist");
        });
    });

    it("Wrong Data Field in Predicate", () => {
      cy.ouiaType("characteristic-item").contains("Char1").click();

      cy.ouiaId("edit-characteristic").within(() => {
        cy.ouiaType("attribute-item").first().click();
      });

      cy.ouiaId("edit-attribute").within(() => {
        cy.ouiaId("predicate").find("div:first").should("have.text", "1test·>·3").type("<{selectall}{del}wrong > 0");
        cy.ouiaId("attribute-partial-score").type("{selectall}{del}-5");
      });
      cy.get("span.attribute-editor__validation-message").should(
        "have.text",
        '"wrong" cannot be found in the Mining Schema.'
      );
      cy.ouiaId("attribute-done").click();

      cy.ouiaType("filler").first().should("be.visible").click();

      cy.get("[data-ouia-component-type='characteristic-item']:contains('Char1')")
        .should("be.visible")
        .within(() => {
          cy.get("span.characteristic-list__item__label").should(($label) => {
            expect($label).to.have.length(3);
            expect($label[0]).to.have.text("Reason code:\u00A010");
            expect($label[1]).to.have.text("Baseline score:\u00A05");
            expect($label[2]).to.have.text("wrong > 0");
            expect($label[2].children[0]).to.have.class("pf-m-orange");
          });
          cy.get("span.attribute-list__item__label").should(($label) => {
            expect($label).to.have.length(1);
            expect($label[0]).to.have.text("Partial score:\u00A0-5");
          });
        });
    });

    it("Rewrite predicate to compound - same pmml as in fixture: compound-predicate.pmml", () => {
      cy.ouiaType("characteristic-item").contains("Char1").click();

      cy.ouiaId("edit-characteristic").within(() => {
        cy.ouiaType("attribute-item").first().click();
      });

      cy.ouiaId("edit-attribute").within(() => {
        cy.ouiaId("predicate").find("div:first").should("have.text", "1test·>·3").type(" or test<0");
        cy.ouiaId("attribute-partial-score").type("{selectall}{del}-5");
      });
      cy.ouiaId("attribute-done").click();

      cy.ouiaType("filler").first().should("be.visible").click();

      cy.get("[data-ouia-component-type='characteristic-item']:contains('Char1')")
        .should("be.visible")
        .within(() => {
          cy.get("span.characteristic-list__item__label").should(($label) => {
            expect($label).to.have.length(3);
            expect($label[0]).to.have.text("Reason code:\u00A010");
            expect($label[1]).to.have.text("Baseline score:\u00A05");
            expect($label[2]).to.have.text("test > 3 or test < 0");
          });
          cy.get("span.attribute-list__item__label").should(($label) => {
            expect($label).to.have.length(1);
            expect($label[0]).to.have.text("Partial score:\u00A0-5");
          });
        });
      cy.buttonPMML().click().editorShouldContains("compound-predicate.pmml").ouiaId("pmml-modal-confirm").click();
    });
  });
});
