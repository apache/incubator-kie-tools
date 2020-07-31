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

import { LanguageData, Router } from "@kogito-tooling/microeditor-envelope-protocol";
import { FACTORY_TYPE } from "../editor/PMMLEditorFactory";
import { PMMLRoute } from "../editor/PMMLRoute";

class MockRouter extends Router {
  constructor() {
    super();
  }

  public getLanguageDataByFileExtension(): Map<string, any> {
    return new Map<string, any>();
  }

  public getRelativePathTo(uri: string): string {
    return `../${uri}`;
  }

  public getLanguageData(fileExtension: string) {
    return this.getLanguageDataByFileExtension().get(fileExtension);
  }

  public getTargetOrigin() {
    return "";
  }
}

const router: MockRouter = new MockRouter();
const route: PMMLRoute = new PMMLRoute();

describe("PMMLRoute", () => {
  test("getRoutes", () => {
    const routes: Map<string, LanguageData> = route.getRoutes(router);
    expect(routes).not.toBeUndefined();

    const languageData: LanguageData | undefined = routes.get(FACTORY_TYPE);
    expect(languageData).not.toBeUndefined();
    expect((languageData as LanguageData).type).toBe(FACTORY_TYPE);
  });
});
