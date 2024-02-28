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

import * as React from "react";
import type { Meta, StoryObj } from "@storybook/react";
import * as DmnEditor from "../../../src/DmnEditor";

import { getMarshaller } from "@kie-tools/dmn-marshaller";
import { EMPTY_DMN_15 } from "../DmnDiagramSources";

const meta: Meta<typeof DmnEditor.DmnEditor> = {
  title: "Use cases/Empty",
  component: DmnEditor.DmnEditor,
};

export default meta;
type Story = StoryObj<typeof DmnEditor.DmnEditor>;

export const Empty: Story = {
  args: {
    model: getMarshaller(EMPTY_DMN_15(), { upgradeTo: "latest" }).parser.parse(),
  },
};
