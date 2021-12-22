/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
import { assertWebElementIsDisplayedEnabled } from "../CommonAsserts";
import { BuiltInType } from "./BuiltInType";

/**
 * Class for accessing new DMN Expression editor
 *
 * Helper methods will not work until `activateBetaVersion` is called.
 */
export default class ExpressionEditorHelper {
  constructor(private readonly root: WebElement) {}

  /**
   * Activates the beta version of the editor
   * By default the stable is activated
   */
  public async activateBetaVersion(): Promise<ExpressionEditorHelper> {
    const button = await this.root.findElement(
      By.xpath("//div[@data-field='beta-boxed-expression-toggle']/span/a[@data-field='try-it']")
    );
    await button.click();
    return this;
  }

  public async assertExpressionDetails(
    expectedTitle: string,
    expectedType: BuiltInType
  ): Promise<ExpressionEditorHelper> {
    const boxedExpressionEditorContainer = await this.root.findElement(
      By.xpath("//div[@data-field='dmn-new-expression-editor']")
    );
    const boxedExpressionEditor = await boxedExpressionEditorContainer.findElement(
      By.className("boxed-expression-provider")
    );
    const title = await boxedExpressionEditor.findElement(
      By.xpath(`//div[@data-ouia-component-type='expression-column-header-cell-info']/p[text()='${expectedTitle}']`)
    );
    await assertWebElementIsDisplayedEnabled(title);

    const type = await boxedExpressionEditor.findElement(
      By.xpath(`//div[@data-ouia-component-type='expression-column-header-cell-info']/p[contains(@class, 'data-type')]`)
    );
    await assertWebElementIsDisplayedEnabled(type);
    assert.equal(await type.getText(), `(${expectedType})`);

    return this;
  }
}
