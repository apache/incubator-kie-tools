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

import { getUniformsSchema } from "../dist/getUniformsSchema";

describe("getUniformsSchema tests", () => {
  it("get empty uniforms schema", () => {
    expect(getUniformsSchema({})).toEqual({});
  });

  it("get uniforms schema with empty properties", () => {
    expect(getUniformsSchema({ properties: {} })).toEqual({ properties: {} });
  });

  it("get uniforms schema with properties that are empty", () => {
    expect(getUniformsSchema({ properties: { a: {} } })).toEqual({ properties: { a: {} } });
  });

  it("get uniforms schema with input property", () => {
    expect(getUniformsSchema({ properties: { a: { input: true } } })).toEqual({
      properties: { a: { uniforms: { disabled: true } } },
    });
  });

  it("get uniforms schema with output property", () => {
    expect(getUniformsSchema({ properties: { a: { output: true } } })).toEqual({ properties: { a: {} } });
  });

  it("get uniforms schema with input and output property", () => {
    expect(getUniformsSchema({ properties: { a: { input: true, output: true } } })).toEqual({ properties: { a: {} } });
  });
});
