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

export type XmlParserTs<T extends object> = {
  parse: (
    args: { type: "xml"; xml: string | Buffer } | { type: "domdoc"; domdoc: Document; instanceNs: Map<string, string> }
  ) => {
    json: T;
    instanceNs: Map<string, string>;
  };
  build: (args: { json: T; instanceNs: Map<string, string> }) => string;
};

export type XmlParserTsRootElementBaseType = Partial<{ [k: `@_xmlns:${string}`]: string }> & { "@_xmlns"?: string };

export type XmlDocument = {
  "?xml"?: {
    "@_encoding"?: string;
    "@_version"?: string;
  };
};

export type MetaTypeProp = { type: string; isArray: boolean; fromType: string; xsdType: string };

export type MetaType = Record<string, MetaTypeProp>;

export type Meta = Record<string, MetaType>;

export type Root = { element: string; type: string };

export type Subs = Record<string, Record<string, string>>;

export type Elements = Record<string, string>;

export const domParser = {
  getDomDocument: (xml: string | Buffer) => {
    // console.time("parsing dom took (DOMParser)");
    const domdoc = new DOMParser().parseFromString(xml.toString(), "application/xml");
    // console.timeEnd("parsing dom took (DOMParser)");
    return domdoc;
  },
};

/**
 * Returns a bi-directional map with the namespace aliases declared at the root element of a XML document pointing to their URIs and vice-versa. In this map, namespace aliases are suffixed with `:`.
 * E.g. "dmn:" => "https://www.omg.org/spec/DMN/20211108/MODEL/"
 *      "https://www.omg.org/spec/DMN/20211108/MODEL/" => "dmn:"
 */
export function getInstanceNs(domdoc: Document): Map<string, string> {
  // console.time("instanceNs took");

  const nsMap = new Map<string, string>(
    [...domdoc.documentElement.attributes].flatMap((attr) => {
      if (!attr.name.startsWith("xmlns")) {
        return [];
      }

      const nsUri = attr.value;

      const s = attr.name.split(":");
      if (s.length === 1) {
        // That's the default namespace.
        return [
          [nsUri, ""],
          ["", nsUri],
        ];
      } else if (s.length === 2) {
        // Normal namespace mapping.
        return [
          [nsUri, `${s[1]}:`],
          [`${s[1]}:`, nsUri],
        ];
      } else {
        throw new Error(`Invalid xmlns mapping attribute '${attr.name}'`);
      }
    })
  );

  // console.timeEnd("instanceNs took");
  return nsMap;
}

export function getInstanceNsFromJson(rootElement: XmlParserTsRootElementBaseType): Map<string, string> {
  // console.time("instanceNsFromJson took");

  const nsMap = new Map<string, string>(
    [...Object.entries(rootElement)].flatMap(([attr, value]) => {
      if (!attr.startsWith("@_xmlns")) {
        return [];
      }

      const nsUri = value ?? "";

      const s = attr.split(":");
      if (s.length === 1) {
        // That's the default namespace.
        return [
          [nsUri, ""],
          ["", nsUri],
        ];
      } else if (s.length === 2) {
        // Normal namespace mapping.
        return [
          [nsUri, `${s[1]}:`],
          [`${s[1]}:`, nsUri],
        ];
      } else {
        throw new Error(`Invalid xmlns mapping attribute '${attr}'`);
      }
    })
  );

  // console.timeEnd("instanceNsFromJson took");
  return nsMap;
}

