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
package org.kie.workbench.common.dmn.webapp.kogito.common.client.services;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import org.kie.workbench.common.dmn.api.editors.types.DMNSimpleTimeZone;
import org.uberfire.client.views.pfly.widgets.MomentTimeZone;

/**
 * Provides TimeZone details
 */
@ApplicationScoped
public class TimeZonesProvider {

    private final List<DMNSimpleTimeZone> TIME_ZONES = new ArrayList<>();

    public List<DMNSimpleTimeZone> getTimeZones() {
        if (TIME_ZONES.isEmpty()) {
            TIME_ZONES.addAll(buildTimeZones());
        }
        return TIME_ZONES;
    }

    private List<DMNSimpleTimeZone> buildTimeZones() {
        final List<DMNSimpleTimeZone> timeZones = new ArrayList<>();
        final String[] names = getNames();

        for (String name : names) {
            final double offset = getOffset(name);
            final String offsetString = getOffsetString(name);
            timeZones.add(new DMNSimpleTimeZone(name,
                                                offset,
                                                offsetString));
        }
        return timeZones;
    }

    protected String[] getNames() {
        return MomentTimeZone.Builder.tz().names();
    }

    protected double getOffset(final String timeZoneName) {
        return MomentTimeZone.Builder.tz(timeZoneName).utcOffset() / 60;
    }

    protected String getOffsetString(final String timeZoneName) {
        return MomentTimeZone.Builder.tz(timeZoneName).format("Z");
    }
}
