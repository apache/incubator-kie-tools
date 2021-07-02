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

import { CapabilityResponse, CapabilityResponseStatus } from "@kie-tooling-core/backend/dist/api";

describe("utility methods to create a CapabilityResponse", () => {
  test("should be an empty OK response", () => {
    const response = CapabilityResponse.ok();
    expect(response.status).toBe(CapabilityResponseStatus.OK);
    expect(response.body).toBeUndefined();
    expect(response.message).toBeUndefined();
  });

  test("should be an OK response with a body", () => {
    const responseBody = { foo: "bar" };
    const response = CapabilityResponse.ok(responseBody);
    expect(response.status).toBe(CapabilityResponseStatus.OK);
    expect(response.body).toBe(responseBody);
    expect(response.message).toBeUndefined();
  });

  test("should be a NOT_AVAILABLE response", () => {
    const responseMessage = "some message";
    const response = CapabilityResponse.notAvailable(responseMessage);
    expect(response.status).toBe(CapabilityResponseStatus.NOT_AVAILABLE);
    expect(response.body).toBeUndefined();
    expect(response.message).toBe(responseMessage);
  });

  test("should be a MISSING_INFRA response", () => {
    const response = CapabilityResponse.missingInfra();
    expect(response.status).toBe(CapabilityResponseStatus.MISSING_INFRA);
    expect(response.body).toBeUndefined();
  });
});
