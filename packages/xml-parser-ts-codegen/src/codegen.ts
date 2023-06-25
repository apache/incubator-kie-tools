#!/usr/bin/env node

import * as fs from "fs";
import * as path from "path";
import { Parser, getParser } from "@kie-tools/xml-parser-ts";
import {
  TiagoTsImports,
  TiagoTsPrimitiveType,
  TiagoElement,
  TiagoSimpleType,
  TiagoComplexType,
  TiagoComplexTypeNamed,
  TiagoComplexTypeAnonymous,
  TiagoMetaType,
  TiagoMetaTypeProperty,
} from "./types";
import {
  XsdAttribute,
  XsdComplexType,
  XsdSchema,
  XsdSequence,
} from "./schemas/xsd-incomplete--manually-written/ts-gen/types";
import { ns as xsdNs, meta as xsdMeta } from "./schemas/xsd-incomplete--manually-written/ts-gen/meta";

export const __XSD_PARSER = getParser<XsdSchema>({
  ns: xsdNs,
  meta: xsdMeta,
  root: { element: "xs:schema", type: "schema" },
});

export type Unpacked<T> = T extends (infer U)[] ? U : T;

const __LOGS = {
  done: (location: string) => `[codegen] Done for '${location}'.`,
};

export const XSD__TYPES = new Map<string, TiagoTsPrimitiveType>([
  ["xsd:boolean", { type: "primitive", tsEquivalent: "boolean", doc: "xsd:boolean" }],
  ["xsd:QName", { type: "primitive", tsEquivalent: "string", doc: "xsd:QName" }],
  ["xsd:string", { type: "primitive", tsEquivalent: "string", doc: "xsd:string" }],
  ["xsd:int", { type: "primitive", tsEquivalent: "number", doc: "xsd:int" }],
  ["xsd:double", { type: "primitive", tsEquivalent: "number", doc: "xsd:double" }],
  ["xsd:float", { type: "primitive", tsEquivalent: "number", doc: "xsd:float" }],
  ["xsd:IDREF", { type: "primitive", tsEquivalent: "string", doc: "xsd:IDREF" }],
  ["xsd:anyURI", { type: "primitive", tsEquivalent: "string", doc: "xsd:anyURI" }],
  ["xsd:ID", { type: "primitive", tsEquivalent: "string", doc: "xsd:ID" }],
]);

// TODO: Unit tests
async function fetchXsdString(baseLocation: string, relativeLocation: string) {
  try {
    const url = new URL(relativeLocation);
    throw new Error("URLs not yet supported.");
  } catch (e) {
    const p = path.resolve(baseLocation, relativeLocation);
    return { location: p, xsdString: fs.readFileSync(p) };
  }
}

// TODO: Unit tests
async function parseDeep(
  __XSD_PARSER: Parser<XsdSchema>,
  baseLocation: string,
  relativeLocation: string
): Promise<[string, XsdSchema][]> {
  const { xsdString } = await fetchXsdString(baseLocation, relativeLocation);

  const { json: schema } = __XSD_PARSER.parse({ xml: xsdString });

  const importPromises = (schema["xsd:schema"]["xsd:import"] ?? []).map((i) =>
    parseDeep(__XSD_PARSER, baseLocation, i["@_schemaLocation"])
  );

  const imports = (await Promise.all(importPromises)).flatMap((s) => s);

  return [[relativeLocation, schema], ...imports];
}

