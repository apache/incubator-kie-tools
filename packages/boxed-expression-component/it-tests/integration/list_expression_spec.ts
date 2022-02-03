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

import * as buildEnv from "@kie-tools/build-env";

describe("List Expression Tests", () => {
  beforeEach(() => {
    cy.visit(`http://localhost:${buildEnv.boxedExpressionComponent.dev.port}/`);
  });

  it("Define List expression", () => {
    // Entry point for each new expression
    cy.ouiaId("expression-container").click();

    // Define new expression as List
    cy.ouiaId("expression-popover-menu").contains("List").click({ force: true });

    // Assert some content
    cy.ouiaId("expression-row-0").should("contain.text", "1");
  });
});
