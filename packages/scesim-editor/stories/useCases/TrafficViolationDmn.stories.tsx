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
import { TRAFFIC_VIOLATION_DMN } from "./TrafficViolationDmn.ts";

function TrafficViolationDmn() {
  const ref = useRef<TestScenarioEditorRef>(null);
  useEffect(() => {
    ref.current?.setContent("TrafficViolationTest.scesim", TRAFFIC_VIOLATION_DMN);
  }, []);
  return <TestScenarioEditor ref={ref} />;
}
const meta: Meta<typeof TrafficViolationDmn> = {
  title: "Use Cases/Traffic Violation DMN",
  component: TrafficViolationDmn,
};

export default meta;
type Story = StoryObj<typeof TrafficViolationDmn>;

export const TrafficViolation: Story = {
  render: (args) => TrafficViolationDmn(),
  args: {},
};
