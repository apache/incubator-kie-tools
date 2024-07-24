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

import { expect, Page } from "@playwright/test";
import { Diagram } from "../../diagram";
import { DataTypeProperties } from "../parts/dataTypeProperties";
import { ConstraintType, RangeConstraintPosition } from "../../dataTypes";
import { NameProperties } from "../parts/nameProperties";
import { BeePropertiesPanelBase } from "./beePropertiesPanelBase";

export class DecisionTableInputRulePropertiesPanel extends BeePropertiesPanelBase {
  private nameProperties: NameProperties;
  private dataTypeProperties: DataTypeProperties;

  constructor(
    public diagram: Diagram,
    public page: Page
  ) {
    super(diagram, page);
    this.nameProperties = new NameProperties(this.panel(), page);
    this.dataTypeProperties = new DataTypeProperties(this.panel(), page);
  }

  public getDataType() {
    return this.dataTypeProperties.getDataType();
  }

  public getConstraintSection() {
    return this.dataTypeProperties.getConstraintSection();
  }

  public getConstraintButton(args: { type: ConstraintType }) {
    return this.dataTypeProperties.getConstraintButton({ ...args });
  }

  public async expectConstraintButtonsToBeDisabled() {
    await expect(this.getConstraintButton({ type: ConstraintType.NONE })).toBeDisabled();
    await expect(this.getConstraintButton({ type: ConstraintType.ENUMERATION })).toBeDisabled();
    await expect(this.getConstraintButton({ type: ConstraintType.EXPRESSION })).toBeDisabled();
    await expect(this.getConstraintButton({ type: ConstraintType.RANGE })).toBeDisabled();
  }

  public getEnumerationValueAt(element: number) {
    return this.dataTypeProperties.getEnumerationElementAt(element);
  }

  public getExpressionConstraintValue() {
    return this.dataTypeProperties.getExpressionConstraintValue();
  }

  public getNoneConstraint() {
    return this.dataTypeProperties.getNoneConstraint();
  }

  public getRangeConstraintValueAt(position: RangeConstraintPosition) {
    return this.dataTypeProperties.getRangeConstraintValue(position);
  }
}
