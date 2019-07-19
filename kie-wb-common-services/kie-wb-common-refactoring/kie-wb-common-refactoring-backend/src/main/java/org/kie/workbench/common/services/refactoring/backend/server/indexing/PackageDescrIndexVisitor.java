/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.services.refactoring.backend.server.indexing;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.drools.compiler.compiler.DrlExprParser;
import org.drools.compiler.lang.descr.AccumulateDescr;
import org.drools.compiler.lang.descr.AccumulateDescr.AccumulateFunctionCallDescr;
import org.drools.compiler.lang.descr.AccumulateImportDescr;
import org.drools.compiler.lang.descr.ActionDescr;
import org.drools.compiler.lang.descr.AndDescr;
import org.drools.compiler.lang.descr.AnnotatedBaseDescr;
import org.drools.compiler.lang.descr.AnnotationDescr;
import org.drools.compiler.lang.descr.AtomicExprDescr;
import org.drools.compiler.lang.descr.AttributeDescr;
import org.drools.compiler.lang.descr.BaseDescr;
import org.drools.compiler.lang.descr.BehaviorDescr;
import org.drools.compiler.lang.descr.BindingDescr;
import org.drools.compiler.lang.descr.CollectDescr;
import org.drools.compiler.lang.descr.ConditionalBranchDescr;
import org.drools.compiler.lang.descr.ConnectiveDescr;
import org.drools.compiler.lang.descr.ConstraintConnectiveDescr;
import org.drools.compiler.lang.descr.DeclarativeInvokerDescr;
import org.drools.compiler.lang.descr.EntryPointDeclarationDescr;
import org.drools.compiler.lang.descr.EntryPointDescr;
import org.drools.compiler.lang.descr.EnumDeclarationDescr;
import org.drools.compiler.lang.descr.EnumLiteralDescr;
import org.drools.compiler.lang.descr.EvalDescr;
import org.drools.compiler.lang.descr.EvaluatorBasedRestrictionDescr;
import org.drools.compiler.lang.descr.ExistsDescr;
import org.drools.compiler.lang.descr.ExprConstraintDescr;
import org.drools.compiler.lang.descr.FactTemplateDescr;
import org.drools.compiler.lang.descr.FieldConstraintDescr;
import org.drools.compiler.lang.descr.FieldTemplateDescr;
import org.drools.compiler.lang.descr.ForFunctionDescr;
import org.drools.compiler.lang.descr.ForallDescr;
import org.drools.compiler.lang.descr.FromDescr;
import org.drools.compiler.lang.descr.FunctionDescr;
import org.drools.compiler.lang.descr.FunctionImportDescr;
import org.drools.compiler.lang.descr.GlobalDescr;
import org.drools.compiler.lang.descr.ImportDescr;
import org.drools.compiler.lang.descr.LiteralDescr;
import org.drools.compiler.lang.descr.LiteralRestrictionDescr;
import org.drools.compiler.lang.descr.MVELExprDescr;
import org.drools.compiler.lang.descr.NamedConsequenceDescr;
import org.drools.compiler.lang.descr.NotDescr;
import org.drools.compiler.lang.descr.OrDescr;
import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.compiler.lang.descr.PatternDescr;
import org.drools.compiler.lang.descr.PredicateDescr;
import org.drools.compiler.lang.descr.QualifiedIdentifierRestrictionDescr;
import org.drools.compiler.lang.descr.QueryDescr;
import org.drools.compiler.lang.descr.RelationalExprDescr;
import org.drools.compiler.lang.descr.RestrictionConnectiveDescr;
import org.drools.compiler.lang.descr.ReturnValueRestrictionDescr;
import org.drools.compiler.lang.descr.RuleDescr;
import org.drools.compiler.lang.descr.TypeDeclarationDescr;
import org.drools.compiler.lang.descr.TypeFieldDescr;
import org.drools.compiler.lang.descr.UnitDescr;
import org.drools.compiler.lang.descr.VariableRestrictionDescr;
import org.drools.compiler.lang.descr.WindowDeclarationDescr;
import org.drools.compiler.lang.descr.WindowReferenceDescr;
import org.kie.internal.builder.conf.LanguageLevelOption;
import org.kie.soup.commons.validation.PortablePreconditions;
import org.kie.soup.project.datamodel.oracle.ModelField;
import org.kie.soup.project.datamodel.oracle.ModuleDataModelOracle;
import org.kie.workbench.common.services.refactoring.ResourceReference;
import org.kie.workbench.common.services.refactoring.SharedPart;
import org.kie.workbench.common.services.refactoring.backend.server.impact.ResourceReferenceCollector;
import org.kie.workbench.common.services.refactoring.service.PartType;
import org.kie.workbench.common.services.refactoring.service.ResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.ext.metadata.model.KProperty;

