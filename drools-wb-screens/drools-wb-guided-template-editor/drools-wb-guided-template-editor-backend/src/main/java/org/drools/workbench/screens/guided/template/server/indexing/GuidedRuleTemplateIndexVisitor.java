/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.guided.template.server.indexing;

import java.util.Set;

import org.drools.workbench.models.datamodel.rule.ActionFieldValue;
import org.drools.workbench.models.datamodel.rule.ActionInsertFact;
import org.drools.workbench.models.datamodel.rule.ActionSetField;
import org.drools.workbench.models.datamodel.rule.CompositeFactPattern;
import org.drools.workbench.models.datamodel.rule.CompositeFieldConstraint;
import org.drools.workbench.models.datamodel.rule.ConnectiveConstraint;
import org.drools.workbench.models.datamodel.rule.DSLSentence;
import org.drools.workbench.models.datamodel.rule.FactPattern;
import org.drools.workbench.models.datamodel.rule.FieldConstraint;
import org.drools.workbench.models.datamodel.rule.FreeFormLine;
import org.drools.workbench.models.datamodel.rule.FromAccumulateCompositeFactPattern;
import org.drools.workbench.models.datamodel.rule.FromCollectCompositeFactPattern;
import org.drools.workbench.models.datamodel.rule.FromCompositeFactPattern;
import org.drools.workbench.models.datamodel.rule.IAction;
import org.drools.workbench.models.datamodel.rule.IFactPattern;
import org.drools.workbench.models.datamodel.rule.IPattern;
import org.drools.workbench.models.datamodel.rule.RuleAttribute;
import org.drools.workbench.models.datamodel.rule.SingleFieldConstraint;
import org.drools.workbench.models.datamodel.rule.SingleFieldConstraintEBLeftSide;
import org.drools.workbench.models.guided.template.shared.TemplateModel;
import org.kie.soup.commons.validation.PortablePreconditions;
import org.kie.soup.project.datamodel.imports.Import;
import org.kie.workbench.common.services.refactoring.ResourceReference;
import org.kie.workbench.common.services.refactoring.backend.server.impact.ResourceReferenceCollector;
import org.kie.workbench.common.services.refactoring.backend.server.indexing.AttributeIndexBuilder;
import org.kie.workbench.common.services.refactoring.backend.server.indexing.DefaultIndexBuilder;
import org.kie.workbench.common.services.refactoring.service.PartType;
import org.kie.workbench.common.services.refactoring.service.ResourceType;
import org.uberfire.ext.metadata.model.KProperty;

/**
 * Visitor to extract index information from a Guided Rule Model
 */
public class GuidedRuleTemplateIndexVisitor extends ResourceReferenceCollector {

    private final DefaultIndexBuilder builder;
    private final TemplateModel model;

    public GuidedRuleTemplateIndexVisitor(final DefaultIndexBuilder builder,
                                          final TemplateModel model) {
        this.builder = PortablePreconditions.checkNotNull("builder",
                                                          builder);
        this.model = PortablePreconditions.checkNotNull("model",
                                                        model);
    }

    public Set<KProperty<?>> visit() {
        visit(model);
        return builder.build();
    }

