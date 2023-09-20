import * as React from "react";
import {
  DMN15__tDecision,
  DMN15__tDecisionService,
  DMN15__tInputData,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { ClipboardCopy } from "@patternfly/react-core/dist/js/components/ClipboardCopy";
import { FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import { TextArea } from "@patternfly/react-core/dist/js/components/TextArea";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { DocumentationLinksInput } from "./DocumentationLinksInput";
import { DataTypeSelector } from "../dataTypes/DataTypeSelector";
import { useDmnEditorStore, useDmnEditorStoreApi } from "../store/Store";
import { XmlQName } from "../xml/xmlQNames";
import { useOtherDmns } from "../includedModels/DmnEditorDependenciesContext";
import { useMemo } from "react";
import { buildXmlHref, parseXmlHref } from "../xml/xmlHrefs";
import { DmnObjectListItem } from "../externalNodes/DmnObjectListItem";

export type AllKnownDrgElementsByHref = Map<
  string,
  ({ __$$element: "decision" } & DMN15__tDecision) | ({ __$$element: "inputData" } & DMN15__tInputData)
>;

export function DecisionServiceProperties({
  decisionService,
  decisionServiceNamespace,
  index,
}: {
  decisionService: DMN15__tDecisionService;
  decisionServiceNamespace: string | undefined;
  index: number;
}) {
  const { setState } = useDmnEditorStoreApi();

  const thisDmn = useDmnEditorStore((s) => s.dmn);
  const { otherDmnsByNamespace } = useOtherDmns();

  const alldrgElementsByHref = useMemo(() => {
    const ret: AllKnownDrgElementsByHref = new Map();

    const allOtherDmns = [{ model: thisDmn.model }, ...Object.values(otherDmnsByNamespace)];

    for (let i = 0; i < allOtherDmns.length; i++) {
      const anyDmn = allOtherDmns[i]!;

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
  }, [otherDmnsByNamespace, thisDmn]);

  return (
    <>
      <FormGroup label="Name">
        <TextInput
          aria-label={"Name"}
          type={"text"}
          isDisabled={false}
          onChange={(newName) => {
            setState((state) => {
              (state.dmn.model.definitions.drgElement![index] as DMN15__tDecisionService).variable!["@_name"] = newName;
              (state.dmn.model.definitions.drgElement![index] as DMN15__tDecisionService)["@_name"] = newName;
            });
          }}
          value={decisionService["@_name"]}
          placeholder={"Enter a name..."}
        />
      </FormGroup>

      <FormGroup label="Data type">
        <DataTypeSelector
          name={decisionService.variable?.["@_typeRef"]}
          onChange={(newTypeRef) => {
            setState((state) => {
              (state.dmn.model.definitions.drgElement![index] as DMN15__tDecisionService).variable!["@_typeRef"] =
                newTypeRef;
            });
          }}
        />
      </FormGroup>

      <FormGroup label="Description">
        <TextArea
          aria-label={"Description"}
          type={"text"}
          isDisabled={false}
          value={decisionService.description}
          onChange={(newDescription) => {
            setState((state) => {
              (state.dmn.model.definitions.drgElement![index] as DMN15__tDecisionService).description = newDescription;
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

      <FormGroup label="Output decisions">
        <DecisionServiceElementList
          decisionServiceNamespace={decisionServiceNamespace}
          elements={decisionService.outputDecision}
          alldrgElementsByHref={alldrgElementsByHref}
        />
      </FormGroup>
      <FormGroup label="Encapsulated decisions">
        <DecisionServiceElementList
          decisionServiceNamespace={decisionServiceNamespace}
          elements={decisionService.encapsulatedDecision}
          alldrgElementsByHref={alldrgElementsByHref}
        />
      </FormGroup>
      <FormGroup label="Input decisions">
        <DecisionServiceElementList
          decisionServiceNamespace={decisionServiceNamespace}
          elements={decisionService.inputDecision}
          alldrgElementsByHref={alldrgElementsByHref}
        />
      </FormGroup>
      <FormGroup label="Input data">
        <DecisionServiceElementList
          decisionServiceNamespace={decisionServiceNamespace}
          elements={decisionService.inputData}
          alldrgElementsByHref={alldrgElementsByHref}
        />
      </FormGroup>

      <FormGroup label="Documentation links (Work in progress 🔧)">
        <DocumentationLinksInput />
      </FormGroup>
    </>
  );
}

export function DecisionServiceElementList({
  decisionServiceNamespace,
  elements,
  alldrgElementsByHref,
}: {
  decisionServiceNamespace: string | undefined;
  elements: DMN15__tDecisionService["outputDecision"];
  alldrgElementsByHref: AllKnownDrgElementsByHref;
}) {
  const thisDmn = useDmnEditorStore((s) => s.dmn);

  return (
    <ul>
      {(elements ?? []).length <= 0 && (
        <li>
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
        const resolvedNamespace =
          localHref.namespace ?? decisionServiceNamespace ?? thisDmn.model.definitions["@_namespace"];

        const potentialExternalHref = buildXmlHref({
          namespace: resolvedNamespace,
          id: localHref.id,
        });

        return (
          <li key={potentialExternalHref}>
            <DmnObjectListItem
              dmnObjectHref={potentialExternalHref}
              dmnObject={alldrgElementsByHref.get(potentialExternalHref)}
              relativeToNamespace={thisDmn.model.definitions["@_namespace"]}
              namespace={resolvedNamespace}
            />
          </li>
        );
      })}
    </ul>
  );
}
