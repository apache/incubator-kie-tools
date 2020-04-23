/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.dmn.client.commands.clone;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.inject.Alternative;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.api.definition.HasVariable;
import org.kie.workbench.common.dmn.api.definition.model.BusinessKnowledgeModel;
import org.kie.workbench.common.dmn.api.definition.model.DRGElement;
import org.kie.workbench.common.dmn.api.definition.model.Decision;
import org.kie.workbench.common.dmn.api.definition.model.Expression;
import org.kie.workbench.common.dmn.api.definition.model.InformationItemPrimary;
import org.kie.workbench.common.dmn.api.definition.model.IsInformationItem;
import org.kie.workbench.common.dmn.api.property.dmn.DMNExternalLink;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManager;
import org.kie.workbench.common.stunner.core.definition.clone.DeepCloneProcess;
import org.kie.workbench.common.stunner.core.definition.clone.IDeepCloneProcess;
import org.kie.workbench.common.stunner.core.util.ClassUtils;

/**
 * <p>It represents the custom implementation of cloning process for DMN nodes.</p>
 * <p>It is extending the cloning mechanism provided by {@link DeepCloneProcess}, including additional fields and expressions</p>
 */
@Alternative
public class DMNDeepCloneProcess extends DeepCloneProcess implements IDeepCloneProcess {

    protected DMNDeepCloneProcess() {
        this(null,
             null,
             null);
    }

    @Inject
    public DMNDeepCloneProcess(final FactoryManager factoryManager,
                               final AdapterManager adapterManager,
                               final ClassUtils classUtils) {
        super(factoryManager, adapterManager, classUtils);
    }

    /**
     * <p>It defines additive fields, specific to DMN domain, to be included in the target</p>
     * <p>Then, the "classic" clone operation, defined in {@link DeepCloneProcess} will be executed</p>
     * <p>Note that {@link DeepCloneProcess} is already taking care of aspects related to look&feel, such as background color, font, etc.</p>
     *
     * @param source node to be cloned
     * @param target destination of the cloning operation
     * @return cloned instance, i.e. target element
     */
    @Override
    public <S, T> T clone(final S source,
                          final T target) {
        if (source instanceof DRGElement) {
            cloneDRGElementBasicInfo((DRGElement) source, (DRGElement) target);
        }

        if (source instanceof HasVariable) {
            final IsInformationItem sourceVariable = ((HasVariable) source).getVariable();
            final IsInformationItem targetVariable = ((HasVariable) target).getVariable();
            cloneTypeRefInfo(sourceVariable, targetVariable);
        }

        if (source instanceof Decision) {
            cloneDecision((Decision) source, (Decision) target);
        }

        if (source instanceof BusinessKnowledgeModel) {
            cloneBusinessKnowledgeModel((BusinessKnowledgeModel) source, (BusinessKnowledgeModel) target);
        }

        return super.clone(source, target);
    }

    private void cloneDRGElementBasicInfo(final DRGElement source, final DRGElement target) {
        target.setName(source.getName().copy());
        target.setNameHolder(source.getNameHolder().copy());
        target.getLinksHolder().getValue().getLinks().addAll(cloneExternalLinkList(source));
    }

    private void cloneTypeRefInfo(final IsInformationItem srcInformationItem, final IsInformationItem targetInformationItem) {
        if (srcInformationItem instanceof InformationItemPrimary) {
            final InformationItemPrimary srcInformationItemPrimary = (InformationItemPrimary) srcInformationItem;
            final InformationItemPrimary targetInformationItemPrimary = (InformationItemPrimary) targetInformationItem;
            targetInformationItemPrimary.setTypeRefHolder(srcInformationItemPrimary.getTypeRefHolder().copy());
            targetInformationItemPrimary.setTypeRef(srcInformationItemPrimary.getTypeRefHolder().getValue().copy());
        }
    }

    private List<DMNExternalLink> cloneExternalLinkList(final DRGElement sourceDefinition) {
        return sourceDefinition.getLinksHolder().getValue()
                .getLinks()
                .stream()
                .map(srcLink -> new DMNExternalLink(srcLink.getUrl(), srcLink.getDescription()))
                .collect(Collectors.toList());
    }

    private void cloneDecision(final Decision source, final Decision target) {
        target.setId(new Id());
        target.setDescription(source.getDescription().copy());
        target.setName(source.getName().copy());
        target.setQuestion(source.getQuestion().copy());
        target.setAllowedAnswers(source.getAllowedAnswers().copy());
        target.setExpression(Optional.ofNullable(source.getExpression()).map(Expression::copy).orElse(null));
    }

    private void cloneBusinessKnowledgeModel(final BusinessKnowledgeModel source, final BusinessKnowledgeModel target) {
        target.setId(new Id());
        target.setDescription(source.getDescription().copy());
        target.setName(source.getName().copy());
        target.setEncapsulatedLogic(source.getEncapsulatedLogic().copy());
    }
}
