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

import { Page } from "@playwright/test";
import { PropertiesPanelBase } from "./propertiesPanelBase";
import { Diagram } from "../diagram";

export class GatewayPropertiesPanel extends PropertiesPanelBase {
  constructor(
    public diagram: Diagram,
    public page: Page
  ) {
    super(diagram, page);
  }

  public async setDefaultFlow(args: { flowId: string }) {
    const defaultFlowCombobox = this.panel().getByRole("combobox").first();
    await defaultFlowCombobox.click();
    await this.page.getByRole("option", { name: args.flowId, exact: true }).click();
  }

  public async getDefaultFlow(): Promise<string> {
    const defaultFlowSelect = this.panel().getByRole("combobox").first();
    return await defaultFlowSelect.inputValue();
  }
}
