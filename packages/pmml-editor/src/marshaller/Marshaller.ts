/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import * as JSONata from "jsonata";
import { Expression } from "jsonata";
import * as XMLJS from "xml-js";
import {
  Characteristics,
  CompoundPredicate,
  False,
  FieldName,
  MiningSchema,
  PMML,
  Scorecard,
  SimplePredicate,
  True
} from "@kogito-tooling/pmml-editor-codegen";
import { JSON2UI_TRANSFORMATION as json2ui } from "./jsonata/JSON2UI";
import { UI2JSON_TRANSFORMATION as ui2json } from "./jsonata/UI2JSON";

export function XML2PMML(xml: string): PMML {
  const doc: XMLJS.Element = XMLJS.xml2js(xml) as XMLJS.Element;
  const expression: Expression = JSONata(json2ui);
  expression.registerFunction("merge", merge);
  expression.registerFunction("singletonArray", singletonArray);
  expression.registerFunction("scorecardFactory", scorecardFactory);
  expression.registerFunction("json2uiSimplePredicateFactory", json2uiSimplePredicateFactory);
  expression.registerFunction("json2uiCompoundPredicateFactory", json2uiCompoundPredicateFactory);
  expression.registerFunction("json2uiTruePredicateFactory", json2uiTruePredicateFactory);
  expression.registerFunction("json2uiFalsePredicateFactory", json2uiFalsePredicateFactory);

  const pmml: PMML = expression.evaluate(doc);

  return pmml;
}

//See https://github.com/jsonata-js/jsonata/issues/457
//JSONata's $merge([...]) function merges data but not prototype definitions. This specialised version
//merges data into a clone of the first object passed in the argument array.
function merge(arg: any[]): any {
  if (typeof arg === undefined) {
    return undefined;
  }

  const result = Object.create(arg[0]);

  arg.forEach((obj: any) => {
    /* tslint:disable:forin */
    for (const prop in obj) {
      result[prop] = obj[prop];
    }
    /* tslint:enable:forin */
  });
  return result;
}

//See https://github.com/jsonata-js/jsonata/issues/218
//JSONata returns a single object instead of a single item array by design.
//This function fixes that by ensuring an array of values is always returned.
function singletonArray(value: any): any[] {
  if (value === undefined || value === null) {
    return [];
  }
  if (Array.isArray(value)) {
    return value;
  }
  return [value];
}

//Construction of a Scorecard data-structure can be peformed in the JSONata mapping however
//TypeScript's instanceof operator relies on the applicable constructor function having been
//called and therefore we must instantiate the object itself. Furthermore the recurrsive hieracical
//nature of CompoundPredicates cannot be handled by a JSONata mapping.
function scorecardFactory(): Scorecard {
  return new Scorecard({
    MiningSchema: new MiningSchema({ MiningField: [] }),
    Characteristics: new Characteristics({ Characteristic: [] }),
    functionName: "regression"
  });
}

function json2uiSimplePredicateFactory(): SimplePredicate {
  return new SimplePredicate({ field: "" as FieldName, operator: "equal" });
}

function json2uiCompoundPredicateFactory(): CompoundPredicate {
  return new CompoundPredicate({ booleanOperator: "and" });
}

function json2uiTruePredicateFactory(): True {
  return new True({});
}

function json2uiFalsePredicateFactory(): False {
  return new False({});
}

export function PMML2XML(pmml: PMML): string {
  const expression: Expression = JSONata(ui2json);
  const json: any = expression.evaluate(pmml);
  const xml: string = XMLJS.js2xml(json, { spaces: 2 });

  return xml;
}
