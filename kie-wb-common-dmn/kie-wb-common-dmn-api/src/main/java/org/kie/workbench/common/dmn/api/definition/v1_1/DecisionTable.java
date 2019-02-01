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
package org.kie.workbench.common.dmn.api.definition.v1_1;

import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.dmn.api.definition.HasTypeRef;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.stunner.core.util.HashUtil;

import static org.kie.workbench.common.dmn.api.definition.v1_1.common.HasTypeRefHelper.getFlatHasTypeRefs;

@Portable
public class DecisionTable extends Expression {

    private List<InputClause> input;
    private List<OutputClause> output;
    private List<DecisionRule> rule;
    private HitPolicy hitPolicy;
    private BuiltinAggregator aggregation;
    private DecisionTableOrientation preferredOrientation;
    private String outputLabel;

    public DecisionTable() {
        this(new Id(),
             new Description(),
             new QName(),
             null,
             null,
             null,
             HitPolicy.UNIQUE,
             null,
             DecisionTableOrientation.RULE_AS_ROW,
             null);
    }

    public DecisionTable(final Id id,
                         final Description description,
                         final QName typeRef,
                         final List<InputClause> input,
                         final List<OutputClause> output,
                         final List<DecisionRule> rule,
                         final HitPolicy hitPolicy,
                         final BuiltinAggregator aggregation,
                         final DecisionTableOrientation preferredOrientation,
                         final String outputLabel) {
        super(id,
              description,
              typeRef);
        this.input = input;
        this.output = output;
        this.rule = rule;
        this.hitPolicy = hitPolicy;
        this.aggregation = aggregation;
        this.preferredOrientation = preferredOrientation;
        this.outputLabel = outputLabel;
    }

    public List<InputClause> getInput() {
        if (input == null) {
            input = new ArrayList<>();
        }
        return this.input;
    }

    public List<OutputClause> getOutput() {
        if (output == null) {
            output = new ArrayList<>();
        }
        return this.output;
    }

    public List<DecisionRule> getRule() {
        if (rule == null) {
            rule = new ArrayList<>();
        }
        return this.rule;
    }

    public HitPolicy getHitPolicy() {
        if (hitPolicy == null) {
            return HitPolicy.UNIQUE;
        } else {
            return hitPolicy;
        }
    }

    public void setHitPolicy(final HitPolicy value) {
        this.hitPolicy = value;
    }

    public BuiltinAggregator getAggregation() {
        return aggregation;
    }

    public void setAggregation(final BuiltinAggregator value) {
        this.aggregation = value;
    }

    public DecisionTableOrientation getPreferredOrientation() {
        if (preferredOrientation == null) {
            return DecisionTableOrientation.RULE_AS_ROW;
        } else {
            return preferredOrientation;
        }
    }

    public void setPreferredOrientation(final DecisionTableOrientation value) {
        this.preferredOrientation = value;
    }

    public String getOutputLabel() {
        return outputLabel;
    }

    public void setOutputLabel(final String value) {
        this.outputLabel = value;
    }

    @Override
    public List<HasTypeRef> getHasTypeRefs() {

        final List<HasTypeRef> hasTypeRefs = super.getHasTypeRefs();

        hasTypeRefs.addAll(getFlatHasTypeRefs(getInput()));
        hasTypeRefs.addAll(getFlatHasTypeRefs(getOutput()));
        hasTypeRefs.addAll(getFlatHasTypeRefs(getRule()));

        return hasTypeRefs;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DecisionTable)) {
            return false;
        }

        final DecisionTable that = (DecisionTable) o;

        if (id != null ? !id.equals(that.id) : that.id != null) {
            return false;
        }
        if (description != null ? !description.equals(that.description) : that.description != null) {
            return false;
        }
        if (typeRef != null ? !typeRef.equals(that.typeRef) : that.typeRef != null) {
            return false;
        }
        if (input != null ? !input.equals(that.input) : that.input != null) {
            return false;
        }
        if (output != null ? !output.equals(that.output) : that.output != null) {
            return false;
        }
        if (rule != null ? !rule.equals(that.rule) : that.rule != null) {
            return false;
        }
        if (hitPolicy != that.hitPolicy) {
            return false;
        }
        if (aggregation != that.aggregation) {
            return false;
        }
        if (preferredOrientation != that.preferredOrientation) {
            return false;
        }
        return outputLabel != null ? outputLabel.equals(that.outputLabel) : that.outputLabel == null;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(id != null ? id.hashCode() : 0,
                                         description != null ? description.hashCode() : 0,
                                         typeRef != null ? typeRef.hashCode() : 0,
                                         input != null ? input.hashCode() : 0,
                                         output != null ? output.hashCode() : 0,
                                         rule != null ? rule.hashCode() : 0,
                                         hitPolicy != null ? hitPolicy.hashCode() : 0,
                                         aggregation != null ? aggregation.hashCode() : 0,
                                         preferredOrientation != null ? preferredOrientation.hashCode() : 0,
                                         outputLabel != null ? outputLabel.hashCode() : 0);
    }
}