public class PackageDescrIndexVisitor extends ResourceReferenceCollector {

    private static final Logger logger = LoggerFactory.getLogger(PackageDescrIndexVisitor.class);

    private final ModuleDataModelOracle dmo;
    private final DefaultIndexBuilder builder;
    private final PackageDescr packageDescr;
    private final PackageDescrIndexVisitorContext context = new PackageDescrIndexVisitorContext();

    private static class PackageDescrIndexVisitorContext {

        private Deque<PatternDescr> patterns = new ArrayDeque<PatternDescr>();
        private Map<String, String> boundTypes = new HashMap<String, String>();

        void startPattern(final PatternDescr descr) {
            patterns.add(descr);
        }

        void endPattern() {
            patterns.pop();
        }

        PatternDescr getCurrentPattern() {
            return patterns.peek();
        }

        void addBoundType(final String identifier,
                          final String fullyQualifiedClassName) {
            boundTypes.put(identifier,
                           fullyQualifiedClassName);
        }

        boolean isBoundType(final String identifier) {
            return boundTypes.containsKey(identifier);
        }

        String getBoundType(final String identifier) {
            return boundTypes.get(identifier);
        }
    }

    public PackageDescrIndexVisitor(final ModuleDataModelOracle dmo,
                                    final DefaultIndexBuilder builder,
                                    final PackageDescr packageDescr) {
        this.dmo = PortablePreconditions.checkNotNull("dmo",
                                                      dmo);
        this.builder = PortablePreconditions.checkNotNull("builder",
                                                          builder);
        this.packageDescr = PortablePreconditions.checkNotNull("packageDescr",
                                                               packageDescr);
    }

    public Set<KProperty<?>> visit() {
        visit(packageDescr);
        return builder.build();
    }

