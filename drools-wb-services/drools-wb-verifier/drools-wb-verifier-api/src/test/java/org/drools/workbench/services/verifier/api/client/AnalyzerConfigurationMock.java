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
package org.drools.workbench.services.verifier.api.client;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.drools.workbench.services.verifier.api.client.configuration.AnalyzerConfiguration;
import org.drools.workbench.services.verifier.api.client.configuration.CheckConfiguration;
import org.drools.workbench.services.verifier.api.client.configuration.DateTimeFormatProvider;
import org.drools.workbench.services.verifier.api.client.configuration.RunnerType;
import org.drools.workbench.services.verifier.api.client.index.keys.UUIDKeyProvider;

public class AnalyzerConfigurationMock
        extends AnalyzerConfiguration {

    public AnalyzerConfigurationMock() {
        super( "UUID",
               new DateTimeFormatProvider() {
                   @Override
                   public String format( final Date dateValue ) {
                       return new SimpleDateFormat( "dd-MMM-yyyy" ).format( dateValue );
                   }
               },
               new UUIDKeyProvider() {

                   private long index = Long.SIZE;

                   @Override
                   protected String newUUID() {
                       return Long.toString( index-- );
                   }
               },
               CheckConfiguration.newDefault(),
               RunnerType.JAVA );
    }
}
