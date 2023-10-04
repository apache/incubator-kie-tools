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

import { assert } from "chai";
import { By, WebElement } from "vscode-extension-tester";
import Correlation from "./Correlation";

/**
 * Class for accessing expanded BPMN Diagram explorer panel.
 */
export default class CorrelationModalHelper {
  constructor(private readonly root: WebElement) {}

  public async closeModal(): Promise<void> {
    const closeBtn = await this.root.findElement(By.xpath(".//button[@data-dismiss='modal']"));
    await closeBtn.click();
  }

  public async getCorrelations(): Promise<Correlation[]> {
    const correlationRows = await this.root.findElements(By.xpath(".//tr[@id='correlation']"));
    let correlations: Correlation[] = [];
    for (const row of correlationRows) {
      const idInput = await row.findElement(By.xpath(".//input[@data-field='id']"));
      const nameInput = await row.findElement(By.xpath(".//input[@data-field='name']"));
      const propertyIdInput = await row.findElement(By.xpath(".//input[@data-field='propertyId']"));
      const propertyNameInput = await row.findElement(By.xpath(".//input[@data-field='propertyName']"));
      const propertyTypeSelect = await row.findElement(By.xpath(".//select[@data-field='propertyType']/option"));

      const correlationId = await idInput.getAttribute("value");
      const correlationName = await nameInput.getAttribute("value");
      const propertyId = await propertyIdInput.getAttribute("value");
      const propertyName = await propertyNameInput.getAttribute("value");
      const propertyType = await propertyTypeSelect.getText();

      correlations.push(new Correlation(correlationId, correlationName, propertyId, propertyName, propertyType));
    }

    return correlations;
  }

  public async assertCorrelationsSize(expected: number): Promise<void> {
    const correlations = await this.getCorrelations();
    assert.equal(correlations.length, expected, "Expected " + expected + "variables, but found " + correlations.length);
  }

  /**
   * Asserts that current correlations contain expected correlation.
   *
   * @param tested expected Correlation object
   * @returns nothing but fails if the data input is not found.
   */
  public async assertCorrelationsContain(tested: Correlation): Promise<void> {
    const correlations = await this.getCorrelations();
    for (const correlation of correlations) {
      if (
        tested.getId() === correlation.getId() &&
        tested.getName() === correlation.getName() &&
        tested.getPropertyId() === correlation.getPropertyId() &&
        tested.getPropertyName() === correlation.getPropertyName() &&
        tested.getPropertyType() === correlation.getPropertyType()
      ) {
        return;
      }
    }

    assert.fail(
      "Did not find correlation with id: [" +
        tested.getId() +
        "] and name: [" +
        tested.getName() +
        "] and property id: [" +
        tested.getPropertyId() +
        "] and property name: [" +
        tested.getPropertyName() +
        "] and property type: [" +
        tested.getPropertyType() +
        "]."
    );
  }
}
