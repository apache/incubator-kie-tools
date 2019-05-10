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

package org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.picker;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.dmn.api.editors.types.DMNSimpleTimeZone;
import org.kie.workbench.common.dmn.api.editors.types.TimeZoneService;

@ApplicationScoped
public class TimeZoneProvider {

    private final Caller<TimeZoneService> service;

    private final List<DMNSimpleTimeZone> timeZones;

    private final List<String> timeZonesOffsets;

    @Inject
    public TimeZoneProvider(final Caller<TimeZoneService> service) {
        this.service = service;
        this.timeZones = new ArrayList<>();
        this.timeZonesOffsets = new ArrayList<>();
    }

    public void getTimeZones(final RemoteCallback<List<DMNSimpleTimeZone>> successCallback) {
        if (getLoadedTimeZones().isEmpty()) {
            service.call((List<DMNSimpleTimeZone> timeZones) -> {
                getLoadedTimeZones().clear();
                getLoadedTimeZones().addAll(timeZones);
                final DMNSimpleTimeZone[] offSets = timeZones.stream()
                        .sorted(Comparator.comparingDouble(DMNSimpleTimeZone::getOffset))
                        .toArray(DMNSimpleTimeZone[]::new);

                getTimeZonesOffsets().clear();
                for (final DMNSimpleTimeZone offSet : offSets) {
                    final String offsetString = offSet.getOffsetString();
                    if (!getTimeZonesOffsets().contains(offsetString)) {
                        getTimeZonesOffsets().add(offsetString);
                    }
                }

                successCallback.callback(timeZones);
            }).getTimeZones();
        } else {
            successCallback.callback(getLoadedTimeZones());
        }
    }

    public boolean isTimeZone(final String timeZone) {
        if (TimeValueFormatter.UTC.equals(timeZone)) {
            return false;
        }
        return getLoadedTimeZones().stream().anyMatch(s -> s.getId().equals(timeZone));
    }

    public List<String> getTimeZonesOffsets() {
        return this.timeZonesOffsets;
    }

    List<DMNSimpleTimeZone> getLoadedTimeZones() {
        return this.timeZones;
    }
}