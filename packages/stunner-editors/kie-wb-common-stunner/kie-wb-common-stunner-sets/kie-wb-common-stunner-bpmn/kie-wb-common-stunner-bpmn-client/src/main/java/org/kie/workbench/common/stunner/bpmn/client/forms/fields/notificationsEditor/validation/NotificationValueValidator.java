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


package org.kie.workbench.common.stunner.bpmn.client.forms.fields.notificationsEditor.validation;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Predicate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.google.common.collect.ImmutableMap;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.validation.client.GwtValidation;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.Expiration;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.NotificationRow;

import static org.kie.workbench.common.stunner.bpmn.client.forms.fields.notificationsEditor.validation.ExpirationTypeOracle.DURATION;
import static org.kie.workbench.common.stunner.bpmn.client.forms.fields.notificationsEditor.validation.ExpirationTypeOracle.ISO_DATE_TIME;
import static org.kie.workbench.common.stunner.bpmn.client.forms.fields.notificationsEditor.validation.ExpirationTypeOracle.JAVA_VARIABLE;
import static org.kie.workbench.common.stunner.bpmn.client.forms.fields.notificationsEditor.validation.ExpirationTypeOracle.OLD_DURATION;
import static org.kie.workbench.common.stunner.bpmn.client.forms.fields.notificationsEditor.validation.ExpirationTypeOracle.OLD_MULTIPLE_INTERVALS;
import static org.kie.workbench.common.stunner.bpmn.client.forms.fields.notificationsEditor.validation.ExpirationTypeOracle.ONE_TIME_EXECUTION;
import static org.kie.workbench.common.stunner.bpmn.client.forms.fields.notificationsEditor.validation.ExpirationTypeOracle.PERIOD;
import static org.kie.workbench.common.stunner.bpmn.client.forms.fields.notificationsEditor.validation.ExpirationTypeOracle.REPEATABLE;
import static org.kie.workbench.common.stunner.bpmn.client.forms.fields.notificationsEditor.validation.ExpirationTypeOracle.REPEATABLE_DURATION_END;
import static org.kie.workbench.common.stunner.bpmn.client.forms.fields.notificationsEditor.validation.ExpirationTypeOracle.REPEATABLE_START_END;
import static org.kie.workbench.common.stunner.bpmn.client.forms.fields.notificationsEditor.validation.ExpirationTypeOracle.REPEATING_INTERVALS;

@GwtValidation(NotificationRow.class)
public class NotificationValueValidator implements ConstraintValidator<ValidNotificationValue, NotificationRow> {

    public static final String WRONG_EXPIRES_AT_EXPRESSION = "Expression is not valid";

    public static final String keywords[] = {"abstract", "assert", "boolean",
            "break", "byte", "case", "catch", "char", "class", "const",
            "continue", "default", "do", "double", "else", "extends", "false",
            "final", "finally", "float", "for", "goto", "if", "implements",
            "import", "instanceof", "int", "interface", "long", "native",
            "new", "null", "package", "private", "protected", "public",
            "return", "short", "static", "strictfp", "super", "switch",
            "synchronized", "this", "throw", "throws", "transient", "true",
            "try", "void", "volatile", "while"};

    static BiFunction<String, String, Optional<MatchResult>> checkIfPatternMatch = (pattern, maybeIso) -> {
        MatchResult result = RegExp.compile(pattern).exec(maybeIso);
        return result != null ? Optional.of(result) : Optional.empty();
    };