async function main() {
  const __LOCATION = process.argv[2];
  const __ROOT_ELEMENT_NAME = process.argv[3];
  const __BASE_LOCATION = path.dirname(__LOCATION);
  const __RELATIVE_LOCATION = path.basename(__LOCATION);

  const __ROOT_ELEMENT = `${__RELATIVE_LOCATION}__${__ROOT_ELEMENT_NAME}`;

  const __RELATIVE_LOCATION_WITHOUT_EXTENSION = __RELATIVE_LOCATION.replace(path.extname(__RELATIVE_LOCATION), "");

  const __CONVENTIONS = {
    extensionTypesLocation: path.resolve(".", path.join(__BASE_LOCATION, "ts-gen-extensions")),
    outputFileForGeneratedTypes: path.resolve(".", path.join(__BASE_LOCATION, "ts-gen/types.d.ts")),
    outputFileForGeneratedMeta: path.resolve(".", path.join(__BASE_LOCATION, "ts-gen/meta.ts")),
  };

  // gather all the XSDs
  const __XSDS = new Map<string, XsdSchema>(await parseDeep(__XSD_PARSER, __BASE_LOCATION, __RELATIVE_LOCATION));

  // types

  // // process <xsd:simpleType>'s
  const __SIMPLE_TYPES: TiagoSimpleType[] = Array.from(__XSDS.entries()).flatMap(([location, schema]) =>
    (schema["xsd:schema"]["xsd:simpleType"] || []).flatMap((s) => {
      if (s["xsd:restriction"]?.["@_base"] === "xsd:string" && s["xsd:restriction"]["xsd:enumeration"]) {
        return {
          doc: "enum",
          type: "simple",
          kind: "enum",
          name: s["@_name"],
          declaredAtRelativeLocation: location,
          values: s["xsd:restriction"]["xsd:enumeration"].map((e) => e["@_value"]),
        };
      } else if (s["xsd:restriction"]?.["@_base"] === "xsd:int") {
        return {
          doc: "int",
          type: "simple",
          kind: "int",
          restrictionBase: s["xsd:restriction"]["@_base"],
          name: s["@_name"],
          declaredAtRelativeLocation: location,
          minInclusive: s["xsd:restriction"]["xsd:minInclusive"]?.["@_value"],
          maxInclusive: s["xsd:restriction"]["xsd:maxInclusive"]?.["@_value"],
        };
      } else {
        throw new Error(`Unknown xsd:simpleType --> ${JSON.stringify(s, undefined, 2)}`);
      }
    })
  );

  // // process <xsd:complexType>'s
  const __COMPLEX_TYPES: TiagoComplexTypeNamed[] = [];
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
            xsdElementToTiagoElement(xsdCt["@_name"]!, s, location)
          ),
          ...(xsdCt["xsd:sequence"]?.["xsd:element"] ?? []).flatMap((s) =>
            xsdElementToTiagoElement(xsdCt["@_name"]!, s, location)
          ),
          ...(xsdCt["xsd:complexContent"]?.["xsd:extension"]["xsd:sequence"]?.["xsd:element"] ?? []).flatMap((s) =>
            xsdElementToTiagoElement(xsdCt["@_name"]!, s, location)
          ),
          ...(
            xsdCt["xsd:complexContent"]?.["xsd:extension"]["xsd:sequence"]?.["xsd:choice"]?.["xsd:element"] ?? []
          ).flatMap((s) => xsdElementToTiagoElement(xsdCt["@_name"]!, s, location, { forceOptional: true })),
          ...(xsdCt["xsd:complexContent"]?.["xsd:extension"]["xsd:choice"]?.["xsd:element"] ?? []).flatMap((s) =>
            xsdElementToTiagoElement(xsdCt["@_name"]!, s, location, { forceOptional: true })
          ),
          ...(
            xsdCt["xsd:complexContent"]?.["xsd:extension"]["xsd:choice"]?.["xsd:sequence"]?.["xsd:element"] ?? []
          ).flatMap((s) => xsdElementToTiagoElement(xsdCt["@_name"]!, s, location, { forceOptional: true })),
        ],
        attributes: [
          ...(xsdCt["xsd:attribute"] ?? []).map((a) => xsdAttributeToTiagoAttribute(a)),
          ...(xsdCt["xsd:complexContent"]?.["xsd:extension"]["xsd:attribute"] ?? []).map((a) =>
            xsdAttributeToTiagoAttribute(a)
          ),
        ],
      });
    }
  }

  // // process <xsd:element>'s
  const __ELEMENTS = new Map<string, TiagoElement>();
  for (const [location, xsd] of __XSDS.entries()) {
    for (const e of xsd["xsd:schema"]["xsd:element"] || []) {
      __ELEMENTS.set(`${location}__${e["@_name"]}`, {
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
          const subsGroup = getTiagoElementFromLocalElementRef(__XSDS, __ELEMENTS, xLocation, e["@_substitutionGroup"]);
          if (!subsGroup) {
            throw new Error(`Invalid subsitution group for element '${e["@_name"]}'`);
          }
          const elem = getTiagoElementFromLocalElementRef(__XSDS, __ELEMENTS, xLocation, e["@_name"]);
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

  const __NAMED_TYPES_BY_TS_NAME = new Map<string, TiagoComplexType | TiagoSimpleType>([
    ...__SIMPLE_TYPES.map(
      (st) => [getTsNameFromNamedType(st.declaredAtRelativeLocation, st.name), st] as [string, TiagoSimpleType]
    ),
    ...__COMPLEX_TYPES.map(
      (ct) => [getTsNameFromNamedType(ct.declaredAtRelativeLocation, ct.name), ct] as [string, TiagoComplexType]
    ),
  ]);

  const __IMPORTED_TYPES = new Map<string, string>();
  const __IMPORTS: TiagoTsImports = {
    save(typeName: string, relativePath: string) {
      __IMPORTED_TYPES.set(typeName, path.join(__CONVENTIONS.extensionTypesLocation, relativePath));
    },
  };

  const __META_TYPE_MAPPING = new Map<string, TiagoMetaType>();

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
  const rootTsTypeName = getTsNameFromNamedType(
    __RELATIVE_LOCATION_WITHOUT_EXTENSION,
    __ELEMENTS.get(__ROOT_ELEMENT)!.type
  );

  for (const ct of __COMPLEX_TYPES) {
    if (ct.isAbstract) {
      continue;
    }
    const typeName = getTsNameFromNamedType(ct.declaredAtRelativeLocation, ct.name);
    const metaProperties = getMetaProperties(
      __META_TYPE_MAPPING,
      __IMPORTS,
      __ELEMENTS,
      __SUBSTITUTIONS,
      __XSDS,
      __NAMED_TYPES_BY_TS_NAME,
      ct,
      typeName
    );

    const doc = ct.doc.trim() ? `/* ${ct.doc} */` : "";

    const properties = metaProperties
      .map((p) => {
        const optionalMarker = p.isOptional ? "?" : "";
        const arrayMarker = p.isArray ? "[]" : "";
        const tsType = p.metaType.name === "integer" || p.metaType.name === "float" ? "number" : p.metaType.name;
        const ns = getMetaPropertyNs(__RELATIVE_LOCATION, p);
        return `    "${ns}${p.name}"${optionalMarker}: ${tsType}${arrayMarker}; // from type ${p.fromType} @ ${p.declaredAt}`;
      })
      .join("\n");

    const extensionTypeName = getTsExtensionTypeNameFor(ct);
    if (ct.needsExtensionType) {
      __IMPORTS.save(extensionTypeName, extensionTypeName);
    }

    const extensionType = ct.needsExtensionType ? `& ${extensionTypeName}` : "";

    const rootTypeExtraTypes =
      rootTsTypeName === typeName ? "Partial<{ [k: `@_xmlns:${string}`]: string }> & { '@_xmlns'?: string } & " : "";

    ts += `
export type ${typeName} = ${rootTypeExtraTypes} ${doc} {
${properties}
} ${extensionType}
`;
  }

  let imports = "";
  for (const [name, absolutePath] of __IMPORTED_TYPES.entries()) {
    const relativePath = path.relative(path.dirname(__CONVENTIONS.outputFileForGeneratedTypes), absolutePath);
    imports += `import { ${name} } from '${relativePath}'
`;
  }

  ts = `${imports}

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
    element: "${__ROOT_ELEMENT}",
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

// TODO: Import these from parser.ts
export type TypeDef = { type: string; isArray: boolean; isOptional: boolean };
export type Meta = Record<string, Record<string, TypeDef>>;


export const meta = {
`;

  Array.from(__META_TYPE_MAPPING.entries()).forEach(([name, type]) => {
    meta += `    "${name}": {
`;
    type.properties.forEach((p) => {
      const ns = getMetaPropertyNs(__RELATIVE_LOCATION, p);
      meta += `        "${ns}${p.name}": { type: "${p.metaType.name}", isArray: ${p.isArray}, isOptional: ${p.isOptional} },
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

function getMetaPropertyNs(__RELATIVE_LOCATION: string, p: TiagoMetaTypeProperty) {
  return p.name.startsWith("@_")
    ? ""
    : getRealtiveLocationNs(__RELATIVE_LOCATION, p.elem?.declaredAtRelativeLocation ?? p.declaredAt);
}

function getRealtiveLocationNs(__RELATIVE_LOCATION: string, relativeLocation: string) {
  return relativeLocation === __RELATIVE_LOCATION
    ? ""
    : `${relativeLocation.replace(".xsd", "").toLocaleLowerCase().replaceAll(/\d/g, "")}:`;
}

function getTsExtensionTypeNameFor(ct: TiagoComplexType) {
  const tsTypeName = !ct.isAnonymous
    ? getTsNameFromNamedType(ct.declaredAtRelativeLocation, ct.name)
    : `${getTsNameFromNamedType(ct.declaredAtRelativeLocation, ct.parentIdentifierForExtensionType)}__${
        ct.forElementWithName
      }`;

  return `${tsTypeName}ExtensionType`;
}

function resolveElementRef(
  __ELEMENTS: Map<string, TiagoElement>,
  __XSDS: Map<string, XsdSchema>,
  substitutions: Map<string, string[]>,
  referencedElement: TiagoElement
): TiagoElement[] {
  const key = `${referencedElement.declaredAtRelativeLocation}__${referencedElement.name}`;
  const substitutionNamesForReferencedElement = substitutions.get(key);
  if (!substitutionNamesForReferencedElement) {
    return [referencedElement];
  }

  return substitutionNamesForReferencedElement.flatMap((substitutionElementName) => {
    const substitutionElement = __ELEMENTS.get(substitutionElementName);
    if (!substitutionElement) {
      throw new Error(`Can't find element '${substitutionElementName}' for substitution ${key}`);
    }
    return resolveElementRef(__ELEMENTS, __XSDS, substitutions, substitutionElement);
  });
}

function getAnonymousMetaTypeName(elementName: string, metaTypeName: string) {
  return `anonymous__${elementName}__at__${metaTypeName}`;
}

function getMetaTypeName(typeName: string, doc: string) {
  return typeName === "number" ? (doc === "xsd:double" || doc === "xsd:float" ? "float" : "integer") : typeName;
}

function getMetaProperties(
  __META_TYPE_MAPPING: Map<string, TiagoMetaType>,
  __IMPORTS: TiagoTsImports,
  __ELEMENTS: Map<string, TiagoElement>,
  __SUBSTITUTIONS: Map<string, Map<string, string[]>>,
  __XSDS: Map<string, XsdSchema>,
  __NAMED_TYPES_BY_TS_NAME: Map<string, TiagoComplexType | TiagoSimpleType>,
  ct: TiagoComplexType,
  metaTypeName: string
): TiagoMetaTypeProperty[] {
  const metaProperties: TiagoMetaTypeProperty[] = [];

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
        tiagoType: __NAMED_TYPES_BY_TS_NAME.get(tsType.name),
      },
      isArray: false,
      isOptional: a.isOptional,
    });
  }

  for (const e of ct.elements) {
    if (e.kind === "ofRef") {
      const referencedElement = getTiagoElementFromLocalElementRef(
        __XSDS,
        __ELEMENTS,
        ct.declaredAtRelativeLocation,
        e.ref
      );

      if (!referencedElement) {
        throw new Error(`Can't find reference to element '${e.ref}'`);
      }

      const resolutions = resolveElementRef(
        __ELEMENTS,
        __XSDS,
        __SUBSTITUTIONS.get(ct.declaredAtRelativeLocation)!,
        referencedElement
      );

      for (const elem of resolutions) {
        const tsType = getTsTypeFromLocalRef(
          __XSDS,
          __NAMED_TYPES_BY_TS_NAME,
          ct.declaredAtRelativeLocation,
          elem.type
        );
        const isOptionalForSure = e.isOptional || resolutions.length > 1;

        metaProperties.push({
          declaredAt: ct.declaredAtRelativeLocation,
          fromType: metaTypeName,
          name: elem.name,
          elem: elem,
          metaType: {
            name: getMetaTypeName(tsType.name, tsType.doc),
            tiagoType: __NAMED_TYPES_BY_TS_NAME.get(tsType.name),
          },
          isArray: e.isArray,
          isOptional: isOptionalForSure,
        });
      }
    } else if (e.kind === "ofNamedType") {
      const tsType = getTsTypeFromLocalRef(__XSDS, __NAMED_TYPES_BY_TS_NAME, ct.declaredAtRelativeLocation, e.typeName);

      metaProperties.push({
        declaredAt: ct.declaredAtRelativeLocation,
        fromType: metaTypeName,
        name: e.name,
        elem: undefined, // REALLY?
        metaType: {
          name: getMetaTypeName(tsType.name, tsType.doc),
          tiagoType: __NAMED_TYPES_BY_TS_NAME.get(tsType.name),
        },
        isArray: e.isArray,
        isOptional: e.isOptional,
      });
    } else if (e.kind === "ofAnonymousType") {
      const anonymousMetaTypeName = getAnonymousMetaTypeName(e.name, metaTypeName);

      metaProperties.push({
        declaredAt: ct.declaredAtRelativeLocation,
        fromType: metaTypeName,
        name: e.name,
        elem: undefined, // REALLY?
        metaType: { name: "any", tiagoType: undefined },
        isArray: e.isArray,
        isOptional: e.isOptional,
      });
    } else {
      throw new Error(`Unknown kind of TiagoComplexType '${e}'`);
    }
  }

  const immediateParentType = ct.childOf
    ? getTsTypeFromLocalRef(__XSDS, __NAMED_TYPES_BY_TS_NAME, ct.declaredAtRelativeLocation, ct.childOf)
    : undefined;

  let curParentCt = immediateParentType ? __NAMED_TYPES_BY_TS_NAME.get(immediateParentType.name) : undefined;

  while (curParentCt) {
    if (curParentCt?.type === "complex") {
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
          metaType: { name: getMetaTypeName(attributeType.name, attributeType.doc), tiagoType: undefined },
          isArray: false,
          isOptional: a.isOptional,
        });
      }

      for (const e of curParentCt.elements) {
        if (e.kind === "ofAnonymousType") {
          const anonymousTypeName = getAnonymousMetaTypeName(
            e.name,
            getTsNameFromNamedType(curParentCt.declaredAtRelativeLocation, curParentCt.name)
          );

          metaProperties.push({
            elem: undefined, // REALLY?
            declaredAt: curParentCt.declaredAtRelativeLocation,
            fromType: curParentCt.name,
            name: e.name,
            metaType: { name: "any", tiagoType: undefined },
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
              tiagoType: __NAMED_TYPES_BY_TS_NAME.get(tsType.name),
            },
            isArray: e.isArray,
            isOptional: e.isOptional,
          });
        } else if (e.kind === "ofRef") {
          const referencedElement = getTiagoElementFromLocalElementRef(
            __XSDS,
            __ELEMENTS,
            curParentCt.declaredAtRelativeLocation,
            e.ref
          );

          if (!referencedElement) {
            throw new Error(`Can't find reference to element '${e.ref}'`);
          }

          const resolutions = resolveElementRef(
            __ELEMENTS,
            __XSDS,
            __SUBSTITUTIONS.get(ct.declaredAtRelativeLocation)!,
            referencedElement
          );

          for (const elem of resolutions ?? []) {
            const tsType = getTsTypeFromLocalRef(
              __XSDS,
              __NAMED_TYPES_BY_TS_NAME,
              ct.declaredAtRelativeLocation,
              elem.type
            );
            const isOptionalForSure = e.isOptional || resolutions.length > 1;

            metaProperties.push({
              declaredAt: curParentCt.declaredAtRelativeLocation,
              fromType: referencedElement.type,
              name: elem.name,
              elem: elem,
              metaType: {
                name: getMetaTypeName(tsType.name, tsType.doc),
                tiagoType: __NAMED_TYPES_BY_TS_NAME.get(tsType.name)!,
              },
              isArray: e.isArray,
              isOptional: isOptionalForSure,
            });
          }
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

  return metaProperties;
}

