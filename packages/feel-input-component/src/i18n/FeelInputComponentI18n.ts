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

export interface FeelInputComponentDictionary {
  functionDescription: {
    absDescription: (value: string) => string;
    afterPoint: (result: string, point1: string, point2: string) => string;
    afterPointRange: (result: string, point: string, range: string) => string;
    afterRangePoint: (result: string, range: string, point: string) => string;
    afterRange: (result: string, range1: string, range2: string) => string;
    allTrue: (result: string, value: string) => string;
    anyTrue: (result: string, value: string, falseResult: string, nullValue: string) => string;
    append: (list: string) => string;
    beforePoint: (result: string, point1: string, point2: string) => string;
    beforePointRange: (result: string, point: string, range: string) => string;
    beforeRangePoint: (result: string, range: string, point: string) => string;
    beforeRange: (result: string, range1: string, range2: string) => string;
    ceiling: (value: string, nullValue: string) => string;
    ceilingScale: (value: string, scale: string, nullValue: string) => string;
    coincides: (result: string, point1: string, point2: string) => string;
    coincidesRange: (result: string, range1: string, range2: string) => string;
    concatenate: (list: string) => string;
    contains: (value: string, match: string) => string;
    contextKeyValue: (context: string, key: string, value: string, nullValue: string) => string;
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
    datetTimeTimezone: (
      year: string,
      month: string,
      day: string,
      hour: string,
      minute: string,
      second: string,
      timezone: string
    ) => string;
    dayOfWeek: (day: string) => string;
    dateOfYear: (day: string) => string;
    decimal: (n: string, scale: string) => string;
    distinctValues: (list: string) => string;
    duration: (from: string) => string;
    during: (result: string, point: string, range: string) => string;
    duringRange: (result: string, range1: string, range2: string) => string;
    endsWith: (string: string, match: string) => string;
    even: (result: string, number: string, falseResult: string) => string;
    exp: (number: string) => string;
    finishedBy: (result: string, range: string, point: string) => string;
    finishedByRange: (result: string, range1: string, range2: string) => string;
    finishes: (result: string, point: string, range: string) => string;
    finishesRange: (result: string, range1: string, range2: string) => string;
    flattenNestedLists: (list: string) => string;
    floor: (n: string, nullValue: string) => string;
    floorScale: (n: string, scale: string, nullValue: string) => string;
    getEntries: (list: string, m: string) => string;
    getValue: (m: string, key: string) => string;
    includes: (result: string, range: string, point: string) => string;
    includesRange: (result: string, range1: string, range2: string) => string;
    indexOf: (list: string, match: string) => string;
    insertBefore: (list: string, position: string, newItem: string) => string;
    is: (result: string) => string;
    listConstains: (list: string, element: string) => string;
    listReplace: (list: string, newItem: string, position: string) => string;
    listNewItem: (list: string, newItem: string, match: string, trueValue: string) => string;
    log: (number: string) => string;
    lowerCase: (stringValue: string) => string;
    matches: (input: string, pattern: string) => string;
    max: (list: string, nullValue: string) => string;
    mean: (list: string) => string;
    median: (list: string, nullValue: string) => string;
    meets: (result: string, range1: string, range2: string) => string;
    metBy: (result: string, range1: string, range2: string) => string;
    min: (list: string, nullValue: string) => string;
    mode: (list: string) => string;
    modulo: (dividend: string, divisor: string) => string;
    monthOfYear: string;
    nnAll: (result: string, list: string, nullValue: string) => string;
    nnAny: (result: string, list: string, nullValue: string) => string;
    nnCount: (list: string, nullValue: string) => string;
    nnMax: (list: string, nullValue: string) => string;
    nnMean: (nullValue: string) => string;
    nnMedian: (list: string, nullValue: string) => string;
    nnMin: (list: string, nullValue: string) => string;
    nnMode: (list: string, nullValue: string) => string;
    nnStddev: (list: string, nullValue: string) => string;
    nnSum: (list: string, nullValue: string) => string;
    not: (negand: string) => string;
    now: (date: string, time: string) => string;
    numbers: (from: string) => string;
    numberFrom: (from: string) => string;
    odd: (result: string, number: string) => string;
    overlapsAfter: (result: string, range1: string, range2: string) => string;
    overlapsBefore: (result: string, range1: string, range2: string) => string;
    overlaps: (result: string, range1: string, range2: string) => string;
    product: (list: string) => string;
    rangeFrom: (stringValue: string, from: string) => string;
    remove: (list: string, position: string) => string;
    replace: string;
    reverse: (list: string) => string;
    roundDown: (n: string, scale: string, nullValue: string) => string;
    roundDownN: (n: string, nullValue: string) => string;
    roundHalfDown: (n: string, scale: string, nullValue: string) => string;
    roundHalfDownN: (n: string, nullValue: string) => string;
    roundHalfUp: (n: string, scale: string, nullValue: string) => string;
    roundHalfUpN: (n: string, nullValue: string) => string;
    roundUp: (n: string, scale: string, nullValue: string) => string;
    roundUpN: (n: string, nullValue: string) => string;
    sort: (list: string, numberValue: string, stringValue: string) => string;
    sortPrecedes: (list: string) => string;
    split: (list: string, stringValue: string, delimiter: string) => string;
    sqrt: (numberValue: string) => string;
    startedBy: (result: string, range: string, point: string) => string;
    startedByRange: (result: string, range1: string, range2: string) => string;
    startsWith: (result: string, stringValue: string, match: string) => string;
    starts: (result: string, point: string, range: string) => string;
    startsRange: (result: string, range1: string, range2: string) => string;
    stddev: (list: string) => string;
    stringLength: (stringValue: string) => string;
    stringFrom: string;
    stringJoin: (list: string) => string;
    stringJoinDelimiter: (list: string, delimiter: string, nullValue: string) => string;
    sublist: (startPosition: string) => string;
    sublistLength: (startPosition: string, length: string) => string;
    substringAfter: (match: string) => string;
    substringBefore: (match: string) => string;
    substringStartPosition: (startPosition: string) => string;
    substringLength: (startPosition: string, length: string) => string;
    sum: (list: string) => string;
    time: string;
    timeHour: (hour: string, minute: string, second: string) => string;
    timeOffset: (hour: string, minute: string, second: string, offset: string) => string;
    today: string;
    union: (list: string) => string;
    upperCase: (stringValue: string) => string;
    weekOfYear: string;
    yearsAndMonthsDuration: string;
  };
}

export interface FeelInputComponentI18n extends FeelInputComponentDictionary, CommonI18n, ReferenceDictionary {}
