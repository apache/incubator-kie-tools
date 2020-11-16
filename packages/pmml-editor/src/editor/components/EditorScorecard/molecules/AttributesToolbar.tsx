/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import * as React from "react";
import { Button, Split, SplitItem, TextContent, Title, Toolbar, ToolbarContent } from "@patternfly/react-core";
import { Operation } from "../Operation";

interface AttributesToolbarProps {
  activeOperation: Operation;
  onViewOverview: () => void;
  onAddAttribute: () => void;
}

export const AttributesToolbar = (props: AttributesToolbarProps) => {
  const { activeOperation, onViewOverview, onAddAttribute } = props;

  return (
    <Toolbar id="attributes-toolbar" data-testid="attributes-toolbar">
      <ToolbarContent>
        <Split hasGutter={true} style={{ width: "100%" }}>
          <SplitItem>
            <TextContent>
              <Title size="lg" headingLevel="h1">
                Attributes
              </Title>
            </TextContent>
          </SplitItem>
          <SplitItem isFilled={true} />
          <SplitItem>
            <Button onClick={e => onAddAttribute()} isDisabled={activeOperation !== Operation.NONE}>
              Add attribute
            </Button>
          </SplitItem>
          <SplitItem>
            <Button onClick={e => onViewOverview()}>Go back</Button>
          </SplitItem>
        </Split>
      </ToolbarContent>
    </Toolbar>
  );
};
