// manually written

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

export const ns = new Map([
  ["http://www.w3.org/2001/XMLSchema", "xsd:"],
  ["http://www.w3.org/2001/XMLSchema-hasFacetAndProperty", "hfp:"],
  ["xsd:", "http://www.w3.org/2001/XMLSchema"],
  ["hfp:", "http://www.w3.org/2001/XMLSchema-hasFacetAndProperty"],
]);

export const meta = {
  schema: {
    "xsd:import": { type: "import", isArray: true, fromType: "", xsdType: "" },
    "xsd:include": { type: "include", isArray: true, fromType: "", xsdType: "" },
    "xsd:element": { type: "element", isArray: true, fromType: "", xsdType: "" },
    "xsd:annotation": { type: "annotation", isArray: true, fromType: "", xsdType: "" },
    "xsd:simpleType": { type: "simpleType", isArray: true, fromType: "", xsdType: "" },
    "xsd:complexType": { type: "complexType", isArray: true, fromType: "", xsdType: "" },
    "xsd:attributeGroup": { type: "topLevelAttributeGroup", isArray: true, fromType: "", xsdType: "" },
    "xsd:attribute": { type: "attribute", isArray: true, fromType: "", xsdType: "" },
  },
  all: {
    "@_minOccurs": { type: "integer", isArray: false, fromType: "", xsdType: "" },
    "@_maxOccurs": { type: "allNNI", isArray: false, fromType: "", xsdType: "" },
    "xsd:element": { type: "element", isArray: true, fromType: "", xsdType: "" },
  },
  import: {},
  include: {},
  annotation: {},
  simpleType: {
    "xsd:union": { type: "union", isArray: false, fromType: "", xsdType: "" },
  },
  union: {
    "xsd:simpleType": { type: "simpleType", isArray: true, fromType: "", xsdType: "" },
  },
  attribute: {
    "@_name": { type: "string", isArray: false, fromType: "", xsdType: "" },
    "xsd:simpleType": { type: "simpleType", isArray: false, fromType: "", xsdType: "" },
  },
  topLevelAttributeGroup: {
    "@_name": { type: "string", isArray: false, fromType: "", xsdType: "" },
    "xsd:attribute": { type: "attribute", isArray: true, fromType: "", xsdType: "" },
  },
  attributeGroup: {
    "@_ref": { type: "string", isArray: false, fromType: "", xsdType: "" },
  },
  element: {
    "@_abstract": { type: "boolean", isArray: false, fromType: "", xsdType: "" },
    "@_minOccurs": { type: "integer", isArray: false, fromType: "", xsdType: "" },
    "@_maxOccurs": { type: "allNNI", isArray: false, fromType: "", xsdType: "" },
    "xsd:complexType": { type: "complexType", isArray: false, fromType: "", xsdType: "" },
  },
  complexType: {
    "@_abstract": { type: "boolean", isArray: false, fromType: "", xsdType: "" },
    "@_mixed": { type: "boolean", isArray: false, fromType: "", xsdType: "" },
    "xsd:complexContent": { type: "complexContent", isArray: false, fromType: "", xsdType: "" },
    "xsd:simpleContent": { type: "simpleContent", isArray: false, fromType: "", xsdType: "" },
    "xsd:sequence": { type: "sequence", isArray: false, fromType: "", xsdType: "" },
    "xsd:all": { type: "all", isArray: false, fromType: "", xsdType: "" },
    "xsd:attribute": { type: "attribute", isArray: true, fromType: "", xsdType: "" },
    "xsd:attributeGroup": { type: "attributeGroup", isArray: true, fromType: "", xsdType: "" },
  },
  complexContent: {
    "xsd:extension": { type: "extension", isArray: false, fromType: "", xsdType: "" },
  },
  simpleContent: {
    "xsd:extension": { type: "extension", isArray: false, fromType: "", xsdType: "" },
  },
  extension: {
    "xsd:sequence": { type: "sequence", isArray: false, fromType: "", xsdType: "" },
    "xsd:choice": { type: "choice", isArray: false, fromType: "", xsdType: "" },
    "xsd:attribute": { type: "attribute", isArray: true, fromType: "", xsdType: "" },
  },
  sequence: {
    "xsd:element": { type: "element", isArray: true, fromType: "", xsdType: "" },
    "xsd:choice": { type: "choice", isArray: false, fromType: "", xsdType: "" },
    "xsd:any": { type: "any", isArray: false, fromType: "", xsdType: "" },
  },
  any: {
    "@_minOccurs": { type: "integer", isArray: false, fromType: "", xsdType: "" },
    "@_maxOccurs": { type: "allNNI", isArray: false, fromType: "", xsdType: "" },
  },
  choice: {
    "@_minOccurs": { type: "integer", isArray: false, fromType: "", xsdType: "" },
    "@_maxOccurs": { type: "allNNI", isArray: false, fromType: "", xsdType: "" },
    "xsd:element": { type: "element", isArray: true, fromType: "", xsdType: "" },
    "xsd:sequence": { type: "sequence", isArray: false, fromType: "", xsdType: "" },
  },
};
