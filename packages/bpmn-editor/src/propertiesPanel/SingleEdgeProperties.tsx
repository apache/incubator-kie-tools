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

import { SectionHeader } from "@kie-tools/xyflow-react-kie-diagram/dist/propertiesPanel/SectionHeader";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { Form, FormSection } from "@patternfly/react-core/dist/js/components/Form";
import { TimesIcon } from "@patternfly/react-icons/dist/js/icons/times-icon";
import * as React from "react";
import { useMemo, useState } from "react";
import * as RF from "reactflow";
import { BpmnDiagramEdgeData } from "../diagram/BpmnDiagramDomain";
import { AssociationPath, SequenceFlowPath } from "../diagram/edges/EdgeSvgs";
import { UnknownNodeIcon } from "../diagram/nodes/NodeIcons";
import { useBpmnEditorStore, useBpmnEditorStoreApi } from "../store/StoreContext";
import { assertUnreachable } from "../ts-ext/assertUnreachable";
import { AssociationProperties } from "./singleEdgeProperties/AssociationProperties";
import { SequenceFlowProperties } from "./singleEdgeProperties/SequenceFlowProperties";
import { ColumnsIcon } from "@patternfly/react-icons/dist/js/icons/columns-icon";
import { Metadata } from "./metadata/Metadata";
import { useBpmnEditorI18n } from "../i18n";

const handleButtonSize = 34; // That's the size of the button. This is a "magic number", as it was obtained from the rendered page.
const svgViewboxPadding = Math.sqrt(Math.pow(handleButtonSize, 2) / 2) - handleButtonSize / 2; // This lets us create a square that will perfectly fit inside the button circle.
const edgeSvgViewboxSize = 25;
const nodeSvgProps = { width: 100, height: 70, x: 0, y: 15, strokeWidth: 8 };

export function SingleEdgeProperties() {
  const { i18n, locale } = useBpmnEditorI18n();
  const [isSectionExpanded, setSectionExpanded] = React.useState<boolean>(true);

  const bpmnEditorStoreApi = useBpmnEditorStoreApi();

  const selectedEdge = useBpmnEditorStore(
    (s) => [...s.computed(s).getDiagramData().selectedEdgesById.values()][0] as undefined | RF.Edge<BpmnDiagramEdgeData>
  );

  const { properties, title, icon } = useMemo(() => {
    const bpmnElement = selectedEdge?.data?.bpmnElement;
    const e = bpmnElement?.__$$element;
    switch (e) {
      // Events
      case "sequenceFlow":
        return {
          properties: <SequenceFlowProperties sequenceFlow={bpmnElement!} />,
          title: i18n.propertiesPanel.sequenceFlow,
          icon: (
            <svg
              className={"xyflow-react-kie-diagram--round-svg-container"}
              viewBox={`0 0 ${edgeSvgViewboxSize} ${edgeSvgViewboxSize}`}
              style={{ padding: `${svgViewboxPadding}px` }}
            >
              <SequenceFlowPath d={`M2,${edgeSvgViewboxSize - 2} L${edgeSvgViewboxSize - 2},0`} {...nodeSvgProps} />
            </svg>
          ),
        };
      case "association":
        return {
          properties: <AssociationProperties association={bpmnElement!} />,
          title: i18n.propertiesPanel.association,
          icon: (
            <svg
              className={"xyflow-react-kie-diagram--round-svg-container"}
              viewBox={`0 0 ${edgeSvgViewboxSize} ${edgeSvgViewboxSize}`}
              style={{ padding: `${svgViewboxPadding}px` }}
            >
              <AssociationPath
                d={`M2,${edgeSvgViewboxSize - 2} L${edgeSvgViewboxSize},0`}
                {...nodeSvgProps}
                strokeWidth={2}
              />
            </svg>
          ),
        };

      case undefined:
        return {
          properties: (
            <>
              <FormSection style={{ textAlign: "center" }}>{"No properties to edit."}</FormSection>
            </>
          ),
          title: i18n.propertiesPanel.unsupported,
          icon: <UnknownNodeIcon />,
        };
      default:
        assertUnreachable(e);
    }
  }, [
    i18n.propertiesPanel.association,
    i18n.propertiesPanel.sequenceFlow,
    i18n.propertiesPanel.unsupported,
    selectedEdge?.data?.bpmnElement,
  ]);

  const [isMetadataSectionExpanded, setMetadataSectionExpanded] = useState<boolean>(false);

  return (
    <>
      <Form>
        <FormSection
          title={
            <SectionHeader
              icon={icon}
              fixed={true}
              expands={true}
              isSectionExpanded={isSectionExpanded}
              toogleSectionExpanded={() => setSectionExpanded((prev) => !prev)}
              title={title}
              action={
                <Button
                  title={i18n.propertiesPanel.close}
                  variant={ButtonVariant.plain}
                  onClick={() => {
                    bpmnEditorStoreApi.setState((state) => {
                      state.propertiesPanel.isOpen = false;
                    });
                  }}
                >
                  <TimesIcon />
                </Button>
              }
              locale={locale}
            />
          }
        >
          {isSectionExpanded && properties}
        </FormSection>

        <FormSection
          title={
            <SectionHeader
              expands={true}
              isSectionExpanded={isMetadataSectionExpanded}
              toogleSectionExpanded={() => setMetadataSectionExpanded((prev) => !prev)}
              icon={<ColumnsIcon width={16} height={36} style={{ marginLeft: "12px" }} />}
              title={i18n.propertiesPanel.metadata}
              locale={locale}
            />
          }
        >
          {isMetadataSectionExpanded && (
            <>
              <FormSection style={{ paddingLeft: "20px", marginTop: "20px", gap: 0 }}>
                <Metadata obj={selectedEdge?.data?.bpmnEdge} />
              </FormSection>
            </>
          )}
        </FormSection>
      </Form>
    </>
  );
}
