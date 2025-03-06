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

export const DATE_FUNCTIONS = "date_functions";

const _staticBlocks: Map<string, string> = new Map<string, string>();

_staticBlocks.set(
  DATE_FUNCTIONS,
  `
const parseDate = (date?: string): string => {
    if (!date) {
        return '';
    }
    const dateValue: Date = new Date(Date.parse(date));
    return dateValue.toISOString().slice(0, -14);
}

const parseTime = (date?: string): string => {
    if (!date) {
        return '';
    }
    const dateValue: Date = new Date(Date.parse(date));
    let isAm = true;
    let hours = dateValue.getHours();
    if (hours > 12) {
        hours %= 12;
        isAm = false;
    }
    let minutes = dateValue.getMinutes().toString();
    if (minutes.length == 1) {
        minutes = '0' + minutes;
    }
    return \`\${hours}:\${minutes} \${isAm ? 'AM' : 'PM'}\`;
}`
);

export const getStaticCodeBlock = (blockName: string): string | undefined => {
  return _staticBlocks.get(blockName);
};
