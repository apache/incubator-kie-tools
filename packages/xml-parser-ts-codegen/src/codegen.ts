#!/usr/bin/env node

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

import * as fs from "fs";
import * as path from "path";
import { XmlParserTs, getParser } from "@kie-tools/xml-parser-ts";
import {
  XptcTsPrimitiveType,
  XptcElement,
  XptcSimpleType,
  XptcComplexType,
  XptcComplexTypeAnonymous,
  XptcMetaType,
  XptcMetaTypeProperty,
  XptcAttribute,
  XptcTopLevelAttributeGroup,
} from "./types";
import {
  XsdAttribute,
  XsdComplexType,
  XsdSchema,
  XsdSequence,
  XsdSimpleType,
} from "./schemas/xsd-incomplete--manually-written/ts-gen/types";
import { ns as xsdNs, meta as xsdMeta } from "./schemas/xsd-incomplete--manually-written/ts-gen/meta";
import { mergeWith } from "lodash";

export const __XSD_PARSER = getParser<XsdSchema>({
  ns: xsdNs,
  meta: xsdMeta,
  subs: {},
  elements: {},
  root: { element: "xsd:schema", type: "schema" },
});

export type Unpacked<T> = T extends (infer U)[] ? U : T;

const __LOGS = {
  done: (location: string) => `[xml-parser-ts-codegen] Done for '${location}'.`,
};

export const XSD__TYPES = new Map<string, XptcTsPrimitiveType>([
  ["xsd:boolean", { type: "primitive", tsEquivalent: "boolean", annotation: "xsd:boolean" }],
  ["xsd:QName", { type: "primitive", tsEquivalent: "string", annotation: "xsd:QName" }],
  ["xsd:string", { type: "primitive", tsEquivalent: "string", annotation: "xsd:string" }],
  ["xsd:int", { type: "primitive", tsEquivalent: "number", annotation: "xsd:int" }],
  ["xsd:integer", { type: "primitive", tsEquivalent: "number", annotation: "xsd:integer" }],
  ["xsd:dateTime", { type: "primitive", tsEquivalent: "string", annotation: "xsd:dateTime" }],
  ["xsd:double", { type: "primitive", tsEquivalent: "number", annotation: "xsd:double" }],
  ["xsd:long", { type: "primitive", tsEquivalent: "number", annotation: "xsd:long" }],
  ["xsd:float", { type: "primitive", tsEquivalent: "number", annotation: "xsd:float" }],
  ["xsd:duration", { type: "primitive", tsEquivalent: "string", annotation: "xsd:duration" }],
  ["xsd:IDREF", { type: "primitive", tsEquivalent: "string", annotation: "xsd:IDREF" }],
  ["xsd:anyURI", { type: "primitive", tsEquivalent: "string", annotation: "xsd:anyURI" }],
  ["xsd:anyType", { type: "primitive", tsEquivalent: "string", annotation: "xsd:antType" }],
  ["xsd:IDREFS", { type: "primitive", tsEquivalent: "string", annotation: "xsd:IDREFS" }],
  ["xsd:ID", { type: "primitive", tsEquivalent: "string", annotation: "xsd:ID" }],
]);

// TODO: Tiago --> Write unit tests
async function fetchXsdString(baseLocation: string, relativeLocation: string) {
  try {
    const url = new URL(relativeLocation);
    throw new Error("URLs not yet supported.");
  } catch (e) {
    const p = path.resolve(baseLocation, relativeLocation);
    return { location: p, xsdString: fs.readFileSync(p) };
  }
}

// TODO: Tiago --> Write unit tests
async function parseDeep(
  __XSD_PARSER: XmlParserTs<XsdSchema>,
  baseLocation: string,
  relativeLocation: string
): Promise<[string, XsdSchema][]> {
  const { xsdString } = await fetchXsdString(baseLocation, relativeLocation);

  const { json: schema } = __XSD_PARSER.parse({ type: "xml", xml: xsdString });

  const includePromises = (schema["xsd:schema"]["xsd:include"] ?? []).map((i) =>
    parseDeep(__XSD_PARSER, baseLocation, i["@_schemaLocation"])
  );

  const importPromises = (schema["xsd:schema"]["xsd:import"] ?? []).map((i) =>
    parseDeep(__XSD_PARSER, baseLocation, i["@_schemaLocation"])
  );

  const includes = (await Promise.all(includePromises)).flatMap((s) => s);
  const imports = (await Promise.all(importPromises)).flatMap((s) => s);

  const sameNs = includes.filter(
    ([k, v]) => v["xsd:schema"]["@_targetNamespace"] === schema["xsd:schema"]["@_targetNamespace"]
  );
  let schemaPlusIncludes = schema;
  for (const [k, v] of sameNs) {
    schemaPlusIncludes = mergeWith(schemaPlusIncludes, v, (a, b) => {
      if (Array.isArray(a) && Array.isArray(b)) {
        return [...a, ...b];
      }
    });
  }

  const includesFromOtherNs = includes.filter(
    ([k, v]) => v["xsd:schema"]["@_targetNamespace"] !== schema["xsd:schema"]["@_targetNamespace"]
  );
  return [[relativeLocation, schema], ...imports, ...includesFromOtherNs];
}