    private Predicate<String> lessThenMaxInteger = iso -> {
        try {
            Integer.valueOf(iso);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    };

    private Predicate<String> checkIfValueIsNotEmptyOrNegative = iso -> lessThenMaxInteger.and(s -> (Integer.parseInt(iso) >= 1)).test(iso);

    private Predicate<String> checkIfValueRepeatable = iso -> iso.isEmpty() || checkIfValueIsNotEmptyOrNegative.test(iso);
    //Note: "P1M" is a one-month duration and "PT1M" is a one-minute duration;
    private BiFunction<String, String, Boolean> checkIfValueIsMinuteOrMonth = (t, m)
            -> {
        if (m.equals("M")) {
            return ((!m.equals("M") && !t.isEmpty()) || (t.isEmpty() || t.equals("T")) && m.equals("M")) ? true : false;
        } else if (m.equals("H")) {
            return !t.isEmpty();
        } else if (m.equals("D") || m.equals("Y")) {
            return t.isEmpty();
        }
        return false;
    };

    private Predicate<String> isTimePeriodExpressionWithRepeatableSection = (maybeIso) -> {
        Optional<MatchResult> result = checkIfPatternMatch.apply(REPEATABLE + "/" + PERIOD + "$", maybeIso);
        if (result.isPresent()) {
            if (checkIfValueRepeatable.test(result.get().getGroup(1))
                    && checkIfValueIsNotEmptyOrNegative.test(result.get().getGroup(3))
                    && checkIfValueIsMinuteOrMonth.apply(result.get().getGroup(2), result.get().getGroup(4))) {
                return true;
            }
        }
        return false;
    };

    private Predicate<String> isTimePeriodExpression = (maybeIso) -> {
        Optional<MatchResult> result = checkIfPatternMatch.apply("^" + DURATION + "$", maybeIso);
        if (result.isPresent()) {
            if (checkIfValueIsNotEmptyOrNegative.test(result.get().getGroup(2))
                    && checkIfValueIsMinuteOrMonth.apply(result.get()
                                                                 .getGroup(1), result.get()
                                                                 .getGroup(3))) {
                return true;
            }
        }
        return false;
    };

    private Predicate<String> isOneTimeExecution = (maybeIso) -> {
        Optional<MatchResult> result = checkIfPatternMatch.apply(ONE_TIME_EXECUTION, maybeIso);
        if (result.isPresent()) {
            return checkIfValueIsNotEmptyOrNegative.test(result.get().getGroup(1));
        }
        return false;
    };

    private Predicate<String> isRepeatableStartEnd = (maybeIso) -> checkIfPatternMatch.apply(REPEATABLE_START_END, maybeIso).isPresent();

    private Predicate<String> isRepeatableDurationStart = (maybeIso) -> checkIfPatternMatch.apply(REPEATABLE_DURATION_END, maybeIso).isPresent();

    private Predicate<String> isRepeatingIntervals = (maybeIso) -> {
        Optional<MatchResult> result = checkIfPatternMatch.apply(REPEATING_INTERVALS, maybeIso);
        if (result.isPresent()) {
            return result.get().getGroup(1).isEmpty() || checkIfValueIsNotEmptyOrNegative.test(result.get().getGroup(1));
        }
        return false;
    };

    private Predicate<String> isDuration = (maybeIso) -> checkIfPatternMatch.apply("^" + DURATION + "$", maybeIso).isPresent();

    private Predicate<String> isOldDuration = (maybeIso) -> checkIfPatternMatch.apply(OLD_DURATION, maybeIso).isPresent();

    private Predicate<String> isDataTimeLike = (maybeIso) -> {
        Optional<MatchResult> result = checkIfPatternMatch.apply("^" + REPEATABLE + "/" + ISO_DATE_TIME + "/" + DURATION + "$", maybeIso);
        if (result.isPresent()) {
            return result.get().getGroup(1).isEmpty() || checkIfValueIsNotEmptyOrNegative.test(result.get().getGroup(1));
        }
        return false;
    };

    private Predicate<String> isJavaVariable = (maybeIso) -> {
        Optional<MatchResult> result = checkIfPatternMatch.apply(JAVA_VARIABLE, maybeIso);
        if (result.isPresent()) {
            return !isJavaKeyword(result.get().getGroup(1));
        }
        return false;
    };

    private Predicate<String> isOldMultipleIntervals = (maybeIso) -> {
        if (checkIfPatternMatch.apply(OLD_MULTIPLE_INTERVALS, maybeIso).isPresent()) {
            String[] parts = maybeIso.split("\\,");
            return (isRepeatingIntervals.test(parts[0]) && isRepeatingIntervals.test(parts[1])) ||
                    (isDuration.test(parts[0]) && isDuration.test(parts[1]));
        }
        return false;
    };

    private Predicate<String> isValidRepeatableExpression = (maybeIso) -> isTimePeriodExpressionWithRepeatableSection
            .or(isTimePeriodExpression)
            .or(isRepeatableStartEnd)
            .or(isRepeatableDurationStart)
            .or(isRepeatingIntervals)
            .or(isDuration)
            .or(isOldDuration)
            .or(isOneTimeExecution)
            .or(isOldMultipleIntervals)
            .or(isJavaVariable)
            .or(isDataTimeLike)
            .test(maybeIso);

    private Predicate<String> isRepeatableDateTimeExpression = (maybeIso) -> {
        Optional<MatchResult> result = checkIfPatternMatch.apply("^" + REPEATABLE + "/" + ISO_DATE_TIME + "/" + PERIOD + "$", maybeIso);
        if (result.isPresent()) {
            if (checkIfValueRepeatable.test(result.get().getGroup(1))
                    && checkIfValueIsMinuteOrMonth.apply(result.get().getGroup(5), result.get().getGroup(7))
                    && checkIfValueIsNotEmptyOrNegative.test(result.get().getGroup(6))) {
                return true;
            }
        }
        return false;
    };

    private Predicate<String> isDateTimeExpression = (maybeIso)
            -> checkIfPatternMatch.apply("^" + ISO_DATE_TIME, maybeIso).isPresent();

    private Predicate<String> isValidDateTimeExpression = (maybeIso)
            -> isRepeatableDateTimeExpression.or(isDateTimeExpression).test(maybeIso);

    public Map<Expiration, Predicate> validators = ImmutableMap.of(
            Expiration.TIME_PERIOD, isValidRepeatableExpression,
            Expiration.DATETIME, isValidDateTimeExpression,
            Expiration.EXPRESSION, isValidRepeatableExpression);

    private static boolean isJavaKeyword(String keyword) {
        return (Arrays.binarySearch(keywords, keyword) >= 0);
    }

    private Predicate getValidator(Expiration expiration) {
        return validators.get(expiration);
    }

    @Override
    public void initialize(ValidNotificationValue constraintAnnotation) {

    }

    @Override
    public boolean isValid(NotificationRow value, ConstraintValidatorContext context) {
        if (!isValid(value)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(WRONG_EXPIRES_AT_EXPRESSION)
                    .addConstraintViolation();
            return false;
        }
        return true;
    }

    public boolean isValid(NotificationRow value) {
        return getValidator(new ExpirationTypeOracle()
                                    .guess(value.getExpiresAt()))
                .test(value.getExpiresAt());
    }
}