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

import { DataType } from "./DataType";
import { BuiltInTypes } from "./BuiltInTypes";

export class FeelFunctionReturningTypes {
  public static readonly Index: ReadonlyMap<string, DataType> = new Map<string, DataType>([
    ["abs", BuiltInTypes.Number],
    ["after", BuiltInTypes.Boolean],
    ["all", BuiltInTypes.Boolean],
    ["any", BuiltInTypes.Boolean],
    ["append", BuiltInTypes.List],
    ["before", BuiltInTypes.Boolean],
    ["ceiling", BuiltInTypes.Number],
    ["code", BuiltInTypes.Number],
    ["coincides", BuiltInTypes.Boolean],
    ["concatenate", BuiltInTypes.String],
    ["contains", BuiltInTypes.Boolean],
    ["context", BuiltInTypes.Context],
    ["context put", BuiltInTypes.Context],
    ["context merge", BuiltInTypes.Context],
    ["count", BuiltInTypes.Number],
    ["date", BuiltInTypes.Date],
    ["date and time", BuiltInTypes.DateAndTime],
    ["day of week", BuiltInTypes.String],
    ["day of year", BuiltInTypes.Number],
    ["decimal", BuiltInTypes.Number],
    ["decision table", BuiltInTypes.Any],
    ["distinct values", BuiltInTypes.List],
    ["duration", BuiltInTypes.DaysAndTimeDuration],
    ["during", BuiltInTypes.Boolean],
    ["ends with", BuiltInTypes.Boolean],
    ["even", BuiltInTypes.Boolean],
    ["exp", BuiltInTypes.Number],
    ["finished by", BuiltInTypes.Boolean],
    ["finishes", BuiltInTypes.Boolean],
    ["flatten", BuiltInTypes.List],
    ["floor", BuiltInTypes.Number],
    ["get entries", BuiltInTypes.List],
    ["get value", BuiltInTypes.Any],
    ["includes", BuiltInTypes.Boolean],
    ["index of", BuiltInTypes.List],
    ["insert before", BuiltInTypes.List],
    ["invoke", BuiltInTypes.Any],
    ["is", BuiltInTypes.Boolean],
    ["list contains", BuiltInTypes.Boolean],
    ["list replace", BuiltInTypes.List],
    ["log", BuiltInTypes.Number],
    ["lower case", BuiltInTypes.String],
    ["matches", BuiltInTypes.Boolean],
    ["max", BuiltInTypes.Any],
    ["mean", BuiltInTypes.Number],
    ["median", BuiltInTypes.Number],
    ["meets", BuiltInTypes.Boolean],
    ["met by", BuiltInTypes.Boolean],
    ["min", BuiltInTypes.Any],
    ["mode", BuiltInTypes.Number],
    ["modulo", BuiltInTypes.Number],
    ["month of year", BuiltInTypes.String],
    ["nn all", BuiltInTypes.Boolean],
    ["nn any", BuiltInTypes.Boolean],
    ["nn count", BuiltInTypes.Number],
    ["nn max", BuiltInTypes.Number],
    ["nn mean", BuiltInTypes.Number],
    ["nn median", BuiltInTypes.Number],
    ["nn min", BuiltInTypes.Number],
    ["nn mode", BuiltInTypes.Number],
    ["nn stddev", BuiltInTypes.Number],
    ["nn sum", BuiltInTypes.Number],
    ["not", BuiltInTypes.Boolean],
    ["now", BuiltInTypes.DateAndTime],
    ["number", BuiltInTypes.Number],
    ["odd", BuiltInTypes.Boolean],
    ["overlaps after", BuiltInTypes.Boolean],
    ["overlaps before", BuiltInTypes.Boolean],
    ["overlaps", BuiltInTypes.Boolean],
    ["product", BuiltInTypes.Number],
    ["range", BuiltInTypes.Range],
    ["remove", BuiltInTypes.List],
    ["replace", BuiltInTypes.String],
    ["reverse", BuiltInTypes.List],
    ["round down", BuiltInTypes.Number],
    ["round half down", BuiltInTypes.Number],
    ["round half up", BuiltInTypes.Number],
    ["round up", BuiltInTypes.Number],
    ["sort", BuiltInTypes.List],
    ["split", BuiltInTypes.List],
    ["sqrt", BuiltInTypes.Number],
    ["started by", BuiltInTypes.Boolean],
    ["starts with", BuiltInTypes.Boolean],
    ["starts", BuiltInTypes.Boolean],
    ["stddev", BuiltInTypes.Number],
    ["string", BuiltInTypes.String],
    ["string join", BuiltInTypes.String],
    ["string length", BuiltInTypes.Number],
    ["sublist", BuiltInTypes.List],
    ["substring after", BuiltInTypes.String],
    ["substring before", BuiltInTypes.String],
    ["substring", BuiltInTypes.String],
    ["sum", BuiltInTypes.Number],
    ["time", BuiltInTypes.Time],
    ["today", BuiltInTypes.Date],
    ["union", BuiltInTypes.List],
    ["upper case", BuiltInTypes.String],
    ["week of year", BuiltInTypes.Number],
    ["years and months duration", BuiltInTypes.Any],
  ]);
}
