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

export function isString(obj: any): boolean {
  return typeof obj === "string" || obj instanceof String;
}

export function instanceOfString(obj: any): obj is string {
  return isString(obj);
}

export function isArray<T>(obj: any): boolean {
  return obj instanceof Array;
}

export function instanceOfArray<T>(obj: any): obj is T[] {
  return isArray(obj);
}

export function isSet<T>(obj: any): boolean {
  return obj instanceof Set;
}

export function instanceOfSet<T>(obj: any): obj is Set<T> {
  return isSet(obj);
}

export function isMap<T, U>(obj: any): boolean {
  return obj instanceof Map;
}

export function instanceOfMap<T, U>(obj: any): obj is Map<T, U> {
  return isMap(obj);
}

export function isBoolean(obj: any): boolean {
  return typeof obj === "boolean" || obj instanceof Boolean;
}

export function instanceOfBoolean(obj: any): obj is boolean {
  return isBoolean(obj);
}

export function isDate(obj: any): boolean {
  return obj instanceof Date;
}

export function instanceOfDate(obj: any): obj is Date {
  return isDate(obj);
}

export function isNumber(obj: any): boolean {
  return typeof obj === "number";
}

export function instanceOfNumber(obj: any): obj is number {
  return isNumber(obj);
}
