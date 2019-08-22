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
package org.drools.workbench.screens.guided.dtable.backend.server;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.drools.workbench.models.datamodel.rule.IAction;
import org.drools.workbench.models.datamodel.rule.IPattern;
import org.drools.workbench.models.datamodel.rule.RuleModel;
import org.drools.workbench.models.guided.dtable.shared.model.ActionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionInsertFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.BRLActionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLConditionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.CompositeColumn;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.screens.guided.rule.backend.server.GuidedRuleModelVisitor;
import org.kie.soup.commons.validation.PortablePreconditions;
import org.kie.soup.project.datamodel.imports.Import;
import org.kie.soup.project.datamodel.imports.Imports;

/**
 * A RuleModel Visitor to identify fully qualified class names used by the RuleModel
 */
public class GuidedDecisionTableModelVisitor {

    private final GuidedDecisionTable52 model;
    private final String packageName;
    private final Imports imports;

    public GuidedDecisionTableModelVisitor(final GuidedDecisionTable52 model) {
        this.model = PortablePreconditions.checkNotNull("model",
                                                        model);
        this.packageName = model.getPackageName();
        this.imports = model.getImports();
    }

    public Set<String> getConsumedModelClasses() {
        final Set<String> factTypes = new HashSet<String>();

        //Extract Fact Types from model
        for (CompositeColumn<?> cc : model.getConditions()) {

            if (cc instanceof BRLConditionColumn) {
                final List<IPattern> definition = ((BRLConditionColumn) cc).getDefinition();
                factTypes.addAll(getConditionFactTypesFromRuleModel(definition));
            } else if (cc instanceof Pattern52) {
                factTypes.add(((Pattern52) cc).getFactType());
            }
        }
        for (ActionCol52 c : model.getActionCols()) {

            if (c instanceof BRLActionColumn) {
                final List<IAction> definition = ((BRLActionColumn) c).getDefinition();
                factTypes.addAll(getActionFactTypesFromRuleModel(definition));
            } else if (c instanceof ActionInsertFactCol52) {
                factTypes.add(((ActionInsertFactCol52) c).getFactType());
            }
        }

        //Convert Fact Types into Fully Qualified Class Names
        final Set<String> fullyQualifiedClassNames = new HashSet<String>();
        for (String factType : factTypes) {
            fullyQualifiedClassNames.add(convertToFullyQualifiedClassName(factType));
        }

        return fullyQualifiedClassNames;
    }

    //Get the fully qualified class name of the fact type
    private String convertToFullyQualifiedClassName(final String factType) {
        if (factType.contains(".")) {
            return factType;
        }
        String fullyQualifiedClassName = null;
        for (Import imp : imports.getImports()) {
            if (imp.getType().endsWith("." + factType)) {
                fullyQualifiedClassName = imp.getType();
                break;
            }
        }
        if (fullyQualifiedClassName == null) {
            fullyQualifiedClassName = packageName + "." + factType;
        }
        return fullyQualifiedClassName;
    }

    private Set<String> getConditionFactTypesFromRuleModel(final List<IPattern> definition) {
        final RuleModel rm = new RuleModel();
        rm.setPackageName(model.getPackageName());
        rm.setImports(model.getImports());
        for (IPattern p : definition) {
            rm.addLhsItem(p);
        }
        final GuidedRuleModelVisitor visitor = new GuidedRuleModelVisitor(rm);
        return visitor.getConsumedModelClasses();
    }

    private Set<String> getActionFactTypesFromRuleModel(final List<IAction> definition) {
        final RuleModel rm = new RuleModel();
        rm.setPackageName(model.getPackageName());
        rm.setImports(model.getImports());
        for (IAction a : definition) {
            rm.addRhsItem(a);
        }
        final GuidedRuleModelVisitor visitor = new GuidedRuleModelVisitor(rm);
        return visitor.getConsumedModelClasses();
    }
}
