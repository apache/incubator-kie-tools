/**
 * @license
 * Copyright 2015 TAKAHASHI kazunari
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
"use strict";

/**
 * @fileoverview Externs for moment.js 2.10.0
 * @externs
 */

/**
 * @interface
 * @author takahashikzn
 */
function Moment() {}

/**
 * @typedef {{seconds:?number, minutes:?number, hours:?number, weeks:?number, months:?number, years:?number}}
 */
Moment.DateRecord;

/**
 * @typedef {(Moment|string|number|Date|Array.<!number>|Moment.DateRecord)}
 */
Moment.MomentLike;

/**
 * @interface
 * @author takahashikzn
 */
Moment.Duration = function () {};

/**
 * @nosideeffects
 * @type {(function():!Moment|function(!Moment.MomentLike):!Moment|function(!string, !(string|Array<!string>)=):!Moment)}
 */
function moment() {}

/**
 * @since 1.2.0
 * @type {function():!Moment}
 */
Moment.prototype.clone = function () {};

/**
 * @since 1.6.0
 * @type {(function(number, string):!Moment.Duration|function(number):!Moment.Duration|function(Object):!Moment.Duration|function(string):!Moment.Duration)}
 */
Moment.prototype.duration = function () {};

/**
 * @since 1.6.0
 * @param {!number=} a
 * @return {!Moment}
 */
Moment.prototype.unix = function (a) {};

/**
 * @since 1.5.0
 * @type {(function():!Moment|function(!Moment.MomentLike):!Moment|function(!string, !string=, !string=):!Moment|function(!string, !Array<!string>):!Moment)}
 */
Moment.prototype.utc = function () {};

/**
 * @since 2.9.0
 * @type {function():!number|function(!(number|string)):!Moment}
 */
Moment.prototype.utcOffset = function () {};

/**
 * @since 2.3.0
 * @param {!string} a
 * @return {!Moment}
 */
Moment.prototype.parseZone = function (a) {};

/**
 * @nosideeffects
 * @since 1.7.0
 * @return {!boolean}
 */
Moment.prototype.isValid = function () {};

/**
 * @since 1.3.0
 * @type {(function():!number|function(!number):!Moment)}
 */
Moment.prototype.millisecond = function () {};

/**
 * @since 1.3.0
 * @type {(function():!number|function(!number):!Moment)}
 */
Moment.prototype.milliseconds = function () {};

/**
 * @since 1.0.0
 * @type {(function():!number|function(!number):!Moment)}
 */
Moment.prototype.second = function () {};

/**
 * @since 1.0.0
 * @type {(function():!number|function(!number):!Moment)}
 */
Moment.prototype.seconds = function () {};

/**
 * @since 1.0.0
 * @type {(function():!number|function(!number):!Moment)}
 */
Moment.prototype.minute = function () {};

/**
 * @since 1.0.0
 * @type {(function():!number|function(!number):!Moment)}
 */
Moment.prototype.minutes = function () {};

/**
 * @since 1.0.0
 * @type {(function():!number|function(!number):!Moment)}
 */
Moment.prototype.hour = function () {};

/**
 * @since 1.0.0
 * @type {(function():!number|function(!number):!Moment)}
 */
Moment.prototype.hours = function () {};

/**
 * @since 1.0.0
 * @type {(function():!number|function(!number):!Moment)}
 */
Moment.prototype.date = function () {};

/**
 * @since 1.0.0
 * @type {(function():!number|function(!number):!Moment)}
 */
Moment.prototype.dates = function () {};

/**
 * @since 1.3.0
 * @type {(function():!number|function(!(number|string)):!Moment)}
 */
Moment.prototype.day = function () {};

/**
 * @since 1.3.0
 * @type {(function():!number|function(!number):!Moment)}
 */
Moment.prototype.days = function () {};

/**
 * @since 2.1.0
 * @type {(function():!number|function(!number):!Moment)}
 */
Moment.prototype.weekday = function () {};

/**
 * @since 2.1.0
 * @type {(function():!number|function(!number):!Moment)}
 */
