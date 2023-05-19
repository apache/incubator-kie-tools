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

import { assert, AssertionError } from "chai";
import { WebElement } from "vscode-extension-tester";

/**
 * Asserts that a provided WebElement is displayed and enabled.
 *
 * @param tested WebElement to test
 */
export const assertWebElementIsDisplayedEnabled = async (tested: WebElement): Promise<void> => {
  assert.isTrue((await tested.isDisplayed()) && (await tested.isEnabled()));
};

/**
 * Asserts that a provided WebElement had an attribute of a expected value.
 *
 * @param tested WebElement to test
 * @param attribute string reprsentation of the attribute - "value"
 * @param attributeValue expected value of the attribute as string
 */
export const assertWebElementWithAtribute = async (
  tested: WebElement,
  attribute: string,
  attributeValue: string
): Promise<void> => {
  assert.equal(await tested.getAttribute(attribute), attributeValue);
};

/**
 * Asserts the provided variable is defined
 *
 * @param value Variable to check
 * @param valueDescriptor Variable descriptor printed in case of assertion fail
 */
export function assertIsDefined<T>(value: T, valueDescriptor?: String): asserts value is NonNullable<T> {
  if (value === undefined || value === null) {
    throw new AssertionError(`Expected ${valueDescriptor} to be defined, but received ${value}`);
  }
}
