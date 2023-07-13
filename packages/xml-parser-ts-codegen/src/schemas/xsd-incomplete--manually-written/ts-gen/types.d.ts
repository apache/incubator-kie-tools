export type XsdPrimitives = "xsd:int" | "xsd:string" | "xsd:token" | "xsd:integer" | "xsd:anyURI";

export interface XsdImport {
  "@_schemaLocation": string;
}

export interface XsdImport {
  "@_schemaLocation": string;
}

export interface XsdSimpleType {
  "@_name": string;
  "xsd:union"?: {
    "@_memberTypes": XsdPrimitives;
    "xsd:simpleType"?: XsdSimpleType[];
  };
  "xsd:restriction"?: {
    "@_base": XsdPrimitives;
    "xsd:all"?: XsdAll;
    "xsd:enumeration"?: Array<{ "@_value": string }>;
    "xsd:minInclusive"?: { "@_value": number };
    "xsd:maxInclusive"?: { "@_value": number };
  };
}

export interface XsdAttribute {
  "@_name": string;
  "@_type": string;
  "@_use"?: "required" | "optional";
  "@_default": string;
}

export interface XsdSequence {
  "@_name": string;
  "xsd:element"?: Array<XsdElement>;
  "xsd:choice"?: XsdChoice;
  "xsd:any": {
    "@_namespace": "##other";
    "@_processContents": "lax";
    "@_minOccurs": number; // default is 1
    "@_maxOccurs": number | "unbounded"; // default is 1
  };
}

export interface XsdComplexType {
  "@_name"?: string;
  "@_abstract"?: boolean;
  "xsd:attribute"?: XsdAttribute[];
  "xsd:sequence"?: XsdSequence;
  "xsd:all"?: XsdAll;
  "xsd:anyAttribute"?: {
    "@_namespace": string;
    "@_processContents": "lax";
  };
  "xsd:complexContent"?: {
    "xsd:extension"?: {
      "xsd:attribute"?: XsdAttribute[];
      "xsd:sequence"?: XsdSequence;
      "xsd:all"?: XsdAll;
      "xsd:choice"?: XsdChoice;
      "@_base": string;
    };
  };
}

export interface XsdAll {
  "@_minOccurs": number; // default is 1
  "@_maxOccurs": number | "unbounded"; // default is 1
  "xsd:element"?: Array<XsdElement>;
}

export interface XsdElement {
  "@_ref"?: string; // mutualy exclusive with @_name and @_type and xsd:complexType
  "@_name"?: string; // mutualy exclusive with @_ref

  "@_type"?: string; // mutualy exclusive with xsd:complexType and @_ref
  "xsd:complexType"?: XsdComplexType; // mutualy exclusive with @_type and @_ref and xsd:simpleType
  "xsd:simpleType"?: XsdSimpleType; // mutualy exclusive with xsd:complexType

  "@_substitutionGroup"?: string;
  "@_minOccurs": number; // default is 1
  "@_maxOccurs": number | "unbounded"; // default is 1
}

export interface XsdChoice {
  "xsd:element"?: XsdElement[];
  "xsd:sequence"?: XsdSequence;
  "@_minOccurs": number; // default is 1
  "@_maxOccurs": number | "unbounded"; // default is 1
}

export interface XsdTopLevelElement {
  "@_name": string;
  "@_type": string;
  "@_abstract"?: boolean; // default = false
  "@_substitutionGroup"?: string;
}

export interface XsdSchema {
  "xsd:schema": {
    "@_targetNamespace": string;
    "@_elementFormDefault": "qualified";
    "@_attributeFormDefault": "unqualified";
    "xsd:import"?: XsdImport[];
    "xsd:include"?: XsdInclude[];
    "xsd:simpleType"?: XsdSimpleType[];
    "xsd:complexType"?: XsdComplexType[];
    "xsd:element"?: XsdTopLevelElement[];
  };
}
