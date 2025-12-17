/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import { Type } from "./Type";
import { Identifier } from "../Identifier";

export class MapBackedType implements Type {
  private readonly _typeRef: string;
  private readonly _name: string;
  private readonly _properties: Map<string, Type>;
  private readonly _source: Identifier;

  constructor(name: string, typeRef: string, source: Identifier) {
    this._typeRef = typeRef;
    this._name = name;
    this._properties = new Map<string, Type>();
    this._source = source;
  }

  get name(): string {
    return this._name;
  }

  get properties(): Map<string, Type> {
    return this._properties;
  }

  get typeRef(): string {
    return this._typeRef;
  }

  get source(): Identifier {
    return this._source;
  }
}
