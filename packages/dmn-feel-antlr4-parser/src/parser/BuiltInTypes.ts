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
import { Expression } from "./Expression";
import { FeelSyntacticSymbolNature } from "./FeelSyntacticSymbolNature";
import { generateUuid } from "../Uuid";

export class BuiltInTypes {
  public static readonly Number: DataType = {
    uuid: generateUuid(),
    name: "number",
    typeRef: "number",
    properties: new Map([]),

    source: {
      expressionsThatUseTheIdentifier: new Map<string, Expression>(),
      value: "number",
      feelSyntacticSymbolNature: FeelSyntacticSymbolNature.GlobalVariable,
    },
  };

  public static readonly Boolean: DataType = {
    uuid: generateUuid(),
    name: "boolean",
    typeRef: "boolean",
    properties: new Map([]),

    source: {
      expressionsThatUseTheIdentifier: new Map<string, Expression>(),
      value: "boolean",
      feelSyntacticSymbolNature: FeelSyntacticSymbolNature.GlobalVariable,
    },
  };

  public static readonly String: DataType = {
    uuid: generateUuid(),
    name: "string",
    typeRef: "string",
    properties: new Map([]),

    source: {
      expressionsThatUseTheIdentifier: new Map<string, Expression>(),
      value: "string",
      feelSyntacticSymbolNature: FeelSyntacticSymbolNature.GlobalVariable,
    },
  };

  public static readonly DaysAndTimeDuration: DataType = {
    uuid: generateUuid(),
    name: "days and time duration",
    typeRef: "days and time duration",
    properties: new Map([
      ["days", BuiltInTypes.Number],
      ["hours", BuiltInTypes.Number],
      ["minutes", BuiltInTypes.Number],
      ["seconds", BuiltInTypes.Number],
      ["timezone", BuiltInTypes.String],
    ]),

    source: {
      expressionsThatUseTheIdentifier: new Map<string, Expression>(),
      value: "days and time duration",
      feelSyntacticSymbolNature: FeelSyntacticSymbolNature.GlobalVariable,
    },
  };

  public static readonly DateAndTime: DataType = {
    uuid: generateUuid(),
    name: "date and time",
    typeRef: "date and time",
    properties: new Map([
      ["year", BuiltInTypes.Number],
      ["month", BuiltInTypes.Number],
      ["day", BuiltInTypes.Number],
      ["weekday", BuiltInTypes.Number],
      ["hour", BuiltInTypes.Number],
      ["minute", BuiltInTypes.Number],
      ["second", BuiltInTypes.Number],
      ["time offset", BuiltInTypes.DaysAndTimeDuration],
      ["timezone", BuiltInTypes.String],
    ]),

    source: {
      expressionsThatUseTheIdentifier: new Map<string, Expression>(),
      value: "date and time",
      feelSyntacticSymbolNature: FeelSyntacticSymbolNature.GlobalVariable,
    },
  };

  public static readonly YearsAndMonthsDuration: DataType = {
    uuid: generateUuid(),
    name: "years and months duration",
    typeRef: "years and months duration",
    properties: new Map([
      ["years", BuiltInTypes.Number],
      ["months", BuiltInTypes.Number],
    ]),

    source: {
      expressionsThatUseTheIdentifier: new Map<string, Expression>(),
      value: "years and months duration",
      feelSyntacticSymbolNature: FeelSyntacticSymbolNature.GlobalVariable,
    },
  };

  public static readonly Time: DataType = {
    uuid: generateUuid(),
    name: "time",
    typeRef: "time",
    properties: new Map([
      ["hour", BuiltInTypes.Number],
      ["minute", BuiltInTypes.Number],
      ["second", BuiltInTypes.Number],
      ["time offset", BuiltInTypes.DaysAndTimeDuration],
      ["timezone", BuiltInTypes.String],
    ]),
    source: {
      expressionsThatUseTheIdentifier: new Map<string, Expression>(),
      value: "time",
      feelSyntacticSymbolNature: FeelSyntacticSymbolNature.GlobalVariable,
    },
  };

  public static readonly Date: DataType = {
    uuid: generateUuid(),
    name: "date",
    typeRef: "date",
    properties: new Map([
      ["year", BuiltInTypes.Number],
      ["month", BuiltInTypes.Number],
      ["day", BuiltInTypes.Number],
      ["weekday", BuiltInTypes.Number],
    ]),
    source: {
      expressionsThatUseTheIdentifier: new Map<string, Expression>(),
      value: "date",
      feelSyntacticSymbolNature: FeelSyntacticSymbolNature.GlobalVariable,
    },
  };
}
