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

import { Input } from "../model";

export const dataTypes = [
  { typeRef: "Undefined", name: "<Undefined>", isCustom: false },
  { typeRef: "Any", name: "Any", isCustom: false },
  { typeRef: "Boolean", name: "boolean", isCustom: false },
  { typeRef: "Context", name: "context", isCustom: false },
  { typeRef: "Date", name: "date", isCustom: false },
  { typeRef: "DateTime", name: "date and time", isCustom: false },
  { typeRef: "DateTimeDuration", name: "days and time duration", isCustom: false },
  { typeRef: "Number", name: "number", isCustom: false },
  { typeRef: "String", name: "string", isCustom: false },
  { typeRef: "Time", name: "time", isCustom: false },
  { typeRef: "YearsMonthsDuration", name: "years and months duration", isCustom: false },
];

export function generateDecisionTypes(input: Input[]) {
  const types: { typeRef: string; name: string; isCustom: boolean }[] = [];
  dataTypes.forEach((val) => types.push(Object.assign({}, val)));
  input.forEach((input) => {
    types.push({ typeRef: input.name, name: input.name, isCustom: true });
  });

  return types;
}