Moment.prototype.isoWeekday = function () {};

/**
 * @since 2.0.0
 * @type {(function():!number|function(!number):!Moment)}
 */
Moment.prototype.dayOfYear = function () {};

/**
 * @since 2.0.0
 * @type {(function():!number|function(!number):!Moment)}
 */
Moment.prototype.week = function () {};

/**
 * @since 2.0.0
 * @type {(function():!number|function(!number):!Moment)}
 */
Moment.prototype.weeks = function () {};

/**
 * @since 2.0.0
 * @type {(function():!number|function(!number):!Moment)}
 */
Moment.prototype.isoWeek = function () {};

/**
 * @since 2.0.0
 * @type {(function():!number|function(!number):!Moment)}
 */
Moment.prototype.isoWeeks = function () {};

/**
 * @since 1.0.0
 * @type {(function():!number|function(!(number|string)):!Moment)}
 */
Moment.prototype.month = function () {};

/**
 * @since 1.0.0
 * @type {(function():!number|function(!(number|string)):!Moment)}
 */
Moment.prototype.months = function () {};

/**
 * @since 2.6.0
 * @type {(function():!number|function(!number):!Moment)}
 */
Moment.prototype.quarter = function () {};

/**
 * @since 1.0.0
 * @type {(function():!number|function(!number):!Moment)}
 */
Moment.prototype.year = function () {};

/**
 * @since 1.0.0
 * @type {(function():!number|function(!number):!Moment)}
 */
Moment.prototype.years = function () {};

/**
 * @since 2.1.0
 * @type {(function():!number|function(!number):!Moment)}
 */
Moment.prototype.weekYear = function () {};

/**
 * @since 2.1.0
 * @type {(function():!number|function(!number):!Moment)}
 */
Moment.prototype.isoWeekYear = function () {};

/**
 * @since 2.6.0
 * @return {!number}
 */
Moment.prototype.weeksInYear = function () {};

/**
 * @since 2.6.0
 * @return {!number}
 */
Moment.prototype.isoWeeksInYear = function () {};

/**
 * @since 2.2.1
 * @param {!string} unit
 * @return {!number}
 */
Moment.prototype.get = function (unit) {};

/**
 * @since 2.2.1
 * @param {!string} unit
 * @param {!number} value
 * @return {!Moment}
 */
Moment.prototype.set = function (unit, value) {};

/**
 * @since 1.0.0
 * @type {(function(!string, !number):!Moment|function(!number, !string):!Moment|function(!Moment.Duration):!Moment|function(!Moment.DateRecord):!Moment)}
 */
Moment.prototype.add = function () {};

/**
 * @nosideeffects
 * @since 1.0.0
 * @type {(function(!string, !number):!Moment|function(!number, !string):!Moment|function(!Moment.Duration):!Moment|function(!Moment.DateRecord):!Moment)}
 */
Moment.prototype.subtract = function () {};

/**
 * @nosideeffects
 * @since 1.7.0
 * @param {!string} a
 * @return {!Moment}
 */
Moment.prototype.startOf = function (a) {};

/**
 * @nosideeffects
 * @since 1.7.0
 * @param {!string} a
 * @return {!Moment}
 */
Moment.prototype.endOf = function (a) {};

/**
 * @nosideeffects
 * @since 2.1.0
 * @param {function(!Moment.MomentLike):!Moment|function(...Moment):!Moment} a
 * @return {!Moment}
 */
Moment.prototype.max = function (a) {};

/**
 * @nosideeffects
 * @since 2.1.0
 * @param {function(!Moment.MomentLike):!Moment|function(...Moment):!Moment} a
 * @return {!Moment}
 */
Moment.prototype.min = function (a) {};

/**
 * @since 1.5.0
 * @return {!Moment}
 */
Moment.prototype.local = function () {};

/**
 * @nosideeffects
 * @since 1.0.0
 * @param {!string=} a
 * @return {!string}
 */
Moment.prototype.format = function (a) {};

