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

import { Portable } from "./Portable";
import { ErraiObject } from "./model/ErraiObject";
import { ErraiObjectConstants } from "./model/ErraiObjectConstants";

export class UnmarshallingContext {
  private readonly _oracle: Map<string, () => Portable<any>>;
  private readonly _objectsCache: Map<string, Portable<any>>;

  constructor(oracle: Map<string, () => Portable<any>>) {
    this._oracle = oracle;
    this._objectsCache = new Map();
  }

  public cacheObject(input: ErraiObject, obj: Portable<any>) {
    const objectId = input[ErraiObjectConstants.OBJECT_ID];
    if (objectId) {
      this._objectsCache.set(objectId, obj);
    }
  }

  public getCached(input: ErraiObject): Portable<any> | undefined {
    const objectId = input[ErraiObjectConstants.OBJECT_ID];
    if (!objectId) {
      return undefined;
    }

    return this._objectsCache.get(objectId);
  }

  public getFactory(fqcn: string): (() => Portable<any>) | undefined {
    return this._oracle.get(fqcn);
  }
}
