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

package org.drools.workbench.services.verifier.plugin.client.testutil;

import java.util.ArrayList;
import java.util.List;

import org.drools.workbench.models.guided.dtable.backend.util.DataUtilities;
import org.drools.workbench.models.guided.dtable.shared.model.ActionRetractFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.AttributeCol52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;

public class AbstractDecisionTableBuilder {

    protected final GuidedDecisionTable52 table = new GuidedDecisionTable52();

    protected final List<Pattern52> patterns = new ArrayList<Pattern52>();

    protected void addPattern(Pattern52 pattern) {
        if (!table.getConditions().contains(pattern)) {
            table.getConditions().add(pattern);
        }
    }

    protected Pattern52 findPattern(String boundName,
                                    String factType) {
        for (Pattern52 pattern : patterns) {
            if (pattern.getBoundName().equals(boundName) && pattern.getFactType().equals(factType)) {
                return pattern;
            }
        }

        Pattern52 pattern = new Pattern52();
        pattern.setBoundName(boundName);
        pattern.setFactType(factType);

        patterns.add(pattern);
        return pattern;
    }

    public AbstractDecisionTableBuilder withData(Object[][] data) {

        table.setData(DataUtilities.makeDataLists(data));

        return this;
    }

    public GuidedDecisionTable52 buildTable() {
        return table;
    }

    protected Object withAttributeColumn(final String attribute) {
        final AttributeCol52 attributeCol52 = new AttributeCol52();
        attributeCol52.setAttribute(attribute);
        table.getAttributeCols().add(attributeCol52);
        return this;
    }

    public AbstractDecisionTableBuilder withRetract() {

        final ActionRetractFactCol52 col52 = new ActionRetractFactCol52();
        col52.setHeader("retract header");
        table.getActionCols().add(col52);

        return this;
    }

    public Actions actionColumn() {
        return new Actions();
    }

    public class Actions {

        public AbstractDecisionTableBuilder retract() {
            return withRetract();
        }
    }
}
