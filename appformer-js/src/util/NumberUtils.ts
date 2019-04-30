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

export class NumberUtils {
  public static readonly nonNegativeIntegerRegex = new RegExp(/^\d*$/);
  public static readonly integerNumberRegex = new RegExp(/^(-)?\d*$/);
  public static readonly floatNumberRegex = new RegExp(/^(-)?(\d*)(\.)?(\d*)$/);

  public static isNonNegativeIntegerString(str: string): boolean {
    return this.nonNegativeIntegerRegex.test(str);
  }

  public static isIntegerString(str: string): boolean {
    return this.integerNumberRegex.test(str);
  }

  public static isFloatString(str: string): boolean {
    return this.floatNumberRegex.test(str);
  }
}