async function main() {
  const __LOCATION = process.argv[2];
  const __ROOT_ELEMENT_NAME = process.argv[3];
  const __BASE_LOCATION = path.dirname(__LOCATION);
  const __RELATIVE_LOCATION = path.basename(__LOCATION);

  const __ROOT_ELEMENT = `${__RELATIVE_LOCATION}__${__ROOT_ELEMENT_NAME}`;

  const __RELATIVE_LOCATION_WITHOUT_EXTENSION = __RELATIVE_LOCATION.replace(path.extname(__RELATIVE_LOCATION), "");

  const __CONVENTIONS = {
    outputFileForGeneratedTypes: path.resolve(".", path.join(__BASE_LOCATION, "ts-gen/types.ts")),
    outputFileForGeneratedMeta: path.resolve(".", path.join(__BASE_LOCATION, "ts-gen/meta.ts")),
  };

  // gather all the XSDs
  const __XSDS = new Map<string, XsdSchema>(await parseDeep(__XSD_PARSER, __BASE_LOCATION, __RELATIVE_LOCATION));

  // // process <xsd:simpleType>'s
  const __SIMPLE_TYPES: XptcSimpleType[] = Array.from(__XSDS.entries()).flatMap(([location, schema]) =>
    (schema["xsd:schema"]["xsd:simpleType"] || []).flatMap((xsdSimpleType) => {
      if (xsdSimpleType["xsd:union"]) {
        if (xsdSimpleType["xsd:union"]["@_memberTypes"] === "xsd:anyURI") {
          return [
            {
              comment: "xsd:anyURI",
              type: "simple",
              kind: "enum",
              name: xsdSimpleType["@_name"]!,
              declaredAtRelativeLocation: location,
              values: [],
            },
          ];
        }
        return (xsdSimpleType["xsd:union"]["xsd:simpleType"] ?? []).flatMap((ss) =>
          xsdSimpleTypeToXptcSimpleType(ss, location, xsdSimpleType["@_name"]!)
        );
      } else {
        return xsdSimpleTypeToXptcSimpleType(xsdSimpleType, location, xsdSimpleType["@_name"]!);
      }
    })
  );

  const __ATTRIBUTE_GROUPS_BY_QNAME: Map<string, XptcTopLevelAttributeGroup> = new Map(
    Array.from(__XSDS.entries()).flatMap(([location, schema]) =>
      (schema["xsd:schema"]["xsd:attributeGroup"] || []).flatMap((xsdAttrGroup) => {
        const qNamePrefix = Object.keys(schema["xsd:schema"])
          .find(
            (key: keyof (typeof schema)["xsd:schema"]) =>
              key.startsWith("@_xmlns:") && // is a xml namespace declaration
              schema["xsd:schema"][key] === schema["xsd:schema"]["@_targetNamespace"]
          )
          ?.split(":")[1];
        if (!qNamePrefix) {
          return [];
        }

        return [
          [
            `${qNamePrefix}:${xsdAttrGroup["@_name"]}`,
            {
              name: xsdAttrGroup["@_name"],
              attributes: (xsdAttrGroup["xsd:attribute"] ?? []).map((xsdAttr) =>
                xsdAttributeToXptcAttribute(xsdAttr, location)
              ),
            },
          ],
        ];
      })
    )
  );

  // // process <xsd:complexType>'s
  const __COMPLEX_TYPES: XptcComplexType[] = [];
  for (const [location, xsd] of __XSDS.entries()) {
    for (const xsdCt of xsd["xsd:schema"]["xsd:complexType"] || []) {
      const isAbstract = xsdCt["@_abstract"] ?? false;
      const extensionElement =
        xsdCt["xsd:complexContent"]?.["xsd:extension"] ?? xsdCt["xsd:simpleContent"]?.["xsd:extension"];

      __COMPLEX_TYPES.push({
        type: "complex",
        comment: isAbstract ? "abstract" : "",
        isMixed: xsdCt["@_mixed"] ?? false,
        isAbstract,
        isAnonymous: false,
        name: xsdCt["@_name"]!,
        isSimpleContent: !!xsdCt["xsd:simpleContent"],
        needsExtensionType: !!xsdCt["xsd:anyAttribute"] || !!xsdCt["xsd:sequence"]?.["xsd:any"],
        declaredAtRelativeLocation: location,
        childOf: extensionElement?.["@_base"],
        elements: [
          ...(xsdCt["xsd:all"]?.["xsd:element"] ?? []).map((s) =>
            xsdElementToXptcElement(__ATTRIBUTE_GROUPS_BY_QNAME, xsdCt["@_name"]!, s, location)
          ),
          ...(xsdCt["xsd:sequence"]?.["xsd:element"] ?? []).map((s) =>
            xsdElementToXptcElement(__ATTRIBUTE_GROUPS_BY_QNAME, xsdCt["@_name"]!, s, location)
          ),
          ...(extensionElement?.["xsd:sequence"]?.["xsd:element"] ?? []).map((s) =>
            xsdElementToXptcElement(__ATTRIBUTE_GROUPS_BY_QNAME, xsdCt["@_name"]!, s, location)
          ),
          ...(extensionElement?.["xsd:sequence"]?.["xsd:choice"]?.["xsd:element"] ?? []).map((s) =>
            xsdElementToXptcElement(__ATTRIBUTE_GROUPS_BY_QNAME, xsdCt["@_name"]!, s, location, { forceOptional: true })
          ),
          ...(extensionElement?.["xsd:choice"]?.["xsd:element"] ?? []).map((s) =>
            xsdElementToXptcElement(__ATTRIBUTE_GROUPS_BY_QNAME, xsdCt["@_name"]!, s, location, { forceOptional: true })
          ),
          ...(extensionElement?.["xsd:choice"]?.["xsd:sequence"]?.["xsd:element"] ?? []).map((s) =>
            xsdElementToXptcElement(__ATTRIBUTE_GROUPS_BY_QNAME, xsdCt["@_name"]!, s, location, { forceOptional: true })
          ),
        ],
        attributes: [
          ...(xsdCt["xsd:attribute"] ?? []).map((a) => xsdAttributeToXptcAttribute(a, location)),
          ...(extensionElement?.["xsd:attribute"] ?? []).map((a) => xsdAttributeToXptcAttribute(a, location)),
          ...(xsdCt["xsd:attributeGroup"] ?? []).flatMap(
            (attrGroup) => __ATTRIBUTE_GROUPS_BY_QNAME.get(attrGroup["@_ref"])?.attributes ?? []
          ),
        ],
      });
    }
  }

  // // process <xsd:element>'s
  const __GLOBAL_ELEMENTS = new Map<string, XptcElement>();
  for (const [location, xsd] of __XSDS.entries()) {
    for (const e of xsd["xsd:schema"]["xsd:element"] || []) {
      const a = xsdElementToXptcElement(
        __ATTRIBUTE_GROUPS_BY_QNAME,
        "GLOBAL",
        { ...e, "@_minOccurs": 0, "@_maxOccurs": "unbounded" },
        location,
        {
          forceOptional: false,
        }
      );

      let isAbstract = e["@_abstract"];
      if (a.kind === "ofNamedType") {
        const split = a.typeName?.split(":") ?? [];
        const qNamePrefix = split.length === 2 ? split[0] : undefined;
        const typeName = split.length === 2 ? split[1] : split[0];
        const namespace = xsd["xsd:schema"][`@_xmlns:${qNamePrefix}`];
        const [typeXsdLocation, typeXsd] =
          [...__XSDS.entries()].find(([location, xsd]) => xsd["xsd:schema"]["@_targetNamespace"] === namespace) ?? [];

        const type = __COMPLEX_TYPES
          .filter((s) => !s.isAnonymous)
          .filter((s) => (qNamePrefix ? s.declaredAtRelativeLocation === typeXsdLocation : true))
          .find((s) => s.name === typeName);

        if (!type) {
          throw new Error(`Can't find type '${typeName}' for element ${e["@_name"]}`);
        }

        isAbstract ??= type?.isAbstract;
      }

      isAbstract ??= false;

      __GLOBAL_ELEMENTS.set(`${location}__${e["@_name"]}`, {
        name: e["@_name"],
        isAbstract,
        substitutionGroup: e["@_substitutionGroup"],
        type: e["@_type"],
        declaredAtRelativeLocation: location,
        anonymousType: a.kind === "ofAnonymousType" ? a.anonymousType : undefined,
      });
    }
  }

  // // substitutionGroups are SCOPED. Meaning that we need to consider only what the current XSD is importing into it.
  // // This map goes from a relativeLocation to an elementName to a list of elementNames.
  const __SUBSTITUTIONS = new Map<string, Map<string, string[]>>();
  for (const [baseLoc, _] of __XSDS.entries()) {
    const xsds = new Map<string, XsdSchema>(await parseDeep(__XSD_PARSER, __BASE_LOCATION, baseLoc));

    for (const [xLocation, xsd] of xsds.entries()) {
      const localizedSubstitutions = __SUBSTITUTIONS.get(xLocation) ?? new Map<string, string[]>();
      __SUBSTITUTIONS.set(xLocation, localizedSubstitutions);
      for (const e of xsd["xsd:schema"]["xsd:element"] || []) {
        if (e["@_substitutionGroup"]) {
          const subsGroup = getXptcElementFromElementQName(
            __XSDS,
            __GLOBAL_ELEMENTS,
            xLocation,
            e["@_substitutionGroup"]
          );
          if (!subsGroup) {
            throw new Error(`Invalid subsitution group for element '${e["@_name"]}'`);
          }
          const elem = getXptcElementFromElementQName(__XSDS, __GLOBAL_ELEMENTS, xLocation, e["@_name"]);
          if (!elem) {
            throw new Error(`Invalid element '${e["@_name"]}'`);
          }

          const localizedElementName = `${subsGroup.declaredAtRelativeLocation}__${subsGroup.name}`;

          // Using this strategy to remove duplicates.
          const accumulatedSubstitutionElements = new Set([
            ...(localizedSubstitutions.get(localizedElementName) ?? []),
            `${xLocation}__${elem.name}`,
            ...(subsGroup.isAbstract ? [] : [localizedElementName]), // Include itself if not abstract
          ]);

          localizedSubstitutions.set(localizedElementName, [...accumulatedSubstitutionElements]);
        }
      }
    }
  }

  Array.from(__GLOBAL_ELEMENTS.values()).forEach((e) => {
    if (!e.anonymousType) {
      return;
    } else {
      __COMPLEX_TYPES.push(e.anonymousType);
    }
  });

  const __NAMED_TYPES_BY_TS_NAME = new Map<string, XptcComplexType | XptcSimpleType>([
    ...__SIMPLE_TYPES.map(
      (st) => [getTsNameFromNamedType(st.declaredAtRelativeLocation, st.name), st] as [string, XptcSimpleType]
    ),
    ...__COMPLEX_TYPES.map((ct) => {
      if (ct.isAnonymous) {
        const name = getAnonymousMetaTypeName(ct.forElementWithName, "GLOBAL");
        return [getTsNameFromNamedType(ct.declaredAtRelativeLocation, name), ct] as [string, XptcComplexType];
      } else {
        return [getTsNameFromNamedType(ct.declaredAtRelativeLocation, ct.name), ct] as [string, XptcComplexType];
      }
    }),
  ]);

  const __META_TYPE_MAPPING = new Map<string, XptcMetaType>();

  const rootTsTypeName = getTsNameFromNamedType(
    __RELATIVE_LOCATION_WITHOUT_EXTENSION,
    __GLOBAL_ELEMENTS.get(__ROOT_ELEMENT)!.type ?? getAnonymousMetaTypeName(__ROOT_ELEMENT_NAME, "GLOBAL")
  );

  let ts = "";

  for (const sp of __SIMPLE_TYPES) {
    if (sp.kind === "int") {
      // ignore int types, they're only interesting for validation.
      continue;
    }

    const enumName = getTsNameFromNamedType(sp.declaredAtRelativeLocation, sp.name);
    if (sp.comment === "xsd:anyURI") {
      ts += `
export type ${enumName} = string; // ${sp.comment}
`;
    } else if (sp.kind === "enum") {
      ts += `
export type ${enumName} = |
${sp.values.map((v) => `    '${v}'`).join(" |\n")}
`;
    }
  }

  for (const ct of __COMPLEX_TYPES) {
    const typeName = getTsNameFromNamedType(
      ct.declaredAtRelativeLocation,
      ct.isAnonymous ? getAnonymousMetaTypeName(ct.forElementWithName, "GLOBAL") : ct.name
    );

    const { metaProperties, needsExtensionType, anonymousTypes } = getMetaProperties(
      __RELATIVE_LOCATION,
      __META_TYPE_MAPPING,
      __GLOBAL_ELEMENTS,
      __SUBSTITUTIONS,
      __XSDS,
      __NAMED_TYPES_BY_TS_NAME,
      ct,
      typeName
    );

    const properties = metaProperties
      .map((p) => {
        const optionalMarker = p.isOptional ? "?" : "";
        const arrayMarker = p.isArray ? "[]" : "";
        const tsType =
          p.metaType.name === "integer" || p.metaType.name === "float" || p.metaType.name === "long"
            ? "number"
            : p.metaType.name;
        const ns = getMetaPropertyNs(__RELATIVE_LOCATION, p);
        return `    "${ns}${p.name}"${optionalMarker}: ${p.typeBody?.(tsType) ?? tsType}${arrayMarker}; // from type ${
          p.fromType
        } @ ${p.declaredAt}`;
      })
      .join("\n");

    const doc = ct.comment.trim() ? `/* ${ct.comment} */` : "";

    const anonymousTypesString = anonymousTypes
      .map((anonType) => {
        const anonymousTypesProperties = anonType.properties.map(
          (p) =>
            `    "${p.name}": ${
              p.metaType.name === "integer" || p.metaType.name === "float" || p.metaType.name === "long"
                ? "number"
                : p.metaType.name
            };`
        );

        // FIXME: Tiago: Not all anonymous types are extensible!
        return `export interface ${anonType.name} {
    __?: undefined;
${anonymousTypesProperties.join("\n")}
}`;
      })
      .join("\n");

    if (needsExtensionType) {
      const rootElementBaseType = rootTsTypeName === typeName ? "extends XmlParserTsRootElementBaseType" : "";
      ts += `
export interface ${typeName} ${rootElementBaseType} ${doc} {
    __?: undefined;
${properties}
}

${anonymousTypesString}
`;
    } else {
      const rootElementBaseType = rootTsTypeName === typeName ? "XmlParserTsRootElementBaseType & " : "";
      ts += `
export type ${typeName} = ${rootElementBaseType} ${doc} {
${properties}
}

${anonymousTypesString}
`;
    }
  }

  ts = `import { XmlParserTsRootElementBaseType } from "@kie-tools/xml-parser-ts"

  ${ts}
  `;

  ts = `// This file was automatically generated
  
${ts}
  `;

  fs.mkdirSync(path.dirname(__CONVENTIONS.outputFileForGeneratedTypes), { recursive: true });
  fs.writeFileSync(__CONVENTIONS.outputFileForGeneratedTypes, ts);

  // meta

  let meta = `
export const root = {
    element: "${getRealtiveLocationNs(__RELATIVE_LOCATION, __RELATIVE_LOCATION) + __ROOT_ELEMENT_NAME}",
    type: "${rootTsTypeName}" 
} as const;

export const ns = new Map<string, string>([
${[...__XSDS.entries()]
  .map(([k, v]) => {
    const uri = v["xsd:schema"]["@_targetNamespace"];
    const ns = getRealtiveLocationNs(__RELATIVE_LOCATION, k);
    return `    ["${uri}", "${ns}"],
    ["${ns}", "${uri}"],`;
  })
  .join("\n")}
]);

export const subs = {
${Array.from(__SUBSTITUTIONS.entries())
  .map(
    ([namespace, subs]) => `  "${getRealtiveLocationNs(__RELATIVE_LOCATION, namespace)}": {
${Array.from(subs.entries())
  .map(
    ([head, elements]) =>
      `${elements
        .flatMap((e) => {
          const elementName = `${getRealtiveLocationNs(__RELATIVE_LOCATION, e.split("__")[0]) + e.split("__")[1]}`;
          const headName = `${getRealtiveLocationNs(__RELATIVE_LOCATION, head.split("__")[0]) + head.split("__")[1]}`;
          if (elementName === headName) {
            return []; // Do not serialize itself as a substitution.
          } else {
            return `    "${elementName}": "${headName}",`;
          }
        })
        .join("\n")}`
  )
  .join("\n")}
  },`
  )
  .join("\n")}
};

export const elements = {
${Array.from(__GLOBAL_ELEMENTS.entries())
  .map(([k, v]) => {
    const s = v.type?.split(":") || [getAnonymousMetaTypeName(v.name, "GLOBAL")];
    const elementName = `${getRealtiveLocationNs(__RELATIVE_LOCATION, k.split("__")[0])}${v.name}`;
    const elementType = `${getTsNameFromNamedType(v.declaredAtRelativeLocation, s.length === 1 ? s[0] : s[1])}`;
    return `  "${elementName}": "${elementType}",`;
  })
  .join("\n")}
};

export const meta = {
`;

  Array.from(__META_TYPE_MAPPING.entries()).forEach(([name, type]) => {
    meta += `    "${name}": {
`;
    type.properties.forEach((p) => {
      const ns = getMetaPropertyNs(__RELATIVE_LOCATION, p);
      meta += `        "${ns}${p.name}": { type: "${p.metaType.name}", isArray: ${p.isArray}, fromType: "${p.fromType}", xsdType: "${p.metaType.xsdType}" },
`;
    });

    meta += `    },
`;
  });

  meta += `} as const;
`;

  fs.mkdirSync(path.dirname(__CONVENTIONS.outputFileForGeneratedMeta), { recursive: true });
  fs.writeFileSync(__CONVENTIONS.outputFileForGeneratedMeta, meta);

  console.log(__LOGS.done(__LOCATION));
}

