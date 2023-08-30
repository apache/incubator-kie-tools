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

export default class DmnPalette extends PageFragment {
  private static readonly ANNOTATION_LOCATOR: By = By.xpath("//button[@title='DMN Text Annotation']");

  public async waitUntilLoaded(): Promise<void> {
    return await this.tools.by(DmnPalette.ANNOTATION_LOCATOR).wait(1000).untilPresent();
  }

  public async dragAndDropAnnotationToCanvas(): Promise<void> {
    // click annotation
    const annotation = await this.tools.by(DmnPalette.ANNOTATION_LOCATOR).getElement();

    // move to canvas
    await annotation.dragAndDrop(200, 0);

    // click to canvas
    return await annotation.offsetClick(100, 0);
  }
}
