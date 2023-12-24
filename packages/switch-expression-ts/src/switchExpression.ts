/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

export type SwitchExpressionValue = string | number | symbol;

export type PartialWithMandatoryDefault<K extends SwitchExpressionValue, V> =
  // `default` is mandatory, then a partial of Record<K, V> can be part of it as well.
  { default: V } & Partial<Record<K, V>>;

export type SwitchExpressionCases<K extends SwitchExpressionValue, V> =
  | PartialWithMandatoryDefault<K, V>
  | Record<K, V>;

export const switchExpression = <
  S extends SwitchExpressionValue,
  ExplicitRet extends R,
  C extends SwitchExpressionCases<S, R> = SwitchExpressionCases<S, ExplicitRet>,
  R = C extends SwitchExpressionCases<S, infer R> ? R : any
>(
  switchValue: S | undefined,
  cases: C
): R => {
  const hasDefault = Object.hasOwn(cases as PartialWithMandatoryDefault<S, R>, "default");

  if (!switchValue) {
    if (hasDefault) {
      return (cases as PartialWithMandatoryDefault<S, R>).default as R;
    }
    throw new Error("No value provided to switchExpression and no default exists.");
  }

  let propertyValue: R | undefined;
  let didMatch: boolean = false;
  for (const [key, value] of Object.entries(cases)) {
    if (key === switchValue) {
      propertyValue = value;
      didMatch = true;
      break;
    }
  }
  if (didMatch) {
    return propertyValue as R;
  }
  if (hasDefault) {
    return (cases as PartialWithMandatoryDefault<S, R>).default as R;
  }
  throw new Error("Non matched key and no default exists for switchExpression.");
};
