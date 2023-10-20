import * as React from "react";
import { Alert, AlertVariant } from "@patternfly/react-core/dist/esm/components/Alert/Alert";
import { DMN15__tDefinitions, DMNDI15__DMNShape } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { useDmnEditorStore } from "../store/Store";
import { useMemo } from "react";
import { Unpacked } from "../tsExt/tsExt";
import { useDmnEditorDerivedStore } from "../store/DerivedStore";
import { XmlQName, buildXmlQName } from "@kie-tools/xml-parser-ts/dist/qNames";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { useDmnEditor } from "../DmnEditorContext";
import { Divider } from "@patternfly/react-core/dist/js/components/Divider";

export function UnknownProperties(props: { shape: DMNDI15__DMNShape; dmnElementRefQName: XmlQName }) {
  const thisDmn = useDmnEditorStore((s) => s.dmn);

  const { externalDmnsByNamespace } = useDmnEditorDerivedStore();
  const { onRequestToJumpToPath } = useDmnEditor();

  const content = useMemo(() => {
    const namespace = thisDmn.model.definitions[`@_xmlns:${props.dmnElementRefQName.prefix}`];
    if (!namespace) {
      return <p>{`This node references an external node with a namespace that is not declared at this DMN.`}</p>;
    }

    const externalDmn = externalDmnsByNamespace.get(namespace);
    if (!externalDmn) {
      // Nothing that the user can do.
      return (
        <p>{`This node references an external node from a namespace that is not provided on this DMN's external DMNs mapping. `}</p>
      );
    }

    const externalDrgElementsById = (externalDmn.model.definitions.drgElement ?? []).reduce(
      (acc, e, index) => acc.set(e["@_id"]!, { element: e, index }),
      new Map<string, { index: number; element: Unpacked<DMN15__tDefinitions["drgElement"]> }>()
    );

    const externalDrgElement = externalDrgElementsById.get(props.dmnElementRefQName.localPart);
    if (!externalDrgElement) {
      return (
        <>
          <p>{`This node references a DRG element from '${externalDmn.model.definitions["@_name"]}' that doesn't exist.`}</p>
          {onRequestToJumpToPath && (
            <>
              <br />
              <Button
                style={{ paddingLeft: 0 }}
                variant={ButtonVariant.link}
                onClick={() => onRequestToJumpToPath?.(externalDmn.path)}
              >{`Go to '${externalDmn.model.definitions["@_name"]}'`}</Button>
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
  ]);

  return (
    <>
      <Alert title={"This is a placeholder for an unknown node"} isInline={true} variant={AlertVariant.danger}>
        <br />
        {content}
        <Divider style={{ marginTop: "16px" }} />
        <br />
        <p>
          <b>Reference:</b>&nbsp;{`${buildXmlQName(props.dmnElementRefQName)}`}
        </p>
      </Alert>
    </>
  );
}