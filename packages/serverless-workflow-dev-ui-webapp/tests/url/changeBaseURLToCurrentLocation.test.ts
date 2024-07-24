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

import { changeBaseURLToCurrentLocation } from "../../src/url";

describe("changeBaseURLToCurrentLocation", () => {
  beforeAll(() => {
    window = Object.create(window);
    delete (window as any).location;
    window.location = {
      host: "localhost:8080",
      hostname: "localhost",
      href: "http://localhost:8080/test",
      pathname: "/test",
      port: "8080",
      protocol: "http:",
    } as any;
  });

  it.each([
    ["https://greeting.sonataflow-operator-system/graphql", "http://localhost:8080/graphql"],
    ["https://greeting.sonataflow-operator-system/openapi.json", "http://localhost:8080/openapi.json"],
    ["http://localhost:8080/some/page", "http://localhost:8080/some/page"],
    ["https://example.com/path/to/resource", "http://localhost:8080/path/to/resource"],
    ["http://www.test.com/", "http://localhost:8080/"],
    ["/relative/path", "http://localhost:8080/relative/path"],
    ["relative/path", "http://localhost:8080/relative/path"],
  ])("should extract the pathname from URL %s", (url, expected) => {
    expect(changeBaseURLToCurrentLocation(url)).toBe(expected);
  });
});
