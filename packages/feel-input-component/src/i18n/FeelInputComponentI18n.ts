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

import { ReferenceDictionary } from "@kie-tools-core/i18n/dist/core";
import { CommonI18n } from "@kie-tools/i18n-common-dictionary";

interface FeelInputComponentDictionary extends ReferenceDictionary {
  functionDescription: {
    absDescription: (value: string) => string;
    afterPoint: (point1: string, point2: string) => string;
    afterPointRange: (point: string, range: string) => string;
    afterRangePoint: (range: string, point: string) => string;
    afterRange: (range1: string, range2: string) => string;
    allTrue: (value: string) => string;
    anyTrue: (value: string) => string;
    append: string;
    beforePoint: (point1: string, point2: string) => string;
    beforePointRange: (point: string, range: string) => string;
    beforeRangePoint: (range: string, point: string) => string;
    beforeRange: (range1: string, range2: string) => string;
    ceiling: (value: string) => string;
    ceilingScale: (value: string, scale: string) => string;
    coincides: (point1: string, point2: string) => string;
    coincidesRange: (range1: string, range2: string) => string;
    concatenate: string;
    contains: (value: string, match: string) => string;
    contextKeyValue: (context: string, key: string, value: string) => string;
    contextMerge: (context: string, contexts: string) => string;
    context: (context: string) => string;
    contextPut: (context: string, contextPut: string) => string;
    count: (list: string) => string;
    date: (from: string) => string;
    dateyear: (year: string, month: string, day: string) => string;
    dateTimeFrom: (from: string) => string;
    dateTime: (date: string, time: string) => string;
    datetimezone: (date: string, time: string) => string;
    dateYearSecond: (year: string, month: string, day: string, hour: string, minute: string, second: string) => string;
    datetYearOffset: (
      year: string,
      month: string,
      day: string,
      hour: string,
      minute: string,
      second: string,
      offset: string
    ) => string;
  };
}

export interface FeelInputComponentI18n extends FeelInputComponentDictionary, CommonI18n {}
