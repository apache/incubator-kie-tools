/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

import { readFeaturePreviewConfigCookie } from "../../../settings/featurePreview/FeaturePreviewConfig";
import * as cookies from "../../../cookies";

jest.mock("../../../cookies");
const mockCookies = cookies as jest.Mocked<typeof cookies>;

describe("readFeaturePreviewConfigCookie", () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  test("should return true if cookie is set as true", async () => {
    mockCookies.getCookie.mockReturnValueOnce("true");
    expect(readFeaturePreviewConfigCookie().stunnerEnabled).toBeTruthy();
  });

  test("should return false if cookie is set as false", async () => {
    mockCookies.getCookie.mockReturnValueOnce("false");
    expect(readFeaturePreviewConfigCookie().stunnerEnabled).toBeFalsy();
  });

  test("should return default value (true) if cookie is not set", async () => {
    mockCookies.getCookie.mockReturnValueOnce(undefined);
    expect(readFeaturePreviewConfigCookie().stunnerEnabled).toBeTruthy();
  });
});
