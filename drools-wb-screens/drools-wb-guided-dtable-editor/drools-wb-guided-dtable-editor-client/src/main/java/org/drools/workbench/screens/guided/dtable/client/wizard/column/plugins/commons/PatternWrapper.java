/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.commons;

import org.drools.workbench.models.datamodel.rule.FactPattern;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableConstants;

import static org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.common.DecisionTableColumnViewUtils.nil;

public class PatternWrapper {

    public final static PatternWrapper DEFAULT = new PatternWrapper();

    private String boundName = "";

    private String factType = "";

    private Boolean negated = null;

    private String entryPointName = "";

    public PatternWrapper(final String factType,
                          final String boundName) {
        this(factType,
             boundName,
             null);
    }

    public PatternWrapper(final String factType,
                          final String boundName,
                          final Boolean negated) {
        this(factType,
             boundName,
             null,
             negated);
    }

    public PatternWrapper(final String factType,
                          final String boundName,
                          final String entryPointName,
                          final Boolean negated) {
        this.factType = factType;
        this.boundName = boundName;
        this.entryPointName = entryPointName;
        this.negated = negated;
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

    public FactPattern makeFactPattern() {

        final FactPattern factPattern = new FactPattern();

        factPattern.setBoundName(getBoundName());
        factPattern.setFactType(getFactType());

        return factPattern;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PatternWrapper that = (PatternWrapper) o;

        if (!boundName.equals(that.boundName)) {
            return false;
        }
        if (!factType.equals(that.factType)) {
            return false;
        }
        if (negated != null ? !negated.equals(that.negated) : that.negated != null) {
            return false;
        }
        return entryPointName != null ? entryPointName.equals(that.entryPointName) : that.entryPointName == null;
    }

    @Override
    public int hashCode() {
        int result = boundName.hashCode();
        result = ~~result;
        result = 31 * result + factType.hashCode();
        result = ~~result;
        result = 31 * result + (negated != null ? negated.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (entryPointName != null ? entryPointName.hashCode() : 0);
        result = ~~result;
        return result;
    }
}
