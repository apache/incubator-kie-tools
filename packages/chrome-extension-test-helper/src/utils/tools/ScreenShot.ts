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

import { existsSync, mkdirSync, writeFileSync } from "fs";
import ErrorProcessor from "./ErrorProcessor";
import { WebDriver } from "selenium-webdriver";
import { join } from "path";

export default class Screenshots {
  constructor(private readonly driver: WebDriver, private readonly screenshotsDir: string) {
    if (!existsSync(screenshotsDir)) {
      mkdirSync(screenshotsDir, { recursive: true });
    }
  }

  public async takePng(fileName: string): Promise<void> {
    const image: string = await ErrorProcessor.run(
      async () => await this.driver.takeScreenshot(),
      "Error while taking png screenshot with name: " + fileName
    );
    const pngPath = join(this.screenshotsDir, fileName + ".png");
    return writeFileSync(pngPath, image, "base64");
  }

  public async takeHtml(fileName: string): Promise<void> {
    const pageSource: string = await ErrorProcessor.run(
      async () => await this.driver.getPageSource(),
      "Error while getting page source with name: " + fileName
    );
    const htmlPath = join(this.screenshotsDir, fileName + ".html");
    return writeFileSync(htmlPath, pageSource, "utf8");
  }
}