main();

function getMetaPropertyNs(__RELATIVE_LOCATION: string, p: XptcMetaTypeProperty) {
  return p.name.startsWith("@_")
    ? ""
    : getRealtiveLocationNs(__RELATIVE_LOCATION, p.elem?.declaredAtRelativeLocation ?? p.declaredAt);
}

function getRealtiveLocationNs(__RELATIVE_LOCATION: string, relativeLocation: string) {
  return relativeLocation === __RELATIVE_LOCATION
    ? ""
    : `${relativeLocation.replace(".xsd", "").toLocaleLowerCase().replaceAll(/\d/g, "")}:`;
}

function resolveElementRef(
  __GLOBAL_ELEMENTS: Map<string, XptcElement>,
  __XSDS: Map<string, XsdSchema>,
  substitutions: Map<string, string[]>,
  referencedElement: XptcElement
): XptcElement[] {
  const key = `${referencedElement.declaredAtRelativeLocation}__${referencedElement.name}`;
  const substitutionNamesForReferencedElement = substitutions.get(key);
  if (!substitutionNamesForReferencedElement) {
    return [referencedElement];
  }

  const substitutionsWithoutSelfReferences = new Map(
    [...substitutions.entries()].map(([key, value]) => [key, value.filter((v) => v !== key)] as const)
  );

  const resolved = substitutionNamesForReferencedElement.flatMap((substitutionElementName) => {
    const substitutionElement = __GLOBAL_ELEMENTS.get(substitutionElementName);
    if (!substitutionElement) {
      throw new Error(`Can't find element '${substitutionElementName}' for substitution ${key}`);
    }

    if (substitutionElement.isAbstract) {
      return resolveElementRef(__GLOBAL_ELEMENTS, __XSDS, substitutionsWithoutSelfReferences, substitutionElement);
    }

    // Include itself if not abstract.
    return [
      substitutionElement,
      ...resolveElementRef(__GLOBAL_ELEMENTS, __XSDS, substitutionsWithoutSelfReferences, substitutionElement),
    ];
  });

  const seen = new Set<string>();
  return resolved.filter((s) => {
    const key = `${s.declaredAtRelativeLocation}__${s.name}`;
    if (seen.has(key)) {
      return false;
    } else {
      seen.add(key);
      return true;
    }
  });
}

