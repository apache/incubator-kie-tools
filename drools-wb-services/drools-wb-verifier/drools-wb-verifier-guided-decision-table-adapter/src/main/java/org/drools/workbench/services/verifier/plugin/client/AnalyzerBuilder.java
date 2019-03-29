/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.services.verifier.plugin.client;

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;
import org.drools.verifier.core.checks.base.CheckRunner;
import org.drools.verifier.core.configuration.AnalyzerConfiguration;
import org.drools.verifier.core.configuration.DateTimeFormatProvider;
import org.drools.verifier.core.index.Index;
import org.drools.verifier.core.index.keys.UUIDKeyProvider;
import org.drools.verifier.core.main.Analyzer;
import org.drools.verifier.core.main.Reporter;
import org.drools.workbench.services.verifier.plugin.client.api.DrlInitialize;
import org.drools.workbench.services.verifier.plugin.client.builders.BuildException;
import org.drools.workbench.services.verifier.plugin.client.builders.IndexBuilder;
import org.drools.workbench.services.verifier.plugin.client.builders.VerifierColumnUtilities;
import org.uberfire.commons.uuid.UUID;

public class AnalyzerBuilder {

    private Reporter reporter;
    private DrlInitialize initialize;
    private VerifierColumnUtilities columnUtilities;
    private Index index;
    private AnalyzerConfiguration configuration;
    private Analyzer analyzer;
    private CheckRunner checkRunner;

    public Analyzer buildAnalyzer() throws
            BuildException {
        if (analyzer == null) {
            analyzer = new Analyzer(reporter,
                                    getIndex(),
                                    getConfiguration());
        }
        return analyzer;
    }

    Index getIndex() throws
            BuildException {
        if (index == null) {
            index = new IndexBuilder(initialize.getModel(),
                                     initialize.getHeaderMetaData(),
                                     getUtils(),
                                     getConfiguration()).build();
        }
        return index;
    }

    private VerifierColumnUtilities getUtils() {
        if (columnUtilities == null) {
            columnUtilities = new VerifierColumnUtilities(initialize.getModel(),
                                                          initialize.getHeaderMetaData(),
                                                          initialize.getFactTypes());
        }
        return columnUtilities;
    }

    AnalyzerConfiguration getConfiguration() {
        if (configuration == null) {

            configuration = new AnalyzerConfiguration(
                    initialize.getUuid(),
                    new DateTimeFormatProvider() {
                        @Override
                        public String format(final Date dateValue) {
                            return DateTimeFormat.getFormat(initialize.getDateFormat())
                                    .format(dateValue);
                        }

                        @Override
                        public Date parse(String dateValue) {
                            return DateTimeFormat.getFormat(initialize.getDateFormat())
                                    .parse(dateValue);
                        }
                    },
                    new UUIDKeyProvider() {
                        @Override
                        protected String newUUID() {
                            return UUID.uuid();
                        }
                    },
                    CheckConfigurationProvider.get(initialize.getModel()
                                                           .getHitPolicy()),
                    checkRunner);
        }
        return configuration;
    }

    public AnalyzerBuilder with(final Reporter reporter) {
        this.reporter = reporter;
        return this;
    }

    public AnalyzerBuilder with(final DrlInitialize initialize) {
        this.initialize = initialize;
        return this;
    }

    public AnalyzerBuilder with(final CheckRunner checkRunner) {
        this.checkRunner = checkRunner;
        return this;
    }
}
