import * as React from "react";
import { DmnBuiltInDataType } from "@kie-tools/boxed-expression-component/dist/api";
import { buildFeelQNameFromXmlQName } from "../feel/buildFeelQName";
import { useMemo } from "react";
import { useDmnEditorDerivedStore } from "../store/DerivedStore";
import { buildXmlQName, parseXmlQName } from "../xml/xmlQNames";
import { useDmnEditorStore } from "../store/Store";
import { getXmlNamespaceName } from "../xml/xmlNamespaceDeclarations";

const builtInDataTypes = new Set<string>(Object.values(DmnBuiltInDataType));

export function DataTypeLabel({
  typeRef,
  namespace,
  isCollection,
}: {
  isCollection: boolean | undefined;
  typeRef: string | undefined;
  namespace?: string;
}) {
  const { importsByNamespace } = useDmnEditorDerivedStore();
  const thisDmn = useDmnEditorStore((s) => s.dmn);

  const feelName = useMemo(() => {
    if (!typeRef) {
      return undefined;
    }

    // Built-in FEEL types are never namespaced
    if (builtInDataTypes.has(typeRef)) {
      return typeRef;
    }

    return buildFeelQNameFromXmlQName({
      importsByNamespace,
      model: thisDmn.model.definitions,
      namedElement: { "@_name": typeRef },
      namedElementQName: parseXmlQName(
        buildXmlQName({
          type: "xml-qname",
          prefix: getXmlNamespaceName({ model: thisDmn.model.definitions, namespace: namespace ?? "" }),
          localPart: typeRef,
        })
      ),
    }).full;
  }, [thisDmn.model.definitions, importsByNamespace, namespace, typeRef]);

  return (
    <span className={"kie-dmn-editor--data-type-label"}>
      &nbsp;
      <i>{`(${feelName ?? DmnBuiltInDataType.Undefined}${isCollection ? ` []` : ``})`}</i>
    </span>
  );
}