    // pkg-scope to be able to test
    void visit(final Object descr) {
        if (descr instanceof AccumulateDescr) {
            visit((AccumulateDescr) descr);
        } else if (descr instanceof AccumulateImportDescr) {
            visit((AccumulateImportDescr) descr);
        } else if (descr instanceof ActionDescr) {
            visit((ActionDescr) descr);
        } else if (descr instanceof AndDescr) {
            visit((AndDescr) descr);
        } else if (descr instanceof AnnotationDescr) {
            visit((AnnotationDescr) descr);
        } else if (descr instanceof AtomicExprDescr) {
            visit((AtomicExprDescr) descr);
        } else if (descr instanceof AttributeDescr) {
            visit((AttributeDescr) descr);
        } else if (descr instanceof BehaviorDescr) {
            visit((BehaviorDescr) descr);
        } else if (descr instanceof BindingDescr) {
            visit((BindingDescr) descr);
        } else if (descr instanceof CollectDescr) {
            visit((CollectDescr) descr);
        } else if (descr instanceof ConditionalBranchDescr) {
            visit((ConditionalBranchDescr) descr);
        } else if (descr instanceof ConnectiveDescr) {
            visit((ConnectiveDescr) descr);
        } else if (descr instanceof ConstraintConnectiveDescr) {
            visit((ConstraintConnectiveDescr) descr);
        } else if (descr instanceof EntryPointDeclarationDescr) {
            visit((EntryPointDeclarationDescr) descr);
        } else if (descr instanceof EntryPointDescr) {
            visit((EntryPointDescr) descr);
        } else if (descr instanceof EnumDeclarationDescr) {
            visit((EnumDeclarationDescr) descr);
        } else if (descr instanceof EnumLiteralDescr) {
            visit((EnumLiteralDescr) descr);
        } else if (descr instanceof ExistsDescr) {
            visit((ExistsDescr) descr);
        } else if (descr instanceof ExprConstraintDescr) {
            visit((ExprConstraintDescr) descr);
        } else if (descr instanceof EvalDescr) {
            visit((EvalDescr) descr);
        } else if (descr instanceof FactTemplateDescr) {
            visit((FactTemplateDescr) descr);
        } else if (descr instanceof FieldConstraintDescr) {
            visit((FieldConstraintDescr) descr);
        } else if (descr instanceof FieldTemplateDescr) {
            visit((FieldTemplateDescr) descr);
        } else if (descr instanceof ForallDescr) {
            visit((ForallDescr) descr);
        } else if (descr instanceof ForFunctionDescr) {
            visit((ForFunctionDescr) descr);
        } else if (descr instanceof FromDescr) {
            visit((FromDescr) descr);
        } else if (descr instanceof FunctionDescr) {
            visit((FunctionDescr) descr);
        } else if (descr instanceof FunctionImportDescr) {
            visit((FunctionImportDescr) descr);
        } else if (descr instanceof GlobalDescr) {
            visit((GlobalDescr) descr);
        } else if (descr instanceof ImportDescr) {
            visit((ImportDescr) descr);
        } else if (descr instanceof LiteralDescr) {
            visit((LiteralDescr) descr);
        } else if (descr instanceof LiteralRestrictionDescr) {
            visit((LiteralRestrictionDescr) descr);
        } else if (descr instanceof MVELExprDescr) {
            visit((MVELExprDescr) descr);
        } else if (descr instanceof NamedConsequenceDescr) {
            visit((NamedConsequenceDescr) descr);
        } else if (descr instanceof NotDescr) {
            visit((NotDescr) descr);
        } else if (descr instanceof OrDescr) {
            visit((OrDescr) descr);
        } else if (descr instanceof PackageDescr) {
            visit((PackageDescr) descr);
        } else if (descr instanceof PatternDescr) {
            visit((PatternDescr) descr);
        } else if (descr instanceof PredicateDescr) {
            visit((PredicateDescr) descr);
        } else if (descr instanceof QueryDescr) {
            visit((QueryDescr) descr);
        } else if (descr instanceof QualifiedIdentifierRestrictionDescr) {
            visit((QualifiedIdentifierRestrictionDescr) descr);
        } else if (descr instanceof RelationalExprDescr) {
            visit((RelationalExprDescr) descr);
        } else if (descr instanceof RestrictionConnectiveDescr) {
            visit((RestrictionConnectiveDescr) descr);
        } else if (descr instanceof ReturnValueRestrictionDescr) {
            visit((ReturnValueRestrictionDescr) descr);
        } else if (descr instanceof TypeDeclarationDescr) {
            visit((TypeDeclarationDescr) descr);
        } else if (descr instanceof TypeFieldDescr) {
            visit((TypeFieldDescr) descr);
        } else if (descr instanceof UnitDescr) {
            visit((UnitDescr) descr);
        } else if (descr instanceof VariableRestrictionDescr) {
            visit((VariableRestrictionDescr) descr);
        } else if (descr instanceof WindowDeclarationDescr) {
            visit((WindowDeclarationDescr) descr);
        } else if (descr instanceof WindowReferenceDescr) {
            visit((WindowReferenceDescr) descr);

            // extended by other Descr impls -- should be checked last!!
        } else if (descr instanceof AnnotatedBaseDescr) {
            visit((AnnotatedBaseDescr) descr);
        } else if (descr instanceof DeclarativeInvokerDescr) {
            visit((DeclarativeInvokerDescr) descr);
        } else if (descr instanceof EvaluatorBasedRestrictionDescr) {
            visit((EvaluatorBasedRestrictionDescr) descr);
            // RestrictionDescr's are always extended
            // see the visit(..) methods for all of the RestrictionDescr implementation/extension classes
        } else if (descr instanceof RuleDescr) {
            visit((RuleDescr) descr);
        } else {
            logger.error("Not visiting '" + descr.getClass().getName() + "' when indexing rule.");
        }
    }

    protected void visit(final AccumulateDescr descr) {
        visit(descr.getInputPattern());
        for (AccumulateFunctionCallDescr accFuncCallDescr : descr.getFunctions()) {
            visit(accFuncCallDescr);
        }
    }

    protected void visit(final AccumulateFunctionCallDescr descr) {
        // TODO
    }

    protected void visit(final AccumulateImportDescr descr) {
        // add the java class where the accumulate function is defined
        addResourceReference(descr.getTarget(),
                             ResourceType.JAVA);
        // TODO: not sure this is correct..
        addResourceReference(descr.getFunctionName(),
                             ResourceType.FUNCTION);
    }

    protected void visit(final ActionDescr descr) {
        // TODO
    }

    protected void visit(final AndDescr descr) {
        for (BaseDescr baseDescr : descr.getDescrs()) {
            visit(baseDescr);
        }
        visitAnnos(descr);
    }

    protected void visit(final AnnotatedBaseDescr descr) {
        for (AnnotationDescr annoDescr : descr.getAnnotations()) {
            visit(annoDescr);
        }
    }

