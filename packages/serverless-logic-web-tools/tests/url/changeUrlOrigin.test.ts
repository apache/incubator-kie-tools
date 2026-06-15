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

import { changeUrlOrigin } from "../../src/url";

describe("changeUrlOrigin", () => {
  it.each([
    ["http://localhost:8080/", "https://my-app.openshift.com", "https://my-app.openshift.com/"],
    ["http://localhost:8080/hello_world", "https://my-app.openshift.com", "https://my-app.openshift.com/hello_world"],
    ["http://localhost:8080/hello_world", "https://my-app.openshift.com/", "https://my-app.openshift.com/hello_world"],
    ["http://localhost:8080/workflow", "https://example.com:9090/data-index", "https://example.com:9090/workflow"],
    [
      "http://wrong-address.it/workflow?param=value",
      "https://my-app.com/graphql",
      "https://my-app.com/workflow?param=value",
    ],
    ["http://localhost:8080/workflow#section", "https://my-app.com/graphql", "https://my-app.com/workflow#section"],
    [
      "http://localhost:8080/api/v1/workflows/hello",
      "https://prod.example.com:8443",
      "https://prod.example.com:8443/api/v1/workflows/hello",
    ],
    ["http://example.com/path", "https://example.com/other", "https://example.com/path"],
    [
      "http://localhost:8080/hello_world",
      "https://dev-12345-000-demo-username-dev.apps.rm3.7wse.p1.openshiftapps.com/graphql",
      "https://dev-12345-000-demo-username-dev.apps.rm3.7wse.p1.openshiftapps.com/hello_world",
    ],
  ])("should change URL origin from '%s' with new origin '%s' to '%s'", (url, newOrigin, expectedUrl) => {
    expect(changeUrlOrigin(url, newOrigin)).toBe(expectedUrl);
  });

  it.each([
    ["", "https://example.com"],
    ["http://localhost:8080/path", ""],
    ["not-a-valid-url", "https://example.com"],
    ["http://localhost:8080/path", "not-a-valid-url"],
  ])("should throw error for invalid inputs: url='%s', newOrigin='%s'", (url, newOrigin) => {
    expect(() => changeUrlOrigin(url, newOrigin)).toThrow();
  });
});
