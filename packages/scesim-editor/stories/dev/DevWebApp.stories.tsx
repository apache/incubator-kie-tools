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
import { DevWebApp } from "../../dev-webapp/src/DevWebApp";
import { Button, Flex, FlexItem, Tooltip } from "@patternfly/react-core/dist/js";
import { useEffect, useState, useCallback, useRef } from "react";
import React from "react";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { IS_OLD_ENOUGH_RULE, TRAFFIC_VIOLATION_DMN } from "../../dev-webapp/src/ExternalScesimModels";
import { TestScenarioEditor, TestScenarioEditorRef } from "../../src/TestScenarioEditor";
import { Base } from "../misc/Empty/EmptyEditor.stories";

function App() {
  return (
    <div>
      <Flex direction={{ default: "column" }}>
        <FlexItem>
          <Flex style={{ width: "96vw" }}>
            <FlexItem>
              <Button>Empty</Button>
            </FlexItem>
            <FlexItem>
              <Button>Are They Old Enough?</Button>
            </FlexItem>
            <FlexItem>
              <Button>Traffic Violation</Button>
            </FlexItem>
          </Flex>
        </FlexItem>
        <FlexItem>
          <div>
            <DevWebApp />
          </div>
        </FlexItem>
      </Flex>
    </div>
  );
}
const meta: Meta<typeof App> = {
  title: "Dev/Web App",
  component: App,
};

export default meta;
type Story = StoryObj<typeof App>;

export const WebApp: Story = {
  render: (args) => App(),
  args: {},
};
