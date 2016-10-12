/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
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
import org.drools.workbench.services.verifier.api.client.cache.util.HasKeys;

public class AnalyzerConfiguration {

    private DateTimeFormatProvider dateTimeFormatter;

    private final UUIDKeyProvider uuidKeyProvider;

    public AnalyzerConfiguration( final DateTimeFormatProvider dateTimeFormatter,
                                  final UUIDKeyProvider uuidKeyProvider ) {
        this.dateTimeFormatter = dateTimeFormatter;
        this.uuidKeyProvider = uuidKeyProvider;
    }

    public UUIDKey getUUID( final HasKeys hasKeys ) {
        return uuidKeyProvider.get( hasKeys );
    }

    public String formatDate( final Date dateValue ) {
        return dateTimeFormatter.format( dateValue );
    }
}

