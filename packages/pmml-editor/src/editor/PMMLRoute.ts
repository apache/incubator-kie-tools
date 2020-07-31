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
import { LanguageData, Router, Routes as CoreRoutes } from "@kogito-tooling/microeditor-envelope-protocol";
import { FACTORY_TYPE } from "./PMMLEditorFactory";

export class PMMLRoute implements CoreRoutes {
  private languageData: Map<string, LanguageData> = new Map<string, LanguageData>();

  constructor() {
    this.languageData.set(FACTORY_TYPE, { type: FACTORY_TYPE });
  }

  public getRoutes(router: Router) {
    return this.languageData;
  }
}
