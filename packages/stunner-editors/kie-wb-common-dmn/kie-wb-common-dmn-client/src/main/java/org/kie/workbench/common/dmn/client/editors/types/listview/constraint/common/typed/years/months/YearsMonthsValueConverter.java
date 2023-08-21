/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.years.months;

import javax.inject.Inject;

import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.common.DurationHelper;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.util.StringUtils;

public class YearsMonthsValueConverter {

    private static final String YEARS = "Y";
    private static final String MONTHS = "M";

    static final String YEARS_TRANSLATION_KEY = "YearsMonthsSelectorView.Years";
    static final String MONTHS_TRANSLATION_KEY = "YearsMonthsSelectorView.Months";

    static final String YEARS_ABBREVIATED_TRANSLATION_KEY = "YearsMonthsSelectorView.YearsAbbreviated";
    static final String MONTHS_ABBREVIATED_TRANSLATION_KEY = "YearsMonthsSelectorView.MonthsAbbreviated";

    private final ClientTranslationService translationService;

    @Inject
    public YearsMonthsValueConverter(final ClientTranslationService translationService) {
        this.translationService = translationService;
    }

    public YearsMonthsValue fromDMNString(final String dmnString) {

        final YearsMonthsValue yearsMonthsValue = new YearsMonthsValue();
        final String value = removePrefixAndSuffix(dmnString);
        final String[] yearsSplit = value.split(YEARS);

        String months = "";

        if (yearsSplit.length > 0 && !yearsSplit[0].equals(value)) {
            yearsMonthsValue.setYears(yearsSplit[0]);

            if (yearsSplit.length == 2) {
                months = getMonths(yearsSplit[1]);
            }
        } else {
            months = getMonths(value);
        }

        yearsMonthsValue.setMonths(months);
        return yearsMonthsValue;
    }

    private String getMonths(final String monthString) {
        final String[] months = monthString.split(MONTHS);
        if (months.length > 0) {
            return months[0];
        }

        return "";
    }

    public String toDMNString(final String years,
                              final String months) {

        final YearsMonthsValue value = new YearsMonthsValue();
        value.setYears(years);
        value.setMonths(months);

        return toDMNString(value);
    }

    private String toDMNString(final YearsMonthsValue yearsMonthsValue) {

        matchSigns(yearsMonthsValue);

        String dmnString = "";
        if (!StringUtils.isEmpty(yearsMonthsValue.getYears())) {
            dmnString += yearsMonthsValue.getYears() + YEARS;
        }

        if (!StringUtils.isEmpty(yearsMonthsValue.getMonths())) {
            dmnString += yearsMonthsValue.getMonths() + MONTHS;
        }

        if (StringUtils.isEmpty(dmnString)) {
            return dmnString;
        } else {
            return addPrefixAndSuffix(dmnString);
        }
    }

    public String toDisplayValue(final String dmnValue) {

        final YearsMonthsValue yearsMonthsValue = fromDMNString(dmnValue);
        return toDisplayValue(yearsMonthsValue);
    }

    void matchSigns(final YearsMonthsValue value) {

        int yearsValue = 0;
        int monthsValue = 0;

        if (!StringUtils.isEmpty(value.getYears())) {
            yearsValue = Integer.parseInt(value.getYears());
        }

        if (!StringUtils.isEmpty(value.getMonths())) {
            monthsValue = Integer.parseInt(value.getMonths());
        }

        if (yearsValue < 0 && monthsValue > 0) {
            value.setMonths("-" + value.getMonths());
        } else if (monthsValue < 0 && yearsValue > 0) {
            value.setYears("-" + value.getYears());
        }
    }

    String toDisplayValue(final YearsMonthsValue yearsMonthsValue) {

        final String years = yearsMonthsValue.getYears();
        final String months = yearsMonthsValue.getMonths();
        final boolean hasYears = !StringUtils.isEmpty(years);
        final boolean hasMonths = !StringUtils.isEmpty(months);

        final String displayValue;
        if (hasYears && hasMonths) {
            final String yearsString = translationService.getValue(YEARS_TRANSLATION_KEY);
            final String monthsString = translationService.getValue(MONTHS_TRANSLATION_KEY);
            displayValue = years + " " + yearsString + ", " + months + " " + monthsString;
        } else if (hasYears) {
            displayValue = years + " " + translationService.getValue(YEARS_ABBREVIATED_TRANSLATION_KEY);
        } else if (hasMonths) {
            displayValue = months + " " + translationService.getValue(MONTHS_ABBREVIATED_TRANSLATION_KEY);
        } else {
            displayValue = "";
        }

        return displayValue;
    }

    static String addPrefixAndSuffix(final String value) {
        return DurationHelper.addFunctionCall("P" + value);
    }

    String removePrefixAndSuffix(final String dmnString) {
        return StringUtils.isEmpty(dmnString) ? "" : DurationHelper.getFunctionParameter(dmnString).substring(1);
    }
}
