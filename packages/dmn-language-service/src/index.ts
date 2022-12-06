/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

const DMN_IMPORT = "dmn:import";
const XML_MIME = "text/xml";
const LOCATION_URI_ATTRIBUTE = "locationURI";

export class DmnLanguageService {
  private readonly parser = new DOMParser();
  DmnLanguageService() {}

  public getImportedModels(models: string | string[]): (string | null)[] {
    if (Array.isArray(models)) {
      return models.flatMap((model) => {
        const xmlContent = this.parser.parseFromString(model, XML_MIME);
        const importedModels = xmlContent.getElementsByTagName(DMN_IMPORT);
        return Array.from(importedModels).map((importedModel) => importedModel.getAttribute(LOCATION_URI_ATTRIBUTE));
      });
    }

    const xmlContent = this.parser.parseFromString(models, XML_MIME);
    const importedModels = xmlContent.getElementsByTagName(DMN_IMPORT);
    return Array.from(importedModels).map((importedModel) => importedModel.getAttribute(LOCATION_URI_ATTRIBUTE));
  }
}
