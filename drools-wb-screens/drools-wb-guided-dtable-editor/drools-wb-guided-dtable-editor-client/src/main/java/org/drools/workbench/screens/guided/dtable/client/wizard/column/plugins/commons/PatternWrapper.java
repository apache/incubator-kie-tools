/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.commons;

import org.drools.workbench.models.guided.dtable.shared.model.ActionInsertFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableConstants;

import static org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.common.DecisionTableColumnViewUtils.nil;

public class PatternWrapper {

    public final static PatternWrapper DEFAULT = new PatternWrapper();

    private String boundName = "";

    private String factType = "";

    private Boolean negated = null;

    private String entryPointName = "";

    public PatternWrapper(final Pattern52 pattern52) {
        this(pattern52.getFactType(),
             pattern52.getBoundName(),
             pattern52.isNegated());
    }

    public PatternWrapper(final ActionInsertFactCol52 actionCol52) {
        this(actionCol52.getFactType(),
             actionCol52.getBoundName(),
             null);
    }

    public PatternWrapper(final String factType,
                          final String boundName,
                          final Boolean negated) {
        this.factType = factType;
        this.boundName = boundName;
        this.negated = negated;
    }

    public PatternWrapper(final String factType,
                          final String boundName,
                          final String entryPointName,
                          final Boolean negated) {
        this(factType,
             boundName,
             negated);

        this.entryPointName = entryPointName;
    }

    public PatternWrapper() {
    }

    private static String not() {
        return GuidedDecisionTableConstants.INSTANCE.negatedPattern();
    }

    public String key() {
        if (nil(factType)) {
            return "";
        }

        return factType + " " + boundName + " " + negateAsString(negated);
    }

    public String name() {
        if (nil(factType)) {
            return "";
        }

        return prefix() + factType + " [" + boundName + "]";
    }

    public boolean isNegated() {
        if (negated == null) {
            return false;
        }

        return negated;
    }

    private String negateAsString(final Boolean negated) {
        if (negated == null) {
            return "";
        }

        return String.valueOf(negated);
    }

    private String prefix() {
        if (negated == null) {
            return "";
        }

        return negated ? not() + " " : "";
    }

    public String getEntryPointName() {
        return entryPointName;
    }

    public void setEntryPointName(String entryPointName) {
        this.entryPointName = entryPointName;
    }

    public String getFactType() {
        return factType;
    }

    public String getBoundName() {
        return boundName;
    }
}
