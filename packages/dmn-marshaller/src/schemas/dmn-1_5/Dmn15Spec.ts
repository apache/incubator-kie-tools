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

import { ns as dmn10ns } from "../dmn-1_0/ts-gen/meta";
import { ns as dmn11ns } from "../dmn-1_1/ts-gen/meta";
import { ns as dmn12ns } from "../dmn-1_2/ts-gen/meta";
import { ns as dmn13ns } from "../dmn-1_3/ts-gen/meta";
import { ns as dmn14ns } from "../dmn-1_4/ts-gen/meta";
import { ns as dmn15ns } from "./ts-gen/meta";

// Those two below are defined by the spec. See S-FEEL grammar. Rule 22: "name".
const feelNameStart =
  /^[?A-Z_a-z\uC0-\uD6\uD8-\uF6\uF8-\u2FF\u370-\u37D\u37F-\u1FFF\u200C-\u200D\u2070-\u218F\u2C00-\u2FEF\u3001-\uD7FF\uF900-\uFDCF\uFDF0-\uFFFD\u10000-\uEFFFF].*$/;
const feelNamePart =
  // ------------------------------------------------------------------------------------------------------ same as nameStart ------------------------------- ----------name part------------ --extra--
  /^.[?A-Z_a-z\uC0-\uD6\uD8-\uF6\uF8-\u2FF\u370-\u37D\u37F-\u1FFF\u200C-\u200D\u2070-\u218F\u2C00-\u2FEF\u3001-\uD7FF\uF900-\uFDCF\uFDF0-\uFFFD\u10000-\uEFFFF\uB7\d\u0300-\u036F\u203F-\u2040.+-/*’\s^]*$/;

// This is not part of the spec.
const forbiddenEndingChars = /^.*[.:+-/*\s^]$/; // Although they're fine by the Spec, they can seriously complicate FEEL expressions readability.

export type UniqueNameIndex = Map<string, string>;

export const DMN15_SPEC = {
  namedElement: {
    isValidName: (id: string, name: string | undefined, allUniqueNames: UniqueNameIndex): boolean => {
      return (
        !!name?.trim() && // Names need to be non-empty.
        !!name.match(feelNameStart) &&
        !!name.match(feelNamePart) &&
        !name?.trim().match(forbiddenEndingChars) &&
        (!allUniqueNames.get(name?.trim()) || allUniqueNames.get(name?.trim()) === id) // All names need to be unique.
      );
    },
  },
  expressionLanguage: { default: `https://www.omg.org/spec/DMN/20230324/FEEL/` },
  typeLanguage: { default: `https://www.omg.org/spec/DMN/20230324/FEEL/` },
  IMPORT: {
    name: {
      isValid: (id: string, name: string, allUniqueNames: UniqueNameIndex) => {
        // Empty strings are allowed for imports, so that imported elements can be used without a prefix.
        // Source: https://www.omg.org/spec/DMN/1.5/Beta1/PDF. PDF page 40, document page 32. Section "6.3.3 Import Metamodel".
        return name === "" || DMN15_SPEC.namedElement.isValidName(id, name, allUniqueNames);
      },
    },
  },
  BOXED: {
    DECISION_TABLE: {
      PreferredOrientation: { default: "Rule-as-Row" },
      HitPolicy: { default: "UNIQUE" },
    },
    FUNCTION: {
      kind: { default: "FEEL" },
      JAVA: {
        classFieldName: "class",
        methodSignatureFieldName: "method signature",
      },
      PMML: {
        documentFieldName: "document",
        modelFieldName: "model",
      },
    },
  },
  ITEM_DEFINITIONS: {
    isCollection: { default: "false" },
  },
  ANNOTATIONS: {
    format: { default: "text/plain" },
  },
  ASSOCIATIONS: {
    direction: { default: "None" },
  },
  SHAPE: {
    isCollapsed: {
      default: "false",
    },
  },
};

export const allDmnImportNamespaces = new Set([
  dmn10ns.get("")!,
  dmn11ns.get("")!,
  dmn12ns.get("")!,
  dmn13ns.get("")!,
  dmn14ns.get("")!,
  dmn15ns.get("")!,
]);

export const KIE_DMN_UNKNOWN_NAMESPACE = "https://kie.apache.org/dmn/unknown";