    protected void visit(final AnnotationDescr descr) {
        addResourceReference(descr.getName(),
                             ResourceType.DRL_ANNOTATION);
    }

    protected void visit(final AtomicExprDescr descr) {
        String expression = descr.getExpression();
        parseExpression(expression);
    }

    protected void visit(final AttributeDescr descr) {
        new AttributeIndexBuilder(builder).visit(descr);
    }

    protected void visit(final BehaviorDescr descr) {
        // TODO
    }

    protected void visit(final BindingDescr descr) {
        final String identifier = descr.getVariable();
        final String fullyQualifiedClassName = parseExpression(descr.getExpression());
        // expression can be an eval, which does not result in a FQ class name
        if (fullyQualifiedClassName != null) {
            context.addBoundType(identifier,
                                 fullyQualifiedClassName);
        }
    }

    protected void visit(final CollectDescr descr) {
        visit(descr.getInputPattern());
        for (BaseDescr d : descr.getDescrs()) {
            visit(d);
        }
    }

    protected void visit(final ConditionalBranchDescr descr) {
        visit(descr.getCondition());
        visit(descr.getConsequence());
        ConditionalBranchDescr elseBranch = descr.getElseBranch();
        if (elseBranch != null) {
            visit(elseBranch);
        }
    }

    protected void visit(final ConnectiveDescr descr) {
        // TODO
        // Although, the connective is just a a bunch of strings, so do nothing?
    }

    protected void visit(final ConstraintConnectiveDescr descr) {
        for (BaseDescr d : descr.getDescrs()) {
            visit(d);
        }
        visitAnnos(descr);
    }

    protected void visit(final DeclarativeInvokerDescr descr) {
        // TODO
    }

    protected void visit(final EntryPointDeclarationDescr descr) {
        addSharedReference(descr.getEntryPointId(),
                           PartType.ENTRY_POINT);
        for (AnnotationDescr annoDescr : descr.getAnnotations()) {
            visit(annoDescr);
        }
        visitAnnos(descr);
    }

    protected void visit(final EntryPointDescr descr) {
        addSharedReference(descr.getEntryId(),
                           PartType.ENTRY_POINT);
    }

    protected void visit(final EnumDeclarationDescr descr) {
        String typeName = getPackagePrefix() + descr.getFullTypeName();
        ResourceReference enumLocalRef = addResourceReference(typeName,
                                                              ResourceType.DRL_ENUM);
        for (EnumLiteralDescr enumLitDescr : descr.getLiterals()) {
            enumLocalRef.addPartReference(enumLitDescr.getName(),
                                          PartType.DRL_ENUM_VAL);
            visitAnnos(enumLitDescr);
        }
        visitAnnos(descr);
    }

    protected void visitAnnos(final AnnotatedBaseDescr descr) {
        for (AnnotationDescr annoDescr : descr.getAnnotations()) {
            visit(annoDescr);
        }
    }

    protected void visit(final EnumLiteralDescr descr) {
        String name = descr.getName();
        // TODO
        visitAnnos(descr);
    }

    protected void visit(final EvalDescr descr) {
        // TODO
    }

    protected void visit(final EvaluatorBasedRestrictionDescr descr) {
        // TODO
    }

    protected void visit(final ExistsDescr descr) {
        // ExistsDescr isn't type-safe
        for (Object o : descr.getDescrs()) {
            visit(o);
        }
        visitAnnos(descr);
    }

    protected void visit(final ExprConstraintDescr descr) {
        DrlExprParser parser = new DrlExprParser(LanguageLevelOption.DRL6);
        ConstraintConnectiveDescr result = parser.parse(descr.getExpression());
        visit(result);
    }

    protected void visit(final FactTemplateDescr descr) {
        for (FieldTemplateDescr d : descr.getFields()) {
            visit(d);
        }
    }

    protected void visit(final FieldConstraintDescr descr) {
        // FieldConstraintDescr isn't type-safe
        for (Object o : descr.getRestrictions()) {
            visit(o);
        }
    }

    protected void visit(final FieldTemplateDescr descr) {
        // TODO
    }

    protected void visit(final ForallDescr descr) {
        visit(descr.getBasePattern());
        for (BaseDescr o : descr.getDescrs()) {
            visit(o);
        }
    }

    protected void visit(final ForFunctionDescr descr) {
        // TODO
    }

    protected void visit(final FromDescr descr) {
        // TODO
        visit(descr.getDataSource());
    }

