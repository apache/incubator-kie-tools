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

package org.kie.workbench.common.stunner.core.processors;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

import org.apache.commons.lang3.StringUtils;
import org.kie.workbench.common.stunner.core.definition.annotation.Definition;
import org.kie.workbench.common.stunner.core.definition.annotation.DefinitionSet;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.definition.annotation.morph.Morph;
import org.kie.workbench.common.stunner.core.definition.annotation.morph.MorphBase;
import org.kie.workbench.common.stunner.core.definition.annotation.morph.MorphProperty;
import org.kie.workbench.common.stunner.core.definition.builder.VoidBuilder;
import org.kie.workbench.common.stunner.core.definition.property.PropertyMetaTypes;
import org.kie.workbench.common.stunner.core.definition.property.PropertyType;
import org.kie.workbench.common.stunner.core.processors.definition.BindableDefinitionAdapterGenerator;
import org.kie.workbench.common.stunner.core.processors.definitionset.BindableDefinitionSetAdapterGenerator;
import org.kie.workbench.common.stunner.core.processors.definitionset.DefinitionSetProxyGenerator;
import org.kie.workbench.common.stunner.core.processors.factory.ModelFactoryGenerator;
import org.kie.workbench.common.stunner.core.processors.morph.MorphDefinitionGenerator;
import org.kie.workbench.common.stunner.core.processors.morph.MorphDefinitionProviderGenerator;
import org.kie.workbench.common.stunner.core.processors.morph.MorphPropertyDefinitionGenerator;
import org.kie.workbench.common.stunner.core.processors.property.BindablePropertyAdapterGenerator;
import org.kie.workbench.common.stunner.core.processors.propertyset.BindablePropertySetAdapterGenerator;
import org.kie.workbench.common.stunner.core.processors.rule.BindableDefinitionSetRuleAdapterGenerator;
import org.kie.workbench.common.stunner.core.processors.rule.CardinalityRuleGenerator;
import org.kie.workbench.common.stunner.core.processors.rule.ConnectionRuleGenerator;
import org.kie.workbench.common.stunner.core.processors.rule.ContainmentRuleGenerator;
import org.kie.workbench.common.stunner.core.processors.rule.DockingRuleGenerator;
import org.kie.workbench.common.stunner.core.processors.rule.EdgeCardinalityRuleGenerator;
import org.kie.workbench.common.stunner.core.processors.rule.ExtensionRuleGenerator;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.uberfire.annotations.processors.AbstractErrorAbsorbingProcessor;
import org.uberfire.annotations.processors.AbstractGenerator;
import org.uberfire.annotations.processors.exceptions.GenerationException;

