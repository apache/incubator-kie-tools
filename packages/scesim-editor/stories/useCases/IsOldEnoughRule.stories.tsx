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
import { TestScenarioEditor, TestScenarioEditorRef } from "../../src/TestScenarioEditor";
import { useEffect, useRef } from "react";
import React from "react";
import { IS_OLD_ENOUGH_RULE } from "./IsOldEnoughRule";

function IsOldEnoughRule() {
  const ref = useRef<TestScenarioEditorRef>(null);
  useEffect(() => {
    ref.current?.setContent("AreTheyOldEnoughTest.scesim", IS_OLD_ENOUGH_RULE);
  }, []);
  return <TestScenarioEditor ref={ref} />;
}
const meta: Meta<typeof IsOldEnoughRule> = {
  title: "Use Cases/Is Old Enough Rule",
  component: IsOldEnoughRule,
};

export default meta;
type Story = StoryObj<typeof IsOldEnoughRule>;

export const IsOldEnough: Story = {
  render: (args) => IsOldEnoughRule(),
  args: {},
};
