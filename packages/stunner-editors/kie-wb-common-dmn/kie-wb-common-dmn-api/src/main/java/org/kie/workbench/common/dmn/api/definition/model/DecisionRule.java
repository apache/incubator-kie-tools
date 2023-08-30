/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */
package org.kie.workbench.common.dmn.api.definition.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.dmn.api.definition.HasTypeRef;
import org.kie.workbench.common.dmn.api.definition.HasTypeRefs;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.stunner.core.domainobject.DomainObject;
import org.kie.workbench.common.stunner.core.util.HashUtil;

import static org.kie.workbench.common.dmn.api.definition.model.common.DomainObjectSearcherHelper.getDomainObject;
import static org.kie.workbench.common.dmn.api.definition.model.common.HasTypeRefHelper.getFlatHasTypeRefs;

@Portable
public class DecisionRule extends DMNElement implements HasTypeRefs,
                                                        HasDomainObject {

    private List<UnaryTests> inputEntry;
    private List<LiteralExpression> outputEntry;
    private List<RuleAnnotationClauseText> annotationEntry;

    public DecisionRule() {
        this(new Id(),
             new Description(),
             null,
             null);
    }

    public DecisionRule(final Id id,
                        final Description description,
                        final List<UnaryTests> inputEntry,
                        final List<LiteralExpression> outputEntry) {
        super(id,
              description);
        this.inputEntry = inputEntry;
        this.outputEntry = outputEntry;
    }

    public DecisionRule copy() {
        DecisionRule clonedDecisionRule = new DecisionRule();
        clonedDecisionRule.description = Optional.ofNullable(description).map(Description::copy).orElse(null);
        clonedDecisionRule.inputEntry = inputEntry.stream().map(UnaryTests::copy).collect(Collectors.toList());
        clonedDecisionRule.outputEntry = outputEntry.stream().map(LiteralExpression::copy).collect(Collectors.toList());
        clonedDecisionRule.annotationEntry = Optional.ofNullable(annotationEntry)
                .map(annotationEntryList ->
                             annotationEntryList.stream().map(RuleAnnotationClauseText::copy).collect(Collectors.toList()))
                .orElse(null);
        return clonedDecisionRule;
    }

    public DecisionRule exactCopy() {
        DecisionRule exactelyClonedDecisionRule = new DecisionRule();
        exactelyClonedDecisionRule.id = Optional.ofNullable(id).map(Id::copy).orElse(null);
        exactelyClonedDecisionRule.description = Optional.ofNullable(description).map(Description::copy).orElse(null);
        exactelyClonedDecisionRule.inputEntry = inputEntry.stream().map(UnaryTests::exactCopy).collect(Collectors.toList());
        exactelyClonedDecisionRule.outputEntry = outputEntry.stream().map(LiteralExpression::exactCopy).collect(Collectors.toList());
        exactelyClonedDecisionRule.annotationEntry = Optional.ofNullable(annotationEntry)
                .map(annotationEntryList ->
                        annotationEntryList.stream().map(RuleAnnotationClauseText::copy).collect(Collectors.toList()))
                .orElse(null);
        return exactelyClonedDecisionRule;
    }

    public List<RuleAnnotationClauseText> getAnnotationEntry() {
        if (annotationEntry == null) {
            annotationEntry = new ArrayList<>();
        }
        return this.annotationEntry;
    }

    public List<UnaryTests> getInputEntry() {
        if (inputEntry == null) {
            inputEntry = new ArrayList<>();
        }
        return this.inputEntry;
    }

    public List<LiteralExpression> getOutputEntry() {
        if (outputEntry == null) {
            outputEntry = new ArrayList<>();
        }
        return this.outputEntry;
    }

    @Override
    public List<HasTypeRef> getHasTypeRefs() {
        return new ArrayList<>(getFlatHasTypeRefs(getOutputEntry()));
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DecisionRule)) {
            return false;
        }

        final DecisionRule that = (DecisionRule) o;

        if (id != null ? !id.equals(that.id) : that.id != null) {
            return false;
        }
        if (description != null ? !description.equals(that.description) : that.description != null) {
            return false;
        }
        if (inputEntry != null ? !inputEntry.equals(that.inputEntry) : that.inputEntry != null) {
            return false;
        }
        if (annotationEntry != null ? !annotationEntry.equals(that.annotationEntry) : that.annotationEntry != null) {
            return false;
        }
        return outputEntry != null ? outputEntry.equals(that.outputEntry) : that.outputEntry == null;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(id != null ? id.hashCode() : 0,
                                         description != null ? description.hashCode() : 0,
                                         inputEntry != null ? inputEntry.hashCode() : 0,
                                         outputEntry != null ? outputEntry.hashCode() : 0,
                                         annotationEntry != null ? annotationEntry.hashCode() : 0);
    }

    @Override
    public Optional<DomainObject> findDomainObject(final String uuid) {

        final Optional<DomainObject> domainObject = getDomainObject(getInputEntry(), uuid);
        if (domainObject.isPresent()) {
            return domainObject;
        }

        return getDomainObject(getOutputEntry(), uuid);
    }
}
