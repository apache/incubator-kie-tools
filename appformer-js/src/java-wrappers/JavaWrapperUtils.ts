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
import { isArray, isBoolean, isDate, isMap, isSet, isString } from "../util/TypeUtils";
import { JavaArrayList } from "./JavaArrayList";
import { JavaHashSet } from "./JavaHashSet";
import { JavaHashMap } from "./JavaHashMap";
import { JavaBoolean } from "./JavaBoolean";
import { JavaString } from "./JavaString";
import { JavaDate } from "./JavaDate";
import { JavaEnum } from "./JavaEnum";

export class JavaWrapperUtils {
  private static wrappingFuncForType: Map<(obj: any) => boolean, (obj: any) => JavaWrapper<any>> = new Map([
    [isArray, (obj: any) => new JavaArrayList(obj) as JavaWrapper<any>],
    [isSet, (obj: any) => new JavaHashSet(obj) as JavaWrapper<any>],
    [isMap, (obj: any) => new JavaHashMap(obj) as JavaWrapper<any>],
    [isBoolean, (obj: any) => new JavaBoolean(obj) as JavaWrapper<any>],
    [isString, (obj: any) => new JavaString(obj) as JavaWrapper<any>],
    [isDate, (obj: any) => new JavaDate(obj) as JavaWrapper<any>]
  ]);

  public static needsWrapping(obj: any): boolean {
    return this.getWrappingFunction(obj) !== undefined;
  }

  public static wrapIfNeeded<U>(obj: U): JavaWrapper<U> | U {
    const func = this.getWrappingFunction(obj);
    if (!func) {
      return obj;
    }

    return func(obj);
  }

  public static isJavaType(fqcn: string): boolean {
    for (const type in JavaType) {
      if (JavaType[type] === fqcn) {
        return true;
      }
    }
    return false;
  }

  public static isEnum(obj: any): boolean {
    return obj instanceof JavaEnum;
  }

  private static getWrappingFunction(obj: any): ((obj: any) => JavaWrapper<any>) | undefined {
    //tslint:disable-next-line
    let result: ((obj: any) => JavaWrapper<any>) | undefined = undefined;

    this.wrappingFuncForType.forEach((wrapFunction, typeFilterFunction) => {
      if (result) {
        return;
      }

      if (typeFilterFunction(obj)) {
        result = wrapFunction;
      }
    });

    return result;
  }
}
