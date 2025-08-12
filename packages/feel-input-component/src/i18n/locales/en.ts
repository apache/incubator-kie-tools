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
    absDescription: (value: string): string => `Returns the absolute value of ${value}`,
    afterPoint: (point1: string, point2: string): string => `Returns true when ${point1} is after ${point2}`,
    afterPointRange: (point: string, range: string): string => `Returns true when ${point} is after ${range}`,
    afterRangePoint: (range: string, point: string): string => `Returns true when ${range} is after ${point}`,
    afterRange: (range1: string, range2: string): string => `Returns true when ${range1} is after ${range2}`,
    allTrue: (list: string): string => `Returns true if all elements in the ${list} are true.`,
    anyTrue: (list: string): string =>
      `Returns true if any ${list} item is true, else false if empty or all ${list} items are false, else null`,
    append: "Returns new list with items appended",
    beforePoint: (point1: string, point2: string): string => `Returns true when ${point1} is before ${point2}`,
    beforePointRange: (point: string, range: string): string => `Returns true when ${point} is before ${range}`,
    beforeRangePoint: (range: string, point: string): string => `Returns true when a ${range} is before ${point}`,
    beforeRange: (range1: string, range2: string): string => `Returns true when ${range1} is before ${range2}`,
    ceiling: (value: string): string =>
      `Returns ${value} with rounding mode ceiling. If ${value} is null the result is null.`,
    ceilingScale: (value: string, scale: string): string =>
      `Returns ${value} with given scale and rounding mode ceiling. If at least one of ${value} or ${scale} is null, the result is null. The ${scale} must be in the range [−6111..6176].`,
    coincides: (point1: string, point2: string): string => `Returns true when ${point1} coincides with ${point2}`,
    coincidesRange: (range1: string, range2: string): string => `Returns true when ${range1} coincides with ${range2}`,
    concatenate: "Returns a new list that is a concatenation of the arguments",
    contains: (value: string, match: string): string => `Does the ${value} contain the ${match}?`,
  },
};
