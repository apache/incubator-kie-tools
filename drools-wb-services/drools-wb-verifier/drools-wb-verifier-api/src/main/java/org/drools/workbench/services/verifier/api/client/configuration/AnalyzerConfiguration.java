/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.services.verifier.api.client.configuration;

import java.util.Date;

import org.drools.workbench.services.verifier.api.client.index.keys.UUIDKey;
import org.drools.workbench.services.verifier.api.client.index.keys.UUIDKeyProvider;
import org.drools.workbench.services.verifier.api.client.maps.util.HasKeys;
import org.kie.soup.commons.validation.PortablePreconditions;

public class AnalyzerConfiguration {

    private final UUIDKeyProvider uuidKeyProvider;
    private final String webWorkerUUID;
    private final DateTimeFormatProvider dateTimeFormatter;
    private final CheckConfiguration checkConfiguration;
    private final RunnerType runnerType;

    public AnalyzerConfiguration(final String webWorkerUUID,
                                 final DateTimeFormatProvider dateTimeFormatter,
                                 final UUIDKeyProvider uuidKeyProvider,
                                 final CheckConfiguration checkConfiguration,
                                 final RunnerType runnerType) {
        this.webWorkerUUID = PortablePreconditions.checkNotNull("webWorkerUUID",
                                                                webWorkerUUID);
        this.dateTimeFormatter = PortablePreconditions.checkNotNull("dateTimeFormatter",
                                                                    dateTimeFormatter);
        this.uuidKeyProvider = PortablePreconditions.checkNotNull("uuidKeyProvider",
                                                                  uuidKeyProvider);
        this.checkConfiguration = PortablePreconditions.checkNotNull("checkConfiguration",
                                                                     checkConfiguration);
        this.runnerType = PortablePreconditions.checkNotNull("runnerType",
                                                             runnerType);
    }

    public String getWebWorkerUUID() {
        return webWorkerUUID;
    }

    public UUIDKey getUUID(final HasKeys hasKeys) {
        return uuidKeyProvider.get(hasKeys);
    }

    public String formatDate(final Date dateValue) {
        return dateTimeFormatter.format(dateValue);
    }

    public CheckConfiguration getCheckConfiguration() {
        return checkConfiguration;
    }

    public RunnerType getRunnerType() {
        return runnerType;
    }
}

