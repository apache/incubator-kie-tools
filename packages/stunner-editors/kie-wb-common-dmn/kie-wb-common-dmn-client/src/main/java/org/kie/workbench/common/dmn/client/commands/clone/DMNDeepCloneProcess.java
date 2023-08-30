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
package org.kie.workbench.common.dmn.client.commands.clone;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.enterprise.inject.Alternative;
import javax.inject.Inject;

import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import org.kie.workbench.common.dmn.api.definition.HasText;
import org.kie.workbench.common.dmn.api.definition.HasVariable;
import org.kie.workbench.common.dmn.api.definition.model.BusinessKnowledgeModel;
import org.kie.workbench.common.dmn.api.definition.model.DMNElement;
import org.kie.workbench.common.dmn.api.definition.model.DRGElement;
import org.kie.workbench.common.dmn.api.definition.model.Decision;
import org.kie.workbench.common.dmn.api.definition.model.Expression;
import org.kie.workbench.common.dmn.api.definition.model.InformationItemPrimary;
import org.kie.workbench.common.dmn.api.definition.model.IsInformationItem;
import org.kie.workbench.common.dmn.api.definition.model.NamedElement;
import org.kie.workbench.common.dmn.api.property.dmn.DMNExternalLink;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.NameHolder;
import org.kie.workbench.common.dmn.api.property.dmn.Text;
import org.kie.workbench.common.dmn.client.property.dmn.DefaultValueUtilities;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManager;
import org.kie.workbench.common.stunner.core.definition.clone.DeepCloneProcess;
import org.kie.workbench.common.stunner.core.definition.clone.IDeepCloneProcess;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.util.ClassUtils;

/**
 * <p>It represents the custom implementation of cloning process for DMN nodes.</p>
 * <p>It is extending the cloning mechanism provided by {@link DeepCloneProcess}, including additional fields and expressions</p>
 */
@Alternative
public class DMNDeepCloneProcess extends DeepCloneProcess implements IDeepCloneProcess {

    private static final RegExp NAME_SUFFIX_REGEX = RegExp.compile("[?!-]\\d+$");
    private static final String HYPHEN = "-";
    private final SessionManager sessionManager;

    protected DMNDeepCloneProcess() {
        this(null,
             null,
             null,
             null);
    }

    @Inject
    public DMNDeepCloneProcess(final FactoryManager factoryManager,
                               final AdapterManager adapterManager,
                               final ClassUtils classUtils,
                               final SessionManager sessionManager) {
        super(factoryManager, adapterManager, classUtils);
        this.sessionManager = sessionManager;
    }

    /**
     * <p>It defines additive fields, specific to DMN domain, to be included in the target</p>
     * <p>Then, the "classic" clone operation, defined in {@link DeepCloneProcess} will be executed</p>
     * <p>Note that {@link DeepCloneProcess} is already taking care of aspects related to look&feel, such as background color, font, etc.</p>
     * <p>Every time we copy a node, in order to respect the name uniqueness logic, a new node will be created with a suffix {@code -X},
     * where {@code X} it is an incremental numeric value</p>
     *
     * @param source node to be cloned
     * @param target destination of the cloning operation
     * @return cloned instance, i.e. target element
     */
    @Override
    public <S, T> T clone(final S source,
                          final T target) {

        super.clone(source, target);

        if (source instanceof DRGElement) {
            cloneDRGElementBasicInfo((DRGElement) source, (DRGElement) target);
        }

        if (source instanceof DMNElement) {
            cloneDMNElement((DMNElement) source, (DMNElement) target);
        }

        if (source instanceof HasText) {
            cloneTextElementBasicInfo((HasText) source, (HasText) target);
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

        return target;
    }

    private void cloneDMNElement(final DMNElement source, final DMNElement target) {
        target.setId(new Id());
        target.setDescription(source.getDescription().copy());
    }

    private void cloneDRGElementBasicInfo(final DRGElement source, final DRGElement target) {
        final String uniqueNodeName = composeUniqueNodeName(source.getName().getValue());
        target.setNameHolder(new NameHolder(new Name(uniqueNodeName)));
        target.setParent(source.getParent());
        target.getLinksHolder().getValue().getLinks().addAll(cloneExternalLinkList(source));
    }

    private void cloneTextElementBasicInfo(final HasText source, final HasText target) {
        final String uniqueNodeName = composeUniqueNodeName(source.getText().getValue());
        target.setText(new Text(uniqueNodeName));
    }

    protected String composeUniqueNodeName(final String name) {
        final String originalName = Optional.ofNullable(name).orElse("");

        final MatchResult nameSuffixMatcher = NAME_SUFFIX_REGEX.exec(originalName);
        if (nameSuffixMatcher != null) {
            return buildNameWithIncrementedSuffixIndex(originalName, nameSuffixMatcher);
        }

        return joinPrefixWithIndexedSuffix(originalName);
    }

    private String buildNameWithIncrementedSuffixIndex(final String nameValue, final MatchResult matchResult) {
        final String suffix = matchResult.getGroup(0);
        final String prefix = Optional.ofNullable(nameValue.split(suffix)[0]).orElse("");
        return joinPrefixWithIndexedSuffix(prefix);
    }

    private String joinPrefixWithIndexedSuffix(final String originalName) {
        final String originalNameWithHyphen = originalName + HYPHEN;
        return originalNameWithHyphen + getMaxUnusedIndexByNamePrefix(originalNameWithHyphen);
    }

    private int getMaxUnusedIndexByNamePrefix(final String namePrefix) {
        final List<String> nodeNameList = StreamSupport.stream(getGraphNodes().spliterator(), true)
                .map(this::nodeNamesMapper)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return DefaultValueUtilities.getMaxUnusedIndex(nodeNameList, namePrefix);
    }

    private Iterable<Node<View, Edge>> getGraphNodes() {
        return sessionManager
                .getCurrentSession()
                .getCanvasHandler()
                .getDiagram()
                .getGraph()
                .nodes();
    }

    private String nodeNamesMapper(final Node<View, Edge> node) {
        if (node.getContent().getDefinition() instanceof NamedElement) {
            final NamedElement namedElement = (NamedElement) node.getContent().getDefinition();
            return namedElement.getName().getValue();
        }
        if (node.getContent().getDefinition() instanceof HasText) {
            final HasText textWrapper = (HasText) node.getContent().getDefinition();
            return textWrapper.getText().getValue();
        }
        return null;
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
        target.setQuestion(source.getQuestion().copy());
        target.setAllowedAnswers(source.getAllowedAnswers().copy());
        target.setExpression(Optional.ofNullable(source.getExpression()).map(Expression::copy).orElse(null));
    }

    private void cloneBusinessKnowledgeModel(final BusinessKnowledgeModel source, final BusinessKnowledgeModel target) {
        target.setEncapsulatedLogic(source.getEncapsulatedLogic().copy());
    }
}
