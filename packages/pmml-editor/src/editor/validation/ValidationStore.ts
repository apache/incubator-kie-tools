/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

import ownKeys = Reflect.ownKeys;
import { get, set, unset } from "lodash";
import { Path } from "../paths";
import { ValidationEntry } from "./ValidationRegistry";

export class ValidationStore {
  private readonly registry = {};

  public set = (path: Path, entry: ValidationEntry): void => {
    set(this.registry, path.path, entry);
  };

  public get = (path: Path): ValidationEntry[] => {
    const node = path.path === "" ? this.registry : get(this.registry, path.path);
    if (node === undefined) {
      return [];
    }
    //In case entries were created with the constructor
    if (node instanceof ValidationEntry) {
      return [node];
    }
    //In case entries were created as JSON
    if (node.level !== undefined) {
      return [node];
    }
    if (!(node instanceof Object)) {
      return [];
    }
    const mapped = ownKeys(node).map((key) => this.get({ path: this.childPath(path.path, String(key)) }));
    if (mapped.length === 0) {
      return [];
    }
    return mapped.reduce((pv, cv) => {
      return pv.concat(cv);
    });
  };

  private childPath = (parentPath: string, key: string): string => {
    if (parentPath === "") {
      return key;
    }
    return `${parentPath}.${key}`;
  };

  public clear = (path: Path): void => {
    unset(this.registry, path.path);
  };
}
