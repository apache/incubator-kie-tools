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
import { useMemo, useState } from "react";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { Form, FormSection, FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import { ClipboardCopy } from "@patternfly/react-core/dist/js/components/ClipboardCopy";
import { TimesIcon } from "@patternfly/react-icons/dist/js/icons/times-icon";
import { useDmnEditorStore, useDmnEditorStoreApi } from "../store/StoreContext";
import { useExternalModels } from "../includedModels/DmnEditorDependenciesContext";
import { useDmnEditorI18n } from "../i18n";
import { PropertiesPanelHeader } from "./PropertiesPanelHeader";
import { EDGE_TYPES } from "../diagram/edges/EdgeTypes";
import {
  InformationRequirementPath,
  KnowledgeRequirementPath,
  AuthorityRequirementPath,
  AssociationPath,
} from "../diagram/edges/Edges";

const handleButtonSize = 34;
const svgViewboxPadding = Math.sqrt(Math.pow(handleButtonSize, 2) / 2) - handleButtonSize / 2;
const edgeSvgViewboxSize = 25;

export function SingleEdgeProperties({ edgeId }: { edgeId: string }) {
  const { i18n } = useDmnEditorI18n();
  const dmnEditorStoreApi = useDmnEditorStoreApi();
  const { externalModelsByNamespace } = useExternalModels();

  const edge = useDmnEditorStore((s) =>
    s.computed(s).getDiagramData(externalModelsByNamespace).selectedEdgesById.get(edgeId)
  );

  const [isSectionExpanded, setSectionExpanded] = useState<boolean>(true);

  const Icon = useMemo(() => {
    if (!edge) {
      return () => null;
    }
    switch (edge.type) {
      case EDGE_TYPES.informationRequirement:
        return () => (
          <svg
            viewBox={`0 0 ${edgeSvgViewboxSize} ${edgeSvgViewboxSize}`}
            className={"kie-dmn-editor--round-svg-container"}
            style={{ padding: `${svgViewboxPadding}px` }}
          >
            <InformationRequirementPath d={`M2,${edgeSvgViewboxSize - 2} L${edgeSvgViewboxSize - 2},0`} />
          </svg>
        );
      case EDGE_TYPES.knowledgeRequirement:
        return () => (
          <svg
            viewBox={`0 0 ${edgeSvgViewboxSize} ${edgeSvgViewboxSize}`}
            className={"kie-dmn-editor--round-svg-container"}
            style={{ padding: `${svgViewboxPadding}px` }}
          >
            <KnowledgeRequirementPath d={`M2,${edgeSvgViewboxSize - 2} L${edgeSvgViewboxSize - 2},0`} />
          </svg>
        );
      case EDGE_TYPES.authorityRequirement:
        return () => (
          <svg
            viewBox={`0 0 ${edgeSvgViewboxSize} ${edgeSvgViewboxSize}`}
            className={"kie-dmn-editor--round-svg-container"}
            style={{ padding: `${svgViewboxPadding}px` }}
          >
            <AuthorityRequirementPath
              d={`M2,${edgeSvgViewboxSize - 2} L${edgeSvgViewboxSize - 2},2`}
              centerToConnectionPoint={false}
            />
          </svg>
        );
      case EDGE_TYPES.association:
        return () => (
          <svg
            viewBox={`0 0 ${edgeSvgViewboxSize} ${edgeSvgViewboxSize}`}
            className={"kie-dmn-editor--round-svg-container"}
            style={{ padding: `${svgViewboxPadding}px` }}
          >
            <AssociationPath d={`M2,${edgeSvgViewboxSize - 2} L${edgeSvgViewboxSize},0`} strokeWidth={2} />
          </svg>
        );
      default:
        return () => null;
    }
  }, [edge]);

  const title = useMemo(() => {
    if (!edge) {
      return i18n.propertiesPanel.edge;
    }
    switch (edge.type) {
      case EDGE_TYPES.informationRequirement:
        return i18n.propertiesPanel.informationRequirement;
      case EDGE_TYPES.knowledgeRequirement:
        return i18n.propertiesPanel.knowledgeRequirement;
      case EDGE_TYPES.authorityRequirement:
        return i18n.propertiesPanel.authorityRequirement;
      case EDGE_TYPES.association:
        return i18n.propertiesPanel.association;
      default:
        return i18n.propertiesPanel.edge;
    }
  }, [edge, i18n]);

  if (!edge) {
    return <>{i18n.propertiesPanel.edgeNotFound(edgeId)}</>;
  }

  return (
    <Form>
      <FormSection
        className={!isSectionExpanded ? "kie-dmn-editor--single-node-properties-title-colapsed" : ""}
        title={
          <PropertiesPanelHeader
            expands={true}
            fixed={true}
            isSectionExpanded={isSectionExpanded}
            toogleSectionExpanded={() => setSectionExpanded((prev) => !prev)}
            icon={<Icon />}
            title={title}
            action={
              <Button
                title={i18n.close}
                variant={ButtonVariant.plain}
                onClick={() => {
                  dmnEditorStoreApi.setState((state) => {
                    state.boxedExpressionEditor.propertiesPanel.isOpen = false;
                    state.diagram.propertiesPanel.isOpen = false;
                  });
                }}
              >
                <TimesIcon />
              </Button>
            }
          />
        }
      >
        {isSectionExpanded && (
          <FormSection style={{ paddingLeft: "20px" }}>
            <FormGroup label={i18n.propertiesPanel.id}>
              <ClipboardCopy
                isReadOnly={true}
                hoverTip={i18n.propertiesPanel.copy}
                clickTip={i18n.propertiesPanel.copied}
              >
                {edgeId}
              </ClipboardCopy>
            </FormGroup>
          </FormSection>
        )}
      </FormSection>
    </Form>
  );
}
