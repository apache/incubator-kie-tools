/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

import { basename, parse } from "path";
import { resolveExtension } from "../../extension";

export const decoder = new TextDecoder("utf-8");
export const encoder = new TextEncoder();

export interface BaseFileProps {
  relativePath: string;
  getFileContents: () => Promise<Uint8Array>;
}

export abstract class BaseFile {
  constructor(protected readonly args: BaseFileProps) {}

  get getFileContentsAsString() {
    return () => this.getFileContents().then((c) => decoder.decode(c));
  }

  get getFileContents() {
    return this.args.getFileContents;
  }

  get relativePath() {
    return this.args.relativePath;
  }

  get relativePathWithoutExtension() {
    return this.relativePath.replace(`.${this.extension}`, "");
  }

  get relativeDirPath() {
    return parse(this.relativePath).dir;
  }

  get extension() {
    return resolveExtension(this.relativePath);
  }

  get nameWithoutExtension() {
    return basename(this.relativePath, `.${this.extension}`);
  }

  get name() {
    return basename(this.relativePath);
  }

  abstract get parentId(): string;
}
