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

import { until, WebElement } from "vscode-extension-tester";
import { assertWebElementIsDisplayedEnabled } from "@kie-tools/vscode-extension-common-test-helpers";
import { aComponentWithText } from "../CommonLocators";

/**
 * Class for accessing expanded BPMN Diagram explorer panel.
 */
export default class DiagramExplorerHelper {
  constructor(private readonly root: WebElement) {}

  /**
   * Selects a BPMN diagram node by click it the BPMN Diagram Explorer panel.
   *
   * @param nodeName node name to select
   */
  public async selectDiagramNode(nodeName: string): Promise<DiagramExplorerHelper> {
    const node: WebElement = await this.getDiagramNode(nodeName);
    await node.click();

    return this;
  }

  /**
   * Check if a given node is present in the BPMN Diagram explorer panel
   *
   * @param nodeName node name that will be asserted
   */
  public async assertDiagramNodeIsPresent(nodeName: string): Promise<void> {
    await assertWebElementIsDisplayedEnabled(await this.getDiagramNode(nodeName));
  }

  private async getDiagramNode(nodeName: string): Promise<WebElement> {
    return this.root
      .getDriver()
      .wait(until.elementLocated(aComponentWithText(nodeName)), 5000, `${nodeName} not found in 5 seconds`);
  }
}