function getMetaTypeName({ name, annotation }: { name: string; annotation: string }) {
  return name === "number" ? (annotation === "xsd:double" || annotation === "xsd:float" ? "float" : "integer") : name;
}

function getTypeBodyForElementRef(
  __RELATIVE_LOCATION: string,
  __META_TYPE_MAPPING: Map<string, XptcMetaType>,
  __GLOBAL_ELEMENTS: Map<string, XptcElement>,
  __SUBSTITUTIONS: Map<string, Map<string, string[]>>,
  __XSDS: Map<string, XsdSchema>,
  __NAMED_TYPES_BY_TS_NAME: Map<string, XptcComplexType | XptcSimpleType>,
  ct: XptcComplexType,
  referencedElement: XptcElement
) {
  const resolutions = resolveElementRef(
    __GLOBAL_ELEMENTS,
    __XSDS,
    __SUBSTITUTIONS.get(ct.declaredAtRelativeLocation)!,
    referencedElement
  );

  // No substitutions occured, proceed with normal type.
  if (resolutions.length === 1 && resolutions[0] === referencedElement) {
    return undefined;
  }

  return `( /* From subsitution groups */
${resolutions
  .flatMap((element) => {
    const elementNs = getRealtiveLocationNs(__RELATIVE_LOCATION, element.declaredAtRelativeLocation);
    const elementName = `${elementNs}${element.name}`;
    return [
      `        ({ __$$element: "${elementName}" } & ${
        getTsTypeFromQName(
          __XSDS,
          __NAMED_TYPES_BY_TS_NAME,
          ct.declaredAtRelativeLocation,
          element.type ?? getAnonymousMetaTypeName(element.name, "GLOBAL")
        ).name
      })`,
    ];
  })
  .join(" |\n")}
    )`;
}

