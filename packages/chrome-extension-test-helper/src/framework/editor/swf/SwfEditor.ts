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
import Editor from "../Editor";
import Element from "../../Element";

export default class SwfEditor extends Editor {
  private static readonly DIAGRAM_EDITOR_LOCATOR: By = By.xpath(
    "//iframe[contains(@src, 'serverless-workflow-diagram-editor-envelope.html')]"
  );
  private static readonly EDITOR_TEXTAREA_LOCATOR: By = By.xpath("//div[contains(@class, 'monaco-editor')]//textarea");
  private static readonly KEYBOARD_SHORTCUTS_ICON_LOCATOR: By = By.xpath(
    "//div[@data-ouia-component-id='keyboard-shortcuts-icon']"
  );
  private static readonly TEXT_EDITOR_LOCATOR: By = By.xpath(
    "//iframe[contains(@src, 'serverless-workflow-text-editor-envelope.html')]"
  );

  public async isTextEditorPresent(): Promise<boolean> {
    return await this.tools.by(SwfEditor.TEXT_EDITOR_LOCATOR).wait(1000).isPresent();
  }

  public async isDiagramEditorPresent(): Promise<boolean> {
    return await this.tools.by(SwfEditor.DIAGRAM_EDITOR_LOCATOR).wait(1000).isPresent();
  }

  public async isTextEditorKeyboardShortcutsIconPresent(): Promise<boolean> {
    return await this.isKeyboardShortcutsIconPresent(SwfEditor.TEXT_EDITOR_LOCATOR);
  }

  public async isDiagramEditorKeyboardShortcutsIconPresent(): Promise<boolean> {
    return await this.isKeyboardShortcutsIconPresent(SwfEditor.DIAGRAM_EDITOR_LOCATOR);
  }

  public async getTextEditorContent(): Promise<string> {
    await this.enterIFrame(SwfEditor.TEXT_EDITOR_LOCATOR);
    const textEditor: Element = await this.tools.by(SwfEditor.EDITOR_TEXTAREA_LOCATOR).getElement();
    const content: string = await textEditor.getAttribute("value");
    await this.leaveIFrame();
    return content;
  }

  private async enterIFrame(iframeBy: By): Promise<void> {
    const iFrame: Element = await this.tools.by(iframeBy).getElement();
    await iFrame.enterFrame();
  }

  private async leaveIFrame(): Promise<void> {
    await this.tools.window().leaveFrame();
    await this.enter();
  }

  private async isKeyboardShortcutsIconPresent(iFrameBy: By): Promise<boolean> {
    await this.enterIFrame(iFrameBy);
    const isPresent: boolean = await this.tools.by(SwfEditor.KEYBOARD_SHORTCUTS_ICON_LOCATOR).wait(1000).isPresent();
    await this.leaveIFrame();
    return isPresent;
  }
}
