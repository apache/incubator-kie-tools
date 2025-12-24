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

import { BPMN20__tParallelGateway } from "@kie-tools/bpmn-marshaller/dist/schemas/bpmn-2_0/ts-gen/types";
import { FormSection } from "@patternfly/react-core/dist/js/components/Form";
import * as React from "react";
import { Normalized } from "../../normalization/normalize";
import { NameDocumentationAndId } from "../nameDocumentationAndId/NameDocumentationAndId";
import { GatewayIcon } from "../../diagram/nodes/NodeIcons";
import { PropertiesPanelHeaderFormSection } from "./_PropertiesPanelHeaderFormSection";
import { useBpmnEditorI18n } from "../../i18n";

export function ParallelGatewayProperties({
  parallelGateway,
}: {
  parallelGateway: Normalized<BPMN20__tParallelGateway> & { __$$element: "parallelGateway" };
}) {
  const { i18n } = useBpmnEditorI18n();
  return (
    <PropertiesPanelHeaderFormSection
      title={parallelGateway["@_name"] || i18n.singleNodeProperties.parallelGateway}
      icon={<GatewayIcon variant={parallelGateway.__$$element} isIcon={true} />}
    >
      <NameDocumentationAndId element={parallelGateway} />
    </PropertiesPanelHeaderFormSection>
  );
}
