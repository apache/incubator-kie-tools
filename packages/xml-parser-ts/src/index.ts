import * as fxp from "fast-xml-parser";
import { X2jOptionsOptional } from "fast-xml-parser";

export type Parser<T extends object> = {
  parse: (args: { xml: string | Buffer; instanceNs?: Map<string, string> }) => {
    json: T;
    instanceNs: Map<string, string>;
  };
  build: (args: { json: T; instanceNs: Map<string, string> }) => string;
};

export const TERMINAL_TYPES = ["boolean", "integer", "float", "string"];

export type TypeDef = { type: string; isArray: boolean; isOptional: boolean };
export type Meta = Record<string, Record<string, TypeDef>>;

function parseInt(attrValue: string) {
  try {
    return Number.parseInt(attrValue);
  } catch (e) {
    throw new Error(`Cannot parse integer value '${attrValue}'`);
  }
}

function parseFloat(attrValue: string) {
  try {
    return Number.parseFloat(attrValue);
  } catch (e) {
    throw new Error(`Cannot parse float value '${attrValue}'`);
  }
}

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

export function traverse(meta: Meta, jsonPath: string, root: { element: string; type: string }): TypeDef | undefined {
  const path = jsonPath.split(".");
  path.shift(); // Discard the first, as it's always empty.

  let ret: TypeDef | undefined = undefined;
  let currentType: Record<string, TypeDef | undefined> = meta[root.type];

  for (const prop of path) {
    if (!currentType) {
      return undefined; //Unmapped property inside an `any`-typed element.
    }
    const propType = currentType[prop];
    if (!propType) {
      return undefined; // Unmapped property, let the default act.
    }
    ret = propType;
    currentType = meta[propType.type];
  }

  return ret;
}

const __FXP_OPTS: X2jOptionsOptional = {
  ignoreAttributes: false, // Obviously, we want the attributes to be part of the JSON too.
  alwaysCreateTextNode: false,
  textNodeName: "#text",
  trimValues: true, //
  numberParseOptions: {
    leadingZeros: true,
    hex: true,
    skipLike: /.*/, // We don't want FXP messing with values. We treat them with our meta map.
    // eNotation: false
  },
  parseAttributeValue: false, // We don't want FXP messing with values. We treat them with our meta map.
  parseTagValue: false, // We don't want FXP messing with values. We treat them with our meta map.
  processEntities: true, // Escaping characters.
  attributeNamePrefix: "@_", // Our standard. XML attributes always begin with @_ on JSON.
};

// The depth option is not documented. It works because of the local patch we have.
const __FXP_SHALLOW_PARSER = new fxp.XMLParser({ ...__FXP_OPTS, depth: 0 } as any);

const __FXP_BUILDER = new fxp.XMLBuilder({
  attributeNamePrefix: "@_",
  textNodeName: "#text",
  ignoreAttributes: false,
  processEntities: true,
  format: true,
  suppressBooleanAttributes: false,
});

/**
 * Returns a bi-directional map with the namespace aliases declared at the root element of a XML document pointing to their URIs and vice-versa. In this map, namespace aliases are suffixed with `:`.
 * E.g. "dmn:" => "https://www.omg.org/spec/DMN/20211108/MODEL/"
 *      "https://www.omg.org/spec/DMN/20211108/MODEL/" => "dmn:"
 */
export function getInstanceNs(xml: string | Buffer): Map<string, string> {
  console.time("instanceNs took");

  // We don't need to parse the entire document, just the first level, as namespaces are always declared at the root element.
  const shallowXml = __FXP_SHALLOW_PARSER.parse(xml);

  // Find the root element. As there can be only one root element, we're safe looking for the first element.
  const rootElementKey: string = Object.keys(shallowXml).find((attribute) => {
    return !attribute.startsWith("?") && !attribute.startsWith("!");
  })!;

  const nsMap = new Map<string, string>(
    Object.keys(shallowXml[rootElementKey] ?? {})
      .filter((p) => p.startsWith("@_xmlns"))
      .flatMap((p) => {
        const s = p.split(":");

        const nsUri = shallowXml[rootElementKey][p];

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
          throw new Error(`Invalid xmlns mapping attribute '${p}'`);
        }
      })
  );

  console.timeEnd("instanceNs took");
  return nsMap;
}