/**
 * @nosideeffects
 * @since 1.0.0
 * @param {!boolean=} a
 * @return {!string}
 */
Moment.prototype.fromNow = function (a) {};

/**
 * @nosideeffects
 * @since 1.0.0
 * @param {!Moment.MomentLike} a
 * @param {!boolean=} b
 * @return {!string}
 */
Moment.prototype.from = function (a, b) {};

/**
 * @nosideeffects
 * @since 2.10.0
 * @param {!boolean=} a
 * @return {!string}
 */
Moment.prototype.toNow = function (a) {};

/**
 * @nosideeffects
 * @since 2.10.0
 * @param {!Moment.MomentLike} a
 * @param {!boolean=} b
 * @return {!string}
 */
Moment.prototype.to = function (a, b) {};

/**
 * @nosideeffects
 * @since 1.3.0
 * @return {!string}
 */
Moment.prototype.calendar = function () {};

/**
 * @nosideeffects
 * @since 1.0.0
 * @param {!Moment.MomentLike} a
 * @param {!string=} b
 * @param {!boolean=} c
 * @return {!number}
 */
Moment.prototype.diff = function (a, b, c) {};

/**
 * @nosideeffects
 * @since 1.0.0
 * @return {!number}
 */
Moment.prototype.valueOf = function () {};

/**
 * @deprecated
 * @since 1.2.0
 * @type {(function():!number|function(!(number|string)):!Moment)}
 */
Moment.prototype.zone = function () {};

/**
 * @nosideeffects
 * @since 1.5.0
 * @return {!number}
 */
Moment.prototype.daysInMonth = function () {};

/**
 * @nosideeffects
 * @since 1.0.0
 * @return {!Date}
 */
Moment.prototype.toDate = function () {};

/**
 * @nosideeffects
 * @since 2.0.0
 * @return {!Moment.DateRecord}
 */
Moment.prototype.toJSON = function () {};

/**
 * @nosideeffects
 * @since 2.1.0
 * @return {!string}
 */
Moment.prototype.toISOString = function () {};

/**
 * @nosideeffects
 * @since 2.10.0
 * @return {!Moment.DateRecord}
 */
Moment.prototype.toObject = function () {};

/**
 * @nosideeffects
 * @since 2.0.0
 * @param {!Moment.MomentLike} a
 * @param {!string=} b
 * @return {!boolean}
 */
Moment.prototype.isBefore = function (a, b) {};

/**
 * @nosideeffects
 * @since 2.0.0
 * @param {!Moment.MomentLike} a
 * @param {!string=} b
 * @return {!boolean}
 */
Moment.prototype.isSame = function (a, b) {};

/**
 * @nosideeffects
 * @since 2.0.0
 * @param {!Moment.MomentLike} a
 * @param {!string=} b
 * @return {!boolean}
 */
Moment.prototype.isAfter = function (a, b) {};

/**
 * @nosideeffects
 * @since 2.10.7
 * @param {!Moment.MomentLike} a
 * @param {string=} b
 * @return {boolean}
 */
Moment.prototype.isSameOrAfter = function (a, b) {};

/**
 * @nosideeffects
 * @since 2.10.7
 * @param {!Moment.MomentLike} a
 * @param {string=} b
 * @return {boolean}
 */
Moment.prototype.isSameOrBefore = function (a, b) {};

/**
 * @nosideeffects
 * @since 2.9.0
 * @param {!Moment.MomentLike} a
 * @param {!Moment.MomentLike} b
 * @param {!string=} c
 * @return {!boolean}
 */
Moment.prototype.isBetween = function (a, b, c) {};

/**
 * @nosideeffects
 * @since 1.0.0
 * @return {!boolean}
 */
Moment.prototype.isLearYear = function () {};

/**
 * @nosideeffects
 * @since 1.0.0
 * @return {!boolean}
 */
Moment.prototype.isDST = function () {};

/**
 * @nosideeffects
 * @since 2.3.0
 * @return {!boolean}
 */
Moment.prototype.isDSTShifted = function () {};