function getMetaProperties(
  __RELATIVE_LOCATION: string,
  __META_TYPE_MAPPING: Map<string, XptcMetaType>,
  __GLOBAL_ELEMENTS: Map<string, XptcElement>,
  __SUBSTITUTIONS: Map<string, Map<string, string[]>>,
  __XSDS: Map<string, XsdSchema>,
  __NAMED_TYPES_BY_TS_NAME: Map<string, XptcComplexType | XptcSimpleType>,
  ct: XptcComplexType,
  metaTypeName: string
): { anonymousTypes: XptcMetaType[]; needsExtensionType: boolean; metaProperties: XptcMetaTypeProperty[] } {
  /** Accumulates all properties of this complex type (ct). Attributes and elements. */
  let ctMetaProperties: XptcMetaTypeProperty[] = [];

  /** Accumulates all anonymous types instantiated on this complex type's hierarchy */
  const anonymousTypes: XptcMetaType[] = [];

  const immediateParentType = ct.childOf
    ? getTsTypeFromQName(__XSDS, __NAMED_TYPES_BY_TS_NAME, ct.declaredAtRelativeLocation, ct.childOf)
    : undefined;

  let curParentCt = immediateParentType ? __NAMED_TYPES_BY_TS_NAME.get(immediateParentType.name) : undefined;

  let needsExtensionType = ct.needsExtensionType;

  let isMixed = ct.isMixed;

  while (curParentCt) {
    const curParentCtMetaProperties: XptcMetaTypeProperty[] = [];
    if (curParentCt?.type === "complex") {
      const curParentCtMetaTypeName = getTsNameFromNamedType(
        curParentCt.declaredAtRelativeLocation,
        curParentCt.isAnonymous ? getAnonymousMetaTypeName(curParentCt.forElementWithName, "GLOBAL") : curParentCt.name
      );
      needsExtensionType = needsExtensionType || curParentCt.needsExtensionType;
      if (curParentCt.isAnonymous) {
        throw new Error("Anonymous types are never parent types.");
      }

      isMixed ||= curParentCt.isMixed;

      for (const a of curParentCt.attributes) {
        const attributeType = getTsTypeFromQName(
          __XSDS,
          __NAMED_TYPES_BY_TS_NAME,
          curParentCt.declaredAtRelativeLocation,
          a.localTypeRef
        );
        if (!attributeType) {
          throw new Error(`Can't resolve local type ref ${a.localTypeRef}`);
        }

        curParentCtMetaProperties.push({
          declaredAt: curParentCt.declaredAtRelativeLocation,
          fromType: curParentCtMetaTypeName,
          elem: undefined,
          name: `@_${a.name}`,
          metaType: { name: getMetaTypeName(attributeType), xsdType: attributeType.annotation },
          isArray: false,
          isOptional: a.isOptional,
        });
      }

      for (const e of curParentCt.elements) {
        if (e.kind === "ofAnonymousType") {
          const anonymousTypeName = getAnonymousMetaTypeName(e.name, metaTypeName);
          const mp = getMetaProperties(
            __RELATIVE_LOCATION,
            __META_TYPE_MAPPING,
            __GLOBAL_ELEMENTS,
            __SUBSTITUTIONS,
            __XSDS,
            __NAMED_TYPES_BY_TS_NAME,
            e.anonymousType,
            anonymousTypeName
          );
          anonymousTypes.push({ name: anonymousTypeName, properties: mp.metaProperties });
          anonymousTypes.push(...mp.anonymousTypes);
          __META_TYPE_MAPPING.set(anonymousTypeName, {
            name: anonymousTypeName,
            properties: mp.metaProperties,
          });
          curParentCtMetaProperties.push({
            elem: undefined, // REALLY?
            declaredAt: curParentCt.declaredAtRelativeLocation,
            fromType: curParentCtMetaTypeName,
            name: e.name,
            metaType: { name: anonymousTypeName, xsdType: "Anonumous type..." },
            isArray: e.isArray,
            isOptional: e.isOptional,
          });
        } else if (e.kind === "ofNamedType") {
          const tsType = getTsTypeFromQName(
            __XSDS,
            __NAMED_TYPES_BY_TS_NAME,
            ct.declaredAtRelativeLocation,
            e.typeName
          );

          curParentCtMetaProperties.push({
            declaredAt: curParentCt.declaredAtRelativeLocation,
            fromType: curParentCtMetaTypeName,
            elem: undefined, // REALLY?
            name: e.name,
            metaType: { name: getMetaTypeName(tsType), xsdType: tsType.annotation },
            typeBody: getTsTypeBody(tsType),
            isArray: e.isArray,
            isOptional: e.isOptional,
          });
        } else if (e.kind === "ofRef") {
          const referencedElement = getXptcElementFromElementQName(
            __XSDS,
            __GLOBAL_ELEMENTS,
            ct.declaredAtRelativeLocation,
            e.ref
          );

          if (!referencedElement) {
            throw new Error(`Can't find reference to element '${e.ref}'`);
          }

          const tsType = referencedElement.type
            ? getTsTypeFromQName(
                __XSDS,
                __NAMED_TYPES_BY_TS_NAME,
                ct.declaredAtRelativeLocation,
                referencedElement.type
              )
            : {
                name: getTsNameFromNamedType(
                  ct.declaredAtRelativeLocation,
                  getAnonymousMetaTypeName(referencedElement.name, "GLOBAL")
                ),
                annotation: "Anonymous type from element " + referencedElement.name,
              };

          curParentCtMetaProperties.push({
            declaredAt: referencedElement?.declaredAtRelativeLocation,
            fromType: ct.isAnonymous ? "" : curParentCtMetaTypeName,
            name: referencedElement.name,
            elem: referencedElement,
            metaType: { name: getMetaTypeName(tsType), xsdType: tsType.annotation },
            typeBody: () =>
              getTypeBodyForElementRef(
                __RELATIVE_LOCATION,
                __META_TYPE_MAPPING,
                __GLOBAL_ELEMENTS,
                __SUBSTITUTIONS,
                __XSDS,
                __NAMED_TYPES_BY_TS_NAME,
                ct,
                referencedElement
              ),
            isArray: e.isArray,
            isOptional: e.isOptional,
          });
        } else {
          throw new Error("Unknonwn type of element " + e);
        }
      }

      const nextParentType = curParentCt.childOf
        ? getTsTypeFromQName(
            __XSDS,
            __NAMED_TYPES_BY_TS_NAME,
            curParentCt.declaredAtRelativeLocation,
            curParentCt.childOf
          )
        : undefined;

      // Make sure the inheritance order is respected. Elements should be listed always from the most generic to the most specific type.
      // Since we're iterating upwards in the hierarchy, we need to invert prepend the array with the props we find on each step of the hierarchy.
      ctMetaProperties = [...curParentCtMetaProperties, ...ctMetaProperties];
      curParentCt = nextParentType ? __NAMED_TYPES_BY_TS_NAME.get(nextParentType.name) : undefined;
    } else if (curParentCt?.type === "simple") {
      throw new Error("Can't have a non-complex type as parent of another.");
    } else {
      curParentCt = undefined;
    }
  }

  // Own properties are parsed later to ensure xsd:sequence order.

  for (const a of ct.attributes) {
    const attributeType = getTsTypeFromQName(
      __XSDS,
      __NAMED_TYPES_BY_TS_NAME,
      ct.declaredAtRelativeLocation,
      a.localTypeRef
    );

    ctMetaProperties.push({
      declaredAt: ct.declaredAtRelativeLocation,
      fromType: metaTypeName,
      name: `@_${a.name}`,
      elem: undefined,
      metaType: { name: getMetaTypeName(attributeType), xsdType: attributeType.annotation },
      isArray: false,
      isOptional: a.isOptional,
    });
  }

  for (const e of ct.elements) {
    if (e.kind === "ofRef") {
      const referencedElement = getXptcElementFromElementQName(
        __XSDS,
        __GLOBAL_ELEMENTS,
        ct.declaredAtRelativeLocation,
        e.ref
      );

      if (!referencedElement) {
        throw new Error(`Can't find reference to element '${e.ref}'`);
      }

      const tsType = referencedElement.type
        ? getTsTypeFromQName(__XSDS, __NAMED_TYPES_BY_TS_NAME, ct.declaredAtRelativeLocation, referencedElement.type)
        : {
            name: getTsNameFromNamedType(
              ct.declaredAtRelativeLocation,
              getAnonymousMetaTypeName(referencedElement.name, "GLOBAL")
            ),
            annotation: "Anonymous type from element " + referencedElement.name,
          };

      ctMetaProperties.push({
        declaredAt: referencedElement?.declaredAtRelativeLocation,
        fromType: ct.isAnonymous ? "" : metaTypeName,
        name: referencedElement.name,
        elem: referencedElement,
        metaType: { name: getMetaTypeName(tsType), xsdType: tsType.annotation },
        typeBody: () =>
          getTypeBodyForElementRef(
            __RELATIVE_LOCATION,
            __META_TYPE_MAPPING,
            __GLOBAL_ELEMENTS,
            __SUBSTITUTIONS,
            __XSDS,
            __NAMED_TYPES_BY_TS_NAME,
            ct,
            referencedElement
          ),
        isArray: e.isArray,
        isOptional: e.isOptional,
      });
    } else if (e.kind === "ofNamedType") {
      const tsType = getTsTypeFromQName(__XSDS, __NAMED_TYPES_BY_TS_NAME, ct.declaredAtRelativeLocation, e.typeName);
      ctMetaProperties.push({
        declaredAt: ct.declaredAtRelativeLocation,
        fromType: metaTypeName,
        name: e.name,
        elem: undefined, // REALLY?
        metaType: { name: getMetaTypeName(tsType), xsdType: tsType.annotation },
        typeBody: getTsTypeBody(tsType),
        isArray: e.isArray,
        isOptional: e.isOptional,
      });
    } else if (e.kind === "ofAnonymousType") {
      const anonymousTypeName = getAnonymousMetaTypeName(e.name, metaTypeName);
      const mp = getMetaProperties(
        __RELATIVE_LOCATION,
        __META_TYPE_MAPPING,
        __GLOBAL_ELEMENTS,
        __SUBSTITUTIONS,
        __XSDS,
        __NAMED_TYPES_BY_TS_NAME,
        e.anonymousType,
        anonymousTypeName
      );
      anonymousTypes.push({ name: anonymousTypeName, properties: mp.metaProperties });
      anonymousTypes.push(...mp.anonymousTypes);
      __META_TYPE_MAPPING.set(anonymousTypeName, {
        name: anonymousTypeName,
        properties: mp.metaProperties,
      });
      ctMetaProperties.push({
        declaredAt: ct.declaredAtRelativeLocation,
        fromType: metaTypeName,
        name: e.name,
        elem: undefined, // REALLY?
        metaType: { name: anonymousTypeName, xsdType: "Anonymous type..." },
        isArray: e.isArray,
        isOptional: e.isOptional,
      });
    } else {
      throw new Error(`Unknown kind of XptcComplexType '${e}'`);
    }
  }

  if (ct.isSimpleContent && ct.childOf) {
    const t = getTsTypeFromQName(__XSDS, __NAMED_TYPES_BY_TS_NAME, ct.declaredAtRelativeLocation, ct.childOf);
    ctMetaProperties.push({
      declaredAt: ct.declaredAtRelativeLocation,
      fromType: metaTypeName,
      name: `__$$text`,
      elem: undefined,
      metaType: {
        name: t.name,
        xsdType: t.annotation,
      },
      isArray: false,
      isOptional: false,
    });
  }

  if (isMixed) {
    ctMetaProperties.push({
      declaredAt: ct.declaredAtRelativeLocation,
      fromType: metaTypeName,
      name: `__$$text`,
      elem: undefined,
      metaType: {
        name: "string",
        xsdType: "xsd:string",
      },
      isArray: false,
      isOptional: true,
    });
  }

  if (!(ct.type === "complex" && !ct.isAnonymous && ct.isAbstract)) {
    __META_TYPE_MAPPING.set(metaTypeName, {
      name: metaTypeName,
      properties: [...ctMetaProperties.reduce((acc, p) => acc.set(p.name, p), new Map()).values()], // Removing duplicates.
    });
  }

  return { metaProperties: ctMetaProperties, needsExtensionType, anonymousTypes };
}

