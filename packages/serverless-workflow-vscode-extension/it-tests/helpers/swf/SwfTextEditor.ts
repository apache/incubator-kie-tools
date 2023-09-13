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

import { sleep } from "@kie-tools/vscode-extension-common-test-helpers";
import { expect } from "chai";
import { TextEditor } from "vscode-extension-tester";

/**
 * Helper class which represents Serverless Workflow text editor.
 */
export default class SwfTextEditor extends TextEditor {
  constructor() {
    super();
  }

  /**
   * Selects a value from the content assist.
   *
   * @param value The value to be selected.
   */
  public async selectFromContentAssist(value: string): Promise<void> {
    try {
      const contentAssist = await this.toggleContentAssist(true);
      const item = await contentAssist?.getItem(value);
      await sleep(1000);
      expect(await item?.getLabel()).contain(value);
      await item?.click();
    } catch (e) {
      throw new Error(
        `The ContentAssist menu is not available or it was not possible to select the element '${value}'!`
      );
    }
  }
}
