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
import { JavaType } from "./JavaType";

export class JavaOptional<T> extends JavaWrapper<T | undefined> {
  private readonly _fqcn = JavaType.OPTIONAL;

  private _value: T | undefined;

  constructor(value?: T) {
    super();
    this.set(value);
  }

  public get(): T {
    if (this._value === null || this._value === undefined) {
      throw new Error("No value present");
    }

    return this._value!;
  }

  public isPresent(): boolean {
    return this._value !== undefined;
  }

  public set(val: ((current: T | undefined) => T | undefined) | T | undefined): void {
    if (typeof val === "function") {
      this._value = val(this.get());
    } else {
      this._value = val;
    }
  }
}
