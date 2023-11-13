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

import { Key, WebDriver, WebElement } from "selenium-webdriver";
import ErrorProcessor from "./ErrorProcessor";
import { platform } from "os";

export default class Clipboard {
  public constructor(private readonly driver: WebDriver) {}

  /**
   * Get content of clipboard.
   *
   * Chrome does not allow to get the clipboard context directly. It must be done through helper input.
   */
  public async getContent(): Promise<string> {
    const input: WebElement = await this.addHelperInputToPage();
    await this.pasteContentToHelperInput(input);
    return await this.getTextFromHelperInput();
  }

  /**
   * Set the clipboard content.
   * @param textToClipboard Text to set as the content of the clipboard.
   */
  public async setContent(textToClipboard: string): Promise<void> {
    await this.driver.executeScript("navigator.clipboard.writeText('" + textToClipboard + "');");
  }

  private async getTextFromHelperInput(): Promise<string> {
    const GET_TEXT_FROM_INPUT_CMD: string =
      "input=document.getElementById('copyPaste');" +
      "text=document.getElementById('copyPaste').value;" +
      "input.remove();" +
      "return text;";

    return await ErrorProcessor.run(
      async () => await this.driver.executeScript(GET_TEXT_FROM_INPUT_CMD),
      "Error while getting text from helper input."
    );
  }

  private async pasteContentToHelperInput(input: WebElement): Promise<void> {
    await ErrorProcessor.run(
      async () => await input.sendKeys(this.getCtrvKeys()),
      "Error while pasting contenct from clipboard to helper input by keys: " + this.getCtrvKeys()
    );
  }

  /**
   * Return Ctrl + v command for specific OS.
   * @returns Ctrl + v command.
   */
  public getCtrvKeys(): string {
    // "darwin" is  MacOS
    if (platform() === "darwin") {
      return Key.SHIFT + Key.INSERT;
    } else {
      return Key.CONTROL + "v";
    }
  }

  private async addHelperInputToPage(): Promise<WebElement> {
    const ADD_HELPER_INPUT_CMD: string =
      "input=document.createElement('input');" +
      "input.setAttribute('id','copyPaste');" +
      "return document.getElementsByTagName('body')[0].appendChild(input)";

    return await this.driver.executeScript(ADD_HELPER_INPUT_CMD);
  }
}