export function getParser<T extends object>(args: {
  /** Meta information about the structure of the XML. Used for deciding whether a property is array, boolean, float or integer. */
  meta: Meta;
  /** Substituion group mapping going from concrete elements to their substitution group head. */
  subs: Subs;
  /** Element types mapped by their namespaced names. */
  elements: Elements;
  /** Bi-directional namespace --> URI mapping. This is the one used to normalize the resulting JSON, independent of the namespaces declared on the XML instance. */
  ns: Map<string, string>;
  /** Information about the root element used on the XML documents */
  root: Root;
}): XmlParserTs<T> {
  return {
    parse: (parseArgs) => {
      const domdoc = parseArgs.type === "domdoc" ? parseArgs.domdoc : domParser.getDomDocument(parseArgs.xml);
      const instanceNs = parseArgs.type === "domdoc" ? parseArgs.instanceNs : getInstanceNs(domdoc);

      // console.time("parsing overhead took");
      const rootType = { [args.root.element]: { type: args.root.type, isArray: false, xsdType: "", fromType: "" } };
      const json = parse({ ...args, instanceNs, node: domdoc, nodeMetaType: rootType });
      // console.timeEnd("parsing overhead took");

      return { json, instanceNs };
    },
    build: ({ json, instanceNs }) => {
      // console.time("building took");
      const __json = JSON.parse(JSON.stringify(json));

      for (const [k, v] of [...args.ns.entries()]) {
        if (k.endsWith(":") || k === "" /* Filters only `xmlns --> URL` mappings, since `ns` is bi-directional.*/) {
          const instanceNsKey = instanceNs.get(v)?.slice(0, -1);
          const originalXmlnsPropName = instanceNsKey ? `@_xmlns:${instanceNsKey}` : `@_xmlns`;
          if (instanceNsKey === undefined || !__json[args.root.element][originalXmlnsPropName]) {
            const nsName = k.slice(0, -1);
            const newXmlnsPropName = nsName ? `@_xmlns:${nsName}` : `@_xmlns`;
            console.warn(`Adding NS mapping to XML: ${newXmlnsPropName} --> ${v}`);
            __json[args.root.element][newXmlnsPropName] = v;
          }
        }
      }

      __json["?xml"] = {
        "@_version": "1.0",
        "@_encoding": "UTF-8",
      };

      // Since building starts from a level above the root element, we need create this pseudo-metaType to correctly type the tree we're building.
      const rootMetaType = {
        [args.root.element]: { type: args.root.type, fromType: "root", isArray: false, xsdType: "// root" },
      };

      const declaredElementsOrder = [];
      for (const e in args.elements ?? {}) {
        declaredElementsOrder.push(e);
      }

      const xml = build({
        json: __json,
        ns: args.ns,
        instanceNs,
        elements: args.elements,
        meta: args.meta,
        metaType: rootMetaType,
        declaredElementsOrder,
        indent: "",
      });
      // console.timeEnd("building took");
      return xml;
    },
  };
}

/////////////
/// PARSE ///
/////////////

export function parse(args: {
  node: Node;
  nodeMetaType: MetaType | undefined;
  ns: Map<string, string>;
  instanceNs: Map<string, string>;
  meta: Meta;
  elements: Elements;
  subs: Subs;
}) {
  const json: any = {};

  const children = args.node.childNodes;
  for (let ii = 0; ii < children.length; ii++) {
    const elemNode = children[ii];

    if (elemNode.nodeType === 1 /* ELEMENT_NODE */) {
      const { nsedName, subsedName } = resolveQName(elemNode.nodeName, args.nodeMetaType, args);

      const elemMetaProp = args.nodeMetaType?.[subsedName ?? nsedName];

      const elemMetaType =
        args.meta[args.elements[nsedName]] ?? // Regardless of subsitutions that might have occured, we need the type information for the concrete implementation of the element. (E.g., On DMN, tDecision is substituted by tDrgElement)
        (elemMetaProp
          ? args.meta[elemMetaProp.type] // If we can't find this type with the `elements` mapping, we try directly from `meta`.
          : undefined); // If the current element is not known, we simply ignore its type and go with the defaults.

      // If the elemNode's meta type has a __$$text property, this is the one we use to parse its value.
      // All other properties (except `__$$element`) on `elemType` are certainly attributes, which are handled below.
      // The `__$$element` property is also processed below, since there can be an element with both `__$$text` and `__$$element` properties.
      const t = elemMetaType?.["__$$text"]?.type ?? elemMetaProp?.type;

      let elemValue: any = {};
      if (t === "string") {
        elemValue["__$$text"] = elemNode.textContent ?? "";
      } else if (t === "boolean") {
        elemValue["__$$text"] = parseBoolean(elemNode.textContent ?? "");
      } else if (t === "float") {
        elemValue["__$$text"] = parseFloat(elemNode.textContent ?? "");
      } else if (t === "integer") {
        elemValue["__$$text"] = parseFloat(elemNode.textContent ?? "");
      } else {
        elemValue = parse({ ...args, node: elemNode, nodeMetaType: elemMetaType });
      }

      if (subsedName !== nsedName) {
        // substitution occurred, need to save the original, normalized element name
        elemValue["__$$element"] = nsedName;
      }

      const argsForAttrs = { ns: args.ns, instanceNs: args.instanceNs, subs: {} }; // Attributes can't use substitution groups.
      const attrs = (elemNode as Element).attributes;
      for (let i = 0; i < attrs.length; i++) {
        const attr = attrs[i];
        const resolvedAttrQName = resolveQName(attr.name, args.nodeMetaType, argsForAttrs);

        // If the attribute's name is not qualified, we don't mess
        // with it. We treat it as having no namespace, instead of
        // potentially using the default namespace mapped with `xmlns=`.
        const attrName = resolvedAttrQName.isQualified
          ? resolvedAttrQName.subsedName ?? resolvedAttrQName.nsedName
          : attr.name;

        const attrPropType = elemMetaType?.[`@_${attrName}`];

        let attrValue: any;
        if (attrPropType?.type === "string") {
          attrValue = attr.value;
        } else if (attrPropType?.type === "boolean") {
          attrValue = parseBoolean(attr.value);
        } else if (attrPropType?.type === "float") {
          attrValue = parseFloat(attr.value);
        } else if (attrPropType?.type === "integer") {
          attrValue = parseFloat(attr.value);
        } else if (attrPropType?.type === "allNNI") {
          attrValue = parseAllNNI(attr.value);
        } else {
          attrValue = attr.value; // Unknown type, default to the text from the XML.
        }

        elemValue[`@_${attrName}`] = attrValue;
      }

      const currentValue = json[subsedName ?? nsedName];

      if (elemMetaProp?.isArray) {
        json[subsedName ?? nsedName] ??= [];
        json[subsedName ?? nsedName].push(elemValue);
      } else if (currentValue) {
        if (elemMetaProp && !elemMetaProp.isArray) {
          console.warn(
            `[xml-parser-ts] Accumulating values on known non-array property '${subsedName}' (${nsedName}) of type '${elemMetaProp.type}'.`
          );
        }

        if (Array.isArray(currentValue)) {
          currentValue.push(elemValue);
        } else {
          json[subsedName ?? nsedName] = [currentValue, elemValue]; // Default behavior of accumulating elements with the same name inside an array.
        }
      } else {
        json[subsedName ?? nsedName] = elemValue;
      }
    }
  }

  return json;
}

