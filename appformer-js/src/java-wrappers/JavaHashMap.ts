/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

import { JavaWrapper } from "./JavaWrapper";
import { instanceOfMap } from "../util/TypeUtils";
import { JavaType } from "./JavaType";

export class JavaHashMap<T, U> extends JavaWrapper<Map<T, U>> {
  private readonly _fqcn = JavaType.HASH_MAP;

  private _value: Map<T, U>;

  constructor(value: Map<T, U>) {
    super();
    this.set(value);
  }

  public get(): Map<T, U> {
    return this._value;
  }

  public set(val: ((current: Map<T, U>) => Map<T, U>) | Map<T, U>): void {
    if (instanceOfMap(val)) {
      this._value = val;
    } else {
      this._value = val(this.get());
    }
  }
}
