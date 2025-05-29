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

import { XmlParserTsRootElementBaseType } from "@kie-tools/xml-parser-ts";

export type XsdPrimitives = "xsd:int" | "xsd:string" | "xsd:token" | "xsd:integer" | "xsd:anyURI";

export interface XsdImport {
  "@_schemaLocation": string;
}

export interface XsdImport {
  "@_schemaLocation": string;
}

export interface XsdSimpleType {
  "@_name"?: string;
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
  "xsd:simpleType"?: XsdSimpleType;
}

export interface XsdTopLevelAttributeGroup {
  "xsd:attribute": XsdAttribute[];
  "@_name": string;
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

export interface XsdExtension {
  "xsd:attribute"?: XsdAttribute[];
  "xsd:sequence"?: XsdSequence;
  "xsd:all"?: XsdAll;
  "xsd:choice"?: XsdChoice;
  "@_base": string;
}

export interface XsdComplexType {
  "@_name"?: string;
  "@_abstract"?: boolean;
  "@_mixed"?: boolean;
  "xsd:attribute"?: XsdAttribute[];
  "xsd:attributeGroup"?: Array<{ "@_ref": string }>;
  "xsd:sequence"?: XsdSequence;
  "xsd:all"?: XsdAll;
  "xsd:anyAttribute"?: {
    "@_namespace": string;
    "@_processContents": "lax";
  };
  "xsd:complexContent"?: {
    "xsd:extension"?: XsdExtension;
  };
  "xsd:simpleContent"?: {
    "xsd:extension"?: XsdExtension;
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
  "xsd:schema": XmlParserTsRootElementBaseType & {
    "@_targetNamespace": string;
    "@_elementFormDefault": "qualified";
    "@_attributeFormDefault": "unqualified";
    "xsd:import"?: XsdImport[];
    "xsd:include"?: XsdInclude[];
    "xsd:simpleType"?: XsdSimpleType[];
    "xsd:complexType"?: XsdComplexType[];
    "xsd:element"?: XsdTopLevelElement[];
    "xsd:attribute"?: XsdAttribute[];
    "xsd:attributeGroup"?: XsdTopLevelAttributeGroup[];
  };
}
