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
import { useState, useMemo } from "react";
import { Flex } from "@patternfly/react-core/dist/js/layouts/Flex";
import { Form, FormSection } from "@patternfly/react-core/dist/js/components/Form";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { FontOptions } from "./FontOptions";
import { useDmnEditorStoreApi } from "../store/StoreContext";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { TimesIcon } from "@patternfly/react-icons/dist/js/icons/times-icon";
import { Truncate } from "@patternfly/react-core/dist/js/components/Truncate";
import { PropertiesPanelHeader } from "./PropertiesPanelHeader";
import { ShapeOptions } from "./ShapeOptions";

export function MultipleNodeProperties({ nodeIds }: { nodeIds: string[] }) {
  const [isSectionExpanded, setSectionExpanded] = useState<boolean>(true);
  const dmnEditorStoreApi = useDmnEditorStoreApi();
  const size = useMemo(() => nodeIds.length, [nodeIds.length]);

  return (
    <Form>
      <FormSection>
        <PropertiesPanelHeader
          fixed={true}
          isSectionExpanded={isSectionExpanded}
          toogleSectionExpanded={() => setSectionExpanded((prev) => !prev)}
          title={
            <Flex justifyContent={{ default: "justifyContentCenter" }}>
              <TextContent>
                <Text component={TextVariants.h4}>
                  <Truncate
                    content={`Multiple nodes selected (${size})`}
                    position={"middle"}
                    trailingNumChars={size.toString().length + 2}
                  />
                </Text>
              </TextContent>
            </Flex>
          }
          action={
            <Button
              title={"Close"}
              variant={ButtonVariant.plain}
              onClick={() => {
                dmnEditorStoreApi.setState((state) => {
                  state.diagram.propertiesPanel.isOpen = false;
                });
              }}
            >
              <TimesIcon />
            </Button>
          }
        />
      </FormSection>
      <FormSection>
        <FontOptions startExpanded={true} nodeIds={nodeIds} />
        <ShapeOptions
          startExpanded={true}
          nodeIds={nodeIds}
          isDimensioningEnabled={false}
          isPositioningEnabled={false}
        />
      </FormSection>
    </Form>
  );
}
