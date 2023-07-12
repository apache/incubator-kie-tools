#!/usr/bin/env node

import * as fs from "fs";
import * as path from "path";
import { XmlParserTs, getParser } from "@kie-tools/xml-parser-ts";
import {
  XptcTsPrimitiveType,
  XptcElement,
  XptcSimpleType,
  XptcComplexType,
  XptcComplexTypeNamed,
  XptcComplexTypeAnonymous,
  XptcMetaType,
  XptcMetaTypeProperty,
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
  ["xsd:boolean", { type: "primitive", tsEquivalent: "boolean", doc: "xsd:boolean" }],
  ["xsd:QName", { type: "primitive", tsEquivalent: "string", doc: "xsd:QName" }],
  ["xsd:string", { type: "primitive", tsEquivalent: "string", doc: "xsd:string" }],
  ["xsd:int", { type: "primitive", tsEquivalent: "number", doc: "xsd:int" }],
  ["xsd:integer", { type: "primitive", tsEquivalent: "number", doc: "xsd:integer" }],
  ["xsd:double", { type: "primitive", tsEquivalent: "number", doc: "xsd:double" }],
  ["xsd:float", { type: "primitive", tsEquivalent: "number", doc: "xsd:float" }],
  ["xsd:IDREF", { type: "primitive", tsEquivalent: "string", doc: "xsd:IDREF" }],
  ["xsd:anyURI", { type: "primitive", tsEquivalent: "string", doc: "xsd:anyURI" }],
  ["xsd:anyType", { type: "primitive", tsEquivalent: "string", doc: "xsd:antType" }],
  ["xsd:IDREFS", { type: "primitive", tsEquivalent: "string", doc: "xsd:IDREFS" }],
  ["xsd:ID", { type: "primitive", tsEquivalent: "string", doc: "xsd:ID" }],
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

  const { json: schema } = __XSD_PARSER.parse({ xml: xsdString });

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
    (schema["xsd:schema"]["xsd:simpleType"] || []).flatMap((s) => {
      if (s["xsd:union"]) {
        return (s["xsd:union"]["xsd:simpleType"] ?? []).flatMap((ss) =>
          xsdSimpleTypeToXptcSimpleType(ss, location, s["@_name"])
        );
      } else {
        return xsdSimpleTypeToXptcSimpleType(s, location, s["@_name"]);
      }
    })
  );

  // // process <xsd:complexType>'s
  const __COMPLEX_TYPES: XptcComplexTypeNamed[] = [];
  for (const [location, xsd] of __XSDS.entries()) {
    for (const xsdCt of xsd["xsd:schema"]["xsd:complexType"] || []) {
      const isAbstract = xsdCt["@_abstract"] ?? false;
      __COMPLEX_TYPES.push({
        type: "complex",
        doc: isAbstract ? "abstract" : "",
        isAbstract,
        isAnonymous: false,
        name: xsdCt["@_name"]!,
        needsExtensionType: !!xsdCt["xsd:anyAttribute"] || !!xsdCt["xsd:sequence"]?.["xsd:any"],
        declaredAtRelativeLocation: location,
        childOf: xsdCt["xsd:complexContent"]?.["xsd:extension"]?.["@_base"],
        elements: [
          ...(xsdCt["xsd:all"]?.["xsd:element"] ?? []).flatMap((s) =>
            xsdElementToXptcElement(xsdCt["@_name"]!, s, location)
          ),
          ...(xsdCt["xsd:sequence"]?.["xsd:element"] ?? []).flatMap((s) =>
            xsdElementToXptcElement(xsdCt["@_name"]!, s, location)
          ),
          ...(xsdCt["xsd:complexContent"]?.["xsd:extension"]?.["xsd:sequence"]?.["xsd:element"] ?? []).flatMap((s) =>
            xsdElementToXptcElement(xsdCt["@_name"]!, s, location)
          ),
          ...(
            xsdCt["xsd:complexContent"]?.["xsd:extension"]?.["xsd:sequence"]?.["xsd:choice"]?.["xsd:element"] ?? []
          ).flatMap((s) => xsdElementToXptcElement(xsdCt["@_name"]!, s, location, { forceOptional: true })),
          ...(xsdCt["xsd:complexContent"]?.["xsd:extension"]?.["xsd:choice"]?.["xsd:element"] ?? []).flatMap((s) =>
            xsdElementToXptcElement(xsdCt["@_name"]!, s, location, { forceOptional: true })
          ),
          ...(
            xsdCt["xsd:complexContent"]?.["xsd:extension"]?.["xsd:choice"]?.["xsd:sequence"]?.["xsd:element"] ?? []
          ).flatMap((s) => xsdElementToXptcElement(xsdCt["@_name"]!, s, location, { forceOptional: true })),
        ],
        attributes: [
          ...(xsdCt["xsd:attribute"] ?? []).map((a) => xsdAttributeToXptcAttribute(a)),
          ...(xsdCt["xsd:complexContent"]?.["xsd:extension"]?.["xsd:attribute"] ?? []).map((a) =>
            xsdAttributeToXptcAttribute(a)
          ),
        ],
      });
    }
  }

  // // process <xsd:element>'s
  const __GLOBAL_ELEMENTS = new Map<string, XptcElement>();
  for (const [location, xsd] of __XSDS.entries()) {
    for (const e of xsd["xsd:schema"]["xsd:element"] || []) {
      __GLOBAL_ELEMENTS.set(`${location}__${e["@_name"]}`, {
        name: e["@_name"],
        isAbstract: e["@_abstract"] ?? false,
        substitutionGroup: e["@_substitutionGroup"],
        type: e["@_type"],
        declaredAtRelativeLocation: location,
      });
    }
  }

  // // substitutionGroups are SCOPED. Meaning that we need to consider only what the current XSD is importing into it.
  // // This map goes from a relativeLocation to an elementName to a list of elementNames.
  const __SUBSTITUTIONS = new Map<string, Map<string, string[]>>();
  for (const [baseLoc, _] of __XSDS.entries()) {
    const xsds = new Map<string, XsdSchema>(await parseDeep(__XSD_PARSER, __BASE_LOCATION, baseLoc));

    for (const [xLocation, xsd] of xsds.entries()) {
      const localizedSubstitutions = new Map<string, string[]>();
      __SUBSTITUTIONS.set(xLocation, localizedSubstitutions);
      for (const e of xsd["xsd:schema"]["xsd:element"] || []) {
        if (e["@_substitutionGroup"]) {
          const subsGroup = getXptcElementFromLocalElementRef(
            __XSDS,
            __GLOBAL_ELEMENTS,
            xLocation,
            e["@_substitutionGroup"]
          );
          if (!subsGroup) {
            throw new Error(`Invalid subsitution group for element '${e["@_name"]}'`);
          }
          const elem = getXptcElementFromLocalElementRef(__XSDS, __GLOBAL_ELEMENTS, xLocation, e["@_name"]);
          if (!elem) {
            throw new Error(`Invalid element '${e["@_name"]}'`);
          }

          localizedSubstitutions.set(`${subsGroup.declaredAtRelativeLocation}__${subsGroup.name}`, [
            ...(localizedSubstitutions.get(`${xLocation}__${subsGroup.name}`) ?? []),
            `${xLocation}__${elem.name}`,
          ]);
        }
      }
    }
  }

  const __NAMED_TYPES_BY_TS_NAME = new Map<string, XptcComplexType | XptcSimpleType>([
    ...__SIMPLE_TYPES.map(
      (st) => [getTsNameFromNamedType(st.declaredAtRelativeLocation, st.name), st] as [string, XptcSimpleType]
    ),
    ...__COMPLEX_TYPES.map(
      (ct) => [getTsNameFromNamedType(ct.declaredAtRelativeLocation, ct.name), ct] as [string, XptcComplexType]
    ),
  ]);

  const __DIRECT_CHILDREN = __COMPLEX_TYPES.reduce((acc, ct) => {
    if (ct.childOf) {
      const { name: parentName } = getTsTypeFromLocalRef(
        __XSDS,
        __NAMED_TYPES_BY_TS_NAME,
        ct.declaredAtRelativeLocation,
        ct.childOf
      );
      acc.set(parentName, [
        ...(acc.get(parentName) ?? []),
        getTsNameFromNamedType(ct.declaredAtRelativeLocation, ct.name),
      ]);
    }

    return acc;
  }, new Map<string, string[]>());

  const __META_TYPE_MAPPING = new Map<string, XptcMetaType>();

  const rootTsTypeName = getTsNameFromNamedType(
    __RELATIVE_LOCATION_WITHOUT_EXTENSION,
    __GLOBAL_ELEMENTS.get(__ROOT_ELEMENT)!.type
  );

  let ts = "";

  for (const sp of __SIMPLE_TYPES) {
    if (sp.kind === "int") {
      // ignore int types, they're only interesting for validation.
      continue;
    }

    if (sp.kind === "enum") {
      const enumName = getTsNameFromNamedType(sp.declaredAtRelativeLocation, sp.name);
      const enumValues = sp.values.map((v) => `    '${v}'`);
      ts += `
export type ${enumName} = |
${enumValues.join(" |\n")}
`;
    }
  }

  for (const ct of __COMPLEX_TYPES) {
    const typeName = getTsNameFromNamedType(ct.declaredAtRelativeLocation, ct.name);

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
        const tsType = p.metaType.name === "integer" || p.metaType.name === "float" ? "number" : p.metaType.name;
        const ns = getMetaPropertyNs(__RELATIVE_LOCATION, p);
        return `    "${ns}${p.name}"${optionalMarker}: ${p.typeBody ?? tsType}${arrayMarker}; // from type ${
          p.fromType
        } @ ${p.declaredAt}`;
      })
      .join("\n");

    const doc = ct.doc.trim() ? `/* ${ct.doc} */` : "";

    const anonymousTypesString = anonymousTypes
      .map((anonType) => {
        const anonymousTypesProperties = anonType.properties.map((p) => {
          return `    "${p.name}": ${p.metaType.name};`;
        });

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
};

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
        .map((e) => {
          const elementName = `${getRealtiveLocationNs(__RELATIVE_LOCATION, e.split("__")[0]) + e.split("__")[1]}`;
          const headName = `${getRealtiveLocationNs(__RELATIVE_LOCATION, head.split("__")[0]) + head.split("__")[1]}`;
          return `    "${elementName}": "${headName}",`;
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
    const s = v.type.split(":");
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
      meta += `        "${ns}${p.name}": { type: "${p.metaType.name}", isArray: ${p.isArray} },
`;
    });

    meta += `    },
`;
  });

  meta += `}
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

  return substitutionNamesForReferencedElement.flatMap((substitutionElementName) => {
    const substitutionElement = __GLOBAL_ELEMENTS.get(substitutionElementName);
    if (!substitutionElement) {
      throw new Error(`Can't find element '${substitutionElementName}' for substitution ${key}`);
    }
    return resolveElementRef(__GLOBAL_ELEMENTS, __XSDS, substitutions, substitutionElement);
  });
}

