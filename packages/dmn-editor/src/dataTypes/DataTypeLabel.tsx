import * as React from "react";
import { DmnBuiltInDataType } from "@kie-tools/boxed-expression-component/dist/api";
import { buildFeelQNameFromXmlQName } from "../feel/buildFeelQName";
import { useMemo } from "react";
import { useDmnEditorDerivedStore } from "../store/DerivedStore";
import { buildXmlQName, parseXmlQName } from "../xml/xmlQNames";
import { useDmnEditorStore } from "../store/Store";
import { getXmlNamespaceDeclarationName } from "../xml/xmlNamespaceDeclarations";
import { parseFeelQName } from "../feel/parseFeelQName";

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

    const parsedFeelQName = parseFeelQName(typeRef);

    const fullFeelQName = buildFeelQNameFromXmlQName({
      importsByNamespace,
      relativeToNamespace: thisDmn.model.definitions["@_namespace"],
      model: thisDmn.model.definitions,
      namedElement: { "@_name": parsedFeelQName.importName ? parsedFeelQName.localPart : typeRef },
      namedElementQName: parseXmlQName(
        buildXmlQName({
          type: "xml-qname",
          prefix: getXmlNamespaceDeclarationName({ model: thisDmn.model.definitions, namespace: namespace ?? "" }),
          localPart: parsedFeelQName.importName ? parsedFeelQName.localPart : typeRef,
        })
      ),
    }).full;

    if (parsedFeelQName.importName) {
      if (typeRef !== fullFeelQName) {
        console.warn(
          `DMN EDITOR: Data Type label was rendered with discrepancy between provided namespace and the FEEL QName. Going with the provided typeRef. (typeRef: '${typeRef}', feelQName: '${fullFeelQName}').`
        );
      }
      return typeRef;
    }

    return fullFeelQName;
  }, [thisDmn.model.definitions, importsByNamespace, namespace, typeRef]);

  return (
    <span className={"kie-dmn-editor--data-type-label"}>
      <i>
        {typeRef && (
          <>
            <span>{`(`}</span>
            {`${feelName ?? DmnBuiltInDataType.Undefined}`}
            {isCollection && (
              <>
                &nbsp;
                {`[]`}
              </>
            )}
            <span>{`)`}</span>
          </>
        )}
      </i>
    </span>
  );
}