export function getParser<T extends object>(args: {
  /** Meta information about the structure of the XML. Used for deciding whether a property is array, boolean, float or integer. */
  meta: Meta;
  /** Bi-directional namespace --> URI mapping. This is the one used to normalize the resulting JSON, independent of the namespaces declared on the XML instance. */
  ns: Map<string, string>;
  /** Information about the root element used on the XML documents */
  root: { element: string; type: string };
}): Parser<T> {
  const actualParser = (instanceNs: Map<string, string>) => {
    // console.info(instanceNs);
    return new fxp.XMLParser({
      ...__FXP_OPTS,
      isArray: (tagName, jsonPath, isLeafNode, isAttribute) => {
        if (isAttribute) {
          return false; // Attributes are unique per element. Thus, will never be an array.
        }
        const t = traverse(args.meta, jsonPath, args.root);
        return t?.isArray ?? false;
      },
      transformTagName: (tagName) => {
        const s = tagName.split(":");
        if (s.length === 1) {
          const ns = args.ns.get(instanceNs.get("")!) ?? "";
          return `${ns}${s}`;
        } else if (s.length === 2) {
          const ns = args.ns.get(instanceNs.get(`${s[0]}:`)!) ?? `${s[0]}:`;
          return `${ns}${s[1]}`;
        } else {
          throw new Error(`Invalid tag name '${tagName}'.`);
        }
      },
      tagValueProcessor: (tagName, tagValue, jsonPath, hasAttributes, isLeaftNode) => {
        const t = traverse(args.meta, jsonPath, args.root);
        if (!t) {
          return tagValue;
        } else if (t.type === "string") {
          return tagValue || "";
        } else if (t.type === "integer" || t.type === "float") {
          throw new Error("Can't have an empty number tag");
        } else if (t.type === "boolean") {
          throw new Error("Can't have an empty boolean tag");
        } else if (t.type === "any") {
          return tagValue || {}; // That's the empty object. The tag is there, but it doesn't have any information.
        } else {
          return tagValue || {}; // That's the empty object. The tag is there, but it doesn't have any information.
        }
      },
      attributeValueProcessor: (attrName, attrValue, jsonPath) => {
        const attrJsonPath = `${jsonPath}.@_${attrName}`;
        const t = traverse(args.meta, attrJsonPath, args.root);
        if (t?.type === "boolean") {
          return parseBoolean(attrValue);
        } else if (t?.type === "integer") {
          return parseInt(attrValue);
        } else if (t?.type === "float") {
          return parseFloat(attrValue);
        } else if (t?.type === "allNNI") {
          return parseAllNNI(attrValue);
        } else {
          return attrValue;
        }
      },
    });
  };

  return {
    parse: ({ xml, instanceNs }) => {
      instanceNs = instanceNs ?? getInstanceNs(xml);

      console.time("parsing took");
      const json = actualParser(instanceNs).parse(xml);
      console.timeEnd("parsing took");
      return { json, instanceNs };
    },
    build: ({ json, instanceNs }) => {
      console.time("building took");
      const xml = __FXP_BUILDER.build(applyInstanceNsMap(json, args.ns, instanceNs));
      console.timeEnd("building took");
      return xml;
    },
  };
}

/**
 * Converts the JSON to a XML, applying the original instanceNs map on top of the generated ns map.
 *
 * @param json The data to be transformed to XML.
 * @param ns The ns map representing the data on the JSON.
 * @param instanceNs The ns map representing the data on the final XML.
 * @returns An XML string representation of `json`.
 */
function applyInstanceNsMap<T extends object>(json: T, ns: Map<string, string>, instanceNs: Map<string, string>): any {
  if (typeof json !== "object") {
    return json;
  }

  if (Array.isArray(json)) {
    return json.map((element) => {
      return applyInstanceNsMap(element, ns, instanceNs);
    });
  }

  const res: any = {};

  for (const propertyName in json) {
    if (propertyName.startsWith("@_") || propertyName.startsWith("?") || propertyName === "#text") {
      res[propertyName] = json[propertyName];
      continue;
    }

    const s = propertyName.split(":");

    let newPropertyName: string = propertyName;
    if (s.length === 1) {
      const newPropertyNs = instanceNs.get(ns.get("")!) ?? "";
      newPropertyName = `${newPropertyNs}${propertyName}`;
    } else if (s.length === 2) {
      const propertyNs = `${s[0]}:`;
      const newPropertyNs = instanceNs.get(ns.get(propertyNs) ?? "obviously non-existent key") ?? propertyNs;
      newPropertyName = `${newPropertyNs}${s[1]}`;
    } else {
      throw new Error(`Invalid tag name '${propertyName}'.`);
    }

    res[newPropertyName] = applyInstanceNsMap(json[propertyName] as any, ns, instanceNs);
  }

  return res;
}