export function resolveQName(
  name: string,
  parentMetaType: MetaType | undefined,
  {
    ns,
    instanceNs,
    subs,
  }: {
    ns: Map<string, string>;
    instanceNs: Map<string, string>;
    subs: Subs;
  }
) {
  let nameNs = undefined;
  let nameName = undefined;
  let isQualified: boolean;

  const s = name.split(":");
  if (s.length === 1) {
    nameNs = ns.get(instanceNs.get("")!) ?? "";
    nameName = s[0];
    isQualified = false;
  } else if (s.length === 2) {
    nameNs = ns.get(instanceNs.get(`${s[0]}:`)!) ?? `${s[0]}:`;
    nameName = s[1];
    isQualified = true;
  } else {
    throw new Error(name);
  }

  // nsedName stands for "Namespaced name".
  const nsedName = `${nameNs}${nameName}`;

  const nsedSubs = subs[nameNs];

  // subsedName stands for "Substituted name";
  let subsedName: string | undefined = nsedName;

  // Resolve substituionGroups
  while (nsedSubs && !parentMetaType?.[subsedName]) {
    if (subsedName === undefined) {
      break; // Not mapped, ignore unknown element...
    }
    subsedName = nsedSubs[subsedName];
  }

  return { nsedName, subsedName, isQualified };
}

function parseInt(attrValue: string) {
  let i: number;
  try {
    i = Number.parseInt(attrValue);
  } catch (e) {
    throw new Error(`Cannot parse integer value '${attrValue}'`);
  }

  if (Number.isNaN(i)) {
    throw new Error(`Stopping NaN from propagating. Tried to parse from (integer) '${attrValue}'`);
  }

  return i;
}

function parseFloat(attrValue: string) {
  let f: number;
  try {
    f = Number.parseFloat(attrValue);
  } catch (e) {
    throw new Error(`Cannot parse float value '${attrValue}'`);
  }

  if (Number.isNaN(f)) {
    throw new Error(`Stopping NaN from propagating. Tried to parse from (float) '${attrValue}'`);
  }

  return f;
}

//** AllNNI stands for All non-negative integers. This comes from the XSD specification. */
function parseAllNNI(attrValue: string) {
  try {
    return attrValue === "unbounded" ? "unbounded" : parseInt(attrValue);
  } catch (e) {
    throw new Error(`Cannot parse allNNI value '${attrValue}'`);
  }
}

function parseBoolean(attrValue: string) {
  if (attrValue === "true") {
    return true;
  } else if (attrValue === "false") {
    return false;
  } else {
    throw new Error(`Cannot parse boolean value '${attrValue}'`);
  }
}

/////////////
/// BUILD ///
/////////////