function getMetaTypeName(typeName: string, doc: string) {
  return typeName === "number" ? (doc === "xsd:double" || doc === "xsd:float" ? "float" : "integer") : typeName;
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
        getTsTypeFromLocalRef(__XSDS, __NAMED_TYPES_BY_TS_NAME, ct.declaredAtRelativeLocation, element.type).name
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
  const metaProperties: XptcMetaTypeProperty[] = [];
  const anonymousTypes: XptcMetaType[] = [];

  for (const a of ct.attributes) {
    const tsType = getTsTypeFromLocalRef(
      __XSDS,
      __NAMED_TYPES_BY_TS_NAME,
      ct.declaredAtRelativeLocation,
      a.localTypeRef
    );

    metaProperties.push({
      declaredAt: ct.declaredAtRelativeLocation,
      fromType: metaTypeName,
      name: `@_${a.name}`,
      elem: undefined,
      metaType: {
        name: getMetaTypeName(tsType.name, tsType.doc),
      },
      isArray: false,
      isOptional: a.isOptional,
    });
  }

  for (const e of ct.elements) {
    if (e.kind === "ofRef") {
      const referencedElement = getXptcElementFromLocalElementRef(
        __XSDS,
        __GLOBAL_ELEMENTS,
        ct.declaredAtRelativeLocation,
        e.ref
      );

      if (!referencedElement) {
        throw new Error(`Can't find reference to element '${e.ref}'`);
      }

      const tsType = getTsTypeFromLocalRef(
        __XSDS,
        __NAMED_TYPES_BY_TS_NAME,
        ct.declaredAtRelativeLocation,
        referencedElement.type
      );

      metaProperties.push({
        declaredAt: referencedElement?.declaredAtRelativeLocation,
        fromType: ct.isAnonymous ? "" : ct.name,
        name: referencedElement.name,
        elem: referencedElement,
        metaType: {
          name: getMetaTypeName(tsType.name, tsType.doc),
        },
        typeBody: getTypeBodyForElementRef(
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
      const tsType = getTsTypeFromLocalRef(__XSDS, __NAMED_TYPES_BY_TS_NAME, ct.declaredAtRelativeLocation, e.typeName);

      metaProperties.push({
        declaredAt: ct.declaredAtRelativeLocation,
        fromType: metaTypeName,
        name: e.name,
        elem: undefined, // REALLY?
        metaType: {
          name: getMetaTypeName(tsType.name, tsType.doc),
        },
        isArray: e.isArray,
        isOptional: e.isOptional,
      });
    } else if (e.kind === "ofAnonymousType") {
      const anonymousType = getAnonymousMetaTypeName(e.name, metaTypeName);
      const mp = getMetaProperties(
        __RELATIVE_LOCATION,
        __META_TYPE_MAPPING,
        __GLOBAL_ELEMENTS,
        __SUBSTITUTIONS,
        __XSDS,
        __NAMED_TYPES_BY_TS_NAME,
        e.anonymousType,
        anonymousType
      );
      anonymousTypes.push({ name: anonymousType, properties: mp.metaProperties });
      anonymousTypes.push(...mp.anonymousTypes);
      __META_TYPE_MAPPING.set(anonymousType, {
        name: anonymousType,
        properties: mp.metaProperties,
      });
      metaProperties.push({
        declaredAt: ct.declaredAtRelativeLocation,
        fromType: metaTypeName,
        name: e.name,
        elem: undefined, // REALLY?
        metaType: { name: anonymousType },
        isArray: e.isArray,
        isOptional: e.isOptional,
      });
    } else {
      throw new Error(`Unknown kind of XptcComplexType '${e}'`);
    }
  }

  const immediateParentType = ct.childOf
    ? getTsTypeFromLocalRef(__XSDS, __NAMED_TYPES_BY_TS_NAME, ct.declaredAtRelativeLocation, ct.childOf)
    : undefined;

  let curParentCt = immediateParentType ? __NAMED_TYPES_BY_TS_NAME.get(immediateParentType.name) : undefined;

  let needsExtensionType = ct.needsExtensionType;
  while (curParentCt) {
    if (curParentCt?.type === "complex") {
      needsExtensionType = needsExtensionType || curParentCt.needsExtensionType;
      if (curParentCt.isAnonymous) {
        throw new Error("Anonymous types are never parent types.");
      }
      const d = curParentCt.declaredAtRelativeLocation;

      for (const a of curParentCt.attributes) {
        const attributeType = getTsTypeFromLocalRef(__XSDS, __NAMED_TYPES_BY_TS_NAME, d, a.localTypeRef);
        if (!attributeType) {
          throw new Error(`Can't resolve local type ref ${a.localTypeRef}`);
        }
        metaProperties.push({
          declaredAt: curParentCt.declaredAtRelativeLocation,
          fromType: curParentCt.name,
          elem: undefined,
          name: `@_${a.name}`,
          metaType: { name: getMetaTypeName(attributeType.name, attributeType.doc) },
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
          metaProperties.push({
            elem: undefined, // REALLY?
            declaredAt: curParentCt.declaredAtRelativeLocation,
            fromType: curParentCt.name,
            name: e.name,
            metaType: { name: anonymousTypeName },
            isArray: e.isArray,
            isOptional: e.isOptional,
          });
        } else if (e.kind === "ofNamedType") {
          const tsType = getTsTypeFromLocalRef(
            __XSDS,
            __NAMED_TYPES_BY_TS_NAME,
            ct.declaredAtRelativeLocation,
            e.typeName
          );

          metaProperties.push({
            declaredAt: curParentCt.declaredAtRelativeLocation,
            fromType: curParentCt.name,
            elem: undefined, // REALLY?
            name: e.name,
            metaType: {
              name: getMetaTypeName(tsType.name, tsType.doc),
            },
            isArray: e.isArray,
            isOptional: e.isOptional,
          });
        } else if (e.kind === "ofRef") {
          const referencedElement = getXptcElementFromLocalElementRef(
            __XSDS,
            __GLOBAL_ELEMENTS,
            ct.declaredAtRelativeLocation,
            e.ref
          );

          if (!referencedElement) {
            throw new Error(`Can't find reference to element '${e.ref}'`);
          }

          const tsType = getTsTypeFromLocalRef(
            __XSDS,
            __NAMED_TYPES_BY_TS_NAME,
            ct.declaredAtRelativeLocation,
            referencedElement.type
          );

          metaProperties.push({
            declaredAt: referencedElement?.declaredAtRelativeLocation,
            fromType: ct.isAnonymous ? "" : ct.name,
            name: referencedElement.name,
            elem: referencedElement,
            metaType: {
              name: getMetaTypeName(tsType.name, tsType.doc),
            },
            typeBody: getTypeBodyForElementRef(
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
        ? getTsTypeFromLocalRef(
            __XSDS,
            __NAMED_TYPES_BY_TS_NAME,
            curParentCt.declaredAtRelativeLocation,
            curParentCt.childOf
          )
        : undefined;
      curParentCt = nextParentType ? __NAMED_TYPES_BY_TS_NAME.get(nextParentType.name) : undefined;
    } else if (curParentCt?.type === "simple") {
      throw new Error("Can't have a non-complex type as parent of another.");
    } else {
      curParentCt = undefined;
    }
  }

  if (!(ct.type === "complex" && !ct.isAnonymous && ct.isAbstract)) {
    __META_TYPE_MAPPING.set(metaTypeName, {
      name: metaTypeName,
      properties: [...metaProperties.reduce((acc, p) => acc.set(p.name, p), new Map()).values()], // Removing duplicates.
    });
  }

  return { metaProperties, needsExtensionType, anonymousTypes };
}

function getAnonymousMetaTypeName(elementName: string, metaTypeName: string) {
  return `${metaTypeName}__${elementName}`;
}

function getTsNameFromNamedType(relativeLocation: string, namedTypeName: string) {
  const filenameWithoutExtension = path.basename(relativeLocation).replace(path.extname(relativeLocation), "");
  return `${filenameWithoutExtension}__${namedTypeName}`.replaceAll(/[ -.]/g, "_");
}

function getTsTypeFromLocalRef(
  __XSDS: Map<string, XsdSchema>,
  __NAMED_TYPES_BY_TS_NAME: Map<string, XptcComplexType | XptcSimpleType>,
  relativeLocation: string,
  namedTypeLocalRef: string
): { name: string; doc: string } {
  // check if it's a local ref to another namespace
  if (namedTypeLocalRef.includes(":") && namedTypeLocalRef.split(":").length === 2) {
    const [localNsName, namedTypeName] = namedTypeLocalRef.split(":");
    const xmlnsKey = `@_xmlns:${localNsName}`;
    const namespace = (__XSDS.get(relativeLocation)?.["xsd:schema"] as any)[xmlnsKey];

    // short circuit here. we don't parse XSD's XSD.
    if (namespace === "http://www.w3.org/2001/XMLSchema") {
      const xsdType = XSD__TYPES.get(namedTypeLocalRef);
      if (!xsdType) {
        throw new Error(`Unknown XSD type '${namedTypeLocalRef}'`);
      }
      return { name: xsdType.tsEquivalent, doc: xsdType.doc };
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
      return getTsTypeFromLocalRef(__XSDS, __NAMED_TYPES_BY_TS_NAME, relativeLocation, namedType.restrictionBase);
    }

    // found it!
    return { name: tsTypeName, doc: `type found from local ref '${localNsName}'.` };
  }

  // not a reference to a type in another namespace. simply local name.
  return {
    name: getTsNameFromNamedType(relativeLocation, namedTypeLocalRef),
    doc: "// local type",
  };
}

function xsdSimpleTypeToXptcSimpleType(s: XsdSimpleType, location: string, name: string): XptcSimpleType {
  if (
    (s["xsd:restriction"]?.["@_base"] === "xsd:string" || s["xsd:restriction"]?.["@_base"] === "xsd:token") &&
    s["xsd:restriction"]["xsd:enumeration"]
  ) {
    return {
      doc: "enum",
      type: "simple",
      kind: "enum",
      name: s["@_name"] ?? name,
      declaredAtRelativeLocation: location,
      values: s["xsd:restriction"]["xsd:enumeration"].map((e) => e["@_value"]),
    };
  } else if (s["xsd:restriction"]?.["@_base"] === "xsd:int") {
    return {
      doc: "int",
      type: "simple",
      kind: "int",
      restrictionBase: s["xsd:restriction"]["@_base"],
      name: s["@_name"] ?? name,
      declaredAtRelativeLocation: location,
      minInclusive: s["xsd:restriction"]["xsd:minInclusive"]?.["@_value"],
      maxInclusive: s["xsd:restriction"]["xsd:maxInclusive"]?.["@_value"],
    };
  } else {
    throw new Error(`Unknown xsd:simpleType --> ${JSON.stringify(s, undefined, 2)}`);
  }
}

function getXptcElementFromLocalElementRef(
  __XSDS: Map<string, XsdSchema>,
  __GLOBAL_ELEMENTS: Map<string, XptcElement>,
  relativeLocation: string,
  localElementRef: string
): XptcElement | undefined {
  // check if it's a local ref to another namespace
  if (localElementRef.includes(":") && localElementRef.split(":").length === 2) {
    const [localNsName, referencedElementName] = localElementRef.split(":");
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

  return __GLOBAL_ELEMENTS.get(`${relativeLocation}__${localElementRef}`);
}

function xsdElementToXptcElement(
  parentIdentifierForExtensionType: string,
  xsdElement: NonNullable<Unpacked<XsdSequence["xsd:element"]>>,
  location: string,
  args?: { forceOptional: boolean }
): XptcComplexType["elements"] {
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
    return [
      {
        name: xsdElement["@_name"],
        typeName: xsdElement["@_type"],
        kind: "ofNamedType",
        isArray,
        isOptional,
      },
    ];
  }

  if (xsdElement["xsd:complexType"] && xsdElement["@_name"]) {
    return [
      {
        name: xsdElement["@_name"],
        kind: "ofAnonymousType",
        isArray,
        isOptional,
        anonymousType: xsdComplexTypeToAnonymousXptcComplexType(
          parentIdentifierForExtensionType,
          xsdElement["xsd:complexType"],
          location,
          xsdElement["@_name"]
        ),
      },
    ];
  }

  if (xsdElement["xsd:simpleType"] && xsdElement["@_name"]) {
    throw new Error("Simple types not implemented for anonymous element types.");
  }

  if (xsdElement["@_ref"]) {
    return [
      {
        ref: xsdElement["@_ref"],
        kind: "ofRef",
        isArray,
        isOptional,
      },
    ];
  }

  throw new Error(`Unknown xsd:element structure. ${JSON.stringify(xsdElement)}`);
}

function xsdAttributeToXptcAttribute(xsdAttribute: XsdAttribute): Unpacked<XptcComplexType["attributes"]> {
  return {
    name: xsdAttribute["@_name"],
    localTypeRef: xsdAttribute["@_type"],
    isOptional: xsdAttribute["@_use"] === undefined || xsdAttribute["@_use"] === "optional",
  };
}

function xsdComplexTypeToAnonymousXptcComplexType(
  parentIdentifierForExtensionType: string,
  xsdCt: XsdComplexType,
  location: string,
  element: string
): XptcComplexTypeAnonymous {
  return {
    type: "complex",
    doc: "",
    isAnonymous: true,
    parentIdentifierForExtensionType,
    forElementWithName: element,
    needsExtensionType: !!xsdCt["xsd:anyAttribute"] || !!xsdCt["xsd:sequence"]?.["xsd:any"],
    declaredAtRelativeLocation: location,
    childOf: xsdCt["xsd:complexContent"]?.["xsd:extension"]?.["@_base"],
    elements: [
      ...(xsdCt["xsd:sequence"]?.["xsd:element"] ?? []).flatMap((s) =>
        xsdElementToXptcElement(`${parentIdentifierForExtensionType}__${element}`, s, location)
      ),
      ...(xsdCt["xsd:complexContent"]?.["xsd:extension"]?.["xsd:sequence"]?.["xsd:element"] ?? []).flatMap((s) =>
        xsdElementToXptcElement(`${parentIdentifierForExtensionType}__${element}`, s, location)
      ),
    ],
    attributes: [
      ...(xsdCt["xsd:attribute"] ?? []).map((a) => xsdAttributeToXptcAttribute(a)),
      ...(xsdCt["xsd:complexContent"]?.["xsd:extension"]?.["xsd:attribute"] ?? []).map((a) =>
        xsdAttributeToXptcAttribute(a)
      ),
    ],
  };
}
