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

import { ErraiObject } from "./model/ErraiObject";
import { ErraiObjectConstants } from "./model/ErraiObjectConstants";
import { Portable } from "./Portable";
import { JavaWrapper } from "../java-wrappers/JavaWrapper";

export class MarshallingContext {
  private objContext: Map<Portable<any>, ErraiObject>;
  private objectId: number;

  constructor() {
    this.objContext = new Map();
    this.objectId = 0;
  }

  public incrementAndGetObjectId() {
    return ++this.objectId;
  }

  public cacheObject(key: Portable<any>, obj: ErraiObject) {
    this.objContext.set(this.unwrap(key), {
      [ErraiObjectConstants.ENCODED_TYPE]: obj[ErraiObjectConstants.ENCODED_TYPE],
      [ErraiObjectConstants.OBJECT_ID]: obj[ErraiObjectConstants.OBJECT_ID]
    });
  }

  public getCached(key: Portable<any>): ErraiObject | undefined {
    return this.objContext.get(this.unwrap(key))!;
  }

  private unwrap(key: Portable<any>) {
    if (JavaWrapper.extendsJavaWrapper(key)) {
      // When handling wrapped values, we use the raw typescript value as cache key.
      // This is needed because in the marshalling flow we wrap the values automatically
      // if they represent a Java type, creating a new wrapper object every time. If we use the wrapper
      // object directly, the value will never be found in cache, because it'll always be a different pointer
      return key.get();
    }

    return key;
  }
}
