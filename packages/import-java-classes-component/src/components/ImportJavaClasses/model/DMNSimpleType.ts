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

export enum DMNSimpleType {
  NUMBER = "number",
  STRING = "string",
  BOOLEAN = "boolean",
  DURATION_DAYS_TIME = "days and time duration",
  DURATION_YEAR_MONTH = "years and months duration",
  TIME = "time",
  DATE_TIME = "date and time",
  ANY = "Any",
  DATE = "date",
  CONTEXT = "context",
  UNDEFINED = "<Undefined>",
}
/** It refers to "JavaBackedType" class determineTypeFromClass() method */
export const JAVA_TO_DMN_MAP = new Map([
  /** Number types */
  ["AtomicInteger", DMNSimpleType.NUMBER],
  ["AtomicLong", DMNSimpleType.NUMBER],
  ["BigDecimal", DMNSimpleType.NUMBER],
  ["BigInteger", DMNSimpleType.NUMBER],
  ["Byte", DMNSimpleType.NUMBER],
  ["byte", DMNSimpleType.NUMBER],
  ["Double", DMNSimpleType.NUMBER],
  ["double", DMNSimpleType.NUMBER],
  ["DoubleAccumulator", DMNSimpleType.NUMBER],
  ["DoubleAdder", DMNSimpleType.NUMBER],
  ["Float", DMNSimpleType.NUMBER],
  ["float", DMNSimpleType.NUMBER],
  ["Integer", DMNSimpleType.NUMBER],
  ["int", DMNSimpleType.NUMBER],
  ["Long", DMNSimpleType.NUMBER],
  ["long", DMNSimpleType.NUMBER],
  ["LongAccumulator", DMNSimpleType.NUMBER],
  ["LongAdder", DMNSimpleType.NUMBER],
  ["Number", DMNSimpleType.NUMBER],
  ["Short", DMNSimpleType.NUMBER],
  ["short", DMNSimpleType.NUMBER],
  ["Striped64", DMNSimpleType.NUMBER],
  /** String types */
  ["Character", DMNSimpleType.STRING],
  ["char", DMNSimpleType.STRING],
  ["String", DMNSimpleType.STRING],
  /** Date types */
  ["LocalDate", DMNSimpleType.DATE],
  /** Time types */
  ["LocalTime", DMNSimpleType.TIME],
  ["OffsetTime", DMNSimpleType.TIME],
  /** DateTime types */
  ["ZonedDateTime", DMNSimpleType.DATE_TIME],
  ["OffsetDateTime", DMNSimpleType.DATE_TIME],
  ["LocalDateTime", DMNSimpleType.DATE_TIME],
  ["Date", DMNSimpleType.DATE_TIME],
  /** Duration types */
  ["Duration", DMNSimpleType.DURATION_DAYS_TIME],
  ["ChronoPeriod", DMNSimpleType.DURATION_DAYS_TIME],
  /** Boolean */
  ["Boolean", DMNSimpleType.BOOLEAN],
  ["boolean", DMNSimpleType.BOOLEAN],
  /** Context */
  ["Map", DMNSimpleType.CONTEXT],
  ["LinkedHashMap", DMNSimpleType.CONTEXT],
  ["HashMap", DMNSimpleType.CONTEXT],
  ["TreeMap", DMNSimpleType.CONTEXT],
  /** List */
  ["List", DMNSimpleType.ANY],
  ["ArrayList", DMNSimpleType.ANY],
  ["LinkedList", DMNSimpleType.ANY],
  /** Any */
  ["Class", DMNSimpleType.ANY],
  ["Object", DMNSimpleType.ANY],
]);
