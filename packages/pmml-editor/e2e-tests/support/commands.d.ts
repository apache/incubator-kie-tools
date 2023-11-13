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

declare namespace Cypress {
  interface Chainable {
    /**
     * Returns DOM Element for button which open new file.
     */
    newButtonPMML(): Chainable<JQuery<HTMLBodyElement>>;

    /**
     * Return DOM Element for button which upload new file.
     */
    uploadButtonPMML(): Chainable<JQuery<HTMLBodyElement>>;

    /**
     * Return DOM Element for button which open Data Dictionary Editor.
     */
    buttonDataDictionary(): Chainable<JQuery<HTMLBodyElement>>;

    /**
     * Return DOM Element for button which open Mining Schema Editor.
     */

    buttonMiningSchema(): Chainable<JQuery<HTMLBodyElement>>;

    /**
     * Return DOM Element for button which open Outputs Editor.
     */

    buttonOutputs(): Chainable<JQuery<HTMLBodyElement>>;

    /**
     * Return DOM Element for button which invoke Undo operation.
     */
    buttonUndo(): Chainable<JQuery<HTMLBodyElement>>;

    /**
     * Return DOM Element for button which invoke Redo operation.
     */

    buttonRedo(): Chainable<JQuery<HTMLBodyElement>>;

    /**
     * Return DOM Element for button which open PMML Source Editor.
     */
    buttonPMML(): Chainable<JQuery<HTMLBodyElement>>;

    /**
     * Return DOM Element for button which validate PMML.
     */
    buttonValidation(): Chainable<JQuery<HTMLBodyElement>>;

    /**
     * Return DOM Element which match the value of data-ouia-component-id attribute.
     */
    ouiaId(valueStr: string): Chainable;

    /**
     * Return DOM Element which match the value of data-ouia-component-type attribute.
     */
    ouiaType(valueStr: string): Chainable;

    /**
     * Return DOM Element which match the value of data-ouia-component-id and data-ouia-component-type attributes.
     */
    ouia(id: string, type: string): Chainable;

    /**
     * Assert that the actual state of the PMML editor is similar as PMML file saved in the fixtures folder.
     * Run this command in scope of the main page.
     */
    editorShouldContains(fileName: string): Chainable;
  }
}
