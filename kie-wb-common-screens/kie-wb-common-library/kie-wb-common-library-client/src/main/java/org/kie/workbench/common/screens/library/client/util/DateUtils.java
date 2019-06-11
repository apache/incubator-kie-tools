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

package org.kie.workbench.common.screens.library.client.util;

import java.util.Date;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;

@ApplicationScoped
public class DateUtils {

    public static final int ONE_DAY_IN_MS = 1000 * 60 * 60 * 24;

    private static final int SEVEN_DAYS = 7;

    private TranslationService translationService;

    @Inject
    public DateUtils(TranslationService translationService) {
        this.translationService = translationService;
    }

    public String format(Date date) {
        int diffInDays = diffInDaysFromNow(date);

        if (diffInDays < SEVEN_DAYS) {
            return formatInDays(diffInDays);
        } else {
            return formatInWeeks(diffInDays);
        }
    }

    private int diffInDaysFromNow(Date date) {
        int diffInDays = (int) ((new Date().getTime() - date.getTime()) / ONE_DAY_IN_MS);
        return Math.abs(diffInDays);
    }

    private String formatInDays(int diffInDays) {
        if (diffInDays == 0) {
            return translationService.getTranslation(LibraryConstants.Today);
        } else if (diffInDays == 1) {
            return translationService.getTranslation(LibraryConstants.OneDayAgo);
        } else {
            return translationService.format(LibraryConstants.DaysAgo, diffInDays);
        }
    }

    private String formatInWeeks(int diffInDays) {
        int numberOfWeeks = diffInDays / SEVEN_DAYS;
        if (numberOfWeeks <= 1) {
            return translationService.getTranslation(LibraryConstants.OneWeekAgo);
        } else {
            return translationService.format(LibraryConstants.WeeksAgo, numberOfWeeks);
        }
    }
}
