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

import { NullableMarshaller } from "./NullableMarshaller";
import { ErraiObjectConstants } from "../model/ErraiObjectConstants";
import { GenericsTypeMarshallingUtils } from "./util/GenericsTypeMarshallingUtils";
import { JavaHashMap } from "../../java-wrappers/JavaHashMap";
import { MarshallingContext } from "../MarshallingContext";
import { ErraiObject } from "../model/ErraiObject";
import { isString } from "../../util/TypeUtils";
import { UnmarshallingContext } from "../UnmarshallingContext";
import { ValueBasedErraiObject } from "../model/ValueBasedErraiObject";
import { MarshallerProvider } from "../MarshallerProvider";

export class JavaHashMapMarshaller<T, U> extends NullableMarshaller<
  JavaHashMap<T | undefined, U | undefined>,
  ErraiObject,
  ErraiObject,
  Map<T | undefined, U | undefined>
> {
  public notNullMarshall(input: JavaHashMap<T, U>, ctx: MarshallingContext): ErraiObject {
    const cachedObject = ctx.getCached(input);
    if (cachedObject) {
      return cachedObject;
    }

    const marshalledEntriesMap = this.marshallEntries(input.get().entries(), ctx);

    const fqcn = (input as any)._fqcn;
    const value = marshalledEntriesMap;
    const objectId = ctx.incrementAndGetObjectId().toString(10);
    const result = new ValueBasedErraiObject(fqcn, value, objectId).asErraiObject();

    ctx.cacheObject(input, result);

    return result;
  }

  public notNullUnmarshall(input: ErraiObject, ctx: UnmarshallingContext): Map<T | undefined, U | undefined> {
    const cachedObject = ctx.getCached(input);
    if (cachedObject) {
      return (cachedObject as JavaHashMap<T | undefined, U | undefined>).get();
    }

    const mapObj = ValueBasedErraiObject.from(input).value;
    if (!mapObj) {
      throw new Error(`Invalid Map value ${mapObj}. Can't unmarshall json ${input}`);
    }

    const map = this.unmarshallEntries(mapObj, ctx);

    ctx.cacheObject(input, map);

    return map.get();
  }

  private marshallEntries(entries: IterableIterator<[T, U]>, ctx: MarshallingContext) {
    return Array.from(entries)
      .map(([key, value]) => this.marshallEntry(key, value, ctx))
      .reduce((acc, cur) => ({ ...acc, ...cur }), {});
  }

  private marshallEntry(key: T, value: U, ctx: MarshallingContext) {
    const marshalledKey = GenericsTypeMarshallingUtils.marshallGenericsTypeElement(key, ctx);
    const marshalledValue = GenericsTypeMarshallingUtils.marshallGenericsTypeElement(value, ctx);

    if (marshalledKey === null) {
      return { [ErraiObjectConstants.NULL]: marshalledValue };
    }

    if (!isString(marshalledKey)) {
      // need to prefix the key in order to tell errai-marshalling that the key is not a native string
      return { [ErraiObjectConstants.JSON + JSON.stringify(marshalledKey)]: marshalledValue };
    }

    return { [`${marshalledKey}`]: marshalledValue };
  }

  private unmarshallEntries(map: any, ctx: UnmarshallingContext): JavaHashMap<T | undefined, U | undefined> {
    const unmarshalledMap = new Map<T | undefined, U | undefined>();

    Object.keys(map).forEach(key => {
      const unmarshalledKey = this.unmarshallKey(key, ctx);

      const unmarshalledValue = MarshallerProvider.getForObject(map[key]).unmarshall(map[key], ctx);

      unmarshalledMap.set(unmarshalledKey, unmarshalledValue);
    });

    return new JavaHashMap(unmarshalledMap);
  }

  private unmarshallKey(key: string, ctx: UnmarshallingContext): T | undefined {
    if (!key) {
      throw new Error(`Invalid Map's key ${key}. Can't unmarshall json!`);
    }

    if (key === ErraiObjectConstants.NULL) {
      return undefined;
    }

    if (key.startsWith(ErraiObjectConstants.JSON)) {
      // this prefix indicates that the key is not a native string, it is a json object serialized to string
      const keyJson = JSON.parse(key.replace(ErraiObjectConstants.JSON, ""));
      return MarshallerProvider.getForObject(keyJson).unmarshall(keyJson, ctx);
    }

    // the map key has type string
    return key as any;
  }
}