    private void visit(final Object o) {
        if (o instanceof TemplateModel) {
            visitRuleModel((TemplateModel) o);
        } else if (o instanceof RuleAttribute) {
            visitRuleAttribute((RuleAttribute) o);
        } else if (o instanceof FactPattern) {
            visitFactPattern((FactPattern) o);
        } else if (o instanceof CompositeFieldConstraint) {
            visitCompositeFieldConstraint((CompositeFieldConstraint) o);
        } else if (o instanceof SingleFieldConstraintEBLeftSide) {
            visitSingleFieldConstraint((SingleFieldConstraintEBLeftSide) o);
        } else if (o instanceof SingleFieldConstraint) {
            visitSingleFieldConstraint((SingleFieldConstraint) o);
        } else if (o instanceof ConnectiveConstraint) {
            visitConnectiveConstraint((ConnectiveConstraint) o);
        } else if (o instanceof CompositeFactPattern) {
            visitCompositeFactPattern((CompositeFactPattern) o);
        } else if (o instanceof FreeFormLine) {
            visitFreeFormLine((FreeFormLine) o);
        } else if (o instanceof FromAccumulateCompositeFactPattern) {
            visitFromAccumulateCompositeFactPattern((FromAccumulateCompositeFactPattern) o);
        } else if (o instanceof FromCollectCompositeFactPattern) {
            visitFromCollectCompositeFactPattern((FromCollectCompositeFactPattern) o);
        } else if (o instanceof FromCompositeFactPattern) {
            visitFromCompositeFactPattern((FromCompositeFactPattern) o);
        } else if (o instanceof DSLSentence) {
            visitDSLSentence((DSLSentence) o);
        } else if (o instanceof ActionInsertFact) {
            visitActionFieldList((ActionInsertFact) o);
        }
    }

    private void visitRuleAttribute(final RuleAttribute attr) {
        new AttributeIndexBuilder(builder).visit(attr.getAttributeName(), attr.getValue());
    }

    //ActionInsertFact, ActionSetField, ActionUpdateField
    private void visitActionFieldList(final ActionInsertFact afl) {
        String fullyQualifiedClassName = getFullyQualifiedClassName(afl.getFactType());
        addResourceReference(fullyQualifiedClassName,
                             ResourceType.JAVA);
    }

    private void visitActionFieldList(final String fullyQualifiedClassName,
                                      final ActionSetField afl) {
        for (ActionFieldValue afv : afl.getFieldValues()) {
            visit(fullyQualifiedClassName,
                  afv);
        }
    }

    private void visitCompositeFactPattern(final CompositeFactPattern pattern) {
        String fullyQualifiedClassName = getFullyQualifiedClassName(pattern.getType());
        addResourceReference(fullyQualifiedClassName,
                             ResourceType.JAVA);
        if (pattern.getPatterns() != null) {
            for (IFactPattern fp : pattern.getPatterns()) {
                visit(fp);
            }
        }
    }

    private void visitCompositeFieldConstraint(final CompositeFieldConstraint cfc) {
        if (cfc.getConstraints() != null) {
            for (int i = 0; i < cfc.getConstraints().length; i++) {
                FieldConstraint fc = cfc.getConstraints()[i];
                visit(fc);
            }
        }
    }

    private void visitDSLSentence(final DSLSentence sentence) {
        //TODO - Index DSLSentences
    }

    private void visitFactPattern(final FactPattern pattern) {
        String fullyQualifiedClassName = getFullyQualifiedClassName(pattern.getFactType());
        addResourceReference(fullyQualifiedClassName,
                             ResourceType.JAVA);
        for (FieldConstraint fc : pattern.getFieldConstraints()) {
            visit(fc);
        }
    }

    private void visitFreeFormLine(final FreeFormLine ffl) {
        //TODO - Index FreeFormLines
    }

    private void visitFromAccumulateCompositeFactPattern(final FromAccumulateCompositeFactPattern pattern) {
        visit(pattern.getFactPattern());
        visit(pattern.getExpression());
        visit(pattern.getSourcePattern());
    }

    private void visitFromCollectCompositeFactPattern(final FromCollectCompositeFactPattern pattern) {
        visit(pattern.getExpression());
        visit(pattern.getFactPattern());
        visit(pattern.getRightPattern());
    }

    private void visitFromCompositeFactPattern(final FromCompositeFactPattern pattern) {
        visit(pattern.getExpression());
        visit(pattern.getFactPattern());
    }

