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
import { DmnBuiltInDataType } from "@kie-tools/boxed-expression-component/dist/api";
import { buildFeelQNameFromXmlQName } from "../feel/buildFeelQName";
import { useMemo } from "react";
import { buildXmlQName, parseXmlQName } from "@kie-tools/xml-parser-ts/dist/qNames";
import { useDmnEditorStore } from "../store/StoreContext";
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
  const importsByNamespace = useDmnEditorStore((s) => s.computed(s).importsByNamespace());
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
      rootElement: thisDmn.model.definitions,
      namespace: relativeToNamespace ?? "",
    });

    // DMN typeRefs are *NOT* XML QNames, but we use them to make it easier to build the FEEL QName.
    const xmlQName = parseXmlQName(
      buildXmlQName({
        type: "xml-qname",
        prefix: xmlNamespaceName,
        localPart: parsedFeelQName.importName ? parsedFeelQName.localPart : typeRef,
      })
    );

    // This is a special case for some DMNs which still declare typeRefs as XML QNames. We leave them unaltered.
    if (xmlQName.prefix) {
      return typeRef;
    }

    const fullFeelQName = buildFeelQNameFromXmlQName({
      importsByNamespace,
      relativeToNamespace: thisDmn.model.definitions["@_namespace"],
      model: thisDmn.model.definitions,
      namedElement: { "@_name": parsedFeelQName.importName ? parsedFeelQName.localPart : typeRef },
      namedElementQName: xmlQName,
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
