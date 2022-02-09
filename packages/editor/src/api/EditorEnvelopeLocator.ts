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

import { IMinimatch, Minimatch } from "minimatch";

export class EnvelopeMapping {
  public matcher: IMinimatch;

  constructor(
    public readonly type: string,
    public readonly filePathGlob: string,
    public readonly resourcesPathPrefix: string,
    public readonly envelopePath: string
  ) {
    this.matcher = new Minimatch(filePathGlob, { nocase: true });
  }
}

export class EditorEnvelopeLocator {
  constructor(public readonly targetOrigin: string, public readonly envelopeMappings: EnvelopeMapping[]) {}

  public getEnvelopeMapping(path: string) {
    return this.envelopeMappings.find((mapping) => {
      return mapping.matcher.match(path);
    });
  }

  public hasMappingFor(path: string) {
    return this.getEnvelopeMapping(path) !== undefined;
  }
}