@SupportedAnnotationTypes({
        MainProcessor.ANNOTATION_DEFINITION_SET,
        MainProcessor.ANNOTATION_DEFINITION,
        MainProcessor.ANNOTATION_PROPERTY_SET,
        MainProcessor.ANNOTATION_PROPERTY,
        MainProcessor.ANNOTATION_RULE_CAN_CONTAIN,
        MainProcessor.ANNOTATION_RULE_CAN_DOCK,
        MainProcessor.ANNOTATION_RULE_EXTENSIONS,
        MainProcessor.ANNOTATION_RULE_EXTENSION,
        MainProcessor.ANNOTATION_RULE_ALLOWED_CONNECTION,
        MainProcessor.ANNOTATION_RULE_CAN_CONNECT,
        MainProcessor.ANNOTATION_RULE_ALLOWED_EDGE_OCCURRS,
        MainProcessor.ANNOTATION_RULE_EDGE_OCCS,
        MainProcessor.ANNOTATION_RULE_ALLOWED_OCCS,
        MainProcessor.ANNOTATION_RULE_OCCS})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class MainProcessor extends AbstractErrorAbsorbingProcessor {

    public static final String ANNOTATION_DESCRIPTION = "org.kie.workbench.common.stunner.core.definition.annotation.Description";
    public static final String ANNOTATION_NAME = "org.kie.workbench.common.stunner.core.definition.annotation.Name";

    public static final String ANNOTATION_DEFINITION_SET = "org.kie.workbench.common.stunner.core.definition.annotation.DefinitionSet";

    public static final String ANNOTATION_DEFINITION = "org.kie.workbench.common.stunner.core.definition.annotation.Definition";
    public static final String ANNOTATION_DEFINITION_ID = "org.kie.workbench.common.stunner.core.definition.annotation.definition.Id";
    public static final String ANNOTATION_DEFINITION_CATEGORY = "org.kie.workbench.common.stunner.core.definition.annotation.definition.Category";
    public static final String ANNOTATION_DEFINITION_LABELS = "org.kie.workbench.common.stunner.core.definition.annotation.definition.Labels";
    public static final String ANNOTATION_DEFINITION_TITLE = "org.kie.workbench.common.stunner.core.definition.annotation.definition.Title";

    public static final String[] DEFINITION_ANNOTATIONS = new String[]{
            ANNOTATION_DEFINITION_ID,
            ANNOTATION_DEFINITION_CATEGORY,
            ANNOTATION_DEFINITION_LABELS,
            ANNOTATION_DEFINITION_TITLE
    };

    public static final String ANNOTATION_PROPERTY_SET = "org.kie.workbench.common.stunner.core.definition.annotation.PropertySet";

    public static final String ANNOTATION_PROPERTY = "org.kie.workbench.common.stunner.core.definition.annotation.Property";
    public static final String ANNOTATION_PROPERTY_ALLOWED_VALUES = "org.kie.workbench.common.stunner.core.definition.annotation.property.AllowedValues";
    public static final String ANNOTATION_PROPERTY_VALUE = "org.kie.workbench.common.stunner.core.definition.annotation.property.Value";
    public static final String ANNOTATION_PROPERTY_CAPTION = "org.kie.workbench.common.stunner.core.definition.annotation.property.Caption";
    public static final String ANNOTATION_PROPERTY_TYPE = "org.kie.workbench.common.stunner.core.definition.annotation.property.Type";
    public static final String ANNOTATION_PROPERTY_READONLY = "org.kie.workbench.common.stunner.core.definition.annotation.property.ReadOnly";
    public static final String ANNOTATION_PROPERTY_OPTIONAL = "org.kie.workbench.common.stunner.core.definition.annotation.property.Optional";

    public static final String ANNOTATION_MORPH = "org.kie.workbench.common.stunner.core.definition.annotation.morph.Morph";
    public static final String ANNOTATION_MORPH_BASE = "org.kie.workbench.common.stunner.core.definition.annotation.morph.MorphBase";
    public static final String ANNOTATION_MORPH_PROPERTY = "org.kie.workbench.common.stunner.core.definition.annotation.morph.MorphProperty";

    public static final String ANNOTATION_RULE_CAN_CONTAIN = "org.kie.workbench.common.stunner.core.rule.annotation.CanContain";
    public static final String ANNOTATION_RULE_CAN_DOCK = "org.kie.workbench.common.stunner.core.rule.annotation.CanDock";
    public static final String ANNOTATION_RULE_EXTENSIONS = "org.kie.workbench.common.stunner.core.rule.annotation.RuleExtensions";
    public static final String ANNOTATION_RULE_EXTENSION = "org.kie.workbench.common.stunner.core.rule.annotation.RuleExtension";
    public static final String ANNOTATION_RULE_ALLOWED_CONNECTION = "org.kie.workbench.common.stunner.core.rule.annotation.AllowedConnections";
    public static final String ANNOTATION_RULE_CAN_CONNECT = "org.kie.workbench.common.stunner.core.rule.annotation.CanConnect";
    public static final String ANNOTATION_RULE_ALLOWED_OCCS = "org.kie.workbench.common.stunner.core.rule.annotation.AllowedOccurrences";
    public static final String ANNOTATION_RULE_OCCS = "org.kie.workbench.common.stunner.core.rule.annotation.Occurrences";
    public static final String ANNOTATION_RULE_ALLOWED_EDGE_OCCURRS = "org.kie.workbench.common.stunner.core.rule.annotation.AllowedEdgeOccurrences";
    public static final String ANNOTATION_RULE_EDGE_OCCS = "org.kie.workbench.common.stunner.core.rule.annotation.EdgeOccurrences";

    public static final String MORPH_DEFINITION_CLASSNAME = "MorphDefinition";
    public static final String MORPH_PROPERTY_DEFINITION_CLASSNAME = "MorphPropertyDefinition";
    public static final String MORPH_PROVIDER_CLASSNAME = "MorphDefinitionProvider";

    public static final String RULE_CONTAINMENT_SUFFIX_CLASSNAME = "ContainmentRule";
    public static final String RULE_DOCKING_SUFFIX_CLASSNAME = "DockingRule";
    public static final String RULE_EXTENSION_SUFFIX_CLASSNAME = "ExtRule";
    public static final String RULE_CONNECTION_SUFFIX_CLASSNAME = "ConnectionRule";
    public static final String RULE_CARDINALITY_SUFFIX_CLASSNAME = "CardinalityRule";
    public static final String RULE_EDGE_CARDINALITY_SUFFIX_CLASSNAME = "EdgeCardinalityRule";

    public static final String DEFINITIONSET_ADAPTER_CLASSNAME = "DefinitionSetAdapterImpl";
    public static final String DEFINITIONSET_PROXY_CLASSNAME = "DefinitionSetProxyImpl";
    public static final String DEFINITION_FACTORY_CLASSNAME = "ModelFactoryImpl";
    public static final String DEFINITION_ADAPTER_CLASSNAME = "DefinitionAdapterImpl";
    public static final String PROPERTYSET_ADAPTER_CLASSNAME = "PropertySetAdapterImpl";
    public static final String PROPERTY_ADAPTER_CLASSNAME = "PropertyAdapterImpl";
    public static final String RULE_ADAPTER_CLASSNAME = "RuleAdapterImpl";

    private final ProcessingContext processingContext = ProcessingContext.getInstance();
    private final ContainmentRuleGenerator containmentRuleGenerator;
    private final ConnectionRuleGenerator connectionRuleGenerator;
    private final CardinalityRuleGenerator cardinalityRuleGenerator;
    private final EdgeCardinalityRuleGenerator edgeCardinalityRuleGenerator;
    private final DockingRuleGenerator dockingRuleGenerator;
    private final ExtensionRuleGenerator extensionRuleGenerator;
    private BindableDefinitionSetAdapterGenerator definitionSetAdapterGenerator;
    private BindableDefinitionAdapterGenerator definitionAdapterGenerator;
    private BindablePropertySetAdapterGenerator propertySetAdapterGenerator;
    private BindablePropertyAdapterGenerator propertyAdapterGenerator;
    private BindableDefinitionSetRuleAdapterGenerator ruleAdapterGenerator;
    private DefinitionSetProxyGenerator definitionSetProxyGenerator;
    private MorphDefinitionGenerator morphDefinitionGenerator;
    private MorphPropertyDefinitionGenerator morphPropertyDefinitionGenerator;
    private MorphDefinitionProviderGenerator morphDefinitionProviderGenerator;
    private ModelFactoryGenerator generatedDefinitionFactoryGenerator;

    public MainProcessor() {
        ContainmentRuleGenerator ruleGenerator = null;
        ConnectionRuleGenerator connectionRuleGenerator = null;
        CardinalityRuleGenerator cardinalityRuleGenerator = null;
        EdgeCardinalityRuleGenerator edgeCardinalityRuleGenerator = null;
        DockingRuleGenerator dockingRuleGenerator = null;
        ExtensionRuleGenerator extensionRuleGenerator = null;
        BindableDefinitionSetAdapterGenerator definitionSetAdapterGenerator = null;
        BindableDefinitionAdapterGenerator definitionAdapterGenerator = null;
        BindablePropertySetAdapterGenerator propertySetAdapterGenerator = null;
        BindablePropertyAdapterGenerator propertyAdapter = null;
        MorphDefinitionGenerator morphDefinitionGenerator = null;
        MorphPropertyDefinitionGenerator morphPropertyDefinitionGenerator = null;
        MorphDefinitionProviderGenerator morphDefinitionProviderGenerator = null;
        BindableDefinitionSetRuleAdapterGenerator ruleAdapter = null;
        DefinitionSetProxyGenerator definitionSetProxyGenerator = null;
        ModelFactoryGenerator generatedDefinitionFactoryGenerator = null;
        try {
            ruleGenerator = new ContainmentRuleGenerator();
            propertyAdapter = new BindablePropertyAdapterGenerator();
            ruleAdapter = new BindableDefinitionSetRuleAdapterGenerator();
            connectionRuleGenerator = new ConnectionRuleGenerator();
            cardinalityRuleGenerator = new CardinalityRuleGenerator();
            edgeCardinalityRuleGenerator = new EdgeCardinalityRuleGenerator();
            dockingRuleGenerator = new DockingRuleGenerator();
            extensionRuleGenerator = new ExtensionRuleGenerator();
            definitionAdapterGenerator = new BindableDefinitionAdapterGenerator();
            definitionSetAdapterGenerator = new BindableDefinitionSetAdapterGenerator();
            propertySetAdapterGenerator = new BindablePropertySetAdapterGenerator();
            definitionSetProxyGenerator = new DefinitionSetProxyGenerator();
            morphDefinitionGenerator = new MorphDefinitionGenerator();
            morphPropertyDefinitionGenerator = new MorphPropertyDefinitionGenerator();
            morphDefinitionProviderGenerator = new MorphDefinitionProviderGenerator();
            generatedDefinitionFactoryGenerator = new ModelFactoryGenerator();
        } catch (Throwable t) {
            rememberInitializationError(t);
        }
        this.containmentRuleGenerator = ruleGenerator;
        this.connectionRuleGenerator = connectionRuleGenerator;
        this.cardinalityRuleGenerator = cardinalityRuleGenerator;
        this.edgeCardinalityRuleGenerator = edgeCardinalityRuleGenerator;
        this.dockingRuleGenerator = dockingRuleGenerator;
        this.extensionRuleGenerator = extensionRuleGenerator;
        this.definitionSetAdapterGenerator = definitionSetAdapterGenerator;
        this.definitionAdapterGenerator = definitionAdapterGenerator;
        this.propertySetAdapterGenerator = propertySetAdapterGenerator;
        this.propertyAdapterGenerator = propertyAdapter;
        this.ruleAdapterGenerator = ruleAdapter;
        this.definitionSetProxyGenerator = definitionSetProxyGenerator;
        this.morphDefinitionGenerator = morphDefinitionGenerator;
        this.morphPropertyDefinitionGenerator = morphPropertyDefinitionGenerator;
        this.morphDefinitionProviderGenerator = morphDefinitionProviderGenerator;
        this.generatedDefinitionFactoryGenerator = generatedDefinitionFactoryGenerator;
    }

    public static String toValidId(final String id) {
        return StringUtils.uncapitalize(id);
    }

    public static String toClassMemberId(final String className) {
        int i = className.lastIndexOf(".");
        String s = i > -1 ? className.substring(i + 1,
                                                className.length()) : className;
        return toValidId(s);
    }

    @Override
    protected boolean processWithExceptions(final Set<? extends TypeElement> set,
                                            final RoundEnvironment roundEnv) throws Exception {
        if (roundEnv.processingOver()) {
            return processLastRound(set,
                                    roundEnv);
        }
        //If prior processing threw an error exit
        if (roundEnv.errorRaised()) {
            return false;
        }
        final Elements elementUtils = processingEnv.getElementUtils();
        // Process Definition Sets types found on the processing environment.
        for (Element e : roundEnv.getElementsAnnotatedWith(elementUtils.getTypeElement(ANNOTATION_DEFINITION_SET))) {
            processDefinitionSets(set,
                                  e,
                                  roundEnv);
        }
        // Process Definition types found on the processing environment.
        for (Element e : roundEnv.getElementsAnnotatedWith(elementUtils.getTypeElement(ANNOTATION_DEFINITION))) {
            processDefinitions(set,
                               e,
                               roundEnv);
        }
        // Process Property Sets types found on the processing environment
        // AND
        // Process Property Sets types identified as dependant types for the annotated Defininitions. Note that
        // those types cannot be directly found on the processing environment if the classes are in a third party
        // dependency and not directly on the module sources.
        final Set<? extends Element> propertySetElements = new LinkedHashSet<Element>() {{
            addAll(roundEnv.getElementsAnnotatedWith(elementUtils.getTypeElement(ANNOTATION_PROPERTY_SET)));
            addAll(processingContext.getPropertySetElements());
        }};
        for (Element e : propertySetElements) {
            processPropertySets(set,
                                e,
                                roundEnv);
        }
        // Process Property types found on the processing environment
        // AND
        // Process PropertySets types identified as dependant types for the annotated Defininitions or Property Sets.
        // Note that // those types cannot be directly found on the processing environment if the classes are in a
        // third party dependency and not directly on the module sources.
        final Set<? extends Element> propertyElements = new LinkedHashSet<Element>() {{
            addAll(roundEnv.getElementsAnnotatedWith(elementUtils.getTypeElement(ANNOTATION_PROPERTY)));
            addAll(processingContext.getPropertyElements());
        }};
        for (Element e : propertyElements) {
            processProperties(set,
                              e,
                              roundEnv);
        }
        final Set<? extends Element> containRules = new LinkedHashSet<Element>() {{
            addAll(roundEnv.getElementsAnnotatedWith(elementUtils.getTypeElement(ANNOTATION_RULE_CAN_CONTAIN)));
            addAll(processingContext.getDefinitionElements());
            removeAll(processingContext.getContainmentRuleElementsProcessed());
        }};
        for (Element e : containRules) {
            processContainmentRules(e);
        }
        final Set<? extends Element> dockRules = new LinkedHashSet<Element>() {{
            addAll(roundEnv.getElementsAnnotatedWith(elementUtils.getTypeElement(ANNOTATION_RULE_CAN_DOCK)));
            addAll(processingContext.getDefinitionElements());
            removeAll(processingContext.getDockingRuleElementsProcessed());
        }};
        for (Element e : dockRules) {
            processDockingRules(e);
        }

        final Set<? extends Element> extRules = new LinkedHashSet<Element>() {{
            addAll(roundEnv.getElementsAnnotatedWith(elementUtils.getTypeElement(ANNOTATION_RULE_EXTENSIONS)));
            addAll(roundEnv.getElementsAnnotatedWith(elementUtils.getTypeElement(ANNOTATION_RULE_EXTENSION)));
        }};
        for (Element e : extRules) {
            processRuleExtension(e);
        }
        final Set<? extends Element> occRules = new LinkedHashSet<Element>() {{
            addAll(roundEnv.getElementsAnnotatedWith(elementUtils.getTypeElement(ANNOTATION_RULE_ALLOWED_OCCS)));
            addAll(roundEnv.getElementsAnnotatedWith(elementUtils.getTypeElement(ANNOTATION_RULE_OCCS)));
        }};
        for (Element e : occRules) {
            processCardinalityRules(e);
        }
        final Set<? extends Element> edgeOccRules = new LinkedHashSet<Element>() {{
            addAll(roundEnv.getElementsAnnotatedWith(elementUtils.getTypeElement(ANNOTATION_RULE_ALLOWED_EDGE_OCCURRS)));
            addAll(roundEnv.getElementsAnnotatedWith(elementUtils.getTypeElement(ANNOTATION_RULE_EDGE_OCCS)));
        }};
        for (Element e : edgeOccRules) {
            processEdgeCardinalityRules(e);
        }
        final Set<? extends Element> cRules = new LinkedHashSet<Element>() {{
            addAll(roundEnv.getElementsAnnotatedWith(elementUtils.getTypeElement(ANNOTATION_RULE_ALLOWED_CONNECTION)));
            addAll(roundEnv.getElementsAnnotatedWith(elementUtils.getTypeElement(ANNOTATION_RULE_CAN_CONNECT)));
        }};
        for (Element e : cRules) {
            processConnectionRules(e);
        }
        return true;
    }

    private boolean processDefinitionSets(final Set<? extends TypeElement> set,
                                          final Element e,
                                          final RoundEnvironment roundEnv) throws Exception {
        final Messager messager = processingEnv.getMessager();
        final boolean isClass = e.getKind() == ElementKind.CLASS;
        if (isClass) {
            TypeElement classElement = (TypeElement) e;
            PackageElement packageElement = (PackageElement) classElement.getEnclosingElement();
            messager.printMessage(Diagnostic.Kind.NOTE,
                                  "Discovered definition set class [" + classElement.getSimpleName() + "]");
            final String packageName = packageElement.getQualifiedName().toString();
            final String className = classElement.getSimpleName().toString();
            processingContext.setDefinitionSet(packageName,
                                               className);
            String defSetClassName = packageName + "." + className;
            // Description fields.
            processFieldName(classElement,
                             defSetClassName,
                             ANNOTATION_DESCRIPTION,
                             processingContext.getDefSetAnnotations().getDescriptionFieldNames(),
                             false);
            // Definitions identifiers.
            DefinitionSet definitionSetAnn = e.getAnnotation(DefinitionSet.class);
            List<? extends TypeMirror> mirrors = null;
            try {
                Class<?>[] defsClasses = definitionSetAnn.definitions();
            } catch (MirroredTypesException mte) {
                mirrors = mte.getTypeMirrors();
            }
            if (null == mirrors) {
                throw new RuntimeException("No graph class class specifyed for the @DefinitionSet.");
            }
            Set<String> defIds = new LinkedHashSet<>();
            for (TypeMirror mirror : mirrors) {
                if (mirror.getKind().equals(TypeKind.DECLARED)) {
                    final TypeElement t = (TypeElement) ((DeclaredType) mirror).asElement();
                    processingContext.getDefinitionElements().add(t);
                }
                String fqcn = mirror.toString();
                defIds.add(fqcn);
            }
            processingContext.getDefSetAnnotations().getDefinitionIds().addAll(defIds);
            // Builder class.
            processDefinitionSetModelBuilder(e,
                                             defSetClassName,
                                             processingContext.getDefSetAnnotations().getBuilderFieldNames());
            // Graph factory type.
            TypeMirror mirror = null;
            try {
                Class<?> graphClass = definitionSetAnn.graphFactory();
            } catch (MirroredTypeException mte) {
                mirror = mte.getTypeMirror();
            }
            if (null == mirror) {
                throw new RuntimeException("No graph factory class specifyed for the @DefinitionSet.");
            }
            String fqcn = mirror.toString();
            processingContext.getDefSetAnnotations().getGraphFactoryTypes().put(defSetClassName,
                                                                                fqcn);
            // Definition Set's qualifier.
            try {
                Class<?> qualifierClass = definitionSetAnn.qualifier();
            } catch (MirroredTypeException mte) {
                mirror = mte.getTypeMirror();
            }
            if (null == mirror) {
                throw new RuntimeException("No qualifier class specifyed for the @DefinitionSet.");
            }
            processingContext.getDefSetAnnotations().getQualifiers().put(defSetClassName,
                                                                         mirror.toString());
        }
        return true;
    }

    private boolean processDefinitions(final Set<? extends TypeElement> set,
                                       final Element e,
                                       final RoundEnvironment roundEnv) throws Exception {
        final boolean isClass = e.getKind() == ElementKind.CLASS;
        if (isClass) {
            TypeElement classElement = (TypeElement) e;
            PackageElement packageElement = (PackageElement) classElement.getEnclosingElement();
            String defintionClassName = packageElement.getQualifiedName().toString() + "." + classElement.getSimpleName();
            Map<String, String> baseTypes = processingContext.getDefinitionAnnotations().getBaseTypes();
            TypeElement parentElement = getDefinitionInheritedType(classElement);
            if (null != parentElement && !baseTypes.containsKey(defintionClassName)) {
                PackageElement basePackageElement = (PackageElement) parentElement.getEnclosingElement();
                String baseClassName = basePackageElement.getQualifiedName().toString() + "." + parentElement.getSimpleName();
                baseTypes.put(defintionClassName,
                              baseClassName);
            }
            // Id field.
            processFieldName(classElement,
                             defintionClassName,
                             ANNOTATION_DEFINITION_ID,
                             processingContext.getDefinitionAnnotations().getIdFieldNames(),
                             false);
            // Category field.
            processFieldName(classElement,
                             defintionClassName,
                             ANNOTATION_DEFINITION_CATEGORY,
                             processingContext.getDefinitionAnnotations().getCategoryFieldNames(),
                             true);
            // Title field.
            processFieldName(classElement,
                             defintionClassName,
                             ANNOTATION_DEFINITION_TITLE,
                             processingContext.getDefinitionAnnotations().getTitleFieldNames(),
                             false);
            // Description field.
            processFieldName(classElement,
                             defintionClassName,
                             ANNOTATION_DESCRIPTION,
                             processingContext.getDefinitionAnnotations().getDescriptionFieldNames(),
                             false);
            // Labels field.
            processFieldName(classElement,
                             defintionClassName,
                             ANNOTATION_DEFINITION_LABELS,
                             processingContext.getDefinitionAnnotations().getLabelsFieldNames(),
                             true);
            // Builder class.
            processDefinitionModelBuilder(e,
                                          defintionClassName,
                                          processingContext.getDefinitionAnnotations().getBuilderFieldNames());
            // Graph element.
            Definition definitionAnn = e.getAnnotation(Definition.class);
            TypeMirror mirror = null;
            try {
                Class<?> graphClass = definitionAnn.graphFactory();
            } catch (MirroredTypeException mte) {
                mirror = mte.getTypeMirror();
            }
            if (null == mirror) {
                throw new RuntimeException("No graph factory class specified for the @Definition.");
            }
            String fqcn = mirror.toString();
            processingContext.getDefinitionAnnotations().getGraphFactoryFieldNames().put(defintionClassName,
                                                                                         fqcn);
            // PropertySets fields.
            Map<String, Element> propertySetElements = getFieldNames(classElement,
                                                                     ANNOTATION_PROPERTY_SET);
            if (null != propertySetElements && !propertySetElements.isEmpty()) {
                processingContext.getPropertySetElements().addAll(propertySetElements.values());
                processingContext.getDefinitionAnnotations().getPropertySetFieldNames().put(defintionClassName,
                                                                                            new LinkedHashSet<>(propertySetElements.keySet()));
            } else {
                note("Definition for tye [" + defintionClassName + "] have no Property Set members.");
            }
            // Properties fields.
            Map<String, Element> propertyElements = getFieldNames(classElement,
                                                                  ANNOTATION_PROPERTY);
            if (null != propertyElements && !propertyElements.isEmpty()) {
                processingContext.getPropertyElements().addAll(propertyElements.values());
                processingContext.getDefinitionAnnotations().getPropertyFieldNames().put(defintionClassName,
                                                                                         new LinkedHashSet<>(propertyElements.keySet()));
            } else {
                note("Definition for tye [" + defintionClassName + "] have no Property members.");
            }
            // -- Morphing annotations --
            MorphBase morphBaseAnn = e.getAnnotation(MorphBase.class);
            Morph morphAnn = e.getAnnotation(Morph.class);
            if (null != morphBaseAnn && null != morphAnn) {
                TypeElement superElement = getAnnotationInTypeInheritance(classElement,
                                                                          MorphBase.class.getName());
                final String packageName = packageElement.getQualifiedName().toString();
                String morphBaseClassName = packageName + "." + superElement.getSimpleName().toString();
                Map<String, String> defaultTypesMap = processingContext.getMorphingAnnotations().getBaseDefaultTypes();
                if (null == defaultTypesMap.get(morphBaseClassName)) {
                    TypeMirror morphDefaultTypeMirror = null;
                    try {
                        Class<?> defaultTypeClass = morphBaseAnn.defaultType();
                    } catch (MirroredTypeException mte) {
                        morphDefaultTypeMirror = mte.getTypeMirror();
                    }
                    if (null == morphDefaultTypeMirror) {
                        throw new RuntimeException("No default type class specifyed for the @MorphBase.");
                    }
                    String morphDefaultTypeClassName = morphDefaultTypeMirror.toString();
                    processingContext.getMorphingAnnotations().getBaseDefaultTypes().put(morphBaseClassName,
                                                                                         morphDefaultTypeClassName);
                    // MorphBase - targets
                    List<? extends TypeMirror> morphTargetMirrors = null;
                    try {
                        Class<?>[] defsClasses = morphBaseAnn.targets();
                    } catch (MirroredTypesException mte) {
                        morphTargetMirrors = mte.getTypeMirrors();
                    }
                    if (null != morphTargetMirrors) {
                        Set<String> morphTargetMirrorClasses = new LinkedHashSet<>();
                        for (TypeMirror morphTargetMirror : morphTargetMirrors) {
                            String morphTargetMirrorClassName = morphTargetMirror.toString();
                            morphTargetMirrorClasses.add(morphTargetMirrorClassName);
                        }
                        processingContext.getMorphingAnnotations().getBaseTargets().put(morphBaseClassName,
                                                                                        morphTargetMirrorClasses);
                    }
                    // Morph Properties.
                    processMorphProperties(superElement,
                                           morphBaseClassName);
                }
                TypeMirror morphBaseTypeMirror = null;
                try {
                    Class<?> defaultTypeClass = morphAnn.base();
                } catch (MirroredTypeException mte) {
                    morphBaseTypeMirror = mte.getTypeMirror();
                }
                if (null == morphBaseTypeMirror) {
                    throw new RuntimeException("No base type class specifyed for the @MorphBase.");
                }
                String morphBaseTypeClassName = morphBaseTypeMirror.toString();
                Set<String> currentTargets = processingContext.getMorphingAnnotations().getBaseTargets().get(morphBaseTypeClassName);
                if (null == currentTargets) {
                    currentTargets = new LinkedHashSet<>();
                    processingContext.getMorphingAnnotations().getBaseTargets().put(morphBaseTypeClassName,
                                                                                    currentTargets);
                }
                currentTargets.add(defintionClassName);
            }
        }
        return false;
    }

    private String getTypeName(final Element e) {
        TypeElement classElement = (TypeElement) e;
        PackageElement packageElement = (PackageElement) classElement.getEnclosingElement();
        return packageElement.getQualifiedName().toString() + "." + classElement.getSimpleName();
    }

    private TypeElement getAnnotationInTypeInheritance(final TypeElement classElement,
                                                       final String annotation) {
        TypeElement c = classElement;
        while (null != c &&
                !hasAnnotation(c,
                               annotation) &&
                !classElement.getQualifiedName().toString().equals(Object.class.getName())) {
            c = getParent(c);
        }
        return c;
    }

    private boolean hasAnnotation(final TypeElement classElement,
                                  final String annotation) {
        Element actionElement = processingEnv.getElementUtils().getTypeElement(annotation);
        TypeMirror actionType = actionElement.asType();
        if (null != classElement) {
            List<? extends AnnotationMirror> mirrors = classElement.getAnnotationMirrors();
            if (null != mirrors && !mirrors.isEmpty()) {
                for (AnnotationMirror m : mirrors) {
                    if (m.getAnnotationType().equals(actionType)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void processMorphProperties(final TypeElement classElement,
                                        final String definitionClassName) {
        final Messager messager = processingEnv.getMessager();
        final Elements elementUtils = processingEnv.getElementUtils();
        List<VariableElement> variableElements = ElementFilter.fieldsIn(classElement.getEnclosedElements());
        for (VariableElement variableElement : variableElements) {
            if (GeneratorUtils.getAnnotation(elementUtils,
                                             variableElement,
                                             ANNOTATION_MORPH_PROPERTY) != null) {
                final TypeMirror fieldReturnType = variableElement.asType();
                final String fieldReturnTypeName = GeneratorUtils.getTypeMirrorDeclaredName(fieldReturnType);
                final String fieldName = variableElement.getSimpleName().toString();
                messager.printMessage(Diagnostic.Kind.NOTE,
                                      "Discovered Morph Property " +
                                              "for class [" + classElement.getSimpleName() + "] " +
                                              "at field [" + fieldName + "] " +
                                              "of return type [" + fieldReturnTypeName + "]");
                // MorphBase - defaultType
                MorphProperty morphBaseAnn = variableElement.getAnnotation(MorphProperty.class);
                TypeMirror morphDefaultTypeMirror = null;
                try {
                    Class<?> defaultTypeClass = morphBaseAnn.binder();
                } catch (MirroredTypeException mte) {
                    morphDefaultTypeMirror = mte.getTypeMirror();
                }
                if (null == morphDefaultTypeMirror) {
                    throw new RuntimeException("No binder class specifyed for the @MorphProperty.");
                }
                String binderClassName = morphDefaultTypeMirror.toString();
                ProcessingMorphProperty morphProperty = new ProcessingMorphProperty(fieldReturnTypeName,
                                                                                    StringUtils.capitalize(fieldName),
                                                                                    binderClassName);
                List<ProcessingMorphProperty> morphProperties = processingContext.getMorphingAnnotations().getBaseMorphProperties().get(definitionClassName);
                if (null == morphProperties) {
                    morphProperties = new LinkedList<>();
                    processingContext.getMorphingAnnotations().getBaseMorphProperties().put(definitionClassName,
                                                                                            morphProperties);
                }
                morphProperties.add(morphProperty);
            }
        }
    }

    private TypeElement getDefinitionInheritedType(TypeElement classElement) {
        final Elements elementUtils = processingEnv.getElementUtils();
        classElement = getParent(classElement);
        while (!classElement.toString().equals(Object.class.getName())) {
            List<VariableElement> variableElements = ElementFilter.fieldsIn(classElement.getEnclosedElements());
            for (VariableElement variableElement : variableElements) {
                for (String annotation : DEFINITION_ANNOTATIONS) {
                    if (GeneratorUtils.getAnnotation(elementUtils,
                                                     variableElement,
                                                     annotation) != null) {
                        return classElement;
                    }
                }
            }
            classElement = getParent(classElement);
        }
        return null;
    }

    private TypeElement getParent(final TypeElement classElement) {
        return (TypeElement) processingEnv.getTypeUtils().asElement(classElement.getSuperclass());
    }

    private void processDefinitionModelBuilder(final Element e,
                                               final String className,
                                               final Map<String, String> processingContextMap) {
        Definition definitionAnn = e.getAnnotation(Definition.class);
        TypeMirror bMirror = null;
        try {
            Class<?> builderClass = definitionAnn.builder();
        } catch (MirroredTypeException mte) {
            bMirror = mte.getTypeMirror();
        }
        if (null != bMirror && !VoidBuilder.class.getName().equals(bMirror.toString())) {
            String fqcn = bMirror.toString();
            processingContextMap.put(className,
                                     fqcn);
        }
    }

    private void processDefinitionSetModelBuilder(final Element e,
                                                  final String className,
                                                  final Map<String, String> processingContextMap) {
        DefinitionSet definitionAnn = e.getAnnotation(DefinitionSet.class);
        TypeMirror bMirror = null;
        try {
            Class<?> builderClass = definitionAnn.builder();
        } catch (MirroredTypeException mte) {
            bMirror = mte.getTypeMirror();
        }
        if (null != bMirror && !VoidBuilder.class.getName().equals(bMirror.toString())) {
            String fqcn = bMirror.toString();
            processingContextMap.put(className,
                                     fqcn);
        }
    }

    private boolean processPropertySets(final Set<? extends TypeElement> set,
                                        final Element e,
                                        final RoundEnvironment roundEnv) throws Exception {
        final boolean isClass = e.getKind() == ElementKind.CLASS;
        if (isClass) {
            TypeElement classElement = (TypeElement) e;
            PackageElement packageElement = (PackageElement) classElement.getEnclosingElement();
            String propertyClassName = packageElement.getQualifiedName().toString() + "." + classElement.getSimpleName();

            // Name fields.
            processFieldName(classElement,
                             propertyClassName,
                             ANNOTATION_NAME,
                             processingContext.getPropertySetAnnotations().getNameFieldNames(),
                             false);
            // Properties fields.
            Map<String, Element> propertyElements = getFieldNames(classElement,
                                                                  ANNOTATION_PROPERTY);
            if (null != propertyElements) {
                processingContext.getPropertyElements().addAll(propertyElements.values());
                processingContext.getPropertySetAnnotations().getPropertiesFieldNames().put(propertyClassName,
                                                                                            new LinkedHashSet<>(propertyElements.keySet()));
                if (propertyElements.isEmpty()) {
                    note("Property Set for tye [" + propertyClassName + "] have no Property members.");
                }
            }
        }
        return false;
    }

    private boolean processProperties(final Set<? extends TypeElement> set,
                                      final Element e,
                                      final RoundEnvironment roundEnv) throws Exception {
        final boolean isClass = e.getKind() == ElementKind.CLASS;
        if (isClass) {
            TypeElement classElement = (TypeElement) e;
            PackageElement packageElement = (PackageElement) classElement.getEnclosingElement();
            String propertyClassName = packageElement.getQualifiedName().toString() + "." + classElement.getSimpleName();
            // Meta-properties
            Property metaProperty = e.getAnnotation(Property.class);
            if (null != metaProperty) {
                PropertyMetaTypes type = metaProperty.meta();
                if (!PropertyMetaTypes.NONE.equals(type)) {
                    processingContext.getMetaPropertyTypes().put(type,
                                                                 propertyClassName + ".class");
                }
            }
            // Value fields.
            processFieldName(classElement,
                             propertyClassName,
                             ANNOTATION_PROPERTY_VALUE,
                             processingContext.getPropertyAnnotations().getValueFieldNames(),
                             true);
            // Allowed Values fields.
            processFieldName(classElement,
                             propertyClassName,
                             ANNOTATION_PROPERTY_ALLOWED_VALUES,
                             processingContext.getPropertyAnnotations().getAllowedValuesFieldNames(),
                             false);
            // Caption fields.
            processFieldName(classElement,
                             propertyClassName,
                             ANNOTATION_PROPERTY_CAPTION,
                             processingContext.getPropertyAnnotations().getCaptionFieldNames(),
                             false);
            // Description fields.
            processFieldName(classElement,
                             propertyClassName,
                             ANNOTATION_DESCRIPTION,
                             processingContext.getPropertyAnnotations().getDescriptionFieldNames(),
                             false);
            // Property Type field.
            final boolean propTypeAnnFound = processFieldName(classElement,
                                                              propertyClassName,
                                                              ANNOTATION_PROPERTY_TYPE,
                                                              processingContext.getPropertyAnnotations().getTypeFieldNames(),
                                                              false);
            // In case no property type annotation found, try to resolve a default type by checking the class for
            // the value object.
            if (!propTypeAnnFound) {
                Map<String, Element> fieldNames = getFieldNames(classElement,
                                                                ANNOTATION_PROPERTY_VALUE);
                if (!fieldNames.isEmpty()) {
                    final TypeElement te = (TypeElement) fieldNames.values().iterator().next();
                    final String type = te.getQualifiedName().toString();
                    final Class<?> typeClass = Class.forName(type);
                    final Class<? extends PropertyType> defaultPropertyType =
                            DefinitionUtils.getDefaultPropertyType(typeClass);
                    if (null != defaultPropertyType) {
                        processingContext.getPropertyAnnotations().getTypes().put(propertyClassName,
                                                                                  defaultPropertyType.getName());
                    } else {
                        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                                                                 "No property type specified for class " + propertyClassName,
                                                                 classElement);
                    }
                }
            }

            // Read only fields.
            processFieldName(classElement,
                             propertyClassName,
                             ANNOTATION_PROPERTY_READONLY,
                             processingContext.getPropertyAnnotations().getReadOnlyFieldNames(),
                             false);
            // Optional fields.
            processFieldName(classElement,
                             propertyClassName,
                             ANNOTATION_PROPERTY_OPTIONAL,
                             processingContext.getPropertyAnnotations().getOptionalFieldNames(),
                             false);
        }
        return false;
    }

    private boolean processFieldName(final TypeElement classElement,
                                     final String propertyClassName,
                                     final String annotation,
                                     final Map<String, String> ctxMap,
                                     final boolean mandatory) {
        Map<String, Element> fieldNames = getFieldNames(classElement,
                                                        annotation);
        boolean empty = fieldNames.isEmpty();
        if (mandatory && empty) {
            throw new RuntimeException("No annotation of type [" + annotation + "] for Property of class [" + classElement + "]");
        }
        if (!empty) {
            ctxMap.put(propertyClassName,
                       fieldNames.keySet().iterator().next());
            return true;
        }
        return false;
    }

    private Map<String, Element> getFieldNames(TypeElement classElement,
                                               String annotation) {
        final Messager messager = processingEnv.getMessager();
        final Elements elementUtils = processingEnv.getElementUtils();
        Map<String, Element> result = new LinkedHashMap<>();
        while (!classElement.toString().equals(Object.class.getName())) {
            List<VariableElement> variableElements = ElementFilter.fieldsIn(classElement.getEnclosedElements());
            for (VariableElement variableElement : variableElements) {
                if (GeneratorUtils.getAnnotation(elementUtils,
                                                 variableElement,
                                                 annotation) != null) {
                    final TypeMirror fieldReturnType = variableElement.asType();
                    final TypeElement t = (TypeElement) ((DeclaredType) fieldReturnType).asElement();
                    final String fieldReturnTypeName = GeneratorUtils.getTypeMirrorDeclaredName(fieldReturnType);
                    final String fieldName = variableElement.getSimpleName().toString();
                    result.put(fieldName,
                               t);
                    messager.printMessage(Diagnostic.Kind.NOTE,
                                          "Discovered property value " +
                                                  "for class [" + classElement.getSimpleName() + "] " +
                                                  "at field [" + fieldName + "] " +
                                                  "of return type [" + fieldReturnTypeName + "]");
                }
            }
            classElement = getParent(classElement);
        }
        return result;
    }

    private boolean processContainmentRules(final Element e) throws Exception {
        final Messager messager = processingEnv.getMessager();
        final boolean isIface = e.getKind() == ElementKind.INTERFACE;
        final boolean isClass = e.getKind() == ElementKind.CLASS;
        if (isIface || isClass) {
            TypeElement classElement = (TypeElement) e;
            PackageElement packageElement = (PackageElement) classElement.getEnclosingElement();
            messager.printMessage(Diagnostic.Kind.NOTE,
                                  "Discovered containment rule for class [" + classElement.getSimpleName() + "]");
            final String classNameActivity = classElement.getSimpleName() + RULE_CONTAINMENT_SUFFIX_CLASSNAME;
            generateRuleCode(containmentRuleGenerator,
                             messager,
                             classElement,
                             packageElement,
                             classNameActivity);
        }
        return true;
    }

    private boolean processRuleExtension(final Element e) throws Exception {
        final Messager messager = processingEnv.getMessager();
        final boolean isIface = e.getKind() == ElementKind.INTERFACE;
        final boolean isClass = e.getKind() == ElementKind.CLASS;
        if (isIface || isClass) {
            TypeElement classElement = (TypeElement) e;
            PackageElement packageElement = (PackageElement) classElement.getEnclosingElement();
            messager.printMessage(Diagnostic.Kind.NOTE,
                                  "Discovered rule extension for class [" + classElement.getSimpleName() + "]");
            final String classNameActivity = classElement.getSimpleName() + RULE_EXTENSION_SUFFIX_CLASSNAME;
            generateRuleCode(extensionRuleGenerator,
                             messager,
                             classElement,
                             packageElement,
                             classNameActivity);
        }
        return true;
    }

    private boolean processDockingRules(final Element e) throws Exception {
        final Messager messager = processingEnv.getMessager();
        final boolean isIface = e.getKind() == ElementKind.INTERFACE;
        final boolean isClass = e.getKind() == ElementKind.CLASS;
        if (isIface || isClass) {
            TypeElement classElement = (TypeElement) e;
            PackageElement packageElement = (PackageElement) classElement.getEnclosingElement();
            messager.printMessage(Diagnostic.Kind.NOTE,
                                  "Discovered docking rule for class [" + classElement.getSimpleName() + "]");
            final String classNameActivity = classElement.getSimpleName() + RULE_DOCKING_SUFFIX_CLASSNAME;
            generateRuleCode(dockingRuleGenerator,
                             messager,
                             classElement,
                             packageElement,
                             classNameActivity);
        }
        return true;
    }

    private boolean processEdgeCardinalityRules(final Element e) throws Exception {
        final Messager messager = processingEnv.getMessager();
        final boolean isIface = e.getKind() == ElementKind.INTERFACE;
        final boolean isClass = e.getKind() == ElementKind.CLASS;
        if (isIface || isClass) {
            TypeElement classElement = (TypeElement) e;
            PackageElement packageElement = (PackageElement) classElement.getEnclosingElement();
            messager.printMessage(Diagnostic.Kind.NOTE,
                                  "Discovered edge cardinality rule for class [" + classElement.getSimpleName() + "]");
            final String classNameActivity = classElement.getSimpleName() + RULE_EDGE_CARDINALITY_SUFFIX_CLASSNAME;
            generateRuleCode(edgeCardinalityRuleGenerator,
                             messager,
                             classElement,
                             packageElement,
                             classNameActivity);
        }
        return true;
    }

    private boolean processCardinalityRules(final Element e) throws Exception {
        final Messager messager = processingEnv.getMessager();
        final boolean isIface = e.getKind() == ElementKind.INTERFACE;
        final boolean isClass = e.getKind() == ElementKind.CLASS;
        if (isIface || isClass) {
            TypeElement classElement = (TypeElement) e;
            PackageElement packageElement = (PackageElement) classElement.getEnclosingElement();
            messager.printMessage(Diagnostic.Kind.NOTE,
                                  "Discovered cardinality rule for class [" + classElement.getSimpleName() + "]");
            final String classNameActivity = classElement.getSimpleName() + RULE_CARDINALITY_SUFFIX_CLASSNAME;
            generateRuleCode(cardinalityRuleGenerator,
                             messager,
                             classElement,
                             packageElement,
                             classNameActivity);
        }
        return true;
    }

    private boolean processConnectionRules(final Element element) throws Exception {
        final Messager messager = processingEnv.getMessager();
        final boolean isIface = element.getKind() == ElementKind.INTERFACE;
        final boolean isClass = element.getKind() == ElementKind.CLASS;
        if (isIface || isClass) {
            TypeElement classElement = (TypeElement) element;
            PackageElement packageElement = (PackageElement) classElement.getEnclosingElement();
            messager.printMessage(Diagnostic.Kind.NOTE,
                                  "Discovered connection rule for class [" + classElement.getSimpleName() + "]");
            final String classNameActivity = classElement.getSimpleName() + RULE_CONNECTION_SUFFIX_CLASSNAME;
            generateRuleCode(connectionRuleGenerator,
                             messager,
                             classElement,
                             packageElement,
                             classNameActivity);
        }
        return true;
    }

    private void generateRuleCode(final AbstractGenerator generator,
                                  final Messager messager,
                                  final TypeElement classElement,
                                  final PackageElement packageElement,
                                  final String classNameActivity) {
        try {
            final String packageName = packageElement.getQualifiedName().toString();
            //Try generating code for each required class
            messager.printMessage(Diagnostic.Kind.NOTE,
                                  "Generating ryke code for [" + classNameActivity + "]");
            generator.generate(packageName,
                               packageElement,
                               classNameActivity,
                               classElement,
                               processingEnv);
        } catch (GenerationException ge) {
            final String msg = ge.getMessage();
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                                                     msg,
                                                     classElement);
        }
    }

    private boolean processLastRound(final Set<? extends TypeElement> set,
                                     final RoundEnvironment roundEnv) throws Exception {
        processLastRoundDefinitionSetProxyAdapter(set,
                                                  roundEnv);
        processLastRoundDefinitionSetAdapter(set,
                                             roundEnv);
        processLastRoundPropertySetAdapter(set,
                                           roundEnv);
        processLastRoundDefinitionFactory(set,
                                          roundEnv);
        processLastRoundDefinitionAdapter(set,
                                          roundEnv);
        processLastRoundPropertyAdapter(set,
                                        roundEnv);
        processLastRoundRuleAdapter(set,
                                    roundEnv);
        processLastRoundMorphing(set,
                                 roundEnv);
        return true;
    }

    private boolean processLastRoundMorphing(final Set<? extends TypeElement> set,
                                             final RoundEnvironment roundEnv) throws Exception {
        final Messager messager = processingEnv.getMessager();
        try {
            // Ensure visible on both backend and client sides.
            final String packageName = getGeneratedPackageName() + ".definition.morph";
            final Set<String> generatedDefinitionClasses = new LinkedHashSet<>();
            // MORPH DEFINITIONS GENERATION.
            Map<String, Set<String>> baseTargets = processingContext.getMorphingAnnotations().getBaseTargets();
            if (null != baseTargets && !baseTargets.isEmpty()) {
                for (Map.Entry<String, Set<String>> entry : baseTargets.entrySet()) {
                    String baseType = entry.getKey();
                    Set<String> targets = entry.getValue();
                    final String className = getMorphDefinitionClassName(packageName,
                                                                         baseType,
                                                                         MORPH_DEFINITION_CLASSNAME)[0];
                    final String classFQName = getMorphDefinitionClassName(packageName,
                                                                           baseType,
                                                                           MORPH_DEFINITION_CLASSNAME)[1];
                    String defaultType = processingContext.getMorphingAnnotations().getBaseDefaultTypes().get(baseType);
                    messager.printMessage(Diagnostic.Kind.NOTE,
                                          "Starting MorphDefinition adf for class named " + classFQName);
                    final StringBuffer ruleClassCode = morphDefinitionGenerator.generate(packageName,
                                                                                         className,
                                                                                         baseType,
                                                                                         targets,
                                                                                         defaultType,
                                                                                         messager);
                    writeCode(packageName,
                              className,
                              ruleClassCode);
                    generatedDefinitionClasses.add(classFQName);
                }
            }
            // MORPH PROPERTY DEFINITIONS GENERATION.
            Map<String, List<ProcessingMorphProperty>> morphProperties = processingContext.getMorphingAnnotations().getBaseMorphProperties();
            if (null != morphProperties && !morphProperties.isEmpty()) {
                for (Map.Entry<String, List<ProcessingMorphProperty>> entry : morphProperties.entrySet()) {
                    String baseType = entry.getKey();
                    List<ProcessingMorphProperty> properties = entry.getValue();
                    final String className = getMorphDefinitionClassName(packageName,
                                                                         baseType,
                                                                         MORPH_PROPERTY_DEFINITION_CLASSNAME)[0];
                    final String classFQName = getMorphDefinitionClassName(packageName,
                                                                           baseType,
                                                                           MORPH_PROPERTY_DEFINITION_CLASSNAME)[1];
                    String defaultType = processingContext.getMorphingAnnotations().getBaseDefaultTypes().get(baseType);
                    messager.printMessage(Diagnostic.Kind.NOTE,
                                          "Starting MorphPropertyDefinition adf for class named " + classFQName);
                    final StringBuffer ruleClassCode = morphPropertyDefinitionGenerator.generate(packageName,
                                                                                                 className,
                                                                                                 baseType,
                                                                                                 properties,
                                                                                                 defaultType,
                                                                                                 messager);
                    writeCode(packageName,
                              className,
                              ruleClassCode);
                    generatedDefinitionClasses.add(classFQName);
                }
            }
            // MORPH DEFINITIONS PROVIDER GENERATION.
            if (!generatedDefinitionClasses.isEmpty()) {
                final String className = getSetClassPrefix() + MORPH_PROVIDER_CLASSNAME;
                final String classFQName = packageName + "." + className;
                messager.printMessage(Diagnostic.Kind.NOTE,
                                      "Starting MorphDefinitionProvider adf for class named " + classFQName);
                final StringBuffer ruleClassCode = morphDefinitionProviderGenerator.generate(packageName,
                                                                                             className,
                                                                                             generatedDefinitionClasses,
                                                                                             messager);
                writeCode(packageName,
                          className,
                          ruleClassCode);
            }
        } catch (GenerationException ge) {
            final String msg = ge.getMessage();
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                                                     msg);
        }
        return true;
    }

    private String[] getMorphDefinitionClassName(final String packageName,
                                                 final String baseType,
                                                 final String suffix) {
        String baseTypeName = baseType.substring(baseType.lastIndexOf(".") + 1,
                                                 baseType.length());
        final String className = baseTypeName + suffix;
        String fqcn = packageName + "." + className;
        return new String[]{className, fqcn};
    }

    private boolean processLastRoundRuleAdapter(final Set<? extends TypeElement> set,
                                                final RoundEnvironment roundEnv) throws Exception {
        final Messager messager = processingEnv.getMessager();
        try {
            // Ensure visible on both backend and client sides.
            final String packageName = getGeneratedPackageName() + ".definition.adapter.binding";
            final String className = getSetClassPrefix() + RULE_ADAPTER_CLASSNAME;
            final String classFQName = packageName + "." + className;
            messager.printMessage(Diagnostic.Kind.NOTE,
                                  "Starting RuleAdapter adf for class named " + classFQName);
            final StringBuffer ruleClassCode = ruleAdapterGenerator.generate(packageName,
                                                                             className,
                                                                             processingContext.getDefinitionSet().getClassName(),
                                                                             processingContext.getRules(),
                                                                             messager);
            writeCode(packageName,
                      className,
                      ruleClassCode);
        } catch (GenerationException ge) {
            final String msg = ge.getMessage();
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                                                     msg);
        }
        return true;
    }

    private boolean processLastRoundDefinitionSetAdapter(final Set<? extends TypeElement> set,
                                                         final RoundEnvironment roundEnv) throws Exception {
        final Messager messager = processingEnv.getMessager();
        try {
            // Ensure visible on both backend and client sides.
            final String packageName = getGeneratedPackageName() + ".definition.adapter.binding";
            final String className = getSetClassPrefix() + DEFINITIONSET_ADAPTER_CLASSNAME;
            final String classFQName = packageName + "." + className;
            messager.printMessage(Diagnostic.Kind.NOTE,
                                  "Starting ErraiBinderAdapter adf named " + classFQName);
            final StringBuffer ruleClassCode = definitionSetAdapterGenerator.generate(packageName,
                                                                                      className,
                                                                                      processingContext.getDefSetAnnotations(),
                                                                                      messager);
            writeCode(packageName,
                      className,
                      ruleClassCode);
        } catch (GenerationException ge) {
            final String msg = ge.getMessage();
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                                                     msg);
        }
        return true;
    }

    private boolean processLastRoundDefinitionSetProxyAdapter(final Set<? extends TypeElement> set,
                                                              final RoundEnvironment roundEnv) throws Exception {
        final Messager messager = processingEnv.getMessager();
        try {
            // Ensure visible on both backend and client sides.
            final String packageName = getGeneratedPackageName() + ".definition.adapter.binding";
            final String className = getSetClassPrefix() + DEFINITIONSET_PROXY_CLASSNAME;
            final String classFQName = packageName + "." + className;
            messager.printMessage(Diagnostic.Kind.NOTE,
                                  "Starting DefinitionSetProxyAdapter adf for class named " + classFQName);
            final StringBuffer ruleClassCode = definitionSetProxyGenerator.generate(packageName,
                                                                                    className,
                                                                                    processingContext.getDefinitionSet(),
                                                                                    processingContext.getDefSetAnnotations().getBuilderFieldNames(),
                                                                                    messager);
            writeCode(packageName,
                      className,
                      ruleClassCode);
        } catch (GenerationException ge) {
            final String msg = ge.getMessage();
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                                                     msg);
        }
        return true;
    }

    private boolean processLastRoundPropertySetAdapter(final Set<? extends TypeElement> set,
                                                       final RoundEnvironment roundEnv) throws Exception {
        final Messager messager = processingEnv.getMessager();
        try {
            // Ensure visible on both backend and client sides.
            final String packageName = getGeneratedPackageName() + ".definition.adapter.binding";
            final String className = getSetClassPrefix() + PROPERTYSET_ADAPTER_CLASSNAME;
            final String classFQName = packageName + "." + className;
            messager.printMessage(Diagnostic.Kind.NOTE,
                                  "Starting ErraiBinderAdapter adf named " + classFQName);
            final StringBuffer ruleClassCode = propertySetAdapterGenerator.generate(packageName,
                                                                                    className,
                                                                                    processingContext.getPropertySetAnnotations(),
                                                                                    messager);
            writeCode(packageName,
                      className,
                      ruleClassCode);
        } catch (GenerationException ge) {
            final String msg = ge.getMessage();
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                                                     msg);
        }
        return true;
    }

    private boolean processLastRoundDefinitionFactory(final Set<? extends TypeElement> set,
                                                      final RoundEnvironment roundEnv) throws Exception {
        final Messager messager = processingEnv.getMessager();
        try {
            final int size = processingContext.getDefinitionAnnotations().getBuilderFieldNames().size() +
                    processingContext.getDefSetAnnotations().getBuilderFieldNames().size();
            if (size > 0) {
                final Map<String, String> buildersMap = new LinkedHashMap<>();
                if (!processingContext.getDefinitionAnnotations().getBuilderFieldNames().isEmpty()) {
                    buildersMap.putAll(processingContext.getDefinitionAnnotations().getBuilderFieldNames());
                }
                if (!processingContext.getDefSetAnnotations().getBuilderFieldNames().isEmpty()) {
                    buildersMap.putAll(processingContext.getDefSetAnnotations().getBuilderFieldNames());
                }
                // Ensure visible on both backend and client sides.
                final String packageName = getGeneratedPackageName() + ".definition.factory";
                final String className = getSetClassPrefix() + DEFINITION_FACTORY_CLASSNAME;
                final String classFQName = packageName + "." + className;
                messager.printMessage(Diagnostic.Kind.NOTE,
                                      "Starting ModelFactory adf for class named " + classFQName);
                final StringBuffer ruleClassCode = generatedDefinitionFactoryGenerator.generate(packageName,
                                                                                                className,
                                                                                                buildersMap,
                                                                                                messager);
                writeCode(packageName,
                          className,
                          ruleClassCode);
            }
        } catch (GenerationException ge) {
            final String msg = ge.getMessage();
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                                                     msg);
        }
        return true;
    }

    private boolean processLastRoundDefinitionAdapter(final Set<? extends TypeElement> set,
                                                      final RoundEnvironment roundEnv) throws Exception {
        final Messager messager = processingEnv.getMessager();
        try {
            // Ensure visible on both backend and client sides.
            final String packageName = getGeneratedPackageName() + ".definition.adapter.binding";
            final String className = getSetClassPrefix() + DEFINITION_ADAPTER_CLASSNAME;
            final String classFQName = packageName + "." + className;
            messager.printMessage(Diagnostic.Kind.NOTE,
                                  "Starting ErraiBinderAdapter adf named " + classFQName);
            final StringBuffer ruleClassCode = definitionAdapterGenerator.generate(packageName,
                                                                                   className,
                                                                                   processingContext,
                                                                                   messager);
            writeCode(packageName,
                      className,
                      ruleClassCode);
        } catch (GenerationException ge) {
            final String msg = ge.getMessage();
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                                                     msg);
        }
        return true;
    }

    private boolean processLastRoundPropertyAdapter(final Set<? extends TypeElement> set,
                                                    final RoundEnvironment roundEnv) throws Exception {
        final Messager messager = processingEnv.getMessager();
        try {
            // Ensure visible on both backend and client sides.
            final String packageName = getGeneratedPackageName() + ".definition.adapter.binding";
            final String className = getSetClassPrefix() + PROPERTY_ADAPTER_CLASSNAME;
            final String classFQName = packageName + "." + className;
            messager.printMessage(Diagnostic.Kind.NOTE,
                                  "Starting ErraiBinderAdapter adf named " + classFQName);
            final StringBuffer ruleClassCode = propertyAdapterGenerator.generate(packageName,
                                                                                 className,
                                                                                 processingContext.getPropertyAnnotations(),
                                                                                 messager);
            writeCode(packageName,
                      className,
                      ruleClassCode);
        } catch (GenerationException ge) {
            final String msg = ge.getMessage();
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                                                     msg);
        }
        return true;
    }

    private String getGeneratedPackageName() {
        final String s = processingContext.getDefinitionSet().getClassName();
        return s.substring(0,
                           s.lastIndexOf("."));
    }

    private String getSetClassPrefix() {
        return processingContext.getDefinitionSet().getId();
    }

    private void note(String message) {
        log(Diagnostic.Kind.NOTE,
            message);
    }

    private void warn(String message) {
        log(Diagnostic.Kind.WARNING,
            message);
    }

    private void error(String message) {
        log(Diagnostic.Kind.ERROR,
            message);
    }

    private void log(Diagnostic.Kind kind,
                     String message) {
        final Messager messager = processingEnv.getMessager();
        messager.printMessage(kind,
                              message);
    }
}
