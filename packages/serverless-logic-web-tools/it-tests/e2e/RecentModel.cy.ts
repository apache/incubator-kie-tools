/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
import { deleteAllIndexedDBs } from "../support/e2e";

describe("Serverless Logic Web Tools - Recent model test", () => {
  beforeEach(() => {
    deleteAllIndexedDBs();
    cy.visit("/");
  });

  it("should create, edit, save and delete file", () => {
    // create new empty json file
    cy.ouia({ ouiaId: "new-sw.json-button" }).click();
    cy.loadEditor();

    // add some content
    cy.getEditor().within(() => {
      cy.iframe("#kogito-iframe[src='./serverless-workflow-text-editor-envelope.html']").within(() => {
        cy.get(".monaco-editor textarea").type("//test");
      });
    });

    // rename file and save
    cy.wait(1000); // there must be a pause otherwise the file is not renamed
    cy.ouia({ ouiaId: "file-name-input" }).type("{selectAll}testJsonFile{enter}");
    cy.ouia({ ouiaId: "kebab-sm" }).click();
    cy.ouia({ ouiaId: "commit-button" }).click();
    cy.ouia({ ouiaId: "commit-created-alert" }).should("be.visible");

    // open again from main page
    cy.ouia({ ouiaId: "app-title" }).click();
    cy.goToSidebarLink({ ouiaId: "recent-models-nav" });

    cy.ouia({ ouiaId: "OUIA-Generated-TableRow-2" })
      .find("[data-label='Name'] > a")
      .eq(0)
      .should(($item) => expect($item.text().trim()).equal("testJsonFile"))
      .click();

    // check file was saved
    cy.getEditor().within(() => {
      cy.iframe("#kogito-iframe[src='./serverless-workflow-text-editor-envelope.html']").within(() => {
        cy.get(".monaco-editor textarea").should("have.value", "//test");
      });
    });

    // delete file
    cy.ouia({ ouiaId: "kebab-sm" }).click();
    cy.ouia({ ouiaId: "delete-file-button" }).click();

    // check the file is deleted (recent section is emtpy)
    cy.goToSidebarLink({ ouiaId: "recent-models-nav" });
    cy.get(".pf-l-bullseye").should("contain.text", "Nothing here");
  });
});
