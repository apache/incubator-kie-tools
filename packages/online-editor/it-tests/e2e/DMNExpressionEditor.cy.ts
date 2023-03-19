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

describe.skip("DMN Expression Editor Test :: Expressions", () => {
  beforeEach(() => {
    cy.visit("/");
  });

  it("Test New Expresssion editor - context", () => {
    // click Create new decision model button (new DMN)
    cy.ouia({ ouiaId: "try-dmn-sample-button" }).click();

    // wait until loading dialog disappears
    cy.loadEditor();

    // close DMN guided tour dialog
    cy.ouia({ ouiaId: "dmn-guided-tour" }).children("button[aria-label='Close']").click();

    cy.getEditor().within(() => {
      // open decision navigator and check new expression editor for expressions
      cy.ouia({ ouiaId: "docks-item-org.kie.dmn.decision.navigator" }).children("button").click();

      cy.get("li[data-i18n-prefix='DecisionNavigatorTreeView.']").within(($navigator) => {
        cy.get("[title='Back End Ratio'] div span").contains("Context").click();
      });
      // check using beta version
      cy.get("[data-field='beta-boxed-expression-toggle'] [data-field='try-it']").click();
      cy.ouia({ ouiaId: "expression-container" }).contains("Back End Ratio").should("be.visible");
      cy.get(".expression-title").contains("Back End Ratio").should("be.visible");
      cy.get(".expression-type").contains("Context").should("be.visible");
    });
  });

  it("Test New Expresssion editor - function", () => {
    // click Create new decision model button (new DMN)
    cy.ouia({ ouiaId: "try-dmn-sample-button" }).click();

    // wait until loading dialog disappears
    cy.loadEditor();

    // close DMN guided tour dialog
    cy.ouia({ ouiaId: "dmn-guided-tour" }).children("button[aria-label='Close']").click();

    cy.getEditor().within(() => {
      // open decision navigator and check new expression editor for expressions
      cy.ouia({ ouiaId: "docks-item-org.kie.dmn.decision.navigator" }).children("button").click();

      cy.get("li[data-i18n-prefix='DecisionNavigatorTreeView.']").within(($navigator) => {
        cy.get("[title='PITI'] div span").contains("Function").click();
      });
      // check using beta version
      cy.get("[data-field='beta-boxed-expression-toggle'] [data-field='try-it']").click();
      cy.ouia({ ouiaId: "expression-container" }).contains("PITI").should("be.visible");
      cy.get(".expression-title").contains("PITI").should("be.visible");
      cy.get(".expression-type").contains("Function").should("be.visible");
    });
  });

  it("Test New Expresssion editor - decision table", () => {
    // click Create new decision model button (new DMN)
    cy.ouia({ ouiaId: "try-dmn-sample-button" }).click();

    // wait until loading dialog disappears
    cy.loadEditor();

    // close DMN guided tour dialog
    cy.ouia({ ouiaId: "dmn-guided-tour" }).children("button[aria-label='Close']").click();

    cy.getEditor().within(() => {
      // open decision navigator and check new expression editor for expressions
      cy.ouia({ ouiaId: "docks-item-org.kie.dmn.decision.navigator" }).children("button").click();

      cy.get("li[data-i18n-prefix='DecisionNavigatorTreeView.']").within(($navigator) => {
        cy.get("[title='Credit Score Rating'] div span").contains("Decision Table").click();
      });
      // check using beta version
      cy.get("[data-field='beta-boxed-expression-toggle'] [data-field='try-it']").click();
      cy.ouia({ ouiaId: "expression-container" }).contains("Credit Score Rating").should("be.visible");
      cy.get(".expression-title").contains("Credit Score Rating").should("be.visible");
      cy.get(".expression-type").contains("Decision Table").should("be.visible");
    });
  });
});

