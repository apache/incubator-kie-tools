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

import { By } from "selenium-webdriver";
import PageFrament from "../PageFragment";

export default class Properties extends PageFrament {
  private static readonly LABEL_LOCATOR = By.xpath("//h3[text()='Properties']");

  public async waitUntilLoaded(): Promise<void> {
    return await this.tools.by(Properties.LABEL_LOCATOR).wait(1000).untilPresent();
  }

  private getProperty(type: string, nameAttributeSuffix: string): By {
    return By.xpath(`//${type}[contains(@name, '${nameAttributeSuffix}')]`);
  }

  private async getValue(type: string, nameAttributeSuffix: string): Promise<string> {
    return await this.tools.by(this.getProperty(type, nameAttributeSuffix)).wait(2000).untilHasValue();
  }

  public async getNameFromTextArea(): Promise<string> {
    return await this.getValue("textarea", ".general.name");
  }

  public async getProcessNameFromInput(): Promise<string> {
    return await this.getValue("input", "diagramSet.name");
  }

  public async getDmnNameFromInput(): Promise<string> {
    return await this.getValue("input", ".nameHolder");
  }
}
