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
import DmnPalette from "./DmnPalette";
import DmnSideBar from "./DmnSideBar";
import Editor from "../Editor";
import Element from "../../Element";
import Locator from "../../Locator";
import DecisionNavigator from "./DecisionNavigator";
import DmnExpressionEditor from "./DmnExpressionEditor";

export default class DmnEditor extends Editor {
  private static readonly PALETTE_LOCATOR: By = By.className("kie-palette");
  private static readonly EXPRESSION_EDITOR_LOCATOR: By = By.id("dmn-expression-editor-container");
  private static readonly SIDE_BAR_LOCATOR: By = By.className("collapsed-docks-bar-E");
  private static readonly LEFT_SIDE_BAR_LOCATOR: By = By.className("collapsed-docks-bar-W");
  private static readonly DECISION_GRAPH_LOCATOR: By = By.id("decision-graphs-content");

  public async getDmnPalette(): Promise<DmnPalette> {
    const palette: Element = await this.tools.by(DmnEditor.PALETTE_LOCATOR).getElement();
    return await this.tools.createPageFragment(DmnPalette, palette);
  }

  public async getExpressionEditor(): Promise<DmnExpressionEditor> {
    const palette: Element = await this.tools.by(DmnEditor.EXPRESSION_EDITOR_LOCATOR).getElement();
    return await this.tools.createPageFragment(DmnExpressionEditor, palette);
  }

  public async getSideBar(): Promise<DmnSideBar> {
    const sideBar: Locator = this.tools.by(DmnEditor.SIDE_BAR_LOCATOR);
    await sideBar.wait(1000).untilPresent();
    return await this.tools.createPageFragment(DmnSideBar, await sideBar.getElement());
  }

  public async openLeftSideBar(): Promise<DecisionNavigator> {
    const leftSideBarButton = await this.tools.by(DmnEditor.LEFT_SIDE_BAR_LOCATOR).getElement();
    await leftSideBarButton.click();
    return await this.tools.createPageFragment(
      DecisionNavigator,
      await this.tools.by(DmnEditor.DECISION_GRAPH_LOCATOR).getElement()
    );
  }

  public async dragAndDropAnnotationToCanvas(): Promise<void> {
    const dmnPalette: DmnPalette = await this.getDmnPalette();
    return await dmnPalette.dragAndDropAnnotationToCanvas();
  }
}
