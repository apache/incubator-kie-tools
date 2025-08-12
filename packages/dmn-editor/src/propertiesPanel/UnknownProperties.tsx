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
import { Alert, AlertVariant } from "@patternfly/react-core/dist/js/components/Alert/Alert";
import { DMN15__tDefinitions, DMNDI15__DMNShape } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { Normalized } from "@kie-tools/dmn-marshaller/dist/normalization/normalize";
import { useDmnEditorStore } from "../store/StoreContext";
import { useMemo } from "react";
import { Unpacked } from "../tsExt/tsExt";
import { XmlQName, buildXmlQName } from "@kie-tools/xml-parser-ts/dist/qNames";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { useDmnEditor } from "../DmnEditorContext";
import { Divider } from "@patternfly/react-core/dist/js/components/Divider";
import { useExternalModels } from "../includedModels/DmnEditorDependenciesContext";
import { useDmnEditorI18n } from "../i18n";

export function UnknownProperties(props: { shape: Normalized<DMNDI15__DMNShape>; dmnElementRefQName: XmlQName }) {
  const { i18n } = useDmnEditorI18n();
  const thisDmn = useDmnEditorStore((s) => s.dmn);
  const { externalModelsByNamespace } = useExternalModels();
  const externalDmnsByNamespace = useDmnEditorStore(
    (s) => s.computed(s).getDirectlyIncludedExternalModelsByNamespace(externalModelsByNamespace).dmns
  );
  const { onRequestToJumpToPath } = useDmnEditor();

  const content = useMemo(() => {
    const namespace = thisDmn.model.definitions[`@_xmlns:${props.dmnElementRefQName.prefix}`];
    if (!namespace) {
      return <p>{i18n.propertiesPanel.nodeReferenceMessage}</p>;
    }

    const externalDmn = externalDmnsByNamespace.get(namespace);
    if (!externalDmn) {
      // Nothing that the user can do.
      return <p>{i18n.propertiesPanel.externalDmnNodeReference}</p>;
    }

    const externalDrgElementsById = (externalDmn.model.definitions.drgElement ?? []).reduce(
      (acc, e, index) => acc.set(e["@_id"]!, { element: e, index }),
      new Map<string, { index: number; element: Unpacked<Normalized<DMN15__tDefinitions>["drgElement"]> }>()
    );

    const externalDrgElement = externalDrgElementsById.get(props.dmnElementRefQName.localPart);
    if (!externalDrgElement) {
      return (
        <>
          <p>{i18n.propertiesPanel.nameNotExists(externalDmn.model.definitions["@_name"])}</p>
          {onRequestToJumpToPath && (
            <>
              <br />
              <Button
                style={{ paddingLeft: 0 }}
                variant={ButtonVariant.link}
                onClick={() => onRequestToJumpToPath?.(externalDmn.normalizedPosixPathRelativeToTheOpenFile)}
              >
                {i18n.propertiesPanel.goToName(externalDmn.model.definitions["@_name"])}
              </Button>
            </>
          )}
        </>
      );
    }
  }, [
    externalDmnsByNamespace,
    onRequestToJumpToPath,
    props.dmnElementRefQName.localPart,
    props.dmnElementRefQName.prefix,
    thisDmn.model.definitions,
    i18n.propertiesPanel,
  ]);

  return (
    <>
      <Alert title={i18n.propertiesPanel.unknownNodePlaceholder} isInline={true} variant={AlertVariant.danger}>
        <br />
        {content}
        <Divider style={{ marginTop: "16px" }} />
        <br />
        <p>
          <b>{i18n.propertiesPanel.reference}</b>&nbsp;
          {i18n.propertiesPanel.buildXmlName(buildXmlQName(props.dmnElementRefQName))}
        </p>
      </Alert>
    </>
  );
}
