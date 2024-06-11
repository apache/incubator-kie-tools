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

import { By, WebElement } from "vscode-extension-tester";
import {
  addGlobalVariableButton,
  addProcessVariableButton,
  globalVariableDataTypeInput,
  globalVariableNameInput,
  globalVariablesDiv,
  processVariableDataTypeInput,
  processVariableNameInput,
  propertiesSectionAnchor,
} from "./BpmnLocators";
import { labeledAnyElementInPropertiesPanel } from "../CommonLocators";
import { assert } from "chai";
import PropertiesPanelHelper from "./PropertiesPanelHelper";

export default class ImplementationExecutionHelper extends PropertiesPanelHelper {
  constructor(root: WebElement) {
    super(root);
  }

  public async getMIDataInputWidget(): Promise<MIDataIOWidget> {
    const newMIDataInput = new MIDataIOWidget(
      this.root.findElement(labeledAnyElementInPropertiesPanel("MI Data Input", "div"))
    );

    return newMIDataInput;
  }

  public async getMIDataOutputWidget(): Promise<MIDataIOWidget> {
    const newMIDataOutput = new MIDataIOWidget(
      this.root.findElement(labeledAnyElementInPropertiesPanel("MI Data Output", "div"))
    );

    return newMIDataOutput;
  }
}

export class MIDataIOWidget {
  constructor(private readonly root: WebElement) {}

  public async setMIDataInput(value: string) {
    const property = await this.root.findElement(By.xpath(".//input"));
    await property.clear();
    await property.sendKeys(value);

    return this;
  }

  public async setMIDataInputDataType(datatype: string) {
    const property = await this.root.findElement(By.xpath(".//select"));
    await property.click();
    const option = await property.findElement(By.xpath(".//option[@value='" + datatype + "']"));
    await option.click();

    return this;
  }

  public async assertMiDataInput(expectedInputValue: string, expectedDataTypeValue: string) {
    const inputProperty = await this.root.findElement(By.xpath(".//input"));
    const actualInputValue = await inputProperty.getAttribute("value");

    const dataTypeProperty = await this.root.findElement(By.xpath(".//select"));
    const actualDataTypeValue = await dataTypeProperty.getAttribute("value");

    assert.equal(
      actualInputValue,
      expectedInputValue,
      "Value of MI Data Input property did not match the expected value. Actual value is [" +
        actualInputValue +
        "]. Expected value is [" +
        expectedInputValue +
        "]"
    );

    assert.equal(
      actualDataTypeValue,
      expectedDataTypeValue,
      "Data type of MI Data Input property did not match the expected value. Actual value is [" +
        actualDataTypeValue +
        "]. Expected value is [" +
        expectedDataTypeValue +
        "]"
    );
  }
}
