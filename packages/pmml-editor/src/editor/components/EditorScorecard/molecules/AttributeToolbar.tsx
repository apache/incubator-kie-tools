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
import { ArrowAltCircleLeftIcon } from "@patternfly/react-icons";

interface AttributeToolbarProps {
  viewOverview: () => void;
  viewAttributes: () => void;
}

export const AttributeToolbar = (props: AttributeToolbarProps) => {
  const { viewOverview, viewAttributes } = props;

  const onViewOverview = (e: React.MouseEvent) => {
    e.preventDefault();
    viewOverview();
  };

  const onViewAttributes = (e: React.MouseEvent) => {
    e.preventDefault();
    viewAttributes();
  };

  return (
    <Toolbar id="attribute-toolbar" data-testid="attribute-toolbar">
      <ToolbarContent>
        <Split hasGutter={true} style={{ width: "100%" }}>
          <SplitItem isFilled={true}>
            <TextContent>
              <Title size="lg" headingLevel="h1">
                <a onClick={e => onViewOverview(e)}>Characteristics</a>&nbsp;/&nbsp;
                <a onClick={e => onViewAttributes(e)}>Attributes</a>&nbsp;/&nbsp;Attribute
              </Title>
            </TextContent>
          </SplitItem>
          <SplitItem>
            <Button
              variant="primary"
              onClick={e => onViewAttributes(e)}
              icon={<ArrowAltCircleLeftIcon />}
              iconPosition="left"
            >
              Done
            </Button>
          </SplitItem>
        </Split>
      </ToolbarContent>
    </Toolbar>
  );
};