/**
 * @nosideeffects
 * @since 1.5.0
 * @param {?} a
 * @return {!boolean}
 */
Moment.prototype.isMoment = function (a) {};

/**
 * @nosideeffects
 * @since 2.9.0
 * @param {?} a
 * @return {!boolean}
 */
Moment.prototype.isDate = function (a) {};

/**
 * @nosideeffects
 * @since 1.6.0
 * @type {(function(!number, !string=):!Moment.Duration|function(!Moment.DateRecord):!Moment.Duration)}
 */
Moment.prototype.duration = function () {};

/**
 * @nosideeffects
 * @since 1.6.0
 * @param {!boolean=} a
 * @return {!string}
 */
Moment.Duration.prototype.humanize = function (a) {};

/**
 * @nosideeffects
 * @since 1.6.0
 * @return {!number}
 */
Moment.Duration.prototype.milliseconds = function () {};

/**
 * @nosideeffects
 * @since 1.6.0
 * @return {!number}
 */
Moment.Duration.prototype.asMilliseconds = function () {};

/**
 * @nosideeffects
 * @since 1.6.0
 * @return {!number}
 */
Moment.Duration.prototype.seconds = function () {};

/**
 * @nosideeffects
 * @since 1.6.0
 * @return {!number}
 */
Moment.Duration.prototype.asSeconds = function () {};

/**
 * @nosideeffects
 * @since 1.6.0
 * @return {!number}
 */
Moment.Duration.prototype.minutes = function () {};

/**
 * @nosideeffects
 * @since 1.6.0
 * @return {!number}
 */
Moment.Duration.prototype.asMinutes = function () {};

/**
 * @nosideeffects
 * @since 1.6.0
 * @return {!number}
 */
Moment.Duration.prototype.hours = function () {};

/**
 * @nosideeffects
 * @since 1.6.0
 * @return {!number}
 */
Moment.Duration.prototype.asHours = function () {};

/**
 * @nosideeffects
 * @since 1.6.0
 * @return {!number}
 */
Moment.Duration.prototype.days = function () {};

/**
 * @nosideeffects
 * @since 1.6.0
 * @return {!number}
 */
Moment.Duration.prototype.asDays = function () {};

/**
 * @nosideeffects
 * @since 1.6.0
 * @return {!number}
 */
Moment.Duration.prototype.months = function () {};

/**
 * @nosideeffects
 * @since 1.6.0
 * @return {!number}
 */
Moment.Duration.prototype.asMonths = function () {};

/**
 * @nosideeffects
 * @since 1.6.0
 * @return {!number}
 */
Moment.Duration.prototype.years = function () {};

/**
 * @nosideeffects
 * @since 1.6.0
 * @return {!number}
 */
Moment.Duration.prototype.asYears = function () {};

/**
 * @since 2.1.0
 * @type {(function(!number, !string):!Moment.Duration|function(!number):!Moment.Duration|function(!Moment.Duration):!Moment.Duration|function(Object):!Moment.Duration)}
 */
Moment.Duration.prototype.add = function () {};

/**
 * @since 2.1.0
 * @type {(function(!number, !string):!Moment.Duration|function(!number):!Moment.Duration|function(!Moment.Duration):!Moment.Duration|function(Object):!Moment.Duration)}
 */
Moment.Duration.prototype.subtract = function () {};

/**
 * @since 2.1.0
 * @param {!string} a
 * @return {!number}
 */
Moment.Duration.prototype.as = function (a) {};

/**
 * @nosideeffects
 * @since 2.1.0
 * @param {!string} unit
 * @return {!number}
 */
Moment.Duration.prototype.get = function (unit) {};

/**
 * @nosideeffects
 * @since 2.9.0
 * @return {!string}
 */
Moment.Duration.prototype.toJSON = function () {};

/* === Utilities === */

/**
 * @since 2.3.0
 * @param {!string} unit
 * @return {!string}
 */
Moment.normalizeUnits = function (unit) {};

/**
 * @since 2.3.0
 * @param {?} a
 * @return {!Moment}
 */
Moment.invalid = function (a) {};