    protected void visit(final FunctionDescr descr) {
        String funcName = getPackagePrefix() + descr.getName();
        addResource(funcName,
                    ResourceType.FUNCTION);
    }

    protected void visit(final FunctionImportDescr descr) {
        String funcName = descr.getTarget();
        addResourceReference(funcName,
                             ResourceType.FUNCTION);
    }

    protected void visit(final GlobalDescr descr) {
        String fqcn = getFullyQualifiedClassName(descr.getType());
        addResourceReference(fqcn,
                             ResourceType.JAVA);
    }

    protected void visit(final ImportDescr descr) {
        String importStr = descr.getTarget();
        if (!importStr.endsWith("*")) {
            addResourceReference(descr.getTarget(),
                                 ResourceType.JAVA);
        } else {
            logger.debug("Wildcard import encountered : '" + importStr + "'");
        }
    }

    protected void visit(final LiteralDescr descr) {
        // TODO
    }

    protected void visit(final LiteralRestrictionDescr descr) {
        // TODO - Not yet implemented
    }

    protected void visit(final MVELExprDescr descr) {
        // TODO - this will require drools-compiler/dialect support?
    }

    protected void visit(final NamedConsequenceDescr descr) {
        String name = descr.getText();
        // TODO
    }

    protected void visit(final NotDescr descr) {
        // NotDescr isn't type-safe
        for (Object o : descr.getDescrs()) {
            visit(o);
        }
        visitAnnos(descr);
    }

    protected void visit(final OrDescr descr) {
        for (BaseDescr d : descr.getDescrs()) {
            visit(d);
        }
        visitAnnos(descr);
    }

    protected void visit(final PackageDescr descr) {
        for (AccumulateImportDescr accImportDescr : descr.getAccumulateImports()) {
            visit(accImportDescr);
        }
        for (AttributeDescr attrDescr : descr.getAttributes()) {
            visit(attrDescr);
        }
        for (EntryPointDeclarationDescr entryPointDeclDescr : descr.getEntryPointDeclarations()) {
            visit(entryPointDeclDescr);
        }
        for (EnumDeclarationDescr enumDeclDescr : descr.getEnumDeclarations()) {
            visit(enumDeclDescr);
        }
        for (FunctionDescr funcDescr : descr.getFunctions()) {
            visit(funcDescr);
        }
        for (FunctionImportDescr funcImportDescr : descr.getFunctionImports()) {
            visit(funcImportDescr);
        }
        for (GlobalDescr globalDescr : descr.getGlobals()) {
            visit(globalDescr);
        }
        for (ImportDescr importDescr : descr.getImports()) {
            visit(importDescr);
        }
        for (RuleDescr ruleDescr : descr.getRules()) {
            visit(ruleDescr);
        }
        for (TypeDeclarationDescr typeDeclDescr : descr.getTypeDeclarations()) {
            visit(typeDeclDescr);
        }
        for (WindowDeclarationDescr windowDeclDescr : descr.getWindowDeclarations()) {
            visit(windowDeclDescr);
        }
    }

    protected void visit(final PatternDescr descr) {
        context.startPattern(descr);
        final String fullyQualifiedClassName = getFullyQualifiedClassName(descr.getObjectType());
        if (!(descr.getIdentifier() == null || descr.getIdentifier().isEmpty())) {
            context.addBoundType(descr.getIdentifier(),
                                 fullyQualifiedClassName);
        }
        addResourceReference(fullyQualifiedClassName,
                             ResourceType.JAVA);
        visit(descr.getConstraint());
        context.endPattern();
        visitAnnos(descr);
    }

    protected void visit(final PredicateDescr descr) {
        // TODO
    }

    protected void visit(final QualifiedIdentifierRestrictionDescr descr) {
        // TODO
    }

    protected void visit(final QueryDescr descr) {
        visit(descr.getLhs());
        visitAnnos(descr);
    }

    protected void visit(final RestrictionConnectiveDescr descr) {
        // TODO
    }

    protected void visit(final RelationalExprDescr descr) {
        visit(descr.getLeft());
        visit(descr.getRight());
    }

    protected void visit(final ReturnValueRestrictionDescr descr) {
        // TODO
    }

