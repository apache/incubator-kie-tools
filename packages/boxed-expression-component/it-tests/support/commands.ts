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

// ***********************************************
// This example commands.js shows you how to
// create various custom commands and overwrite
// existing commands.
//
// For more comprehensive examples of custom
// commands please read more here:
// https://on.cypress.io/custom-commands
// ***********************************************
//
//
// -- This is a parent command --
// Cypress.Commands.add("login", (email, password) => { ... })
//
//
// -- This is a child command --
// Cypress.Commands.add("drag", { prevSubject: 'element'}, (subject, options) => { ... })
//
//
// -- This is a dual command --
// Cypress.Commands.add("dismiss", { prevSubject: 'optional'}, (subject, options) => { ... })
//
//
// -- This will overwrite an existing command --
// Cypress.Commands.overwrite("visit", (originalFn, url, options) => { ... })

import { addMatchImageSnapshotCommand } from "cypress-image-snapshot/command";

addMatchImageSnapshotCommand({
  customSnapshotsDir: "../it-tests/cypress/snapshots",
  blackout: [".updated-json"],
  failureThreshold: 0.3, // threshold for entire image
  failureThresholdType: "percent", // percent of image or number of pixels
  capture: "fullPage",
});

Cypress.Commands.add("ouiaId", { prevSubject: "optional" }, (subject, id: string, options = {}) => {
  const idSelector = id ? `[data-ouia-component-id='${id}']` : "";
  let el;
  if (subject) {
    el = cy.wrap(subject, options).find(idSelector, options);
  } else {
    el = cy.get(idSelector, options);
  }
});

Cypress.Commands.add("ouiaType", { prevSubject: "optional" }, (subject, type: string, options = {}) => {
  const typeSelector = type ? `[data-ouia-component-type='${type}']` : "";
  let el;
  if (subject) {
    el = cy.wrap(subject, options).find(typeSelector, options);
  } else {
    el = cy.get(typeSelector, options);
  }
});
