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

export enum EnvelopeContentType {
  PATH,
  CONTENT,
}

export type EnvelopeContent =
  | {
      type: EnvelopeContentType.PATH;
      path: string;
    }
  | {
      type: EnvelopeContentType.CONTENT;
      content: string;
    };

export class EnvelopeMapping {
  public matcher: IMinimatch;

  constructor(
    private readonly args: {
      type: string;
      filePathGlob: string;
      resourcesPathPrefix: string;
      envelopeContent: EnvelopeContent;
    }
  ) {
    this.matcher = new Minimatch(args.filePathGlob, { nocase: true, dot: true });
  }

  get type(): string {
    return this.args.type;
  }

  get filePathGlob(): string {
    return this.args.filePathGlob;
  }

  get resourcesPathPrefix(): string {
    return this.args.resourcesPathPrefix;
  }

  get envelopeContent(): EnvelopeContent {
    return this.args.envelopeContent;
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
