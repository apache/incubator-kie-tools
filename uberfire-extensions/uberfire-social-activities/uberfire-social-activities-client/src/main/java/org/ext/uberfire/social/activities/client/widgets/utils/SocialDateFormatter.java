/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.ext.uberfire.social.activities.client.widgets.utils;

import java.util.Date;

import org.ext.uberfire.social.activities.client.resources.i18n.Constants;

public class SocialDateFormatter {

    private static final Constants constants = Constants.INSTANCE;

    public static String format(Date date) {
        int diffInDays = diffInDaysFromNow(date);

        if (diffInDays < 7) {
            return formatInDays(diffInDays);
        } else {
            return formatInWeeks(diffInDays);
        }
    }

    private static int diffInDaysFromNow(Date date) {
        int diffInDays = (int) ((new Date().getTime() - date.getTime())
                / (1000 * 60 * 60 * 24));
        return Math.abs(diffInDays);
    }

    private static String formatInWeeks(int diffInDays) {
        int numberOfWeeks = diffInDays / 7;
        if (numberOfWeeks == 1 || numberOfWeeks == 0) {
            return constants.OneWeekAgo();
        } else {
            return numberOfWeeks + " " + constants.WeeksAgo();
        }
    }

    private static String formatInDays(int diffInDays) {
        if (diffInDays == 0) {
            return constants.Today();
        } else if (diffInDays == 1) {
            return diffInDays + " " + constants.DayAgo();
        } else {
            return diffInDays + " " + constants.DaysAgo();
        }
    }
}