/* === Locale === */

/**
 * @since 2.8.0
 * @interface
 */
Moment.Locale = function () {};

/**
 * @nosideeffects
 * @since 2.8.0
 * @param {!string=} a
 * @return {!Moment.Locale}
 */
Moment.prototype.localeData = function (a) {};

/**
 * @nosideeffects
 * @since 2.8.0
 * @param {!Moment} a
 * @return {!string}
 */
Moment.Locale.prototype.months = function (a) {};

/**
 * @nosideeffects
 * @since 2.8.0
 * @param {!Moment} a
 * @return {!string}
 */
Moment.Locale.prototype.monthsShort = function (a) {};

/**
 * @nosideeffects
 * @since 2.8.0
 * @param {!string} a
 * @return {!number}
 */
Moment.Locale.prototype.monthParse = function (a) {};

/**
 * @nosideeffects
 * @since 2.8.0
 * @param {!Moment} a
 * @return {!string}
 */
Moment.Locale.prototype.weekdays = function (a) {};

/**
 * @nosideeffects
 * @since 2.8.0
 * @param {!Moment} a
 * @return {!string}
 */
Moment.Locale.prototype.weekdaysShort = function (a) {};

/**
 * @nosideeffects
 * @since 2.8.0
 * @param {!Moment} a
 * @return {!string}
 */
Moment.Locale.prototype.weekdaysMin = function (a) {};

/**
 * @nosideeffects
 * @since 2.8.0
 * @param {!string} a
 * @return {!number}
 */
Moment.Locale.prototype.weekdaysParse = function (a) {};

/**
 * @nosideeffects
 * @since 2.8.0
 * @param {!string} a
 * @return {!string}
 */
Moment.Locale.prototype.longDateFormat = function (a) {};

/**
 * @nosideeffects
 * @since 2.8.0
 * @param {!string} a
 * @return {!boolean}
 */
Moment.Locale.prototype.isPM = function (a) {};

/**
 * @nosideeffects
 * @since 2.8.0
 * @param {!number} a
 * @param {!number} b
 * @param {!boolean} c
 * @return {!string}
 */
Moment.Locale.prototype.meridiem = function (a, b, c) {};

/**
 * @nosideeffects
 * @since 2.8.0
 * @param {!string} a
 * @param {!Moment} b
 * @return {!string}
 */
Moment.Locale.prototype.calendar = function (a, b) {};

/**
 * @nosideeffects
 * @since 2.8.0
 * @param {!number} a
 * @param {!string} b
 * @param {!string} c
 * @param {!boolean} d
 * @return {!string}
 */
Moment.Locale.prototype.relativeTime = function (a, b, c, d) {};

/**
 * @nosideeffects
 * @since 2.8.0
 * @param {!number} a
 * @param {!string} b
 * @return {!string}
 */
Moment.Locale.prototype.pastFuture = function (a, b) {};

/**
 * @nosideeffects
 * @since 2.8.0
 * @param {!number} a
 * @return {!string}
 */
Moment.Locale.prototype.ordinal = function (a) {};

/**
 * @nosideeffects
 * @since 2.8.0
 * @param {!string} a
 * @return {!string}
 */
Moment.Locale.prototype.preparse = function (a) {};

/**
 * @nosideeffects
 * @since 2.8.0
 * @param {!string} a
 * @return {!string}
 */
Moment.Locale.prototype.postformat = function (a) {};

/**
 * @nosideeffects
 * @since 2.8.0
 * @param {!Moment} a
 * @return {!number}
 */
Moment.Locale.prototype.week = function (a) {};

/**
 * @nosideeffects
 * @since 2.8.0
 * @return {!string}
 */
Moment.Locale.prototype.invalidDate = function () {};

/**
 * @nosideeffects
 * @since 2.8.0
 * @return {!number}
 */
Moment.Locale.prototype.firstDayOfWeek = function () {};

/**
 * @nosideeffects
 * @since 2.8.0
 * @return {!number}
 */
Moment.Locale.prototype.firstDayOfYear = function () {};
