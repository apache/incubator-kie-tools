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
package org.drools.workbench.services.verifier.plugin.client.builders;

import org.drools.verifier.core.configuration.AnalyzerConfiguration;
import org.drools.verifier.core.index.model.Column;
import org.kie.soup.commons.validation.PortablePreconditions;

public class ColumnBuilder {

    private final AnalyzerConfiguration configuration;

    private int columnIndex;

    public ColumnBuilder(final AnalyzerConfiguration configuration) {
        this.configuration = PortablePreconditions.checkNotNull("configuration",
                                                                configuration);
    }

    public Column build() {
        return new Column(columnIndex,
                          configuration);
    }

    public ColumnBuilder with(final int columnIndex) {
        this.columnIndex = columnIndex;
        return this;
    }
}
