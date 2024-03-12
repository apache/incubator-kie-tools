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
import { SceSimEditorWrapper } from "../scesimEditorStoriesWrapper";
import { Button, Flex, FlexItem, Title, Tooltip } from "@patternfly/react-core/dist/js";
import React, { useEffect, useState } from "react";
import { linkTo } from "@storybook/addon-links";

function App() {
  const [version, setVersion] = useState(-1);

  useEffect(() => {
    setVersion((prev) => prev + 1);
  }, []);

  return (
    <div>
      <Flex direction={{ default: "column" }}>
        <FlexItem>
          <Flex style={{ width: "96vw" }}>
            <FlexItem>
              <Button onClick={linkTo("Misc/Empty SceSim Editor", "Base")}>Empty</Button>
            </FlexItem>
            <FlexItem>
              <Button onClick={linkTo("Use Cases/Is Old Enough Rule", "Is Old Enough")}>Are They Old Enough?</Button>
            </FlexItem>
            <FlexItem>
              <Button onClick={linkTo("Use Cases/Traffic Violation DMN", "Traffic Violation")}>
                Traffic Violation
              </Button>
            </FlexItem>
            <FlexItem align={{ default: "alignRight" }}>
              <Tooltip content={"This number updates everytime the expressionDefinition object is updated"}>
                <Title headingLevel="h2">Updates count: {version}</Title>
              </Tooltip>
            </FlexItem>
          </Flex>
        </FlexItem>
        <FlexItem>
          <div>{SceSimEditorWrapper()}</div>
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
