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
    datetTimeTimezone: (
      year: string,
      month: string,
      day: string,
      hour: string,
      minute: string,
      second: string,
      timezone: string
    ) => string;
    dayOfWeek: string;
    dateOfYear: string;
    decimal: (n: string, scale: string) => string;
    distinctValues: (list: string) => string;
    duration: (from: string) => string;
    during: (point: string, range: string) => string;
    duringRange: (range1: string, range2: string) => string;
    endsWith: (string: string, match: string) => string;
    even: (number: string) => string;
    exp: (number: string) => string;
    finishedBy: (range: string, point: string) => string;
    finishedByRange: (range1: string, range2: string) => string;
    finishes: (point: string, range: string) => string;
    finishesRange: (range1: string, range2: string) => string;
    flattenNestedLists: string;
    floor: (n: string) => string;
    floorScale: (n: string, scale: string) => string;
    getEntries: (m: string) => string;
    getValue: (m: string, key: string) => string;
    includes: (range: string, point: string) => string;
    includesRange: (range1: string, range2: string) => string;
    indexOf: (list: string, match: string) => string;
    insertBefore: (list: string, position: string, newItem: string) => string;
    is: string;
    listConstains: (list: string, element: string) => string;
    listReplace: (newItem: string, position: string) => string;
    listNewItem: (newItem: string, match: string, trueValue: string) => string;
    log: (number: string) => string;
    lowerCase: (stringValue: string) => string;
    matches: (input: string, pattern: string) => string;
    max: (list: string) => string;
    mean: (list: string) => string;
    median: (list: string) => string;
    meets: (range1: string, range2: string) => string;
    metBy: (range1: string, range2: string) => string;
    min: (list: string) => string;
    mode: (list: string) => string;
    modulo: (dividend: string, divisor: string) => string;
    monthOfYear: string;
    nnAll: (list: string) => string;
    nnAny: (list: string) => string;
    nnCount: (list: string) => string;
    nnMax: (list: string) => string;
    nnMean: string;
    nnMedian: (list: string) => string;
    nnMin: (list: string) => string;
    nnMode: (list: string) => string;
    nnStddev: (list: string) => string;
    nnSum: (list: string) => string;
    not: (negand: string) => string;
    now: string;
    numberFrom: (from: string) => string;
    odd: (number: string) => string;
    overlapsAfter: (range1: string, range2: string) => string;
    overlapsBefore: (range1: string, range2: string) => string;
    overlaps: (range1: string, range2: string) => string;
    product: (list: string) => string;
    rangeFrom: (stringValue: string, from: string) => string;
    remove: (position: string) => string;
    replace: string;
    reverse: (list: string) => string;
    roundDown: (n: string, scale: string) => string;
    roundDownN: (n: string) => string;
    roundHalfDown: (n: string, scale: string) => string;
    roundHalfDownN: (n: string) => string;
    roundHalfUp: (n: string, scale: string) => string;
    roundHalfUpN: (n: string) => string;
    roundUp: (n: string, scale: string) => string;
    roundUpN: (n: string) => string;
    sort: (numberValue: string, stringValue: string) => string;
    sortPrecedes: string;
    split: (stringValue: string, delimiter: string) => string;
    sqrt: (numberValue: string) => string;
    startedBy: (range: string, point: string) => string;
    startedByRange: (range1: string, range2: string) => string;
    startsWith: (stringValue: string, match: string) => string;
    starts: (point: string, range: string) => string;
    startsRange: (range1: string, range2: string) => string;
    stddev: (list: string) => string;
    stringLength: (stringValue: string) => string;
    stringFrom: string;
    stringJoin: (list: string) => string;
    stringJoinDelimiter: (list: string, delimiter: string) => string;
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
    union: string;
    upperCase: (stringValue: string) => string;
    weekOfYear: string;
    yearsAndMonthsDuration: string;
  };
}

export interface FeelInputComponentI18n extends FeelInputComponentDictionary, CommonI18n {}
