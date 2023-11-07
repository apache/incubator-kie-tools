import * as React from "react";
import { DmnBuiltInDataType } from "@kie-tools/boxed-expression-component/dist/api";
import { buildFeelQNameFromXmlQName } from "../feel/buildFeelQName";
import { useMemo } from "react";
import { useDmnEditorDerivedStore } from "../store/DerivedStore";
import { buildXmlQName, parseXmlQName } from "@kie-tools/xml-parser-ts/dist/qNames";
import { useDmnEditorStore } from "../store/Store";
import { getXmlNamespaceDeclarationName } from "../xml/xmlNamespaceDeclarations";
import { parseFeelQName } from "../feel/parseFeelQName";
import { builtInFeelTypeNames } from "./BuiltInFeelTypes";

export function TypeRefLabel({
  typeRef,
  relativeToNamespace,
  isCollection,
}: {
  isCollection: boolean | undefined;
  typeRef: string | undefined;
  relativeToNamespace?: string;
}) {
  const { importsByNamespace } = useDmnEditorDerivedStore();
  const thisDmn = useDmnEditorStore((s) => s.dmn);

  const feelName = useMemo(() => {
    if (!typeRef) {
      return undefined;
    }

    // Built-in FEEL types are never namespaced
    if (builtInFeelTypeNames.has(typeRef)) {
      return typeRef;
    }

    const parsedFeelQName = parseFeelQName(typeRef);

    const xmlNamespaceName = getXmlNamespaceDeclarationName({
      model: thisDmn.model.definitions,
      namespace: relativeToNamespace ?? "",
    });

    // DMN typeRefs are *NOT* XML QNames, but we use them to make it easier to build the FEEL QName.
    const xmlQName = buildXmlQName({
      type: "xml-qname",
      prefix: xmlNamespaceName,
      localPart: parsedFeelQName.importName ? parsedFeelQName.localPart : typeRef,
    });

    const fullFeelQName = buildFeelQNameFromXmlQName({
      importsByNamespace,
      relativeToNamespace: thisDmn.model.definitions["@_namespace"],
      model: thisDmn.model.definitions,
      namedElement: { "@_name": parsedFeelQName.importName ? parsedFeelQName.localPart : typeRef },
      namedElementQName: parseXmlQName(xmlQName),
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
  }, [thisDmn.model.definitions, importsByNamespace, relativeToNamespace, typeRef]);

  return (
    <span className={"kie-dmn-editor--data-type-label"}>
      <i>
        {(typeRef && (
          <>
            {`${feelName ?? DmnBuiltInDataType.Undefined}`}
            {isCollection && (
              <>
                &nbsp;
                {`[]`}
              </>
            )}
          </>
        )) || <>{isCollection && <>{`[]`}</>}</>}
      </i>
    </span>
  );
}
