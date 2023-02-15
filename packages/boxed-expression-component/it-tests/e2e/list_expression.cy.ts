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

import { env } from "../../env";
const buildEnv = env;

describe("List Expression Tests", () => {
  beforeEach(() => {
    cy.visit(`http://localhost:${buildEnv.boxedExpressionComponent.dev.port}/`);

    // Entry point for each new expression
    cy.ouiaId("expression-container").click();

    // Define new expression as List
    cy.ouiaId("expression-popover-menu").contains("List").click({ force: true });
  });

  it("Define List expression", () => {
    // Assert some content
    cy.ouiaId("expression-row-0").should("contain.text", "1");
  });

  it("Regression tests: focus on the first data cell", () => {
    cy.ouiaId("expression-column-1").focus().wait(0);

    // check the snapshot for regression
    cy.matchImageSnapshot("data_cell_focus");
  });
});