function getTsNameFromNamedType(relativeLocation: string, namedTypeName: string) {
  const filenameWithoutExtension = path.basename(relativeLocation).replace(path.extname(relativeLocation), "");
  return `${filenameWithoutExtension}__${namedTypeName}`.replaceAll(/[ -.]/g, "_");
}

function getTsTypeFromLocalRef(
  __XSDS: Map<string, XsdSchema>,
  __NAMED_TYPES_BY_TS_NAME: Map<string, TiagoComplexType | TiagoSimpleType>,
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

function getTiagoElementFromLocalElementRef(
  __XSDS: Map<string, XsdSchema>,
  __ELEMENTS: Map<string, TiagoElement>,
  relativeLocation: string,
  localElementRef: string
): TiagoElement | undefined {
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

    return __ELEMENTS.get(`${referencedXsdRelativeLocation}__${referencedElementName}`);
  }

  return __ELEMENTS.get(`${relativeLocation}__${localElementRef}`);
}

function xsdElementToTiagoElement(
  parentIdentifierForExtensionType: string,
  xsdElement: NonNullable<Unpacked<XsdSequence["xsd:element"]>>,
  location: string,
  args?: { forceOptional: boolean }
): TiagoComplexType["elements"] {
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
        anonymousType: xsdComplexTypeToAnonymousTiagoComplexType(
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

function xsdAttributeToTiagoAttribute(xsdAttribute: XsdAttribute): Unpacked<TiagoComplexType["attributes"]> {
  return {
    name: xsdAttribute["@_name"],
    localTypeRef: xsdAttribute["@_type"],
    isOptional: xsdAttribute["@_use"] === undefined || xsdAttribute["@_use"] === "optional",
  };
}

function xsdComplexTypeToAnonymousTiagoComplexType(
  parentIdentifierForExtensionType: string,
  xsdCt: XsdComplexType,
  location: string,
  element: string
): TiagoComplexTypeAnonymous {
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
        xsdElementToTiagoElement(`${parentIdentifierForExtensionType}__${element}`, s, location)
      ),
      ...(xsdCt["xsd:complexContent"]?.["xsd:extension"]["xsd:sequence"]?.["xsd:element"] ?? []).flatMap((s) =>
        xsdElementToTiagoElement(`${parentIdentifierForExtensionType}__${element}`, s, location)
      ),
    ],
    attributes: [
      ...(xsdCt["xsd:attribute"] ?? []).map((a) => xsdAttributeToTiagoAttribute(a)),
      ...(xsdCt["xsd:complexContent"]?.["xsd:extension"]["xsd:attribute"] ?? []).map((a) =>
        xsdAttributeToTiagoAttribute(a)
      ),
    ],
  };
}
