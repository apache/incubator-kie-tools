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

import type { Meta, StoryObj } from "@storybook/react";

import { DevWebApp, EMPTY_DMN_15 } from "../../dev-webapp/src/DevWebApp";
import { DEFAULT_DEV_WEBAPP_DMN } from "../../dev-webapp/src/DefaultDmn";

// More on how to set up stories at: https://storybook.js.org/docs/writing-stories#default-export
const meta: Meta<typeof DevWebApp> = {
  title: "Example/DmnDevWebApp",
  component: DevWebApp,
  parameters: {},
  // This component will have an automatically generated Autodocs entry: https://storybook.js.org/docs/writing-docs/autodocs
  tags: ["autodocs"],
  // More on argTypes: https://storybook.js.org/docs/api/argtypes
  // argTypes: {
  //   backgroundColor: { control: 'color' },
  // },
};

export default meta;

type Story = StoryObj<typeof DevWebApp>;

// More on writing stories with args: https://storybook.js.org/docs/writing-stories/args
export const GettingStartedModel: Story = {
  args: {
    initialModel: DEFAULT_DEV_WEBAPP_DMN,
  },
};

export const EmptyModel: Story = {
  args: {
    initialModel: EMPTY_DMN_15(),
  },
};
