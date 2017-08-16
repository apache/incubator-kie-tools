/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
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

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.QName;

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
             new ArrayList<>(),
             new ArrayList<>(),
             new ArrayList<>(),
             HitPolicy.ANY,
             null,
             DecisionTableOrientation.RULE_AS_ROW,
             "");
    }

    public DecisionTable(final @MapsTo("id") Id id,
                         final @MapsTo("description") Description description,
                         final @MapsTo("typeRef") QName typeRef,
                         final @MapsTo("input") List<InputClause> input,
                         final @MapsTo("output") List<OutputClause> output,
                         final @MapsTo("rule") List<DecisionRule> rule,
                         final @MapsTo("hitPolicy") HitPolicy hitPolicy,
                         final @MapsTo("aggregation") BuiltinAggregator aggregation,
                         final @MapsTo("preferredOrientation") DecisionTableOrientation preferredOrientation,
                         final @MapsTo("outputLabel") String outputLabel) {
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
}