function getAnonymousMetaTypeName(elementName: string, metaTypeName: string) {
  return `${metaTypeName}__${elementName}`;
}

function getTsNameFromNamedType(relativeLocation: string, namedTypeName: string) {
  const filenameWithoutExtension = path.basename(relativeLocation).replace(path.extname(relativeLocation), "");
  return `${filenameWithoutExtension}__${namedTypeName}`.replaceAll(/[ -.]/g, "_");
}

function getTsTypeFromQName(
  __XSDS: Map<string, XsdSchema>,
  __NAMED_TYPES_BY_TS_NAME: Map<string, XptcComplexType | XptcSimpleType>,
  relativeLocation: string,
  namedTypeQName: string
): { name: string; annotation: string } {
  // check if it's a local ref to another namespace
  if (namedTypeQName.includes(":") && namedTypeQName.split(":").length === 2) {
    const [localNsName, namedTypeName] = namedTypeQName.split(":");
    const xmlnsKey = `@_xmlns:${localNsName}`;
    const namespace = (__XSDS.get(relativeLocation)?.["xsd:schema"] as any)[xmlnsKey];

    // short circuit here. we don't parse XSD's XSD.
    if (namespace === "http://www.w3.org/2001/XMLSchema") {
      const xsdType = XSD__TYPES.get(namedTypeQName);
      if (!xsdType) {
        throw new Error(`Unknown XSD type '${namedTypeQName}'`);
      }
      return { name: xsdType.tsEquivalent, annotation: xsdType.annotation };
    }

    // find the XSD with matching namespace declaration.
    const referencedXsd = [...__XSDS.entries()].find(([_, s]) => s["xsd:schema"]["@_targetNamespace"] === namespace);
    if (!referencedXsd) {
      throw new Error(`Can't find referenced namespace '${namespace}'`);
    }

    const [referencedXsdRelativeLocation, _] = referencedXsd;

    // with the referenced XSD location, we can build the TS type name.
    const tsTypeName = getTsNameFromNamedType(referencedXsdRelativeLocation, namedTypeName);
    const namedType = __NAMED_TYPES_BY_TS_NAME.get(tsTypeName);
    if (!namedType) {
      throw new Error(`Named type not found with name '${namedType}'`);
    }

    // some simple types are declared just because of the restrictions it has, they're not interesting for the generated structure.
    if (namedType.type === "simple" && namedType.kind === "int" && namedType.restrictionBase) {
      return getTsTypeFromQName(__XSDS, __NAMED_TYPES_BY_TS_NAME, relativeLocation, namedType.restrictionBase);
    }

    // found it!
    return { name: tsTypeName, annotation: `type found from namespace with declaration name '${localNsName}'.` };
  }

  // not a reference to a type in another namespace. simply local name.
  return {
    name: getTsNameFromNamedType(relativeLocation, namedTypeQName),
    annotation: "// local type",
  };
}

