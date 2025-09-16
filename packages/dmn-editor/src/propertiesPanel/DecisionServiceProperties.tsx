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
import {
  DMN_LATEST__tDMNElementReference,
  DMN_LATEST__tDecision,
  DMN_LATEST__tDecisionService,
  DMN_LATEST__tInputData,
  DMN_LATEST__tDefinitions,
} from "@kie-tools/dmn-marshaller";
import { Normalized } from "@kie-tools/dmn-marshaller/dist/normalization/normalize";
import { buildXmlHref, parseXmlHref } from "@kie-tools/dmn-marshaller/dist/xml/xmlHrefs";
import { ClipboardCopy } from "@patternfly/react-core/dist/js/components/ClipboardCopy";
import { FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import { TextArea } from "@patternfly/react-core/dist/js/components/TextArea";
import { DocumentationLinksFormGroup } from "./DocumentationLinksFormGroup";
import { TypeRefSelector } from "../dataTypes/TypeRefSelector";
import { useDmnEditorStore, useDmnEditorStoreApi } from "../store/StoreContext";
import { useCallback, useMemo } from "react";
import { DmnObjectListItem } from "../externalNodes/DmnObjectListItem";
import { InlineFeelNameInput } from "../feel/InlineFeelNameInput";
import { Divider } from "@patternfly/react-core/dist/js/components/Divider";
import { useDmnEditor } from "../DmnEditorContext";
import { useResolvedTypeRef } from "../dataTypes/useResolvedTypeRef";
import { useExternalModels } from "../includedModels/DmnEditorDependenciesContext";
import { DragAndDrop, Draggable } from "../draggable/Draggable";
import { buildFeelQNameFromNamespace } from "../feel/buildFeelQName";
import { Alert, AlertVariant } from "@patternfly/react-core/dist/js/components/Alert/Alert";
import { generateUuid } from "@kie-tools/boxed-expression-component/dist/api";
import { ExternalDmn } from "../DmnEditor";
import { Unpacked } from "../tsExt/tsExt";
import { useSettings } from "../settings/DmnEditorSettingsContext";
import { useRefactor } from "../refactor/RefactorConfirmationDialog";
import { useDmnEditorI18n } from "../i18n";

export type AllKnownDrgElementsByHref = Map<
  string,
  | ({ __$$element: "decision" } & Normalized<DMN_LATEST__tDecision>)
  | ({ __$$element: "inputData" } & Normalized<DMN_LATEST__tInputData>)
>;

export function DecisionServiceProperties({
  decisionService,
  namespace,
  index,
}: {
  decisionService: Normalized<DMN_LATEST__tDecisionService>;
  namespace: string | undefined;
  index: number;
}) {
  const { i18n } = useDmnEditorI18n();
  const { setState } = useDmnEditorStoreApi();
  const settings = useSettings();
  const { externalModelsByNamespace } = useExternalModels();

  const thisDmn = useDmnEditorStore((s) => s.dmn);

  const allExternalDmns = Object.entries(externalModelsByNamespace ?? {}).reduce((acc, [namespace, externalModel]) => {
    if (!externalModel) {
      console.warn(`DMN EDITOR: Could not find model with namespace '${namespace}'. Ignoring.`);
      return acc;
    }

    if (externalModel.type === "dmn") {
      acc.push(externalModel);
    }

    return acc;
  }, new Array<Normalized<ExternalDmn>>());

  const allDrgElementsByHref = useMemo(() => {
    const ret: AllKnownDrgElementsByHref = new Map();

    const allDmns = [{ model: thisDmn.model }, ...allExternalDmns.values()];

    for (let i = 0; i < allDmns.length; i++) {
      const anyDmn = allDmns[i]!;

      const namespace = anyDmn.model.definitions["@_namespace"];

      const drgElements = anyDmn.model.definitions.drgElement ?? [];
      for (let i = 0; i < drgElements.length; i++) {
        const element = drgElements[i];
        if (element.__$$element === "decision" || element.__$$element === "inputData") {
          ret.set(buildXmlHref({ namespace, id: element["@_id"]! }), element);
        }
      }
    }

    return ret;
  }, [allExternalDmns, thisDmn]);

  const thisDmnsNamespace = useDmnEditorStore((s) => s.dmn.model.definitions["@_namespace"]);
  const isReadOnly = settings.isReadOnly || (!!namespace && namespace !== thisDmnsNamespace);

  const { dmnEditorRootElementRef } = useDmnEditor();

  const resolvedTypeRef = useResolvedTypeRef(decisionService.variable?.["@_typeRef"], namespace);

  const identifierId = useMemo(() => decisionService["@_id"], [decisionService]);
  const oldName = useMemo(() => decisionService["@_label"] ?? decisionService["@_name"], [decisionService]);
  const { setNewIdentifierNameCandidate, refactorConfirmationDialog, newName } = useRefactor({
    index,
    identifierId,
    oldName,
  });
  const currentName = useMemo(() => {
    return newName === "" ? oldName : newName;
  }, [newName, oldName]);

  return (
    <>
      {refactorConfirmationDialog}
      <FormGroup label={i18n.name}>
        <InlineFeelNameInput
          enableAutoFocusing={false}
          isPlain={false}
          id={decisionService["@_id"]!}
          name={currentName}
          isReadOnly={isReadOnly}
          shouldCommitOnBlur={true}
          className={"pf-v5-c-form-control"}
          onRenamed={setNewIdentifierNameCandidate}
          allUniqueNames={useCallback((s) => s.computed(s).getAllFeelVariableUniqueNames(), [])}
        />
      </FormGroup>

      <FormGroup label={i18n.propertiesPanel.dataType}>
        <TypeRefSelector
          heightRef={dmnEditorRootElementRef}
          typeRef={resolvedTypeRef}
          isDisabled={isReadOnly}
          onChange={(newTypeRef) => {
            setState((state) => {
              const drgElement = state.dmn.model.definitions.drgElement![
                index
              ] as Normalized<DMN_LATEST__tDecisionService>;
              drgElement.variable ??= { "@_id": generateUuid(), "@_name": decisionService["@_name"] };
              drgElement.variable["@_typeRef"] = newTypeRef;
            });
          }}
        />
      </FormGroup>

      <FormGroup label={i18n.propertiesPanel.description}>
        <TextArea
          aria-label={"Description"}
          type={"text"}
          isDisabled={isReadOnly}
          value={decisionService.description?.__$$text ?? ""}
          onChange={(_event, newDescription) => {
            setState((state) => {
              (state.dmn.model.definitions.drgElement![index] as Normalized<DMN_LATEST__tDecisionService>).description =
                {
                  __$$text: newDescription,
                };
            });
          }}
          placeholder={i18n.propertiesPanel.descriptionPlaceholder}
          style={{ resize: "vertical", minHeight: "40px" }}
          rows={6}
        />
      </FormGroup>

      <FormGroup label={i18n.propertiesPanel.id}>
        <ClipboardCopy isReadOnly={true} hoverTip="Copy" clickTip="Copied">
          {decisionService["@_id"]}
        </ClipboardCopy>
      </FormGroup>

      <FormGroup
        label={i18n.propertiesPanel.outputDecisions}
        data-testid={"kie-tools--dmn-editor--decision-service-output-decisions"}
      >
        <DecisionServiceElementList
          decisionServiceNamespace={namespace}
          elements={decisionService.outputDecision}
          allDrgElementsByHref={allDrgElementsByHref}
        />
      </FormGroup>
      <FormGroup
        label={i18n.propertiesPanel.encapsulatedDecisions}
        data-testid={"kie-tools--dmn-editor--decision-service-encapsulated-decisions"}
      >
        <DecisionServiceElementList
          decisionServiceNamespace={namespace}
          elements={decisionService.encapsulatedDecision}
          allDrgElementsByHref={allDrgElementsByHref}
        />
      </FormGroup>

      <Divider />
      <FormGroup
        label={i18n.propertiesPanel.inputDecisions}
        data-testid={"kie-tools--dmn-editor--decision-service-input-decisions"}
      >
        <DraggableDecisionServiceElementList
          decisionServiceNamespace={namespace}
          elements={decisionService.inputDecision}
          allDrgElementsByHref={allDrgElementsByHref}
          onChange={(newInputDecisions) => {
            setState((state) => {
              (
                state.dmn.model.definitions.drgElement![index] as Normalized<DMN_LATEST__tDecisionService>
              ).inputDecision = newInputDecisions;
            });
          }}
          isDisabled={isReadOnly}
        />
      </FormGroup>
      <FormGroup
        label={i18n.propertiesPanel.inputData}
        data-testid={"kie-tools--dmn-editor--decision-service-input-data"}
      >
        <DraggableDecisionServiceElementList
          decisionServiceNamespace={namespace}
          elements={decisionService.inputData}
          allDrgElementsByHref={allDrgElementsByHref}
          onChange={(newInputData) => {
            setState((state) => {
              (state.dmn.model.definitions.drgElement![index] as Normalized<DMN_LATEST__tDecisionService>).inputData =
                newInputData;
            });
          }}
          isDisabled={isReadOnly}
        />
      </FormGroup>

      <DecisionServiceEquivalentFunction
        decisionService={decisionService}
        decisionServiceNamespace={namespace}
        allDrgElementsByHref={allDrgElementsByHref}
      />

      <DocumentationLinksFormGroup
        isReadOnly={isReadOnly}
        values={decisionService.extensionElements?.["kie:attachment"]}
        onChange={(newExtensionElements) => {
          setState((state) => {
            (
              state.dmn.model.definitions.drgElement![index] as Normalized<DMN_LATEST__tDecisionService>
            ).extensionElements = {
              "kie:attachment": newExtensionElements,
            };
          });
        }}
      />
    </>
  );
}

export function DecisionServiceElementList({
  decisionServiceNamespace,
  elements,
  allDrgElementsByHref,
}: {
  decisionServiceNamespace: string | undefined;
  elements: Normalized<DMN_LATEST__tDecisionService>["outputDecision"];
  allDrgElementsByHref: AllKnownDrgElementsByHref;
}) {
  const { i18n } = useDmnEditorI18n();
  const thisDmnsNamespace = useDmnEditorStore((s) => s.dmn.model.definitions["@_namespace"]);

  return (
    <ul>
      {(elements ?? []).length <= 0 && (
        <li style={{ paddingLeft: "32px" }}>
          <small>
            <i>({i18n.propertiesPanel.empty})</i>
          </small>
        </li>
      )}
      {(elements ?? []).map((e) => {
        const localHref = parseXmlHref(e["@_href"]);

        // If the localHref has a namespace, then that's the one to use, as it can be that an external node is pointing to another external node in their perspective
        // E.g., (This DMN) --includes--> (DMN A) --includes--> (DMN B)
        // In this case, localHref will have DMN B's namespace, and we need to respect it. It `DMN B` in included in `This DMN`, then
        // we can resolve it, otherwise, the dmnObject referenced by localHref won't be present on `dmnObjectsByHref`, and we'll only show the href.
        // Now, if the localHref doesn't have a namespace, then it is local to the model where the Decision Service is declared, so we use `decisionServiceNamespace`.
        // If none of that is true, then it means that the Decision Service is local to "This DMN", so the namespace is simply "".
        const resolvedNamespace = localHref.namespace ?? decisionServiceNamespace ?? thisDmnsNamespace;

        const potentialExternalHref = buildXmlHref({
          namespace: resolvedNamespace,
          id: localHref.id,
        });

        return (
          <li style={{ paddingLeft: "32px" }} key={potentialExternalHref}>
            <DmnObjectListItem
              dmnObjectHref={potentialExternalHref}
              dmnObject={allDrgElementsByHref.get(potentialExternalHref)}
              relativeToNamespace={thisDmnsNamespace}
              namespace={resolvedNamespace}
            />
          </li>
        );
      })}
    </ul>
  );
}

export function DraggableDecisionServiceElementList({
  decisionServiceNamespace,
  elements,
  allDrgElementsByHref,
  onChange,
  isDisabled,
}: {
  decisionServiceNamespace: string | undefined;
  elements: Normalized<DMN_LATEST__tDecisionService>["outputDecision"];
  allDrgElementsByHref: AllKnownDrgElementsByHref;
  onChange: (hrefs: Normalized<DMN_LATEST__tDMNElementReference>[] | undefined) => void;
  isDisabled: boolean;
}) {
  const { i18n } = useDmnEditorI18n();
  const thisDmnsNamespace = useDmnEditorStore((s) => s.dmn.model.definitions["@_namespace"]);
  const [keys, setKeys] = React.useState(() => elements?.map((e) => e["@_href"]) ?? []);

  const onDragEnd = useCallback(
    (source: number, dest: number) => {
      const reordened = [...(elements ?? [])];
      const [removed] = reordened.splice(source, 1);
      reordened.splice(dest, 0, removed);
      onChange(reordened);
    },
    [elements, onChange]
  );

  const reorder = useCallback((source: number, dest: number) => {
    setKeys((prev) => {
      const reordenedUuid = [...prev];
      const [removedUuid] = reordenedUuid.splice(source, 1);
      reordenedUuid.splice(dest, 0, removedUuid);
      return reordenedUuid;
    });
  }, []);

  const draggableItem = useCallback(
    (element: Normalized<DMN_LATEST__tDMNElementReference>, index: number) => {
      const localHref = parseXmlHref(element["@_href"]);

      // If the localHref has a namespace, then that's the one to use, as it can be that an external node is pointing to another external node in their perspective
      // E.g., (This DMN) --includes--> (DMN A) --includes--> (DMN B)
      // In this case, localHref will have DMN B's namespace, and we need to respect it. It `DMN B` in included in `This DMN`, then
      // we can resolve it, otherwise, the dmnObject referenced by localHref won't be present on `dmnObjectsByHref`, and we'll only show the href.
      // Now, if the localHref doesn't have a namespace, then it is local to the model where the Decision Service is declared, so we use `decisionServiceNamespace`.
      // If none of that is true, then it means that the Decision Service is local to "This DMN", so the namespace is simply "".
      const resolvedNamespace = localHref.namespace ?? decisionServiceNamespace ?? thisDmnsNamespace;

      const potentialExternalHref = buildXmlHref({
        namespace: resolvedNamespace,
        id: localHref.id,
      });

      return (
        <Draggable
          key={keys[index]}
          index={index}
          handlerStyle={
            keys[index] ? { paddingLeft: "16px", paddingRight: "16px" } : { paddingLeft: "16px", paddingRight: "16px" }
          }
          isDisabled={isDisabled}
        >
          <li key={potentialExternalHref}>
            <DmnObjectListItem
              dmnObjectHref={potentialExternalHref}
              dmnObject={allDrgElementsByHref.get(potentialExternalHref)}
              relativeToNamespace={thisDmnsNamespace}
              namespace={resolvedNamespace}
            />
          </li>
        </Draggable>
      );
    },
    [allDrgElementsByHref, decisionServiceNamespace, isDisabled, keys, thisDmnsNamespace]
  );

  return (
    <ul>
      {(elements ?? []).length <= 0 && (
        <li style={{ paddingLeft: "32px" }}>
          <small>
            <i>({i18n.propertiesPanel.empty})</i>
          </small>
        </li>
      )}
      <DragAndDrop
        reorder={reorder}
        onDragEnd={onDragEnd}
        values={elements}
        draggableItem={draggableItem}
        isDisabled={isDisabled}
      />
    </ul>
  );
}

function DecisionServiceEquivalentFunction({
  decisionService,
  allDrgElementsByHref,
  decisionServiceNamespace,
}: {
  decisionService: Normalized<DMN_LATEST__tDecisionService>;
  allDrgElementsByHref: AllKnownDrgElementsByHref;
  decisionServiceNamespace: string | undefined;
}) {
  const { i18n } = useDmnEditorI18n();
  const importsByNamespace = useDmnEditorStore((s) => s.computed(s).importsByNamespace());
  const thisDmnsNamespace = useDmnEditorStore((s) => s.dmn.model.definitions["@_namespace"]);

  const getNodeNameByHref = useCallback(
    (href: string) => {
      const localHref = parseXmlHref(href);

      // If the localHref has a namespace, then that's the one to use, as it can be that an external node is pointing to another external node in their perspective
      // E.g., (This DMN) --includes--> (DMN A) --includes--> (DMN B)
      // In this case, localHref will have DMN B's namespace, and we need to respect it. It `DMN B` in included in `This DMN`, then
      // we can resolve it, otherwise, the dmnObject referenced by localHref won't be present on `dmnObjectsByHref`, and we'll only show the href.
      // Now, if the localHref doesn't have a namespace, then it is local to the model where the Decision Service is declared, so we use `decisionServiceNamespace`.
      // If none of that is true, then it means that the Decision Service is local to "This DMN", so the namespace is simply "".
      const resolvedNamespace = localHref.namespace ?? decisionServiceNamespace ?? thisDmnsNamespace;

      const potentialExternalHref = buildXmlHref({
        namespace: resolvedNamespace,
        id: localHref.id,
      });

      const dmnObject = allDrgElementsByHref.get(potentialExternalHref);

      const isNamespaceDirectlyIncluded =
        importsByNamespace.has(resolvedNamespace) || resolvedNamespace === thisDmnsNamespace;

      return dmnObject && isNamespaceDirectlyIncluded
        ? buildFeelQNameFromNamespace({
            namedElement: dmnObject,
            importsByNamespace,
            namespace: resolvedNamespace,
            relativeToNamespace: thisDmnsNamespace,
          }).full
        : buildDisplayNameForDmnObject(dmnObject, resolvedNamespace);
    },
    [allDrgElementsByHref, decisionServiceNamespace, importsByNamespace, thisDmnsNamespace]
  );

  const buildFunctionArgList = useCallback(
    (
      inputDecisions?: Normalized<DMN_LATEST__tDMNElementReference>[],
      inputData?: Normalized<DMN_LATEST__tDMNElementReference>[]
    ) => {
      const inputDecisionNodeNames = inputDecisions?.map((ide) => getNodeNameByHref(ide["@_href"]));
      const inputDataNodeNames = inputData?.map((ida) => getNodeNameByHref(ida["@_href"]));

      return [...(inputDecisionNodeNames ?? []), ...(inputDataNodeNames ?? [])].reduce(
        (acc, name) => (acc ? `${acc}, ${name}` : name),
        ""
      );
    },
    [getNodeNameByHref]
  );

  return (
    <Alert variant={AlertVariant.info} isInline title={i18n.propertiesPanel.invokingDecisionService}>
      <p data-testid={"kie-tools--dmn-editor--decision-service-feel"} style={{ fontFamily: "monospace" }}>
        {`${decisionService["@_name"]}(${buildFunctionArgList(
          decisionService.inputDecision,
          decisionService.inputData
        )})`}
      </p>
    </Alert>
  );
}

function buildDisplayNameForDmnObject(
  dmnObject: Unpacked<Normalized<DMN_LATEST__tDefinitions>["drgElement"]> | undefined,
  namespace: string
) {
  return `${namespace.substring(0, 11)}...${namespace.substring(namespace.length - 4)}.${dmnObject?.["@_name"]}`;
}
