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

import { DmnDocumentData, DmnLanguageService } from "../src";
import { DmnDecision } from "../src/DmnDecision";
import { decisions } from "./fs/fixtures";
import { asyncGetModelXmlForTestFixtures } from "./fs/getModelXml";

it("get decisions", () => {
  const dmnLs = new DmnLanguageService({ getModelXml: asyncGetModelXmlForTestFixtures });

  expect(dmnLs.getDmnDocumentData(decisions())).toEqual(
    new DmnDocumentData("https://kie.apache.org/dmn/_57B8BED3-0077-4154-8435-30E57EA6F02E", "My Model Name", [
      new DmnDecision("Decision-1"),
      new DmnDecision("Decision-2"),
      new DmnDecision("Decision-3"),
    ])
  );
});
