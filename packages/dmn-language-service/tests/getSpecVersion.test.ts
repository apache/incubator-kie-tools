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

import { DmnLanguageService } from "../src";
import { dmn12D, dmn15B } from "./fs/fixtures";
import { asyncGetModelXmlForTestFixtures } from "./fs/getModelXml";

it("empty", () => {
  const dmnLs = new DmnLanguageService({ getModelXml: asyncGetModelXmlForTestFixtures });
  expect(dmnLs.getSpecVersion("")).toEqual(undefined);
});

it("invalid", () => {
  const dmnLs = new DmnLanguageService({ getModelXml: asyncGetModelXmlForTestFixtures });
  expect(dmnLs.getSpecVersion("aa")).toEqual(undefined);
});

it("1.2", () => {
  const dmnLs = new DmnLanguageService({ getModelXml: asyncGetModelXmlForTestFixtures });
  expect(dmnLs.getSpecVersion(dmn12D())).toEqual("1.2");
});

it("1.5", () => {
  const dmnLs = new DmnLanguageService({ getModelXml: asyncGetModelXmlForTestFixtures });
  expect(dmnLs.getSpecVersion(dmn15B())).toEqual("1.5");
});
