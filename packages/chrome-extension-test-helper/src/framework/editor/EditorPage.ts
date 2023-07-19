/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import BpmnEditor from "./bpmn/BpmnEditor";
import { By } from "selenium-webdriver";
import DmnEditor from "./dmn/DmnEditor";
import Element from "../Element";
import Page from "../Page";
import Locator from "../Locator";

export default abstract class EditorPage extends Page {
  protected static readonly FRAME_LOCATOR = By.xpath(
    "//iframe[contains(@class,'kogito-iframe') or contains(@id,'kogito-iframe')]"
  );

  private async getEditor(): Promise<Element> {
    const frameLocator: Locator = this.tools.by(EditorPage.FRAME_LOCATOR);
    await frameLocator.wait(2000).untilPresent();
    const frame: Element = await frameLocator.getElement();
    await frame.scroll();
    return frame;
  }

  public async getDmnEditor(): Promise<DmnEditor> {
    const editor: Element = await this.getEditor();
    return await this.tools.createPageFragment(DmnEditor, editor);
  }

  public async getBpmnEditor(): Promise<BpmnEditor> {
    const editor: Element = await this.getEditor();
    return await this.tools.createPageFragment(BpmnEditor, editor);
  }
}
