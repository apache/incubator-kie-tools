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

import { BigNumber } from "bignumber.js";
import { JavaWrapper } from "./JavaWrapper";

export abstract class BigNumberWrapper extends JavaWrapper<BigNumber> {
  private _value: BigNumber;

  public constructor(value: string) {
    super();
    const valueAsNumber = this.from(value);

    this.set(valueAsNumber);
  }

  public get(): BigNumber {
    return this._value;
  }

  public set(value: BigNumber | ((current: BigNumber) => BigNumber)): void {
    if (this.instanceOfBigNumber(value)) {
      this._value = this.applyNumericRange(value);
    } else {
      this._value = this.applyNumericRange(value(this.get()));
    }
  }

  protected abstract from(asString: string): BigNumber;

  protected abstract isInRange(n: BigNumber): boolean;

  private applyNumericRange(value: BigNumber) {
    if (!this.isInRange(value)) {
      return new BigNumber(NaN);
    }
    return value;
  }

  private instanceOfBigNumber(value: any): value is BigNumber {
    return BigNumber.isBigNumber(value);
  }
}
