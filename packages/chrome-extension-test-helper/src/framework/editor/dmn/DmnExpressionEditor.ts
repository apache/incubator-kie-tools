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

import { By } from "selenium-webdriver";
import PageFragment from "../../PageFragment";
import Element from "../../Element";
import { assert } from "chai";

export default class DmnExpressionEditor extends PageFragment {
  private static readonly RETURN_TO_GRAPH_LOCATOR: By = By.className("kie-dmn-return-to-link");
  private static readonly BETA_VERSION_ACTIVATOR_LOCATOR: By = By.xpath("//a[@data-field='try-it']");
  private static readonly BETA_VERSION_INDICATOR_LOCATOR: By = By.className("beta-badge");

  public async waitUntilLoaded(): Promise<void> {
    return await this.tools.by(DmnExpressionEditor.RETURN_TO_GRAPH_LOCATOR).wait(1000).untilPresent();
  }

  public async activateBetaVersion(): Promise<void> {
    const activateBetaButton: Element = await this.tools
      .by(DmnExpressionEditor.BETA_VERSION_ACTIVATOR_LOCATOR)
      .getElement();
    await activateBetaButton.click();
    return await this.tools.by(DmnExpressionEditor.BETA_VERSION_INDICATOR_LOCATOR).wait(1000).untilPresent();
  }

  public async assertExpressionIsPresent(expectedTitle: string, expectedType: string): Promise<void> {
    await this.tools
      .by(
        By.xpath(`//div[@data-ouia-component-type='expression-column-header-cell-info']/p[text()='${expectedTitle}']`)
      )
      .wait(1000)
      .untilPresent();
    const type: Element = await this.tools
      .by(
        By.xpath(
          `//div[@data-ouia-component-type='expression-column-header-cell-info']/p[contains(@class, 'data-type')]`
        )
      )
      .getElement();
    assert.equal("(" + expectedType + ")", await type.getText());
    return;
  }
}
