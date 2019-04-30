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

export abstract class NumberWrapper extends JavaWrapper<number> {
  private _value: number;

  public constructor(value: string) {
    super();
    const valueAsNumber = this.from(value);

    this.set(valueAsNumber);
  }

  public get(): number {
    return this._value;
  }

  public set(value: number | ((current: number) => number)): void {
    if (typeof value === "number") {
      this._value = this.applyNumericRange(value);
    } else {
      this._value = this.applyNumericRange(value(this.get()));
    }
  }

  protected abstract from(asString: string): number;

  protected abstract isInRange(n: number): boolean;

  private applyNumericRange(n: number): number {
    if (!this.isInRange(n)) {
      return Number.NaN;
    }
    return n;
  }
}
