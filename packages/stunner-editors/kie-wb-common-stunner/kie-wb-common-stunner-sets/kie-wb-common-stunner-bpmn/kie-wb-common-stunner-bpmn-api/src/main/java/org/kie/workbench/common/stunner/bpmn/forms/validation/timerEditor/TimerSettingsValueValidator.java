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


package org.kie.workbench.common.stunner.bpmn.forms.validation.timerEditor;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.timer.TimerSettingsValue;
import org.kie.workbench.common.stunner.bpmn.util.CronExpression;

public class TimerSettingsValueValidator
        implements ConstraintValidator<ValidTimerSettingsValue, TimerSettingsValue> {

    public static final String TimeDurationInvalid = "The timer duration must be a valid ISO-8601 duration or an expression like #{expression}.";

    public static final String ISOTimeCycleInvalid = "The timer cycle must be a valid ISO-8601 repeatable interval or an expression like #{expression}.";

    public static final String CronTimeCycleInvalid = "The time cycle must be a valid cron interval or an expression like #{expression}.";

    public static final String TimeDateInvalid = "The timer date must be a valid ISO-8601 date time or an expression like #{expression}.";

    public static final String NoValueHasBeenProvided = "At least one field must have a non empty value.";

    public static final String ISO = "none";

    public static final String CRON = "cron";

    public static final String DATE_PATTERN = "(\\d{4}-\\d{2}-\\d{2})";

    /**
     * ISO8601-Duration
     */
    private static final String ISO_DURATION = "P(?!$)(\\d+Y)?(\\d+M)?(\\d+W)?(\\d+D)?(T(?=\\d)(\\d+H)?(\\d+M)?((\\d+((\\.|\\,)\\d+)?)+S)?)?";

    /**
     * ISO8601-Repeatable Intervals
     */
    private static final String REPETITION_MASK = "R(\\d*)?";

    private static final String ISO_REPEATABLE_INTERVAL = REPETITION_MASK + "\\/" + ISO_DURATION;

    private static final String ISO_DATE_TIME = "([\\+-]?\\d{4}(?!\\d{2}\\b))(-)(0[1-9]|1[0-2])(-)(0[1-9]|1[0-9]|2[0-9]|3[0-1])T(0[0-9]|1[0-9]|2[0-4])(:)([0-5][0-9])((:)[0-5][0-9]((.)[0-9][0-9][0-9])?)?(((((\\+)(0[0-9]|1[0-5]))|((\\-)(0[0-9]|1[0-8]))):([0-5][0-9]))|(Z))";

    private static final String ISO_REPEATABLE_DATE_TIME_INTERVAL = REPETITION_MASK + "\\/" + ISO_DATE_TIME + "\\/" + ISO_DURATION;

    private static final String ISO_REPEATABLE_START_END_INTERVAL = REPETITION_MASK + "\\/" + ISO_DATE_TIME + "\\/" + ISO_DATE_TIME;

    private static final String ISO_REPEATABLE_PERIOD_END_INTERVAL = REPETITION_MASK + "\\/" + ISO_DURATION + "\\/" + ISO_DATE_TIME;

    /**
     * Cron interval
     */
    private static final String CRON_INTERVAL = "(\\d+)?(\\d+?d)?(\\s)?(\\d+?h)?(\\s)?(\\d+?m)?(\\s)?(\\d+?s)?(\\s)?(\\d+?ms)?";

    private static final String EXPRESSION = "#{(.+)}";

    private static final RegExp durationExpr = RegExp.compile("^" + ISO_DURATION + "$");

    private static final RegExp repeatableIntervalExpr = RegExp.compile("^" + ISO_REPEATABLE_INTERVAL + "$");

    private static final RegExp cronIntervalExpr = RegExp.compile("^" + CRON_INTERVAL + "$");

    private static final RegExp expressionExpr = RegExp.compile("^" + EXPRESSION + "$");

    private static final RegExp dateTimeExpr = RegExp.compile("^" + ISO_DATE_TIME + "$");

    private static final RegExp repeatableDateTimeExpr = RegExp.compile("^" + ISO_REPEATABLE_DATE_TIME_INTERVAL + "$");

    private static final RegExp repeatableStartEndExpr = RegExp.compile("^" + ISO_REPEATABLE_START_END_INTERVAL + "$");

    private static final RegExp repeatablePeriodEndExpr = RegExp.compile("^" + ISO_REPEATABLE_PERIOD_END_INTERVAL + "$");

    private static final RegExp datePattern = RegExp.compile(DATE_PATTERN, "g");

    public void initialize(ValidTimerSettingsValue constraintAnnotation) {
    }

    @Override
    public boolean isValid(TimerSettingsValue timerSettings,
                           ConstraintValidatorContext constraintValidatorContext) {
        String value;
        String errorMessage = null;

        if (timerSettings.getTimeDuration() != null) {
            value = timerSettings.getTimeDuration();
            if ((looksLikeExpression(value) && !isValidExpression(value)) ||
                    (!looksLikeExpression(value) && !isValidDuration(value))) {
                errorMessage = TimeDurationInvalid;
            }
        } else if (ISO.equals(timerSettings.getTimeCycleLanguage())) {
            value = timerSettings.getTimeCycle();
            if ((looksLikeExpression(value) && !isValidExpression(value)) ||
                    (!looksLikeExpression(value) && !isValidRepeatableInterval(value) &&
                            !isValidRepeatableDateTimeInterval(value) && !isValidRepeatableStartEndInterval(value) &&
                            !isValidRepeatablePeriodEndInterval(value))) {
                errorMessage = ISOTimeCycleInvalid;
            }
        } else if (CRON.equals(timerSettings.getTimeCycleLanguage())) {
            value = timerSettings.getTimeCycle();
            if ((looksLikeExpression(value) && !isValidExpression(value)) ||
                    (!looksLikeExpression(value) && !isValidCronExpression(value))) {
                errorMessage = CronTimeCycleInvalid;
            }
        } else if (timerSettings.getTimeDate() != null) {
            value = timerSettings.getTimeDate();
            if ((looksLikeExpression(value) && !isValidExpression(value)) ||
                    (!looksLikeExpression(value) && !isValidTimeDate(value))) {
                errorMessage = TimeDateInvalid;
            }
        } else {
            errorMessage = NoValueHasBeenProvided;
        }

        if (errorMessage != null) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate(errorMessage)
                    .addConstraintViolation();
            return false;
        }
        return true;
    }

    private static boolean looksLikeExpression(final String value) {
        return hasSomething(value) && (value.startsWith("#{") || value.contains("{") || value.contains("}"));
    }

    private static boolean isValidExpression(final String value) {
        return hasSomething(value) && expressionExpr.test(value) && value.length() > 3;
    }

    private static boolean isValidDuration(final String value) {
        return hasSomething(value) && durationExpr.test(value);
    }

    private static boolean isValidRepeatableInterval(final String value) {
        return hasSomething(value) && repeatableIntervalExpr.test(value);
    }

    private static boolean isValidRepeatableDateTimeInterval(final String value) {
        return hasSomething(value) && repeatableDateTimeExpr.test(value) && mayHaveValidDate(value);
    }

    private static boolean isValidRepeatableStartEndInterval(final String value) {
        return hasSomething(value) && repeatableStartEndExpr.test(value) && mayHaveValidDate(value);
    }

    private static boolean isValidRepeatablePeriodEndInterval(final String value) {
        return hasSomething(value) && repeatablePeriodEndExpr.test(value) && mayHaveValidDate(value);
    }

    private static boolean isValidCronExpression(final String value) {
        return isValidBPMNCronExpression(value) || isValidQuartzExpression(value);
    }

    private static boolean isValidBPMNCronExpression(final String value) {
        return hasSomething(value) && cronIntervalExpr.test(value) && !value.endsWith(" ");
    }

    private static boolean isValidQuartzExpression(final String value) {
        return hasSomething(value) && CronExpression.isValidExpression(value);
    }

    private static boolean isValidTimeDate(final String value) {
        return hasSomething(value) && dateTimeExpr.test(value) && mayHaveValidDate(value);
    }

    private static boolean hasSomething(final String value) {
        return value != null && !value.trim().isEmpty();
    }

    private static List<String> fetchDateStrings(final String value) {
        List<String> matches = new ArrayList<>();
        MatchResult matcher = datePattern.exec(value);

        while (null != matcher) {
            matches.add(matcher.getGroup(1));
            matcher = datePattern.exec(value);
        }

        return matches;
    }

    // if the ISO expression has dates, they must be valid ones
    private static boolean mayHaveValidDate(final String value) {
        List<String> matches = fetchDateStrings(value);
        if (!matches.isEmpty()) {
            try {
                DateTimeFormat dateTimeFormat = DateTimeFormat.getFormat("yyyy-MM-dd");
                matches.forEach(dateTimeFormat::parseStrict);
            } catch (IllegalArgumentException e) {
                return false;
            }
        }
        return true;
    }
}