describe.skip("DMN Expression Editor Test :: Data types", () => {
  beforeEach(() => {
    cy.visit("/");
  });

  it("Change Decision Table from Any to Custom Data Type", () => {
    cy.get("#upload-field").attachFile("testModelWithCustomDataType.dmn", { subjectType: "drag-n-drop" });

    // wait until loading dialog disappears
    cy.loadEditor();

    // close DMN guided tour dialog
    cy.ouia({ ouiaId: "dmn-guided-tour" }).children("button[aria-label='Close']").click();

    cy.getEditor().within(() => {
      // open decision navigator
      cy.ouia({ ouiaId: "docks-item-org.kie.dmn.decision.navigator" }).children("button").click();

      // open decision table expression
      cy.get("li[data-i18n-prefix='DecisionNavigatorTreeView.']").within(($navigator) => {
        cy.get("[title='Final Salary'] div span").contains("Decision Table").click();
      });
      // activate editor beta version
      cy.get("[data-field='beta-boxed-expression-toggle'] [data-field='try-it']").click();

      cy.get(".header-cell-info").contains("Final Salary").click();
      // cy.ouia("edit-expression-data-type").click();

      // define custom tSalary data type
      cy.ouia({ ouiaId: "manage-data-type-link" }).click();
      cy.ouia({ ouiaType: "add-data-type-button", ouiaId: "first" }).click();

      // root
      changeNewDataTypeEntry("tSalary", "Structure");

      // Amount
      changeNewDataTypeEntry("Amount", "number");

      cy.ouia({ ouiaId: "Amount" }).within(($row) => {
        cy.get("[data-type-field='add-data-type-row-button']").click();
      });

      // Currency
      changeNewDataTypeEntry("Currency", "string");

      // go back to expression editor
      cy.get("[data-ouia-component-id='Editor'] a").click();

      // activate editor beta version
      cy.get("[data-field='beta-boxed-expression-toggle'] [data-field='try-it']").click();

      cy.get(".header-cell-info").contains("Final Salary").click();
      cy.ouia({ ouiaId: "edit-expression-data-type" }).click();

      cy.ouia({ ouiaId: "expression-popover-menu" }).within(($menu) => {
        cy.ouia({ ouiaId: "tSalary" }).click({ force: true });
      });

      cy.get(".expression-container").click({ force: true });

      cy.get("[data-ouia-component-type='expression-column-header-cell-info']:contains('tSalary')").should(
        "be.visible"
      );
    });
  });

  it.skip("Change BKM Decision Table from Any to Custom Data Type", () => {
    cy.get("#upload-field").attachFile("testModelWithCustomDataType.dmn", { subjectType: "drag-n-drop" });

    // wait until loading dialog disappears
    cy.loadEditor();

    // close DMN guided tour dialog
    cy.ouiaId("dmn-guided-tour").children("button[aria-label='Close']").click();

    cy.getEditor().within(() => {
      // open decision navigator
      cy.ouiaId("docks-item-org.kie.dmn.decision.navigator").children("button").click();

      // open decision table expression
      cy.get("li[data-i18n-prefix='DecisionNavigatorTreeView.']").within(($navigator) => {
        cy.get("[title='Salary Coefficient'] div span").contains("Function").click();
      });
      // activate editor beta version
      cy.get("[data-field='beta-boxed-expression-toggle'] [data-field='try-it']").click();

      cy.get("[data-ouia-component-type='expression-column-header-cell-info']:contains('number')").should("not.exist");

      cy.get(".header-cell-info").contains("Salary Coefficient").click();
      cy.ouiaId("edit-expression-data-type").click();
      cy.ouiaId("expression-popover-menu").within(($menu) => {
        cy.ouiaId("number").click({ force: true });
      });

      cy.get("[data-ouia-component-type='expression-column-header-cell-info']:contains('number')").should("be.visible");
    });
  });

  it.skip("Change BKM Decision Table from Any to Custom Data Type", () => {
    cy.get("#upload-field").attachFile("testModelWithCustomDataType.dmn", { subjectType: "drag-n-drop" });

    // wait until loading dialog disappears
    cy.loadEditor();

    // close DMN guided tour dialog
    cy.ouiaId("dmn-guided-tour").children("button[aria-label='Close']").click();

    cy.getEditor().within(() => {
      // open decision navigator
      cy.ouiaId("docks-item-org.kie.dmn.decision.navigator").children("button").click();

      // open decision table expression
      cy.get("li[data-i18n-prefix='DecisionNavigatorTreeView.']").within(($navigator) => {
        cy.get("[title='Salary Coefficient'] div span").contains("Function").click();
      });
      // activate editor beta version
      cy.get("[data-field='beta-boxed-expression-toggle'] [data-field='try-it']").click();

      cy.get("[data-ouia-component-type='expression-column-header-cell-info']:contains('number')").should("not.exist");

      cy.get(".header-cell-info").contains("Salary Coefficient").click();
      cy.ouiaId("edit-expression-data-type").click();
      cy.ouiaId("expression-popover-menu").within(($menu) => {
        cy.ouiaId("number").click({ force: true });
      });

      cy.get("[data-ouia-component-type='expression-column-header-cell-info']:contains('number')").should("be.visible");
    });
  });
});

/**
 * Use this method to change "Insert a name" new data type entry specified on 'Data Types'
 */
function changeNewDataTypeEntry(newEntryName: string, newDataType: string): void {
  cy.ouia({ ouiaId: "Insert a name" }).within(($row) => {
    cy.get("input[data-type-field='name-input']").type(newEntryName);
    cy.get("[data-i18n-prefix='DataTypeSelectView.']").should("exist");
    cy.get("[data-i18n-prefix='DataTypeSelectView.']").should("be.visible");
    cy.get("[data-i18n-prefix='DataTypeSelectView.']").within(($navigator) => {
      cy.get("button[data-toggle='dropdown']").click();
    });
  });
  selectInDataTypesSearchableDropdown(newDataType);
  cy.ouia({ ouiaId: "Insert a name" }).within(($row) => {
    cy.get("[data-type-field='save-button']").click();
  });
}

