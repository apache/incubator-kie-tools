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

import { ValidationStore } from "./ValidationStore";
import { Path } from "../paths";
import { ValidationLevel } from "./ValidationLevel";

export class ValidationEntry {
  constructor(public level: ValidationLevel, public message?: string) {}
}

export class ValidationRegistry {
  private readonly store: ValidationStore = new ValidationStore();

  public set = (path: Path, entry: ValidationEntry): void => {
    this.store.set(path, entry);
  };

  public get = (path: Path): ValidationEntry[] => {
    return this.store.get(path);
  };

  public clear = (path: Path): void => {
    this.store.clear(path);
  };
}
