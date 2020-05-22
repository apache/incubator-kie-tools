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
import { PMML } from "../generated/pmml";
import { TRANSFORMATION as json2ui } from "./Template_JSON2UI";
import { TRANSFORMATION as ui2json } from "./Template_UI2JSON";

export function XML2PMML(xml: string): PMML {
  const doc: XMLJS.Element = XMLJS.xml2js(xml) as XMLJS.Element;
  const expression: Expression = JSONata(json2ui);
  expression.registerFunction("singletonArray", singletonArray);

  const pmml: PMML = expression.evaluate(doc);

  return pmml;
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

export function PMML2XML(pmml: PMML): string {
  const expression: Expression = JSONata(ui2json);
  const json: any = expression.evaluate(pmml);
  const xml: string = XMLJS.js2xml(json, { spaces: 2 });

  return xml;
}