/**
 * Use this method to change data type of entry specified on 'Data Types' DMN editor page.
 *
 * Precondition to use this method is to click:
 * - Pencil icon to start edit mode of an entry
 * - Type selectbox to display searchable dropdown
 *
 * @param entryName
 */
function selectInDataTypesSearchableDropdown(entryName: string): void {
  cy.get(".bs-searchbox input").last().type(entryName);
  cy.get("ul").find("li.active").find("a").contains(entryName).should("be.visible");
  cy.get("ul").find("li.active").find("a").contains(entryName).click();
  cy.get("ul").find("li.active").find("a").contains(entryName).should("not.exist");
}

describe.skip("DMN Expression Editor Test :: keyboard shortcuts", () => {
  beforeEach(() => {
    cy.visit("/");
  });

  it("Test New Expresssion editor - undo", () => {
    // click Create new decision model button (new DMN)
    cy.ouia({ ouiaId: "try-dmn-sample-button" }).click();

    // wait until loading dialog disappears
    cy.loadEditor();

    // close DMN guided tour dialog
    cy.ouia({ ouiaId: "dmn-guided-tour" }).children("button[aria-label='Close']").click();

    cy.getEditor().within(() => {
      // open decision navigator and check new expression editor for expressions
      cy.ouia({ ouiaId: "docks-item-org.kie.dmn.decision.navigator" }).children("button").click();

      cy.get("li[data-i18n-prefix='DecisionNavigatorTreeView.']").within(($navigator) => {
        cy.get("[title='Back End Ratio'] div span").contains("Context").click();
      });
      // check using beta version
      cy.get("[data-field='beta-boxed-expression-toggle'] [data-field='try-it']").click();
      cy.ouia({ ouiaId: "expression-container" }).contains("Back End Ratio").should("be.visible");
      cy.get(".expression-title").contains("Back End Ratio").should("be.visible");
      cy.get(".expression-type").contains("Context").should("be.visible");

      // select a cell
      cy.get(".editable-cell").contains("Applicant Data.Monthly.Repayments + Applicant Data.Monthly.Expenses").click();
      // start edit mdoe
      cy.realPress("Enter");

      // do some change - shuldn't affect even result number
      cy.realType(" + 0");

      // finish edit - commit changes
      cy.realPress("Tab");

      // check changes are present
      cy.get(".editable-cell")
        .contains("Applicant Data.Monthly.Repayments + Applicant Data.Monthly.Expenses + 0")
        .should("be.visible");

      // undo changes and check if they were really undo
      cy.realPress(["ControlLeft", "Z"]);
      cy.get(".editable-cell")
        .contains("Applicant Data.Monthly.Repayments + Applicant Data.Monthly.Expenses + 0")
        .should("not.be.visible");
    });
  });
});

describe.skip("DMN Expression Editor Test :: Properties Panel", () => {
  beforeEach(() => {
    cy.visit("/");
  });

  it("Change Decision Table Input column data type: Front_End_Ratio -> Any", () => {
    // click Create new decision model button (new DMN)
    cy.ouia({ ouiaId: "try-dmn-sample-button" }).click();

    // wait until loading dialog disappears
    cy.loadEditor();

    // close DMN guided tour dialog
    cy.ouia({ ouiaId: "dmn-guided-tour" }).children("button[aria-label='Close']").click();

    cy.getEditor().within(() => {
      // open decision navigator and check new expression editor for expressions
      cy.ouia({ ouiaId: "docks-item-org.kie.dmn.decision.navigator" }).children("button").click();

      cy.get("li[data-i18n-prefix='DecisionNavigatorTreeView.']").within(($navigator) => {
        cy.get("[title='Loan Pre-Qualification'] div span").contains("Decision Table").click();
      });
      // check using beta version
      cy.get("[data-field='beta-boxed-expression-toggle'] [data-field='try-it']").click();
      cy.ouia({ ouiaId: "expression-container" }).contains("Loan Pre-Qualification").should("be.visible");
      cy.get(".expression-title").contains("Loan Pre-Qualification").should("be.visible");
      cy.get(".expression-type").contains("Decision Table").should("be.visible");
      cy.ouia({ ouiaType: "expression-column-header" }).contains("Front_End_Ratio").should("exist");

      // open properties panel
      cy.ouia({ ouiaId: "docks-item-DiagramEditorPropertiesScreen" }).children("button").click();

      // select a cell
      cy.ouia({ ouiaType: "expression-column-header" }).contains("Front End Ratio").click();

      cy.get("[data-i18n-prefix='CollapsibleFormGroupViewImpl.']").contains("Input expression").should("be.visible");
      cy.get("[data-i18n-prefix='DataTypePickerWidget.']").get("button[data-id='typeSelector']").click();
      cy.realType("Any");
      cy.realPress("{enter}");

      cy.ouia({ ouiaType: "expression-column-header" }).contains("Front_End_Ratio").should("not.exist");
      cy.ouia({ ouiaType: "expression-column-header" }).contains("Any").should("exist");
    });
  });
});
