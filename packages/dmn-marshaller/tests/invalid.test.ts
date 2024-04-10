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

import { getMarshaller } from "../dist";

describe("invalid", () => {
  test("empty content", () => {
    try {
      getMarshaller(``);

      fail(`An exception should've been thrown.`);
    } catch (e) {
      console.error(e);
    }
  });

  test("invalid content", () => {
    try {
      getMarshaller(`invalid content`);

      fail(`An exception should've been thrown.`);
    } catch (e) {
      console.error(e);
    }
  });

  test("invalid closing tag", () => {
    try {
      getMarshaller(`
<dmn:definitions
    xmlns="https://kie.apache.org/dmn/_5BF56984-FDC7-441B-8307-FF06B0E5B17F"
    xmlns:dmn="https://www.omg.org/spec/DMN/20191111/MODEL/"
><invalid`);

      fail(`An exception should've been thrown.`);
    } catch (e) {
      console.error(e);
    }
  });

  // FIXME: Tiago --> How to make sure that the parsed XML is actually a DMN, and not some random XML?
  test.skip("non-dmn, valid xml", () => {
    try {
      const { parser } = getMarshaller(`
<dmn:definitions
    xmlns="https://kie.apache.org/dmn/_5BF56984-FDC7-441B-8307-FF06B0E5B17F"
    xmlns:dmn="https://www.omg.org/spec/DMN/20191111/MODEL/">
    invalid
</dmn:definitions>`);

      const json = parser.parse();

      fail(`An exception should've been thrown. Parsed content is ${JSON.stringify(json, undefined, 2)}`);
    } catch (e) {
      console.error(e);
    }
  });
});
