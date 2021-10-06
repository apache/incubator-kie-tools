/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import { GuidedTourCookie } from "@kie-tooling-core/guided-tour/dist/core";

describe("GuidedTourCookie", () => {
  const guidedTourCookie = new GuidedTourCookie();

  describe("isDisabled", () => {
    it("returns 'true' when guided tour is disabled", () => {
      document.cookie = "is-guided-tour-enabled=NO";
      expect(guidedTourCookie.isDisabled()).toBeTruthy();
    });

    it("returns 'false' when guided tour is not disabled", () => {
      document.cookie = "is-guided-tour-enabled=";
      expect(guidedTourCookie.isDisabled()).toBeFalsy();
    });
  });

  describe("markAsDisabled", () => {
    it("disables the guided tour", () => {
      document.cookie = "is-guided-tour-enabled=";

      guidedTourCookie.markAsDisabled();

      expect(document.cookie).toContain("is-guided-tour-enabled=NO");
    });
  });
});
