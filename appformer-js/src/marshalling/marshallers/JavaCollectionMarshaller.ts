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

import { JavaCollection } from "../../java-wrappers/JavaCollection";
import { ErraiObject } from "../model/ErraiObject";
import { MarshallingContext } from "../MarshallingContext";
import { Portable } from "../Portable";
import { NullableMarshaller } from "./NullableMarshaller";
import { GenericsTypeMarshallingUtils } from "./util/GenericsTypeMarshallingUtils";
import { UnmarshallingContext } from "../UnmarshallingContext";
import { isArray } from "../../util/TypeUtils";
import { JavaArrayList, JavaHashSet } from "../../java-wrappers";
import { ValueBasedErraiObject } from "../model/ValueBasedErraiObject";
import { MarshallerProvider } from "../MarshallerProvider";

abstract class JavaCollectionMarshaller<T extends Iterable<Portable<any> | null>> extends NullableMarshaller<
  JavaCollection<T>,
  ErraiObject,
  ErraiObject,
  T
> {
  public notNullMarshall(input: JavaCollection<T>, ctx: MarshallingContext): ErraiObject {
    const cachedObject = ctx.getCached(input);
    if (cachedObject) {
      return cachedObject;
    }

    const elements = input.get();

    const serializedValues = [];
    for (const element of Array.from(elements)) {
      serializedValues.push(GenericsTypeMarshallingUtils.marshallGenericsTypeElement(element, ctx));
    }

    const fqcn = (input as any)._fqcn;
    const value = serializedValues;
    const objectId = ctx.incrementAndGetObjectId().toString(10);
    const resultObject = new ValueBasedErraiObject(fqcn, value, objectId).asErraiObject();

    ctx.cacheObject(input, resultObject);

    return resultObject;
  }

  public notNullUnmarshall(input: ErraiObject, ctx: UnmarshallingContext): T {
    const cachedObject = ctx.getCached(input);
    if (cachedObject) {
      return (cachedObject as JavaCollection<T>).get();
    }

    const collection = ValueBasedErraiObject.from(input).value;
    if (!JavaCollectionMarshaller.isValid(collection)) {
      throw new Error(`Invalid collection value ${collection}. Can't unmarshall json ${input}`);
    }

    const unmarshalledValues = [];
    for (const element of Array.from(collection)) {
      unmarshalledValues.push(MarshallerProvider.getForObject(element).unmarshall(element, ctx));
    }

    const javaCollection = this.fromArray(unmarshalledValues);
    ctx.cacheObject(input, javaCollection);

    return javaCollection.get();
  }

  protected abstract fromArray(values: Array<Portable<any>>): JavaCollection<T>;

  private static isValid(input: any): boolean {
    if (input === null || input === undefined) {
      return false;
    }

    // inside the json, all collections are represented as an array
    return isArray(input);
  }
}

export class JavaArrayListMarshaller extends JavaCollectionMarshaller<Array<Portable<any> | null>> {
  protected fromArray(values: Array<Portable<any>>): JavaArrayList<Portable<any>> {
    return new JavaArrayList(values);
  }
}

export class JavaHashSetMarshaller extends JavaCollectionMarshaller<Set<Portable<any> | null>> {
  protected fromArray(values: Array<Portable<any>>): JavaHashSet<Portable<any>> {
    return new JavaHashSet(new Set(values));
  }
}
