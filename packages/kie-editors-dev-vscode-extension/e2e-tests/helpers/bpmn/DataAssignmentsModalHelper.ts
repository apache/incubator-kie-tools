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
import DataInputAssignment from "./DataInputAssignment";

/**
 * Class for accessing Data Assignment modal.
 * Create the class using constructor and use getDataInputAssignments()
 * or directly use the assert method.
 */
export default class DataAssignmentsModalHelper {
  dataInputAssignmentWidget: WebElement;
  dataOutpuAssignmentWidget: WebElement;

  constructor(private readonly root: WebElement) {}

  public async closeModal(): Promise<void> {
    const closeBtn = await this.root.findElement(By.xpath(".//button[@data-dismiss='modal']"));
    await closeBtn.click();
  }

  private async getDataAssignmentWidgets(): Promise<void> {
    const assignmentWidgets = await this.root.findElements(
      By.xpath(".//div[@data-i18n-prefix='ActivityDataIOEditorWidget.']")
    );
    this.dataInputAssignmentWidget = assignmentWidgets[0];
    this.dataOutpuAssignmentWidget = assignmentWidgets[1];
  }

  public async getDataInputAssingments(): Promise<DataInputAssignment[]> {
    await this.getDataAssignmentWidgets();
    const assignmentRows = await this.dataInputAssignmentWidget.findElements(By.xpath(".//tr[@id='assignment']"));
    let dataInputAssignments: DataInputAssignment[] = [];
    for (const row of assignmentRows) {
      const rowNameInput = await row.findElement(By.xpath(".//input"));
      const rowDataTypeSelect = await row.findElement(By.xpath(".//select[@data-field='dataType']/option"));
      const rowSourceSelect = await row.findElement(By.xpath(".//select[@data-field='processVar']/option"));
      const rowName = await rowNameInput.getAttribute("value");
      const rowDataType = await rowDataTypeSelect.getText();
      const rowSource = await rowSourceSelect.getText();

      dataInputAssignments.push(new DataInputAssignment(rowName, rowDataType, rowSource));
    }

    return dataInputAssignments;
  }

  /**
   * Asserts that current data input assignments contain expected assignment.
   *
   * @param name of the input assignment
   * @param dataType of the input assignment
   * @param source of the input assignment
   * @returns nothing but fails if the data input is not found.
   */
  public async assertDataInputContain(name: string, dataType: string, source: string): Promise<void> {
    const dataInputAssignments = await this.getDataInputAssingments();
    for (const assignment of dataInputAssignments) {
      if (name === assignment.getName() && dataType === assignment.getDataType() && source === assignment.getSource()) {
        return;
      }
    }

    assert.fail(
      "Did not find data input assignment with name: [" +
        name +
        "] and datatype: [" +
        dataType +
        "] and source: [" +
        source +
        "]."
    );
  }
}
