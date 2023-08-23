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

package org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.picker;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.api.editors.types.DMNSimpleTimeZone;
import org.kie.workbench.common.dmn.client.service.DMNClientServicesProxy;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;

@ApplicationScoped
public class TimeZoneProvider {

    private final DMNClientServicesProxy clientServicesProxy;

    private final List<DMNSimpleTimeZone> timeZones;

    private final List<String> timeZonesOffsets;

    @Inject
    public TimeZoneProvider(final DMNClientServicesProxy clientServicesProxy) {
        this.clientServicesProxy = clientServicesProxy;
        this.timeZones = new ArrayList<>();
        this.timeZonesOffsets = new ArrayList<>();
    }

    public void getTimeZones(final Consumer<List<DMNSimpleTimeZone>> consumer) {
        if (getLoadedTimeZones().isEmpty()) {
            clientServicesProxy.getTimeZones(new ServiceCallback<List<DMNSimpleTimeZone>>() {
                @Override
                public void onSuccess(final List<DMNSimpleTimeZone> timeZones) {
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

                    consumer.accept(timeZones);
                }

                @Override
                public void onError(final ClientRuntimeError error) {
                    clientServicesProxy.logWarning(error);
                }
            });
        } else {
            consumer.accept(getLoadedTimeZones());
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