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

describe("Serverless Logic Web Tools - Settings test", () => {
  beforeEach(() => {
    cy.visit("/");
  });

  it("should check settings button is visible", () => {
    cy.ouia({ ouiaId: "settings-button" }).should("exist");
    cy.ouia({ ouiaId: "settings-button" }).should("be.visible");
    cy.ouia({ ouiaId: "settings-button" }).should("be.enabled");
  });

  it("should navigate to settings page", () => {
    cy.ouia({ ouiaId: "settings-button" }).click();
    cy.ouia({ ouiaId: "OUIA-Generated-Button-plain-1" }).click();

    cy.get(".chr-c-app-title").should("exist");
    cy.get(".chr-c-app-title").should("have.text", "Settings");
    cy.get(".chr-c-app-title").should("be.visible");
  });

  it("should check that expected settings are available", () => {
    cy.ouia({ ouiaId: "settings-button" }).click();
    // Open sidebar - the tests run on small screen
    cy.ouia({ ouiaId: "OUIA-Generated-Button-plain-1" }).click();

    // Get the sidebar element by its ID.
    cy.get("#page-sidebar")
      .should("be.visible")
      .within(() => {
        // Verify the title within the sidebar.
        cy.get(".chr-c-app-title").should("be.visible").and("have.text", "Settings");

        cy.get("ul.pf-v5-c-nav__list li.pf-v5-c-nav__item a.pf-v5-c-nav__link")
          .should("have.length", 6) // Verify the number of navigation items.
          .then(($items) => {
            // Expected items in expected order
            const expectedItems = [
              "GitHub",
              "OpenShift",
              "Service Account",
              "Service Registry",
              "Storage",
              "Runtime Tools",
            ];

            // Iterate over the found items and assert their text against the expected list.
            cy.wrap($items).each(($item, index) => {
              cy.wrap($item).should("have.text", expectedItems[index]);
            });
          });
      });
  });
});
