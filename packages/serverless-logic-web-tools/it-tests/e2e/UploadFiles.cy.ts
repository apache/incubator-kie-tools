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

describe("Serverless Logic Web Tools - Upload files test", () => {
  beforeEach(() => {
    cy.visit("/");
  });

  it("should upload dashbuider file and check rendered content", () => {
    // upload dashbuilder file
    cy.get("#upload-field").attachFile("uploadFile/helloDashbuilder.dash.yaml", { subjectType: "drag-n-drop" });
    cy.loadEditor();

    // check header labels
    cy.ouia({ ouiaId: "file-name-input" }).should("have.value", "helloDashbuilder");
    cy.ouia({ ouiaId: "file-type-label" }).should("have.text", "Dashboard");

    // check dashbuilder editor and diagram
    cy.getEditor().within(() => {
      cy.get(".monaco-editor textarea")
        .should("contain.value", "pages:")
        .should("contain.value", "- components:")
        .should("contain.value", '- html: <b data-ouia-component-id="hello-text">Hello</b> Dashbuilder!');

      cy.iframe("iframe[src='dashbuilder-client/index.html']").within(() => {
        cy.get("#mainContainer").should("have.text", "Hello Dashbuilder!");
        cy.ouia({ ouiaId: "hello-text" }).should("have.text", "Hello");
      });
    });

    // check there are no problems in dashbuilder file
    cy.get("#total-notifications").should("have.text", 0);
  });

  it("should upload 2 files (JSON and YAML) and check editors and diagrams content", () => {
    // upload JSON and YAML files
    cy.get("#upload-field").attachFile(
      ["uploadFile/helloJsonWorkflow.sw.json", "uploadFile/helloYamlWorkflow.sw.yaml"],
      { subjectType: "drag-n-drop" }
    );
    cy.loadEditor();

    // check JSON header labels
    cy.ouia({ ouiaId: "file-name-input" }).should("have.value", "helloJsonWorkflow");
    cy.ouia({ ouiaId: "file-type-label" }).should("have.text", "Serverless Workflow");
    cy.ouia({ ouiaId: "directory-name-input" }).should("have.value", "Untitled Folder");

    cy.getEditor().within(() => {
      // check JSON editor
      cy.iframe("#kogito-iframe[src='./serverless-workflow-text-editor-envelope.html']").within(() => {
        cy.get(".monaco-editor textarea")
          .should("contain.value", '"id": "jsonhello')
          .should("contain.value", '"name": "Hello JSON workflow",')
          .should("contain.value", '"description": "JSON based hello workflow",')
          .should("contain.value", '"start": "helloJsonState",');
      });

      // check JSON diagram
      cy.iframe("#kogito-iframe[src='./serverless-workflow-diagram-editor-envelope.html']").within(() => {
        cy.get("#canvasPanel").should("contain.html", "<canvas id");
      });
    });

    // check there are no problems in JSON file
    cy.get("#total-notifications").should("have.text", 0);

    // switch to YAML file
    cy.ouia({ ouiaId: "file-type-label" }).click();
    cy.get("a[href$='/file/helloYamlWorkflow.sw.yaml']").click();

    // wait until file is switched
    cy.get("a[download]").eq(0).should("have.attr", "download", "helloYamlWorkflow.sw.yaml");
    cy.loadEditor();

    // check YAML header labels
    cy.ouia({ ouiaId: "file-name-input" }).should("have.value", "helloYamlWorkflow");
    cy.ouia({ ouiaId: "file-type-label" }).should("have.text", "Serverless Workflow");
    cy.ouia({ ouiaId: "directory-name-input" }).should("have.value", "Untitled Folder");

    cy.getEditor().within(() => {
      // check YAML editor
      cy.iframe("#kogito-iframe[src='./serverless-workflow-text-editor-envelope.html']").within(() => {
        cy.get(".monaco-editor textarea")
          .should("contain.value", 'id: "yamlhello"')
          .should("contain.value", 'name: "Hello YAML workflow"')
          .should("contain.value", 'description: "YAML based hello workflow"')
          .should("contain.value", 'start: "helloYamlState"');
      });

      // check YAML diagram state
      cy.iframe("#kogito-iframe[src='./serverless-workflow-diagram-editor-envelope.html']").within(() => {
        cy.get("#canvasPanel").should("contain.html", "<canvas id");
      });
    });

    // check there are no problems in YAML file
    cy.get("#total-notifications").should("have.text", 0);
  });
});
