/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { assert } from "chai";
import { By, WebElement } from "vscode-extension-tester";
import { sleep } from "@kie-tools/vscode-extension-common-test-helpers";
import ProcessVariable from "./ProcessVariable";

/**
 * Class for accessing and asserting process variables in Process Variables Widget.
 */
export default class ProcessVariablesWidgetHelper {
  constructor(private readonly root: WebElement) {}

  public async getProcessVariables(): Promise<ProcessVariable[]> {
    const processVariableRows = await this.root.findElements(By.id("variableRow"));
    await this.scrollPropertyIntoView(processVariableRows[processVariableRows.length - 1]);
    let processVariables: ProcessVariable[] = [];
    for (const variableRow of processVariableRows) {
      const varNameInput = await variableRow.findElement(By.xpath(".//input[@data-field='name']"));
      const varName = await varNameInput.getAttribute("value");
      const varDataTypeSelect = await variableRow.findElement(By.xpath(".//select[@data-field='dataType']/option"));
      const varDataType = await varDataTypeSelect.getText();

      processVariables.push(new ProcessVariable(varName, varDataType));
    }

    return processVariables;
  }

  public async assertProcessVariablesContain(name: String, dataType: String): Promise<void> {
    const variables = await this.getProcessVariables();
    for (const variable of variables) {
      if (name === variable.getName() && dataType === variable.getDataType()) {
        return;
      }
    }

    assert.fail("Did not find process variable with name: [" + name + "] and datatype: [" + dataType + "]");
  }

  public async assertProcessVariablesSize(exepected: number): Promise<void> {
    const variables = await this.getProcessVariables();
    assert.equal(variables.length, exepected, "Expected " + exepected + "variables, but found " + variables.length);
  }

  /**
   * Scrolls desired property element into view.
   *
   * @param propertyElement element that is to be scrolled into view
   */
  private async scrollPropertyIntoView(propertyElement: WebElement): Promise<void> {
    const driver = propertyElement.getDriver();
    await driver.executeScript("arguments[0].scrollIntoView({ behavior: 'auto', block: 'end'});", propertyElement);
    await sleep(500);
  }
}