const ampEntity = { regex: new RegExp(`&`, "g"), replacement: "&amp;" };
const gtEntity = { regex: new RegExp(`>`, "g"), replacement: "&gt;" };
const ltEntity = { regex: new RegExp(`<`, "g"), replacement: "&lt;" };
const aposEntity = { regex: new RegExp(`'`, "g"), replacement: "&apos;" };
const quotEntity = { regex: new RegExp(`"`, "g"), replacement: "&quot;" };

function applyEntities(value: any) {
  return `${value}`
    .replace(ampEntity.regex, ampEntity.replacement)
    .replace(gtEntity.regex, gtEntity.replacement)
    .replace(ltEntity.regex, ltEntity.replacement)
    .replace(aposEntity.regex, aposEntity.replacement)
    .replace(quotEntity.regex, quotEntity.replacement);
}

function buildAttrs(json: any, { ns, instanceNs }: { ns: Map<string, string>; instanceNs: Map<string, string> }) {
  let isEmpty = true;
  let hasText = false;
  let attrs = " ";

  // Attributes don't ever need to be serialized in a particular order.
  for (const propName in json) {
    if (propName[0] === "@") {
      const attrName =
        propName.split(":").length === 2 // only apply namespace if it's qualified name. attributes are unnamespaced by default.
          ? applyInstanceNs({ propName: propName.substring(2), instanceNs, ns })
          : propName.substring(2);

      attrs += `${attrName}="${applyEntities(json[propName])}" `;
    } else if (propName === "__$$text") {
      hasText = true;
      isEmpty = false;
    } else if (propName !== "__$$element") {
      isEmpty = false;
    }
  }

  if (typeof json !== "object") {
    isEmpty = false;
  }

  return { attrs: attrs.substring(0, attrs.length - 1), isEmpty, hasText };
}

export function build(args: {
  json: any;
  ns: Map<string, string>;
  instanceNs: Map<string, string>;
  elements: Elements;
  meta: Meta;
  metaType: MetaType | undefined;
  declaredElementsOrder: string[];
  indent: string;
}): string {
  const { json, ns, instanceNs, indent, metaType } = args;

  if (typeof json !== "object" || json === null) {
    throw new Error(`Can't build XML from a non-object value. '${json}'.`);
  }

  let xml = "";

  // We want to respect a certain order here given xsd:sequence, xsd:choice, and xsd:all declarations
  const sortedJsonProps: string[] = [];
  for (const p in json) {
    sortedJsonProps.push(p);
  }

  const declaredPropOrder: string[] = [];
  for (const p in metaType ?? {}) {
    declaredPropOrder.push(p);
  }

  sortedJsonProps.sort((a, b) => declaredPropOrder.indexOf(a) - declaredPropOrder.indexOf(b));

  // After the correct order is established, we can iterate over the `json` object.
  for (const __unsafeJsonPropName of sortedJsonProps) {
    const jsonPropName = applyEntities(__unsafeJsonPropName);
    const jsonPropValue = json[jsonPropName];

    // attributes are processed individually.
    if (jsonPropName[0] === "@") {
      continue;
    }
    // ignore this, as we won't make it part of the final XML.
    else if (jsonPropName === "__$$element") {
      continue;
    }
    // ignore this. text content is treated inside the "array" and "nested element" sections.
    else if (jsonPropName === "__$$text") {
      continue;
    }
    // pi tag
    else if (jsonPropName[0] === "?") {
      xml = `${indent}<${jsonPropName}${buildAttrs(jsonPropValue, args).attrs} ?>\n` + xml; // PI Tags should always go at the top of the XML
    }
    // empty tag
    else if (jsonPropValue === undefined || jsonPropValue === null || jsonPropValue === "") {
      const elementName = applyInstanceNs({ ns, instanceNs, propName: jsonPropName });
      xml += `${indent}<${elementName} />\n`;
    }
    // primitive element
    else if (typeof jsonPropValue !== "object") {
      const elementName = applyInstanceNs({ ns, instanceNs, propName: jsonPropName });
      xml += `${indent}<${elementName}>${applyEntities(jsonPropValue)}</${elementName}>\n`;
    }
    // array
    else if (Array.isArray(jsonPropValue)) {
      // In order to keep the order of elements of xsd:sequences in a `substituionGroup`
      // we need to sort elements by their declaration order.
      const elemOrder = args.declaredElementsOrder;
      const arr =
        jsonPropValue?.[0]?.__$$element === undefined
          ? jsonPropValue
          : jsonPropValue.toSorted((a, b) => elemOrder.indexOf(a.__$$element) - elemOrder.indexOf(b.__$$element));

      for (const item of arr) {
        const elementName = applyInstanceNs({ ns, instanceNs, propName: item?.["__$$element"] ?? jsonPropName });
        const { attrs, isEmpty, hasText } = buildAttrs(item, args);
        xml += `${indent}<${elementName}${attrs}`;
        if (isEmpty) {
          xml += " />\n";
        } else if (typeof item === "object") {
          if (hasText) {
            xml += `>${applyEntities(item["__$$text"])}</${elementName}>\n`;
          } else {
            xml += `>\n${build({
              ...args,
              json: item,
              metaType: getPropMetaTypeForJsonObj({ args, jsonObj: item, jsonPropName, metaType }),
              indent: `${indent}  `,
            })}`;
            xml += `${indent}</${elementName}>\n`;
          }
        }
      }
    }
    // nested element
    else {
      const item = jsonPropValue;
      const elementName = applyInstanceNs({ ns, instanceNs, propName: item["__$$element"] ?? jsonPropName });
      const { attrs, isEmpty, hasText } = buildAttrs(item, args);
      xml += `${indent}<${elementName}${attrs}`;
      if (isEmpty) {
        xml += " />\n";
      } else if (typeof item === "object") {
        if (hasText) {
          xml += `>${applyEntities(item["__$$text"])}</${elementName}>\n`;
        } else {
          xml += `>\n${build({
            ...args,
            json: item,
            metaType: getPropMetaTypeForJsonObj({ args, jsonObj: item, jsonPropName, metaType }),
            indent: `${indent}  `,
          })}`;
          xml += `${indent}</${elementName}>\n`;
        }
      }
    }
  }

  return xml;
}