function xsdSimpleTypeToXptcSimpleType(
  xsdSimpleType: XsdSimpleType,
  location: string,
  nameIfUnnamed: string
): XptcSimpleType {
  if (
    xsdSimpleType["xsd:restriction"]?.["@_base"] === "xsd:string" ||
    xsdSimpleType["xsd:restriction"]?.["@_base"] === "xsd:token"
  ) {
    if (xsdSimpleType["xsd:restriction"]["xsd:enumeration"]) {
      return {
        comment: "enum",
        type: "simple",
        kind: "enum",
        name: xsdSimpleType["@_name"] ?? nameIfUnnamed,
        declaredAtRelativeLocation: location,
        values: xsdSimpleType["xsd:restriction"]["xsd:enumeration"].map((e) => e["@_value"]),
      };
    } else {
      return {
        comment: "string",
        type: "simple",
        kind: "string",
        name: xsdSimpleType["@_name"] ?? nameIfUnnamed,
        declaredAtRelativeLocation: location,
      };
    }
  } else if (
    xsdSimpleType["xsd:restriction"]?.["@_base"] === "xsd:int" ||
    xsdSimpleType["xsd:restriction"]?.["@_base"] === "xsd:integer"
  ) {
    return {
      comment: "int",
      type: "simple",
      kind: "int",
      restrictionBase: xsdSimpleType["xsd:restriction"]["@_base"],
      name: xsdSimpleType["@_name"] ?? nameIfUnnamed,
      declaredAtRelativeLocation: location,
      minInclusive: xsdSimpleType["xsd:restriction"]["xsd:minInclusive"]?.["@_value"],
      maxInclusive: xsdSimpleType["xsd:restriction"]["xsd:maxInclusive"]?.["@_value"],
    };
  } else {
    throw new Error(`Unknown xsd:simpleType --> ${JSON.stringify(xsdSimpleType, undefined, 2)}`);
  }
}

function getXptcElementFromElementQName(
  __XSDS: Map<string, XsdSchema>,
  __GLOBAL_ELEMENTS: Map<string, XptcElement>,
  relativeLocation: string,
  elementQName: string
): XptcElement | undefined {
  // check if it's a QName to another namespace
  if (elementQName.includes(":") && elementQName.split(":").length === 2) {
    const [localNsName, referencedElementName] = elementQName.split(":");
    const xmlnsKey = `@_xmlns:${localNsName}`;
    const namespace = (__XSDS.get(relativeLocation)?.["xsd:schema"] as any)[xmlnsKey];

    // find the XSD with matching namespace declaration.
    const referencedXsd = [...__XSDS.entries()].find(([_, s]) => s["xsd:schema"]["@_targetNamespace"] === namespace);
    if (!referencedXsd) {
      throw new Error(`Can't find referenced namespace '${namespace}'`);
    }

    const [referencedXsdRelativeLocation, _] = referencedXsd;

    return __GLOBAL_ELEMENTS.get(`${referencedXsdRelativeLocation}__${referencedElementName}`);
  }

  return __GLOBAL_ELEMENTS.get(`${relativeLocation}__${elementQName}`);
}

