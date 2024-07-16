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
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { PlusCircleIcon } from "@patternfly/react-icons/dist/js/icons/plus-circle-icon";
import { Divider } from "@patternfly/react-core/dist/js/components/Divider";
import { DiagramLhsPanel } from "../store/Store";
import { getDrdId } from "./drd/drdId";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { Form, FormGroup, FormSection } from "@patternfly/react-core/dist/js/components/Form";
import { ToggleGroup, ToggleGroupItem } from "@patternfly/react-core/dist/js/components/ToggleGroup";
import { AlternativeInputDataIcon, InputDataIcon } from "../icons/Icons";
import { EmptyState, EmptyStateBody } from "@patternfly/react-core/dist/js/components/EmptyState";
import { Flex } from "@patternfly/react-core/dist/js/layouts/Flex";
import { useCallback } from "react";

export function DrdSelectorPanel() {
  const thisDmn = useDmnEditorStore((s) => s.dmn);
  const diagram = useDmnEditorStore((s) => s.diagram);
  const drdIndex = useDmnEditorStore((s) => s.computed(s).getDrdIndex());
  const isAlternativeInputDataShape = useDmnEditorStore((s) => s.computed(s).isAlternativeInputDataShape());
  const drdName = useDmnEditorStore(
    (s) =>
      s.dmn.model.definitions["dmndi:DMNDI"]?.["dmndi:DMNDiagram"]?.[s.computed(s).getDrdIndex()]?.["@_name"] ||
      getDefaultDrdName({ drdIndex: s.computed(s).getDrdIndex() })
  );

  const dmnEditorStoreApi = useDmnEditorStoreApi();

  const drds = thisDmn.model.definitions["dmndi:DMNDI"]?.["dmndi:DMNDiagram"] ?? [];

  const removeDrd = useCallback(() => {
    dmnEditorStoreApi.setState((s) => {
      const nextDrds = s.dmn.model.definitions["dmndi:DMNDI"]?.["dmndi:DMNDiagram"];
      nextDrds?.splice(s.computed(s).getDrdIndex(), 1);
      s.diagram.__unsafeDrdIndex = Math.max(0, Math.min(s.computed(s).getDrdIndex(), (nextDrds?.length ?? 0) - 1));
    });
  }, [dmnEditorStoreApi]);

  return (
    <>
      <div
        style={{
          display: "grid",
          gridTemplateColumns: "300px 300px",
          gridTemplateRows: "auto auto auto",
          gridTemplateAreas: `
          'header-list header-properties'
          'divider-list divider-properties'
          'content-list content-properties'
          `,
          columnGap: "40px",
        }}
      >
        <div style={{ gridArea: "header-list" }} data-testid={"kie-tools--dmn-editor--drd-selector-popover"}>
          <div style={{ display: "flex", justifyContent: "space-between" }}>
            <TextContent>
              <Text component="h3">DRDs</Text>
            </TextContent>
            <Button
              title={"New DRD"}
              variant={ButtonVariant.link}
              onClick={() => {
                dmnEditorStoreApi.setState((state) => {
                  const allDrds = state.dmn.model.definitions["dmndi:DMNDI"]?.["dmndi:DMNDiagram"] ?? [];
                  const newIndex = allDrds.length;

                  addOrGetDrd({
                    definitions: state.dmn.model.definitions,
                    drdIndex: newIndex,
                  });

                  state.diagram.__unsafeDrdIndex = newIndex;
                  state.diagram.openLhsPanel = DiagramLhsPanel.DRG_NODES;
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
        {(drds.length <= 0 && (
          <>
            <EmptyState>
              <Title size={"md"} headingLevel={"h4"}>
                {"You're on the default DRD"}
              </Title>
              <EmptyStateBody>
                {"Adding nodes or making changes to the Diagram will automatically create a DRD for you."}
              </EmptyStateBody>
            </EmptyState>
          </>
        )) || (
          <div
            style={{ gridArea: "content-list" }}
            className={"kie-dmn-editor--drd-list"}
            data-testid={"kie-tools--dmn-editor--drd-list"}
          >
            {drds.map((drd, i) => (
              <React.Fragment key={drd["@_id"]!}>
                <button
                  className={i === drdIndex ? "active" : undefined}
                  onClick={() => {
                    dmnEditorStoreApi.setState((state) => {
                      state.diagram.__unsafeDrdIndex = i;
                    });
                  }}
                >
                  {`${i + 1}. ${drd["@_name"] || getDefaultDrdName({ drdIndex: i })}`}
                </button>
                <br />
              </React.Fragment>
            ))}
          </div>
        )}

        <div style={{ gridArea: "header-properties" }}>
          <Flex justifyContent={{ default: "justifyContentSpaceBetween" }}>
            <Title headingLevel="h3" style={{ height: "36px" }}>
              {drdName}
            </Title>
            {drds.length > 0 && (
              <Button variant={ButtonVariant.link} onClick={removeDrd} style={{ padding: 0 }} title="Remove DRD">
                Remove
              </Button>
            )}
          </Flex>
        </div>
        <div style={{ gridArea: "divider-properties" }}>
          <Divider style={{ marginBottom: "8px" }} />
        </div>
        <div style={{ gridArea: "content-properties" }}>
          <Form>
            <FormSection>
              <FormGroup label={"Input Data node shape"}>
                <ToggleGroup
                  aria-label="Tweak the shape of the input data node"
                  className={"kie-dmn-editor--drd-properties--input-data-node-shape"}
                >
                  <ToggleGroupItem
                    text="Classic"
                    icon={<InputDataIcon padding={"2px 0 0 0"} height={22} />}
                    buttonId="classic-input-node-shape"
                    isSelected={isAlternativeInputDataShape === false}
                    onChange={() =>
                      dmnEditorStoreApi.setState((s) => {
                        const { diagram: drd } = addOrGetDrd({
                          definitions: s.dmn.model.definitions,
                          drdIndex: s.computed(s).getDrdIndex(),
                        });
                        drd["@_useAlternativeInputDataShape"] = false;
                      })
                    }
                  />
                  <ToggleGroupItem
                    text="Alternative"
                    icon={
                      <AlternativeInputDataIcon
                        padding={"1px 0 0 0"}
                        height={22}
                        viewBox={160}
                        transform={"translate(40, 30)"}
                      />
                    }
                    buttonId="alternative-input-node-shape"
                    isSelected={isAlternativeInputDataShape === true}
                    onChange={() =>
                      dmnEditorStoreApi.setState((s) => {
                        const { diagram: drd } = addOrGetDrd({
                          definitions: s.dmn.model.definitions,
                          drdIndex: s.computed(s).getDrdIndex(),
                        });
                        drd["@_useAlternativeInputDataShape"] = true;
                      })
                    }
                  />
                </ToggleGroup>
              </FormGroup>
            </FormSection>
          </Form>
        </div>
      </div>
    </>
  );
}
