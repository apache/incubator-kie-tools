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

////////////////////////////////////////
// XPTC stands for XmlParserTsCodegen //
////////////////////////////////////////

export type XptcComplexTypeBase = {
  childOf?: string;
  comment: string;
  type: "complex";
  isSimpleContent: boolean;
  needsExtensionType: boolean; // That's for sequences decalring <xsd:anyAttribute> or <xsd:any>
  declaredAtRelativeLocation: string;
  attributes: XptcAttribute[];
  elements: Array<
    {
      isArray: boolean;
      isOptional: boolean;
    } & (
      | {
          name: string;
          kind: "ofNamedType"; // Type declared somewhere else.
          typeName: string;
        }
      | {
          name: string;
          kind: "ofAnonymousType"; // Types declared directly inside the element.
          anonymousType: XptcComplexTypeAnonymous;
        }
      | {
          kind: "ofRef"; // References another element.
          ref: string;
        }
    )
  >;
};

export type XptcComplexTypeAnonymous = XptcComplexTypeBase & {
  isAnonymous: true; // Declared directly inside elements.
  isMixed: boolean;
  forElementWithName: string;
  parentIdentifierForExtensionType: string;
};

export type XptcComplexTypeNamed = XptcComplexTypeBase & {
  name: string;
  isAbstract: boolean;
  isMixed: boolean;
  isAnonymous: false;
};

export type XptcComplexType = XptcComplexTypeNamed | XptcComplexTypeAnonymous;

export type XptcAttribute = {
  name: string;
  localTypeRef: string;
  isOptional: boolean;
  simpleType?: XptcSimpleType;
};

export type XptcTopLevelAttributeGroup = {
  name: string;
  attributes: XptcAttribute[];
};

export type XptcElement = {
  name: string;
  type?: string;
  isAbstract: boolean;
  substitutionGroup?: string;
  declaredAtRelativeLocation: string;
  anonymousType?: XptcComplexTypeAnonymous;
};

export type XptcSimpleType = {
  type: "simple";
  comment: string;
  name: string;
  restrictionBase?: string;
  declaredAtRelativeLocation: string;
} & (
  | {
      kind: "string";
    }
  | {
      kind: "enum";
      values: string[];
    }
  | {
      kind: "int";
      minInclusive?: number;
      maxInclusive?: number;
    }
  | {
      kind: "integer";
      minInclusive?: number;
      maxInclusive?: number;
    }
);

export type XptcTsPrimitiveType = {
  annotation: string;
  type: "primitive";
  tsEquivalent: string;
};

export type XptcTsImports = {
  save: (name: string, location: string) => void;
};

export type XptcMetaType = {
  name: string;
  properties: XptcMetaTypeProperty[];
};

export type XptcMetaTypeProperty = {
  name: string;
  elem: XptcElement | undefined;
  metaType: {
    name: string;
    xsdType: string;
  };
  typeBody?: (type: string) => string | undefined;
  isArray: boolean;
  isOptional: boolean;
  fromType: string;
  declaredAt: string;
};