    protected void visit(final RuleDescr descr) {
        // This is the NAME of the rule, not a reference to it!!
        String ruleName = getPackagePrefix() + descr.getName();
        addResource(ruleName,
                    ResourceType.RULE);

        // This is, on other hand, is a reference to the parent rule (because it's used in inheritance)
        String parentRuleName = descr.getParentName();
        if (parentRuleName != null) {
            addResourceReference(parentRuleName,
                                 ResourceType.RULE);
        }

        for (AttributeDescr d : descr.getAttributes().values()) {
            visit(d);
        }
        visit(descr.getLhs());
        visitConsequence(descr.getConsequence()); // need compilation for this..
        for (String namedConsequence : descr.getNamedConsequences().keySet()) {
            // TODO
            // ? addResourceReference(namedConsequence, PartType.NAMED_CONSEQUENCE);
        }
        visitAnnos(descr);
    }

    protected void visitConsequence(final Object consequence) {
        // TODO
    }

    protected void visit(final TypeDeclarationDescr descr) {
        String fqcn = getFullyQualifiedClassName(descr.getTypeName());
        addResourceReference(fqcn,
                             ResourceType.JAVA);
        if (!(descr.getSuperTypeName() == null || descr.getSuperTypeName().isEmpty())) {
            fqcn = getFullyQualifiedClassName(descr.getSuperTypeName());
            addResourceReference(fqcn,
                                 ResourceType.JAVA);
        }
        visitAnnos(descr);
    }

    protected void visit(final TypeFieldDescr descr) {
        // TODO
        visitAnnos(descr);
    }

    protected void visit(final UnitDescr descr) {
        String fqcn = getFullyQualifiedClassName(descr.getTarget());
        addResourceReference(fqcn,
                             ResourceType.JAVA);
    }

    protected void visit(final VariableRestrictionDescr descr) {
        // TODO
    }

    protected void visit(final WindowDeclarationDescr descr) {
        visit(descr.getPattern());
    }

    protected void visit(final WindowReferenceDescr descr) {
        // TODO
    }

    private String parseExpression(String expression) {
        String factType = context.getCurrentPattern().getObjectType();
        String fullyQualifiedClassName = getFullyQualifiedClassName(factType);
        if (expression.startsWith("eval")) {
            return null;
        }

        while (expression.contains(".")) {
            String fieldName = expression.substring(0,
                                                    expression.indexOf("."));
            if (context.isBoundType(fieldName)) {
                fullyQualifiedClassName = context.getBoundType(fieldName);
                expression = expression.substring(expression.indexOf(".") + 1);
                continue;
            }
            expression = expression.substring(expression.indexOf(".") + 1);
            factType = addField(fieldName,
                                fullyQualifiedClassName);
            if (factType != null) {
                fullyQualifiedClassName = getFullyQualifiedClassName(factType);
            }
        }
        return addField(expression,
                        fullyQualifiedClassName);
    }

    private String addField(final String fieldName,
                            final String fullyQualifiedClassName) {
        final ModelField[] mfs = dmo.getModuleModelFields().get(fullyQualifiedClassName);
        if (mfs != null) {
            for (ModelField mf : mfs) {
                if (mf.getName().equals(fieldName)) {
                    // add the type of the field owner and the field reference
                    ResourceReference resRef = addResourceReference(fullyQualifiedClassName,
                                                                    ResourceType.JAVA);
                    resRef.addPartReference(fieldName,
                                            PartType.FIELD);
                    // add the type of the field
                    addResourceReference(mf.getClassName(),
                                         ResourceType.JAVA);
                    return mf.getClassName();
                }
            }
        }
        return null;
    }

    private String getFullyQualifiedClassName(final String typeName) {
        if (typeName.contains(".")) {
            return typeName;
        }
        for (ImportDescr importDescr : packageDescr.getImports()) {
            if (importDescr.getTarget().endsWith(typeName)) {
                return importDescr.getTarget();
            }
        }

        // We are guessing, like a blindman playing blackjack... there has to be a better way..
        for (Entry<String, ModelField[]> entry : dmo.getModuleModelFields().entrySet()) {
            ModelField[] mfs = entry.getValue();
            String key = entry.getKey();
            for (ModelField mf : mfs) {
                if (mf.getClassType().equals(ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS)) {
                    if (mf.getClassName().endsWith("." + typeName)) {
                        return mf.getClassName();
                    } else if (mf.getType().endsWith("." + typeName)) {
                        return mf.getType();
                    }
                }
            }
        }
        return packageDescr.getName() + "." + typeName;
    }

    private String getPackagePrefix() {
        String pkgName = packageDescr.getName();
        if (!pkgName.isEmpty()) {
            pkgName = pkgName + ".";
        }
        return pkgName;
    }
}
