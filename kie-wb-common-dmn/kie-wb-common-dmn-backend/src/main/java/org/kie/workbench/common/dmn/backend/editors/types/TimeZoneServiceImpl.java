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

package org.kie.workbench.common.dmn.backend.editors.types;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import org.kie.workbench.common.dmn.api.editors.types.DMNSimpleTimeZone;
import org.kie.workbench.common.dmn.api.editors.types.TimeZoneService;

public class TimeZoneServiceImpl implements TimeZoneService {

    @Override
    public List<DMNSimpleTimeZone> getTimeZones() {

        final String[] ids = getAvailableIds();
        final ArrayList<DMNSimpleTimeZone> list = new ArrayList<>(ids.length);

        for (final String id : ids) {
            final TimeZone timeZone = getTimeZone(id);
            final DMNSimpleTimeZone simpleTimeZone = new DMNSimpleTimeZone();
            simpleTimeZone.setId(id);
            final double offset = toHours(timeZone.getRawOffset());
            simpleTimeZone.setOffset(offset);
            simpleTimeZone.setOffsetString(formatOffset(offset));
            list.add(simpleTimeZone);
        }

        return list;
    }

    String formatOffset(final double offSet) {

        final int hours = Math.abs((int) offSet);

        final double decimalMinutes = Math.abs(offSet) - hours;
        final double minutes = 60 * decimalMinutes;

        final String sign = offSet < 0 ? "-" : "+";

        final String formattedHours = String.format("%02d", (int) hours);
        final String formattedMinutes = String.format("%02d", (int) minutes);

        return sign + formattedHours + ":" + formattedMinutes;
    }

    double toHours(final long milliseconds) {
        return milliseconds / 1000.0d / 60.0d / 60.0d;
    }

    TimeZone getTimeZone(final String id){
        return TimeZone.getTimeZone(id);
    }

    String[] getAvailableIds(){
        return TimeZone.getAvailableIDs();
    }
}
