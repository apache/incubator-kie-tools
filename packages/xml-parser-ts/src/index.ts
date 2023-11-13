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
  parse: (args: { xml: string | Buffer; domdoc?: Document; instanceNs?: Map<string, string> }) => {
    json: T;
    instanceNs: Map<string, string>;
  };
  build: (args: { json: T; instanceNs: Map<string, string> }) => string;
};

export type XmlParserTsRootElementBaseType = Partial<{ [k: `@_xmlns:${string}`]: string }> & { "@_xmlns"?: string };

export type MetaTypeDef = { type: string; isArray: boolean };

export type Meta = Record<string, Record<string, MetaTypeDef>>;

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
    parse: ({ xml, domdoc, instanceNs }) => {
      domdoc = domdoc ?? domParser.getDomDocument(xml);
      instanceNs = instanceNs ?? getInstanceNs(domdoc);

      // console.time("parsing overhead took");
      const rootType = { [args.root.element]: { type: args.root.type, isArray: false } };
      const json = parse({ ...args, instanceNs, node: domdoc, nodeType: rootType });
      // console.timeEnd("parsing overhead took");

      return { json, instanceNs };
    },
    build: ({ json, instanceNs }) => {
      // console.time("building took");
      const xml = build({ json, ns: args.ns, instanceNs, indent: "" });
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
  nodeType: Record<string, MetaTypeDef | undefined> | undefined;
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
      const { nsedName, subsedName } = resolveElement(elemNode.nodeName, args.nodeType, args);

      const elemPropType = args.nodeType?.[subsedName ?? nsedName];

      const elemType =
        args.meta[args.elements[nsedName]] ?? // Regardless of subsitutions that might have occured, we need the type information for the concrete implementation of the element. (E.g., On DMN, tDecision is substituted by tDrgElement)
        (elemPropType
          ? args.meta[elemPropType.type] // If we can't find this type with the `elements` mapping, we try directly from `meta`.
          : undefined); // If the current element is not known, we simply ignore its type and go with the defaults.

      // If the elemNode's meta type has a __$$text property, this is the one we use to parse its value.
      // All other properties on `elemType` are certainly attributes, which are handlded below.
      const t = elemType?.["__$$text"]?.type ?? elemPropType?.type;

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
        elemValue = parse({ ...args, node: elemNode, nodeType: elemType });
        if (subsedName !== nsedName) {
          // substitution occurred, need to save the original, normalized element name
          elemValue["__$$element"] = nsedName;
        }
      }

      const attrs = (elemNode as Element).attributes;
      for (let i = 0; i < attrs.length; i++) {
        const attr = attrs[i];
        const attrPropType = elemType?.[`@_${attr.name}`];

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

        elemValue[`@_${attr.name}`] = attrValue;
      }

      const currentValue = json[subsedName ?? nsedName];

      if (elemPropType?.isArray) {
        json[subsedName ?? nsedName] ??= [];
        json[subsedName ?? nsedName].push(elemValue);
      } else if (currentValue) {
        if (elemPropType && !elemPropType.isArray) {
          console.warn(
            `[xml-parser-ts] Accumulating values on known non-array property '${subsedName}' (${nsedName}) of type '${elemPropType.type}'.`
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

function resolveElement(
  name: string,
  parentType: Record<string, MetaTypeDef | undefined> | undefined,
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

  const s = name.split(":");
  if (s.length === 1) {
    nameNs = ns.get(instanceNs.get("")!) ?? "";
    nameName = s[0];
  } else if (s.length === 2) {
    nameNs = ns.get(instanceNs.get(`${s[0]}:`)!) ?? `${s[0]}:`;
    nameName = s[1];
  } else {
    throw new Error(name);
  }

  // nsedName stands for "Namespaced name".
  const nsedName = `${nameNs}${nameName}`;

  const nsedSubs = subs[nameNs];

  // subsedName stands for "Substituted name";
  let subsedName: string | undefined = nsedName;

  // Resolve substituionGroups
  while (nsedSubs && !parentType?.[subsedName]) {
    if (subsedName === undefined) {
      break; // Not mapped, ignore unknown element...
    }
    subsedName = nsedSubs[subsedName];
  }

  return { nsedName, subsedName };
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

function buildAttrs(json: any) {
  let isEmpty = true;
  let hasText = false;
  let attrs = " ";

  for (const propName in json) {
    if (propName[0] === "@") {
      attrs += `${propName.substring(2)}="${applyEntities(json[propName])}" `;
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
  indent: string;
}): string {
  const { json, ns, instanceNs, indent } = args;

  if (typeof json !== "object" || json === null) {
    throw new Error(`Can't build XML from a non-object value. '${json}'.`);
  }

  let xml = "";

  for (const _propName in json) {
    const propName = applyEntities(_propName);
    const propValue = json[propName];

    // attributes are processed individually.
    if (propName[0] === "@") {
      continue;
    }
    // ignore this. this is supposed to be on array elements only.
    else if (propName === "__$$element") {
      continue;
    }
    // ignore this. text content is treated inside the "array" and "nested element" sections.
    else if (propName === "__$$text") {
      continue;
    }
    // pi tag
    else if (propName[0] === "?") {
      xml += `${indent}<${propName}${buildAttrs(propValue).attrs} ?>\n`;
    }
    // empty tag
    else if (propValue === undefined || propValue === null || propValue === "") {
      const elementName = applyInstanceNs({ ns, instanceNs, propName });
      xml += `${indent}<${elementName} />\n`;
    }
    // primitive element
    else if (typeof propValue !== "object") {
      const elementName = applyInstanceNs({ ns, instanceNs, propName });
      xml += `${indent}<${elementName}>${applyEntities(propValue)}</${elementName}>\n`;
    }
    // array
    else if (Array.isArray(propValue)) {
      for (const item of propValue) {
        const elementName = applyInstanceNs({ ns, instanceNs, propName: item["__$$element"] ?? propName });
        const { attrs, isEmpty, hasText } = buildAttrs(item);
        xml += `${indent}<${elementName}${attrs}`;
        if (isEmpty) {
          xml += " />\n";
        } else if (typeof item === "object") {
          if (hasText) {
            xml += `>${applyEntities(item["__$$text"])}</${elementName}>\n`;
          } else {
            xml += `>\n${build({ ...args, json: item, indent: `${indent}  ` })}`;
            xml += `${indent}</${elementName}>\n`;
          }
        }
      }
    }
    // nested element
    else {
      const item = propValue;
      const elementName = applyInstanceNs({ ns, instanceNs, propName: item["__$$element"] ?? propName });
      const { attrs, isEmpty, hasText } = buildAttrs(item);
      xml += `${indent}<${elementName}${attrs}`;
      if (isEmpty) {
        xml += " />\n";
      } else if (typeof item === "object") {
        if (hasText) {
          xml += `>${applyEntities(item["__$$text"])}</${elementName}>\n`;
        } else {
          xml += `>\n${build({ ...args, json: item, indent: `${indent}  ` })}`;
          xml += `${indent}</${elementName}>\n`;
        }
      }
    }
  }

  return xml;
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
 * @returns a single meta, containing the information of `base` and all extensions metas with prefixed properties.
 */
export function mergeMetas(base: Meta, extensionMetasByPrefix: [string, Meta][]) {
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

  return { ...base, ...prefixedMetas };
}
