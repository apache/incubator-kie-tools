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

import { en as en_common } from "@kie-tools/i18n-common-dictionary";
import { FeelInputComponentI18n } from "../FeelInputComponentI18n";

export const en: FeelInputComponentI18n = {
  ...en_common,
  functionDescription: {
    absDescription: (value: string): string => `Returns the absolute value of \`${value}\``,
    afterPoint: (point1: string, point2: string): string => `Returns true when \`${point1}\` is after \`${point2}\``,
    afterPointRange: (point: string, range: string): string => `Returns true when \`${point}\` is after \`${range}\``,
    afterRangePoint: (range: string, point: string): string => `Returns true when \`${range}\` is after \`${point}\``,
    afterRange: (range1: string, range2: string): string => `Returns true when \`${range1}\` is after \`${range2}\``,
    allTrue: (list: string): string => `Returns true if all elements in the \`${list}\` are true.`,
    anyTrue: (list: string): string =>
      `Returns true if any \`${list}\` item is true, else false if empty or all \`${list}\` items are false, else null`,
    append: "Returns new list with items appended",
    beforePoint: (point1: string, point2: string): string => `Returns true when \`${point1}\` is before \`${point2}\``,
    beforePointRange: (point: string, range: string): string => `Returns true when \`${point}\` is before \`${range}\``,
    beforeRangePoint: (range: string, point: string): string =>
      `Returns true when a \`${range}\` is before \`${point}\``,
    beforeRange: (range1: string, range2: string): string => `Returns true when \`${range1} is before \`${range2}\``,
    ceiling: (value: string): string =>
      `Returns \`${value} with rounding mode ceiling. If \`${value}\` is null the result is null.`,
    ceilingScale: (value: string, scale: string): string =>
      `Returns \`${value} with given scale and rounding mode ceiling. If at least one of \`${value}\` or \`${scale}\` is null, the result is null. The \`${scale}\` must be in the range [−6111..6176].`,
    coincides: (point1: string, point2: string): string =>
      `Returns true when \`${point1}\` coincides with \`${point2}\``,
    coincidesRange: (range1: string, range2: string): string =>
      `Returns true when \`${range1}\` coincides with \`${range2}\``,
    concatenate: "Returns a new list that is a concatenation of the arguments",
    contains: (value: string, match: string): string => `Does the \`${value} contain the \`${match}\`?`,
    contextKeyValue: (context: string, key: string, value: string): string =>
      `Returns a new \`${context}\` that includes all specified entries. If a \`${context}\` item contains additional entries beyond the required \`${key}\` and \`${value}\` entries, the additional entries are ignored. If a \`${context}\` item is missing the required \`${key}\` and \`${value}\` entries, the final result is null.`,
    contextMerge: (context: string, contexts: string): string =>
      `Returns a new \`${context}\` that includes all entries from the given \`${contexts}\`; if some of the keys are equal, the entries are overridden. The entries are overridden in the same order as specified by the supplied parameter, with new entries added as the last entry in the new context.`,
    context: (context: string): string =>
      `Returns a new \`${context}\` that includes the new entry, or overrides the existing value if an entry for the same key already exists in the supplied \`${context}\` parameter. A new entry is added as the last entry of the new context. If overriding an existing entry, the order of the keys maintains the same order as in the original context.`,
    contextPut: (context: string, contextPut: string): string =>
      `Returns the composite of nested invocations to \`${contextPut}\` for each item in keys hierarchy in \`${context}\`.`,
    count: (list: string): string => `Returns size of \`${list}\`, or zero if \`${list}\` is empty`,
    date: (from: string): string => `convert \`${from}\` to a date`,
    dateyear: (year: string, month: string, day: string) =>
      `Creates a date from \`${year}\`, \`${month}\`, \`${day}\` component values`,
    dateTimeFrom: (from: string) => `convert \`${from}\` to a date and time`,
    dateTime: (date: string, time: string) =>
      `Creates a date time from the given \`${date}\` (ignoring any time component) and the given \`${time}\``,
    datetimezone: (date: string, time: string) =>
      `Creates a date time from the given \`${date}\`, \`${time}\` and timezone`,
    dateYearSecond: (year: string, month: string, day: string, hour: string, minute: string, second: string) =>
      `Creates a date time from the given \`${year}\`, \`${month}\`, \`${day}\`, \`${hour}\`, \`${minute}\`, and \`${second}\`.`,
    datetYearOffset: (
      year: string,
      month: string,
      day: string,
      hour: string,
      minute: string,
      second: string,
      offset: string
    ): string =>
      `Creates a date time from the given \`${year}\`, \`${month}\`, \`${day}\`, \`${hour}\`, \`${minute}\`, \`${second}\` and \`${offset}\``,
    datetTimeTimezone: (
      year: string,
      month: string,
      day: string,
      hour: string,
      minute: string,
      second: string,
      timezone: string
    ) =>
      `Creates a date time from the given \`${year}\`, \`${month}\`, \`${day}\`, \`${hour}\`, \`${minute}\`, \`${second}\` and \`${timezone}\``,
    dayOfWeek: `Returns the day of the week according to the Gregorian calendar enumeration: “Monday”, “Tuesday”, “Wednesday”, “Thursday”, “Friday”, “Saturday”, “Sunday”`,
    dateOfYear: `Returns the Gregorian number of the day within the year`,
    decimal: (n: string, scale: string) =>
      `Returns \`${n}\` with given \`${scale}\`. The \`${scale}\` must be in the range [−6111..6176].`,
    distinctValues: (list: string) => `Returns \`${list}\` without duplicates`,
    duration: (from: string) => `Converts \`${from}\` to a days and time or years and months duration`,
    during: (point: string, range: string) => `Returns true when \`${point}\` is during \`${range}\``,
    duringRange: (range1: string, range2: string) => `Returns true when a \`${range1}\` is during \`${range2}\``,
    endsWith: (string: string, match: string) => `Does the \`${string}\` end with the \`${match}\`?`,
    even: (number: string) => `Returns true if \`${number}\` is even, false if it is odd`,
    exp: (number: string) => `Returns the Euler’s number e raised to the power of \`${number}\`.`,
    finishedBy: (range: string, point: string) => `Returns true when \`${range}\` is finished by \`${point}\``,
    finishedByRange: (range1: string, range2: string) => `Returns true when \`${range1}\` is finished by \`${range2}\``,
    finishes: (point: string, range: string) => `Returns true when \`${point}\` finishes \`${range}\``,
    finishesRange: (range1: string, range2: string) => `Returns true when \`${range1}\` finishes \`${range2}\``,
    flattenNestedLists: "Flatten nested lists",
    floor: (n: string) => `Returns \`${n}\` with rounding mode flooring. If \`${n}\` is null the result is null.`,
    floorScale: (n: string, scale: string) =>
      `Returns \`${n}\` with given scale and rounding mode flooring. If at least one of \`${n}\` or scale is null, the result is null. The \`${scale}\` must be in the range [−6111..6176].`,
    getEntries: (m: string) => `Produces a list of key,value pairs from a context \`${m}\``,
    getValue: (m: string, key: string) => `Select the value of the entry named \`${key}\` from context \`${m}\``,
    includes: (range: string, point: string) => `Returns true when \`${range}\` includes \`${point}\``,
    includesRange: (range1: string, range2: string) => `Returns true when \`${range1}\` includes \`${range2}\``,
    indexOf: (list: string, match: string) => `Returns ascending list of \`${list}\` positions containing \`${match}\``,
    insertBefore: (list: string, position: string, newItem: string) =>
      `Return new list with \`${newItem}\` inserted at \`${position}\``,
    is: `Returns true if both values are the same element in the FEEL semantic domain`,
    listConstains: (list: string, element: string) => `Does the \`${list}\` contain the \`${element}\`?`,
    listReplace: (newItem: string, position: string) =>
      `Returns new list with \`${newItem}\` replaced at \`${position}\`.`,
    listNewItem: (newItem: string, match: string, trueValue: string) =>
      `Returns new list with \`${newItem}\` replaced at all positions where the \`${match}\` function returned \`${trueValue}\``,
    log: (number: string) => `Returns the natural logarithm (base e) of the \`${number}\` parameter`,
    lowerCase: (stringValue: string) => `Returns lowercased \`${stringValue}\``,
    matches: (input: string, pattern: string) => `Does the \`${input}\` match the regexp \`${pattern}\`?`,
    max: (list: string) => `Returns maximum item, or null if \`${list}\` is empty`,
    mean: (list: string) => `Returns arithmetic mean (average) of \`${list}\` of numbers`,
    median: (list: string) =>
      `Returns the median element of the \`${list}\` of numbers. I.e., after sorting the \`${list}\`, if the \`${list}\` has an odd number of elements, it returns the middle element. If the \`${list}\` has an even number of elements, returns the average of the two middle elements. If the \`${list}\` is empty, returns null`,
    meets: (range1: string, range2: string) => `Returns true when \`${range1}\` meets \`${range2}\``,
    metBy: (range1: string, range2: string) => `Returns true when \`${range1}\` is met \`${range2}\``,
    min: (list: string) => `Returns minimum item, or null if \`${list}\` is empty`,
    mode: (list: string) =>
      `Returns the mode of the numbers in the \`${list}\`. If multiple elements are returned, the numbers are sorted in ascending order.`,
    modulo: (dividend: string, divisor: string) =>
      `Returns the remainder of the division of \`${dividend}\` by \`${divisor}\``,
    monthOfYear: "Returns the month of the year",
    nnAll: (list: string) => `Returns true if all elements in the \`${list}\` are true. null values are ignored`,
    nnAny: (list: string) => `Returns true if any element in the \`${list}\` is true. null values are ignored`,
    nnCount: (list: string) =>
      `Returns size of \`${list}\`, or zero if \`${list}\` is empty. null values are not counted`,
    nnMax: (list: string) => `Returns maximum item, or null if \`${list}\` is empty. null values are ignored`,
    nnMean: `Returns arithmetic mean (average) of numbers. null values are ignored`,
    nnMedian: (list: string) =>
      `Returns the median element of the \`${list}\` of numbers. null values are ignored. I.e., after sorting the \`${list}\`, if the \`${list}\` has an odd number of elements, it returns the middle element. If the \`${list}\` has an even number of elements, returns the average of the two middle elements. If the \`${list}\` is empty, returns null`,
    nnMin: (list: string) => `Returns minimum item, or null if \`${list}\` is empty. null values are ignored`,
    nnMode: (list: string) =>
      `Returns the mode of the numbers in the \`${list}\`. null values are ignored. If multiple elements are returned, the numbers are sorted in ascending order`,
    nnStddev: (list: string) =>
      `Returns the standard deviation of the numbers in the \`${list}\`. null values are ignored.`,
    nnSum: (list: string) => `Returns the sum of the numbers in the \`${list}\`. null values are ignored.`,
    not: (negand: string) => `Performs the logical negation of the \`${negand}\` operand`,
    now: "Returns the current date and time.",
    numberFrom: (from: string) => `Converts \`${from}\` to a number using the specified separators.`,
    odd: (number: string) => `Returns true if the specified \`${number}\` is odd.`,
    overlapsAfter: (range1: string, range2: string) => `Returns true when \`${range1}\` overlaps after \`${range2}\``,
    overlapsBefore: (range1: string, range2: string) => `Returns true when \`${range1}\` overlaps before \`${range2}\``,
    overlaps: (range1: string, range2: string) => `Returns true when \`${range1}\` overlaps \`${range2}\``,
    product: (list: string) => `Returns the product of the numbers in the \`${list}\``,
    rangeFrom: (stringValue: string, from: string) => `Convert from a range \`${stringValue}\` to a \`${from}\`.`,
    remove: (position: string) =>
      `Creates a list with the removed element excluded from the specified \`${position}\`.`,
    replace: "Calculates the regular expression replacement",
    reverse: (list: string) => `Returns a reversed \`${list}\``,
    roundDown: (n: string, scale: string) =>
      `Returns \`${n}\` with given \`${scale}\` and rounding mode round down. If at least one of \`${n}\` or \`${scale}\` is null, the result is null. The \`${scale}\` must be in the range [−6111..6176].`,
    roundDownN: (n: string) =>
      `Returns \`${n}\` with rounding mode round down. If \`${n}\` is null, the result is null.`,
    roundHalfDown: (n: string, scale: string) =>
      `Returns \`${n}\` with given \`${scale}\` and rounding mode round half down. If at least one of \`${n}\` or \`${scale}\` is null, the result is null. The \`${scale}\` must be in the range [−6111..6176].`,
    roundHalfDownN: (n: string) =>
      `Returns \`${n}\` with rounding mode round half down. If \`${n}\` is null, the result is null.`,
    roundHalfUp: (n: string, scale: string) =>
      `Returns \`${n}\` with given \`${scale}\` and rounding mode round half up. If at least one of \`${n}\` or \`${scale}\` is null, the result is null. The \`${scale}\` must be in the range [−6111..6176].`,
    roundHalfUpN: (n: string) =>
      `Returns \`${n}\` with rounding mode round half up. If  \`${n}\` is null, the result is null.`,
    roundUp: (n: string, scale: string) =>
      `Returns \`${n}\` with given \`${scale}\` and rounding mode round up. If at least one of \`${n}\` or \`${scale}\` is null, the result is null. The \`${scale}\` must be in the range [−6111..6176].`,
    roundUpN: (n: string) => `Returns \`${n}\` with rounding mode round up. If \`${n}\` is null, the result is null.`,
    sort: (numberValue: string, stringValue: string) =>
      `Returns a list of the same elements but ordered according a default sorting, if the elements are comparable (eg. \`${numberValue}\` or \`${stringValue}\`)`,
    sortPrecedes: "Returns a list of the same elements but ordered according to the sorting function",
    split: (stringValue: string, delimiter: string) =>
      `Returns a list of the original \`${stringValue}\` and splits it at the \`${delimiter}\` regular expression pattern`,
    sqrt: (numberValue: string) => `Returns the square root of the specified \`${numberValue}\`.`,
    startedBy: (range: string, point: string) => `Returns true when a \`${range}\` is started by a \`${point}\``,
    startedByRange: (range1: string, range2: string) => `Returns true when \`${range1}\` is started by \`${range2}\``,
    startsWith: (stringValue: string, match: string) => `Does the \`${stringValue}\` start with the \`${match}\`?`,
    starts: (point: string, range: string) => `Returns true when \`${point}\` starts a \`${range}\``,
    startsRange: (range1: string, range2: string) => `Returns true when a \`${range1}\` starts a \`${range2}\``,
    stddev: (list: string) => `Returns the standard deviation of the numbers in the \`${list}\``,
    stringLength: (stringValue: string) => `Calculates the length of the specified \`${stringValue}\`.`,
    stringFrom: "Provides a string representation of the specified parameter",
    stringJoin: (list: string) =>
      `Returns a string which is composed by joining all the string elements from the \`${list}\` parameter. Null elements in the \`${list}\` parameter are ignored. If \`${list}\` is empty, the result is the empty string.`,
    stringJoinDelimiter: (list: string, delimiter: string) =>
      `Returns a string which is composed by joining all the string elements from the \`${list}\` parameter, separated by the \`${delimiter}\`. The \`${delimiter}\` can be an empty string. Null elements in the \`${list}\` parameter are ignored. If \`${list}\` is empty, the result is the empty string. If \`${delimiter}\` is null, the string elements are joined without a separator.`,
    sublist: (startPosition: string) => `Returns the sublist from the \`${startPosition}\``,
    sublistLength: (startPosition: string, length: string) =>
      `Returns the sublist from the \`${startPosition}\` for the specified \`${length}\``,
    substringAfter: (match: string) => `Calculates the substring after the \`${match}\``,
    substringBefore: (match: string) => `Calculates the substring before the \`${match}\``,
    substringStartPosition: (startPosition: string) =>
      `Returns the substring from the \`${startPosition}\`. The first character is at position value 1`,
    substringLength: (startPosition: string, length: string) =>
      `Returns the substring from the \`${startPosition}\` for the specified \`${length}\`. The first character is at position value 1`,
    sum: (list: string) => `Returns the sum of the numbers in the \`${list}\``,
    time: "Produces a time from the specified parameter",
    timeHour: (hour: string, minute: string, second: string) =>
      `Creates a time from the given \`${hour}\`, \`${minute}\`, and \`${second}\`.`,
    timeOffset: (hour: string, minute: string, second: string, offset: string) =>
      `Creates a time from the given \`${hour}\`, \`${minute}\`, \`${second}\` and \`${offset}\``,
    today: "Returns the current date",
    union: "Returns a list of all the elements from multiple lists and excludes duplicates",
    upperCase: (stringValue: string) => `Produces an uppercase version of the specified \`${stringValue}\`.`,
    weekOfYear: "Returns the Gregorian week of the year as defined by ISO 8601",
    yearsAndMonthsDuration: "Calculates the years and months duration between the two specified parameters.",
  },
};
