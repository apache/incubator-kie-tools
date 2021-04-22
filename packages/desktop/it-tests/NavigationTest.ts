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

import { Application, SpectronClient } from "spectron";
import { initApp } from "./Tools";

let app: Application;
let client: SpectronClient;

beforeEach(async () => {
  app = await initApp();
  client = app.client;
});

afterEach(async () => {
  await app.stop();
});

test("navigation", async () => {
  // check header brand
  const headerBrand = await client.$(".pf-c-brand");
  expect(await headerBrand.getAttribute("alt")).toEqual("Business Modeler Logo");
  expect(await headerBrand.getAttribute("src")).toContain("images/BusinessModeler_Logo.svg");

  // open sidebar (if it is closed)
  const sidebar = await client.$("#page-sidebar");
  const sidebarClass = await sidebar.getAttribute("class");
  if (!sidebarClass.includes("expanded")) {
    const sidebarButton = await client.$("#nav-toggle");
    await sidebarButton.click();
  }

  // open Learn more page
  const learnMoreNav = await client.$("[data-ouia-component-id='learn-more-nav']");
  await client.waitUntil(async () => await learnMoreNav.isDisplayed(), { timeout: 2000 });
  await learnMoreNav.click();

  // check Learn more cards
  const learnMoreCards = await client.$$("[data-ouia-component-type='PF4/Card'] > div > h2");
  const learMoreCardsTitles = await Promise.all(await learnMoreCards.map(async (card) => await card.getText()));
  expect(learMoreCardsTitles).toEqual(["Why BPMN?", "Why DMN?", "About Business Modeler Preview"]);

  // open Files page
  const filesNav = await client.$("[data-ouia-component-id='files-nav']");
  await filesNav.click();

  // check New files cards
  const newFileCards = await client.$$("[data-ouia-component-type='PF4/Card'] > div > h3");
  const newFileCardTitles = await Promise.all(newFileCards.map(async (card) => await card.getText()));
  expect(newFileCardTitles).toEqual([
    "Blank Workflow (.BPMN)",
    "Blank Decision Model (.DMN)",
    "Sample Workflow (.BPMN)",
    "Sample Decision Model (.DMN)",
    "Open from source",
  ]);

  // check Recent Files section
  const recentFilesSectionTitle = await client.$("[data-ouia-component-id='recent-files-section-title']");
  expect(await recentFilesSectionTitle.getText()).toEqual("Recent Files");
});
