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

import { DataType } from "../../api";

class DefaultDataType implements DataType {
  constructor(public readonly name: string, public readonly defaultValue?: string) {}
}

export const ARRAY: DataType = new DefaultDataType("string[]", "[]");
export const ANY_ARRAY: DataType = new DefaultDataType("any[]");
export const BOOLEAN: DataType = new DefaultDataType("boolean", "false");
export const DATE: DataType = new DefaultDataType("string");
export const NUMBER: DataType = new DefaultDataType("number");
export const STRING: DataType = new DefaultDataType("string", '""');
export const OBJECT: DataType = new DefaultDataType("any");