    public void visitRuleModel(final TemplateModel model) {
        //Add Attributes
        if (model.attributes != null) {
            for (int i = 0; i < model.attributes.length; i++) {
                RuleAttribute attr = model.attributes[i];
                visit(attr);
            }
        }
        //Add Types and Fields used by LHS
        if (model.lhs != null) {
            for (int i = 0; i < model.lhs.length; i++) {
                IPattern pattern = model.lhs[i];
                visit(pattern);
            }
        }
        //Add Types and Fields used by RHS
        if (model.rhs != null) {
            for (int i = 0; i < model.rhs.length; i++) {
                IAction action = model.rhs[i];
                if (action instanceof ActionSetField) {
                    final ActionSetField asf = (ActionSetField) action;
                    final String typeName = getTypeNameForBinding(asf.getVariable());
                    if (typeName != null) {
                        final String fullyQualifiedClassName = getFullyQualifiedClassName(typeName);
                        visitActionFieldList(fullyQualifiedClassName,
                                             asf);
                    }
                } else {
                    visit(action);
                }
            }
        }
        //Add rule names
        final String parentRuleName = model.parentName;
        for (int i = 0; i < model.getRowsCount(); i++) {
            final String ruleName = model.name + "_" + i;
            addResourceReference(ruleName,
                                 ResourceType.RULE);
            if (parentRuleName != null) {
                addResourceReference(parentRuleName,
                                     ResourceType.RULE);
            }
        }
    }

    private String getTypeNameForBinding(final String binding) {
        if (model.getAllLHSVariables().contains(binding)) {
            return model.getLHSBindingType(binding);
        } else if (model.getAllRHSVariables().contains(binding)) {
            return model.getRHSBoundFact(binding).getFactType();
        }
        return null;
    }

    private void visitSingleFieldConstraint(final SingleFieldConstraint sfc) {
        ResourceReference resRef = addResourceReference(getFullyQualifiedClassName(sfc.getFactType()),
                                                        ResourceType.JAVA);
        resRef.addPartReference(sfc.getFieldName(),
                                PartType.FIELD);
        addResourceReference(getFullyQualifiedClassName(sfc.getFieldType()),
                             ResourceType.JAVA);

        if (sfc.getConnectives() != null) {
            for (int i = 0; i < sfc.getConnectives().length; i++) {
                visit(sfc.getConnectives()[i]);
            }
        }
    }

    private void visitConnectiveConstraint(final ConnectiveConstraint cc) {
        ResourceReference resRef = addResourceReference(getFullyQualifiedClassName(cc.getFactType()),
                                                        ResourceType.JAVA);
        resRef.addPartReference(cc.getFieldName(),
                                PartType.FIELD);
        addResourceReference(getFullyQualifiedClassName(cc.getFieldType()),
                             ResourceType.JAVA);
    }

    private void visitSingleFieldConstraint(final SingleFieldConstraintEBLeftSide sfexp) {
        visit(sfexp.getExpressionLeftSide());
        visit(sfexp.getExpressionValue());
        if (sfexp.getConnectives() != null) {
            for (int i = 0; i < sfexp.getConnectives().length; i++) {
                visit(sfexp.getConnectives()[i]);
            }
        }
    }

    private void visit(final String fullyQualifiedClassName,
                       final ActionFieldValue afv) {
        ResourceReference resRef = addResourceReference(fullyQualifiedClassName,
                                                        ResourceType.JAVA);
        resRef.addPartReference(afv.getField(),
                                PartType.FIELD);
        addResourceReference(getFullyQualifiedClassName(afv.getType()),
                             ResourceType.JAVA);
    }

    private String getFullyQualifiedClassName(final String typeName) {
        if (typeName.contains(".")) {
            return typeName;
        }

        for (Import i : model.getImports().getImports()) {
            if (i.getType().endsWith(typeName)) {
                return i.getType();
            }
        }
        final String packageName = model.getPackageName();
        return (!(packageName == null || packageName.isEmpty()) ? packageName + "." + typeName : typeName);
    }
}
