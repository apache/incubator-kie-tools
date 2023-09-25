import { DMN15__tImport, DMN15__tNamedElement } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { XmlQName } from "../xml/xmlQNames";
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
  model,
}: {
  namedElement: DMN15__tNamedElement;
  namedElementQName: XmlQName;
  importsByNamespace: Map<string, DMN15__tImport>;
  model: XmlParserTsRootElementBaseType;
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
    thisDmnsNamespace: model["@_xmlns"]!,
  });
}

export function buildFeelQNameFromNamespace({
  namedElement,
  namespace,
  importsByNamespace,
  thisDmnsNamespace,
}: {
  namedElement: DMN15__tNamedElement;
  namespace: string;
  importsByNamespace: Map<string, DMN15__tImport>;
  thisDmnsNamespace: string;
}): FeelQNameBuild {
  if (thisDmnsNamespace === namespace) {
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
