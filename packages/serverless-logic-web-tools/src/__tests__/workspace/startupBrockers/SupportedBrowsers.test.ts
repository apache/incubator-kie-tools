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

import {
  BowserSatisfies,
  mapSupportedVersionsToBowser,
  MinVersionForFeature,
} from "../../../workspace/startupBlockers/SupportedBrowsers";

describe("SupportedBrowsers", () => {
  test("mapSupportedVersionsToBowser should extract the supported versions properly", async () => {
    const feature1: MinVersionForFeature = {
      chrome: 1,
      edge: 2,
      firefox: 2,
      opera: 2,
      safari: 1,
    };
    const feature2: MinVersionForFeature = {
      chrome: 2,
      edge: 2,
      firefox: 1,
      opera: 2,
      safari: 1,
    };
    const feature3: MinVersionForFeature = {
      chrome: 3,
      edge: 3,
      firefox: 1,
      opera: 1,
      safari: 1,
    };

    const expected: BowserSatisfies = {
      chrome: ">=3",
      edge: ">=3",
      firefox: ">=2",
      opera: ">=2",
      safari: ">=1",
    };

    expect(mapSupportedVersionsToBowser(feature1, feature2, feature3)).toStrictEqual(expected);
  });
});
