/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { TextEncoder, TextDecoder } from "util";
(global as any).TextEncoder = TextEncoder;
(global as any).TextDecoder = TextDecoder;

import * as jsdom from "jsdom";
import * as index from "..";

index.domParser.getDomDocument = (xml: string | Buffer) => {
  // console.time("parsing dom took (jsdom)");
  const domdoc = new jsdom.JSDOM(xml, { contentType: "application/xml" }).window.document;
  // console.timeEnd("parsing dom took (jsdom)");
  return domdoc;
};

export * from "..";
