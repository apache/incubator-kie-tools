/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
import { WebElement } from "vscode-extension-tester";

/**
 * Asserts that a provided WebElement is displayed and enabled.
 * 
 * @param tested WebElement to test
 */
export const assertWebElementIsDisplayedEnabled = async (tested: WebElement): Promise<void> => {
    assert.isTrue(await tested.isDisplayed() && await tested.isEnabled());
}