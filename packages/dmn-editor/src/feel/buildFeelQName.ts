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

import { DMN15__tImport, DMN15__tNamedElement } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { XmlQName } from "@kie-tools/xml-parser-ts/dist/qNames";
import { XmlParserTsRootElementBaseType } from "@kie-tools/xml-parser-ts";

export type FeelQNameBuild = {
  full: string;
  name: string;
  prefix: string | undefined;
  isExternal: boolean;
};

export function buildFeelQNameFromXmlQName({
  namedElement,
  namedElementQName,
  importsByNamespace,
  relativeToNamespace,
  model,
}: {
  namedElement: DMN15__tNamedElement;
  namedElementQName: XmlQName;
  importsByNamespace: Map<string, DMN15__tImport>;
  model: XmlParserTsRootElementBaseType;
  relativeToNamespace: string;
}): FeelQNameBuild {
  if (!namedElementQName.prefix) {
    return { full: namedElement["@_name"], prefix: undefined, name: namedElement["@_name"], isExternal: false };
  }

  const namespace = model[`@_xmlns:${namedElementQName.prefix}`];
  if (!namespace) {
    throw new Error(`Can't find namespace declaration for namespace with name '${namedElementQName.prefix}'.`);
  }

  return buildFeelQNameFromNamespace({
    namedElement,
    namespace,
    importsByNamespace,
    relativeToNamespace,
  });
}

export function buildFeelQNameFromNamespace({
  namedElement,
  namespace,
  importsByNamespace,
  relativeToNamespace,
}: {
  namedElement: DMN15__tNamedElement;
  namespace: string;
  importsByNamespace: Map<string, DMN15__tImport>;
  relativeToNamespace: string;
}): FeelQNameBuild {
  if (relativeToNamespace === namespace) {
    return {
      full: namedElement["@_name"],
      prefix: undefined,
      name: namedElement["@_name"],
      isExternal: false,
    };
  }

  const _import = importsByNamespace.get(namespace);
  if (!_import) {
    throw new Error(`Can't find included model with namespace '${namespace}'.`);
  }

  // Special case of DMN 1.5. Imports with the empty string as their names can have their namespaces ommitted.
  if (_import["@_name"] === "") {
    return { full: namedElement["@_name"], prefix: undefined, name: namedElement["@_name"], isExternal: true };
  }

  // FEEL namespaces elements with a `.`, while XML does so by using a `:` on QNames. Note that the FEEL
  // namespace is determined by the `@_name` attribute of a DMN15__tImport, not by its namespace declaration
  // name on the XML itself.
  return {
    full: `${_import["@_name"]}.${namedElement["@_name"]}`,
    prefix: _import["@_name"],
    name: namedElement["@_name"],
    isExternal: true,
  };
}
