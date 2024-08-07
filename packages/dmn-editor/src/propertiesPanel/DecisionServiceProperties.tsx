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
  DMN15__tDMNElementReference,
  DMN15__tDecision,
  DMN15__tDecisionService,
  DMN15__tInputData,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { ClipboardCopy } from "@patternfly/react-core/dist/js/components/ClipboardCopy";
import { FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import { TextArea } from "@patternfly/react-core/dist/js/components/TextArea";
import { DocumentationLinksFormGroup } from "./DocumentationLinksFormGroup";
import { TypeRefSelector } from "../dataTypes/TypeRefSelector";
import { useDmnEditorStore, useDmnEditorStoreApi } from "../store/StoreContext";
import { useCallback, useMemo } from "react";
import { buildXmlHref, parseXmlHref } from "../xml/xmlHrefs";
import { DmnObjectListItem } from "../externalNodes/DmnObjectListItem";
import { renameDrgElement } from "../mutations/renameNode";
import { InlineFeelNameInput } from "../feel/InlineFeelNameInput";
import { Divider } from "@patternfly/react-core/dist/js/components/Divider";
import { useDmnEditor } from "../DmnEditorContext";
import { useResolvedTypeRef } from "../dataTypes/useResolvedTypeRef";
import { useExternalModels } from "../includedModels/DmnEditorDependenciesContext";
import { DragAndDrop, Draggable } from "../draggable/Draggable";
import { buildFeelQNameFromNamespace } from "../feel/buildFeelQName";
import { Alert, AlertVariant } from "@patternfly/react-core/dist/js/components/Alert/Alert";
import { Normalized } from "../normalization/normalize";
import { generateUuid } from "@kie-tools/boxed-expression-component/dist/api";

export type AllKnownDrgElementsByHref = Map<
  string,
  | ({ __$$element: "decision" } & Normalized<DMN15__tDecision>)
  | ({ __$$element: "inputData" } & Normalized<DMN15__tInputData>)
>;

export function DecisionServiceProperties({
  decisionService,
  namespace,
  index,
}: {
  decisionService: Normalized<DMN15__tDecisionService>;
  namespace: string | undefined;
  index: number;
}) {
  const { setState } = useDmnEditorStoreApi();

  const thisDmn = useDmnEditorStore((s) => s.dmn);
  const { externalModelsByNamespace } = useExternalModels();
  const externalDmnsByNamespace = useDmnEditorStore(
    (s) => s.computed(s).getExternalModelTypesByNamespace(externalModelsByNamespace).dmns
  );

  const allDrgElementsByHref = useMemo(() => {
    const ret: AllKnownDrgElementsByHref = new Map();

    const allDmns = [{ model: thisDmn.model }, ...externalDmnsByNamespace.values()];

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
  }, [externalDmnsByNamespace, thisDmn]);

  const thisDmnsNamespace = useDmnEditorStore((s) => s.dmn.model.definitions["@_namespace"]);
  const isReadonly = !!namespace && namespace !== thisDmnsNamespace;

  const { dmnEditorRootElementRef } = useDmnEditor();

  const resolvedTypeRef = useResolvedTypeRef(decisionService.variable?.["@_typeRef"], namespace);

  return (
    <>
      <FormGroup label="Name">
        <InlineFeelNameInput
          enableAutoFocusing={false}
          isPlain={false}
          id={decisionService["@_id"]!}
          name={decisionService["@_name"]}
          isReadonly={isReadonly}
          shouldCommitOnBlur={true}
          className={"pf-c-form-control"}
          onRenamed={(newName) => {
            setState((state) => {
              renameDrgElement({
                definitions: state.dmn.model.definitions,
                index,
                newName,
              });
            });
          }}
          allUniqueNames={useCallback((s) => s.computed(s).getAllFeelVariableUniqueNames(), [])}
        />
      </FormGroup>

      <FormGroup label="Data type">
        <TypeRefSelector
          heightRef={dmnEditorRootElementRef}
          typeRef={resolvedTypeRef}
          isDisabled={isReadonly}
          onChange={(newTypeRef) => {
            setState((state) => {
              const drgElement = state.dmn.model.definitions.drgElement![index] as Normalized<DMN15__tDecisionService>;
              drgElement.variable ??= { "@_id": generateUuid(), "@_name": decisionService["@_name"] };
              drgElement.variable["@_typeRef"] = newTypeRef;
            });
          }}
        />
      </FormGroup>

      <FormGroup label="Description">
        <TextArea
          aria-label={"Description"}
          type={"text"}
          isDisabled={isReadonly}
          value={decisionService.description?.__$$text}
          onChange={(newDescription) => {
            setState((state) => {
              (state.dmn.model.definitions.drgElement![index] as Normalized<DMN15__tDecisionService>).description = {
                __$$text: newDescription,
              };
            });
          }}
          placeholder={"Enter a description..."}
          style={{ resize: "vertical", minHeight: "40px" }}
          rows={6}
        />
      </FormGroup>

      <FormGroup label="ID">
        <ClipboardCopy isReadOnly={true} hoverTip="Copy" clickTip="Copied">
          {decisionService["@_id"]}
        </ClipboardCopy>
      </FormGroup>

      <FormGroup label="Output decisions" data-testid={"kie-tools--dmn-editor--decision-service-output-decisions"}>
        <DecisionServiceElementList
          decisionServiceNamespace={namespace}
          elements={decisionService.outputDecision}
          allDrgElementsByHref={allDrgElementsByHref}
        />
      </FormGroup>
      <FormGroup
        label="Encapsulated decisions"
        data-testid={"kie-tools--dmn-editor--decision-service-encapsulated-decisions"}
      >
        <DecisionServiceElementList
          decisionServiceNamespace={namespace}
          elements={decisionService.encapsulatedDecision}
          allDrgElementsByHref={allDrgElementsByHref}
        />
      </FormGroup>

      <Divider />
      <FormGroup label="Input decisions" data-testid={"kie-tools--dmn-editor--decision-service-input-decisions"}>
        <DraggableDecisionServiceElementList
          decisionServiceNamespace={namespace}
          elements={decisionService.inputDecision}
          allDrgElementsByHref={allDrgElementsByHref}
          onChange={(newInputDecisions) => {
            setState((state) => {
              (state.dmn.model.definitions.drgElement![index] as Normalized<DMN15__tDecisionService>).inputDecision =
                newInputDecisions;
            });
          }}
          isDisabled={isReadonly}
        />
      </FormGroup>
      <FormGroup label="Input data" data-testid={"kie-tools--dmn-editor--decision-service-input-data"}>
        <DraggableDecisionServiceElementList
          decisionServiceNamespace={namespace}
          elements={decisionService.inputData}
          allDrgElementsByHref={allDrgElementsByHref}
          onChange={(newInputData) => {
            setState((state) => {
              (state.dmn.model.definitions.drgElement![index] as Normalized<DMN15__tDecisionService>).inputData =
                newInputData;
            });
          }}
          isDisabled={isReadonly}
        />
      </FormGroup>

      <DecisionServiceEquivalentFunction
        decisionService={decisionService}
        decisionServiceNamespace={namespace}
        allDrgElementsByHref={allDrgElementsByHref}
      />

      <DocumentationLinksFormGroup
        isReadonly={isReadonly}
        values={decisionService.extensionElements?.["kie:attachment"]}
        onChange={(newExtensionElements) => {
          setState((state) => {
            (state.dmn.model.definitions.drgElement![index] as Normalized<DMN15__tDecisionService>).extensionElements =
              {
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
  elements: Normalized<DMN15__tDecisionService>["outputDecision"];
  allDrgElementsByHref: AllKnownDrgElementsByHref;
}) {
  const thisDmnsNamespace = useDmnEditorStore((s) => s.dmn.model.definitions["@_namespace"]);

  return (
    <ul>
      {(elements ?? []).length <= 0 && (
        <li style={{ paddingLeft: "32px" }}>
          <small>
            <i>(Empty)</i>
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
  elements: Normalized<DMN15__tDecisionService>["outputDecision"];
  allDrgElementsByHref: AllKnownDrgElementsByHref;
  onChange: (hrefs: Normalized<DMN15__tDMNElementReference>[] | undefined) => void;
  isDisabled: boolean;
}) {
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
    (element: Normalized<DMN15__tDMNElementReference>, index: number) => {
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
            <i>(Empty)</i>
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
  decisionService: Normalized<DMN15__tDecisionService>;
  allDrgElementsByHref: AllKnownDrgElementsByHref;
  decisionServiceNamespace: string | undefined;
}) {
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

      return dmnObject
        ? buildFeelQNameFromNamespace({
            namedElement: dmnObject,
            importsByNamespace,
            namespace: resolvedNamespace,
            relativeToNamespace: thisDmnsNamespace,
          }).full
        : potentialExternalHref;
    },
    [allDrgElementsByHref, decisionServiceNamespace, importsByNamespace, thisDmnsNamespace]
  );

  const buildFunctionArgList = useCallback(
    (
      inputDecisions?: Normalized<DMN15__tDMNElementReference>[],
      inputData?: Normalized<DMN15__tDMNElementReference>[]
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
    <Alert variant={AlertVariant.info} isInline title="Invoking this Decision Service in FEEL">
      <p data-testid={"kie-tools--dmn-editor--decision-service-feel"} style={{ fontFamily: "monospace" }}>
        {`${decisionService["@_name"]}(${buildFunctionArgList(
          decisionService.inputDecision,
          decisionService.inputData
        )})`}
      </p>
    </Alert>
  );
}
