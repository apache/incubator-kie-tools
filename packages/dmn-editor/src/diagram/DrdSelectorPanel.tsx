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
import { useDmnEditorStore, useDmnEditorStoreApi } from "../store/StoreContext";
import { addOrGetDrd, getDefaultDrdName } from "../mutations/addOrGetDrd";
import { Text, TextContent } from "@patternfly/react-core/dist/js/components/Text";
import { Flex } from "@patternfly/react-core/dist/js/layouts/Flex";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { PlusCircleIcon } from "@patternfly/react-icons/dist/js/icons/plus-circle-icon";
import { Divider } from "@patternfly/react-core/dist/js/components/Divider";
import { DiagramNodesPanel } from "../store/Store";
import { getDrdId } from "./drd/drdId";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { Form, FormGroup, FormSection } from "@patternfly/react-core/dist/js/components/Form";
import { Switch } from "@patternfly/react-core/dist/js/components/Switch";

export function DrdSelectorPanel() {
  const thisDmn = useDmnEditorStore((s) => s.dmn);
  const diagram = useDmnEditorStore((s) => s.diagram);
  const isAlternativeInputDataShape = useDmnEditorStore((s) => s.computed(s).isAlternativeInputDataShape());

  const dmnEditorStoreApi = useDmnEditorStoreApi();

  return (
    <>
      <div
        style={{
          display: "grid",
          gridTemplateColumns: "300px 200px",
          gridTemplateRows: "auto auto auto",
          gridTemplateAreas: `
          'header-list header-properties'
          'divider-list divider-properties'
          'content-list content-properties'
          `,
          columnGap: "40px",
        }}
      >
        <div style={{ gridArea: "header-list" }}>
          <div style={{ display: "flex", justifyContent: "space-between" }}>
            <TextContent>
              <Text component="h3">DRDs</Text>
            </TextContent>
            <Button
              variant={ButtonVariant.link}
              onClick={() => {
                dmnEditorStoreApi.setState((state) => {
                  const allDrds = state.dmn.model.definitions["dmndi:DMNDI"]?.["dmndi:DMNDiagram"] ?? [];
                  const newIndex = allDrds.length;

                  addOrGetDrd({
                    definitions: state.dmn.model.definitions,
                    drdIndex: newIndex,
                  });

                  state.diagram.drdIndex = newIndex;
                  state.diagram.drdSelector.isOpen = false;
                  state.diagram.openNodesPanel = DiagramNodesPanel.DRG_NODES;
                  state.focus.consumableId = getDrdId({ drdIndex: newIndex });
                });
              }}
            >
              <PlusCircleIcon />
            </Button>
          </div>
        </div>
        <div style={{ gridArea: "divider-list" }}>
          <Divider style={{ marginBottom: "8px" }} />
        </div>
        <div style={{ gridArea: "content-list" }} className={"kie-dmn-editor--drd-list"}>
          {thisDmn.model.definitions["dmndi:DMNDI"]?.["dmndi:DMNDiagram"]?.map((drd, i) => (
            <React.Fragment key={drd["@_id"]!}>
              <button
                className={i === diagram.drdIndex ? "active" : undefined}
                onClick={() => {
                  dmnEditorStoreApi.setState((state) => {
                    state.diagram.drdIndex = i;
                    // state.diagram.drdSelector.isOpen = false;
                  });
                }}
              >
                {`${i + 1}. ${drd["@_name"] || getDefaultDrdName({ drdIndex: i })}`}
              </button>
              <br />
            </React.Fragment>
          ))}
        </div>

        <div style={{ gridArea: "header-properties" }}>
          <Title headingLevel="h3" style={{ height: "36px" }}>
            Properties
          </Title>
        </div>
        <div style={{ gridArea: "divider-properties" }}>
          <Divider style={{ marginBottom: "8px" }} />
        </div>
        <div style={{ gridArea: "content-properties" }}>
          <Form>
            <FormSection>
              <FormGroup label={"Input Node Shape"}>
                <Switch
                  id="reversed-switch"
                  label="Alternative"
                  labelOff="Classic"
                  isChecked={isAlternativeInputDataShape}
                  onChange={() =>
                    dmnEditorStoreApi.setState((state) => {
                      state.dmn.model.definitions["dmndi:DMNDI"] ??= {};
                      state.dmn.model.definitions["dmndi:DMNDI"]["dmndi:DMNDiagram"] ??= [];
                      state.dmn.model.definitions["dmndi:DMNDI"]["dmndi:DMNDiagram"][state.diagram.drdIndex][
                        "@_useAlternativeInputDataShape"
                      ] = !isAlternativeInputDataShape;
                    })
                  }
                />
              </FormGroup>
            </FormSection>
          </Form>
        </div>
      </div>
    </>
  );
}