function xsdElementToXptcElement(
  __ATTRIBUTE_GROUPS_BY_QNAME: Map<string, XptcTopLevelAttributeGroup>,
  parentIdentifierForExtensionType: string,
  xsdElement: NonNullable<Unpacked<XsdSequence["xsd:element"]>>,
  location: string,
  args?: { forceOptional: boolean }
): Unpacked<XptcComplexType["elements"]> {
  const minOccurs = xsdElement["@_minOccurs"] ?? 1;
  const maxOccurs = xsdElement["@_maxOccurs"] ?? 1;

  let isArray = false;
  let isOptional = false;
  if (maxOccurs !== "unbounded" && maxOccurs < minOccurs) {
    throw new Error(`maxOccurs cannot be smaller than minOccurs (at '${parentIdentifierForExtensionType}').`);
  }
  if (minOccurs === 0) {
    if (maxOccurs === "unbounded") {
      isArray = true;
      isOptional = true;
    } else if (maxOccurs === 0) {
      throw new Error(
        `minOccurs and maxOccurs cannot be 0 at the same time at element '${parentIdentifierForExtensionType}'.`
      );
    } else if (maxOccurs === 1) {
      isOptional = true;
    } else if (maxOccurs > 1) {
      isArray = true; // at max 'maxOccurs' --> validation
    } else {
      throw new Error(`Impossible scenario for minOccurs === 0. maxOccurs is '${maxOccurs}'.`);
    }
  } else if (minOccurs === 1) {
    if (maxOccurs === "unbounded") {
      isArray = true; // at least one element --> validation
    } else if (maxOccurs === 1) {
      isOptional = false;
    } else if (maxOccurs > 1) {
      isArray = true; // at least one element and at max 'maxOccurs' --> validation
    } else {
      throw new Error(`Impossible scenario for minOccurs === 1. maxOccurs is '${maxOccurs}'.`);
    }
  } else if (minOccurs > 1) {
    if (maxOccurs === "unbounded") {
      isArray = true; // at least 'minOccurs' elements --> validation
    } else if (maxOccurs >= minOccurs) {
      isArray = true; // at least one element and at max 'maxOccurs' --> validation
    } else {
      throw new Error(`Impossible scenario for minOccurs > 1. maxOccurs is '${maxOccurs}'.`);
    }
  } else {
    throw new Error(
      `Impossible scenario for minOccurs. minOccurs is '${minOccurs}(${typeof minOccurs})' inside element '${JSON.stringify(
        xsdElement
      )}'.`
    );
  }

  if (args?.forceOptional) {
    isOptional = true;
  }

  if (xsdElement["@_type"] && xsdElement["@_name"]) {
    return {
      name: xsdElement["@_name"],
      typeName: xsdElement["@_type"],
      kind: "ofNamedType",
      isArray,
      isOptional,
    };
  }

  if (xsdElement["xsd:complexType"] && xsdElement["@_name"]) {
    return {
      name: xsdElement["@_name"],
      kind: "ofAnonymousType",
      isArray,
      isOptional,
      anonymousType: xsdComplexTypeToAnonymousXptcComplexType(
        __ATTRIBUTE_GROUPS_BY_QNAME,
        parentIdentifierForExtensionType,
        xsdElement["xsd:complexType"],
        location,
        xsdElement["@_name"]
      ),
    };
  }

  if (xsdElement["xsd:simpleType"] && xsdElement["@_name"]) {
    throw new Error("Simple types not implemented for anonymous element types.");
  }

  if (xsdElement["@_ref"]) {
    return {
      ref: xsdElement["@_ref"],
      kind: "ofRef",
      isArray,
      isOptional,
    };
  }

  throw new Error(`Unknown xsd:element structure. ${JSON.stringify(xsdElement)}`);
}

function xsdAttributeToXptcAttribute(xsdAttribute: XsdAttribute, location: string): XptcAttribute {
  return {
    name: xsdAttribute["@_name"],
    localTypeRef: xsdAttribute["@_type"],
    isOptional: xsdAttribute["@_use"] === undefined || xsdAttribute["@_use"] === "optional",
    simpleType: xsdAttribute["xsd:simpleType"]
      ? xsdSimpleTypeToXptcSimpleType(xsdAttribute["xsd:simpleType"], location, `${xsdAttribute["@_name"]}simpleType`)
      : undefined,
  };
}

function xsdComplexTypeToAnonymousXptcComplexType(
  __ATTRIBUTE_GROUPS_BY_QNAME: Map<string, XptcTopLevelAttributeGroup>,
  parentIdentifierForExtensionType: string,
  xsdCt: XsdComplexType,
  location: string,
  element: string
): XptcComplexTypeAnonymous {
  return {
    type: "complex",
    comment: "",
    isMixed: xsdCt["@_mixed"] ?? false,
    isSimpleContent: false, // No reason why an anonymous type couldn't be simpleContent... Could be implemented.
    isAnonymous: true,
    parentIdentifierForExtensionType,
    forElementWithName: element,
    needsExtensionType: !!xsdCt["xsd:anyAttribute"] || !!xsdCt["xsd:sequence"]?.["xsd:any"],
    declaredAtRelativeLocation: location,
    childOf: xsdCt["xsd:complexContent"]?.["xsd:extension"]?.["@_base"],
    elements: [
      ...(xsdCt["xsd:sequence"]?.["xsd:element"] ?? []).map((s) =>
        xsdElementToXptcElement(
          __ATTRIBUTE_GROUPS_BY_QNAME,
          `${parentIdentifierForExtensionType}__${element}`,
          s,
          location
        )
      ),
      ...(xsdCt["xsd:complexContent"]?.["xsd:extension"]?.["xsd:sequence"]?.["xsd:element"] ?? []).map((s) =>
        xsdElementToXptcElement(
          __ATTRIBUTE_GROUPS_BY_QNAME,
          `${parentIdentifierForExtensionType}__${element}`,
          s,
          location
        )
      ),
    ],
    attributes: [
      ...(xsdCt["xsd:attribute"] ?? []).map((a) => xsdAttributeToXptcAttribute(a, location)),
      ...(xsdCt["xsd:complexContent"]?.["xsd:extension"]?.["xsd:attribute"] ?? []).map((a) =>
        xsdAttributeToXptcAttribute(a, location)
      ),
      ...(xsdCt["xsd:attributeGroup"] ?? []).flatMap(
        (attrGroup) => __ATTRIBUTE_GROUPS_BY_QNAME.get(attrGroup["@_ref"])?.attributes ?? []
      ),
    ],
  };
}

function getTsTypeBody(tsType: { name: string; annotation: string }): XptcMetaTypeProperty["typeBody"] {
  return tsType.annotation.startsWith("xsd:") ? (tsTypeName) => `{ __$$text: ${tsTypeName} }` : undefined;
}