// To know what the metaType of `jsonObj` is, we first need to check if it is mapped as an element.
// If it is, we use the type mapped to elements with its `__$$element` attribute or `jsonPropName`
// If it's not, we proceed normally with traversing the metaType tree.
function getPropMetaTypeForJsonObj({
  args: { elements, meta },
  jsonObj,
  jsonPropName,
  metaType,
}: {
  args: {
    elements: Elements;
    meta: Meta;
  };
  jsonObj: any;
  jsonPropName: string;
  metaType: MetaType | undefined;
}): MetaType | undefined {
  return meta[elements[jsonObj?.["__$$element"] ?? jsonPropName] ?? metaType?.[jsonPropName]?.type ?? ""];
}

function applyInstanceNs({
  propName,
  ns,
  instanceNs,
}: {
  ns: Map<string, string>;
  instanceNs: Map<string, string>;
  propName: string;
}) {
  const s = propName.split(":");

  if (s.length === 1) {
    const newPropertyNs = instanceNs.get(ns.get("")!) ?? "";
    return `${newPropertyNs}${propName}`;
  } else if (s.length === 2) {
    const propertyNs = `${s[0]}:`;
    const newPropertyNs = instanceNs.get(ns.get(propertyNs) ?? "obviously non-existent key") ?? propertyNs;
    return `${newPropertyNs}${s[1]}`;
  } else {
    throw new Error(`Invalid tag name '${propName}'.`);
  }
}

//////////////////
/// EXTENSIONS ///
//////////////////

export type NamespacedProperty<P extends string, K> = K extends string
  ? K extends `@_${string}` | `${string}:${string}` | "__$$text" | "__$$element" // @_xxx are attributes, xxx:xxx are elements referencing other namespaces; __$$element and __$$text are special properties with no domain-related characteristcs. Therefore, not namespace-able.
    ? K
    : `${P}:${K}`
  : never;

export type Namespaced<P extends string, T> = {
  [K in keyof T as NamespacedProperty<P, K>]: NonNullable<T[K]> extends Array<infer R> ? Array<Namespaced<P, R>> : T[K];
};

/**
 * Receives a base meta and an array of prefix-meta pair. Types described by the extension metas have their properties prefixed by their corresponding prefix.
 *
 * Modifies `base` to include properties on `extensionMetasByPrefix`.
 */
export function mergeMetas(base: Meta, extensionMetasByPrefix: [string, Meta][]): void {
  const prefixedMetas = extensionMetasByPrefix.reduce((acc, [k, m]) => {
    return {
      ...acc,
      ...Object.keys(m).reduce((macc, t) => {
        macc[t] = Object.keys(m[t]).reduce((tacc, p) => {
          if (p.includes(":") || p.startsWith("@_")) {
            tacc[p] = m[t][p];
          } else {
            tacc[`${k}${p}`] = m[t][p];
          }
          return tacc;
        }, {} as any);
        return macc;
      }, {} as any),
    };
  }, {});

  [...Object.entries(prefixedMetas)].forEach(([k, v]) => {
    base[k] = v as any;
  });
}
