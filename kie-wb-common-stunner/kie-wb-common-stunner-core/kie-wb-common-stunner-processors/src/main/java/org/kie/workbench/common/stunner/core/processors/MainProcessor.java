/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.processors;

import org.apache.commons.lang3.StringUtils;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;
import org.kie.workbench.common.stunner.core.definition.annotation.Definition;
import org.kie.workbench.common.stunner.core.definition.annotation.DefinitionSet;
import org.kie.workbench.common.stunner.core.definition.annotation.Shape;
import org.kie.workbench.common.stunner.core.definition.annotation.ShapeSet;
import org.kie.workbench.common.stunner.core.definition.annotation.morph.Morph;
import org.kie.workbench.common.stunner.core.definition.annotation.morph.MorphBase;
import org.kie.workbench.common.stunner.core.definition.annotation.morph.MorphProperty;
import org.kie.workbench.common.stunner.core.definition.annotation.property.NameProperty;
import org.kie.workbench.common.stunner.core.definition.builder.VoidBuilder;
import org.kie.workbench.common.stunner.core.processors.definition.BindableDefinitionAdapterGenerator;
import org.kie.workbench.common.stunner.core.processors.definitionset.BindableDefinitionSetAdapterGenerator;
import org.kie.workbench.common.stunner.core.processors.definitionset.DefinitionSetProxyGenerator;
import org.kie.workbench.common.stunner.core.processors.factory.ModelFactoryGenerator;
import org.kie.workbench.common.stunner.core.processors.morph.MorphDefinitionGenerator;
import org.kie.workbench.common.stunner.core.processors.morph.MorphDefinitionProviderGenerator;
import org.kie.workbench.common.stunner.core.processors.morph.MorphPropertyDefinitionGenerator;
import org.kie.workbench.common.stunner.core.processors.property.BindablePropertyAdapterGenerator;
import org.kie.workbench.common.stunner.core.processors.propertyset.BindablePropertySetAdapterGenerator;
import org.kie.workbench.common.stunner.core.processors.rule.*;
import org.kie.workbench.common.stunner.core.processors.shape.BindableShapeFactoryGenerator;
import org.kie.workbench.common.stunner.core.processors.shape.BindableShapeSetGenerator;
import org.uberfire.annotations.processors.AbstractErrorAbsorbingProcessor;
import org.uberfire.annotations.processors.exceptions.GenerationException;

import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import java.util.*;

@SupportedAnnotationTypes( {
        MainProcessor.ANNOTATION_DEFINITION_SET,
        MainProcessor.ANNOTATION_DEFINITION,
        MainProcessor.ANNOTATION_PROPERTY_SET,
        MainProcessor.ANNOTATION_PROPERTY,
        MainProcessor.ANNOTATION_NAME_PROPERTY,
        MainProcessor.ANNOTATION_RULE_CAN_CONTAIN,
        MainProcessor.ANNOTATION_RULE_CAN_DOCK,
        MainProcessor.ANNOTATION_RULE_ALLOWED_CONNECTION,
        MainProcessor.ANNOTATION_RULE_ALLOWED_EDGE_OCCURRS,
        MainProcessor.ANNOTATION_RULE_ALLOWED_OCCS,
        MainProcessor.ANNOTATION_SHAPE,
        MainProcessor.ANNOTATION_SHAPE_SET } )
@SupportedSourceVersion( SourceVersion.RELEASE_8 )
public class MainProcessor extends AbstractErrorAbsorbingProcessor {

    public static final String ANNOTATION_DESCRIPTION = "org.kie.workbench.common.stunner.core.definition.annotation.Description";
    public static final String ANNOTATION_NAME = "org.kie.workbench.common.stunner.core.definition.annotation.Name";

    public static final String ANNOTATION_DEFINITION_SET = "org.kie.workbench.common.stunner.core.definition.annotation.DefinitionSet";

    public static final String ANNOTATION_DEFINITION = "org.kie.workbench.common.stunner.core.definition.annotation.Definition";
    public static final String ANNOTATION_DEFINITION_CATEGORY = "org.kie.workbench.common.stunner.core.definition.annotation.definition.Category";
    public static final String ANNOTATION_DEFINITION_LABELS = "org.kie.workbench.common.stunner.core.definition.annotation.definition.Labels";
    public static final String ANNOTATION_DEFINITION_TITLE = "org.kie.workbench.common.stunner.core.definition.annotation.definition.Title";

    public static final String[] DEFINITION_ANNOTATIONS = new String[]{
            ANNOTATION_DEFINITION_CATEGORY, ANNOTATION_DEFINITION_LABELS,
            ANNOTATION_DEFINITION_TITLE
    };

    public static final String ANNOTATION_PROPERTY_SET = "org.kie.workbench.common.stunner.core.definition.annotation.PropertySet";

    public static final String ANNOTATION_PROPERTY = "org.kie.workbench.common.stunner.core.definition.annotation.Property";
    public static final String ANNOTATION_NAME_PROPERTY = "org.kie.workbench.common.stunner.core.definition.annotation.property.NameProperty";
    public static final String ANNOTATION_PROPERTY_DEFAULT_VALUE = "org.kie.workbench.common.stunner.core.definition.annotation.property.DefaultValue";
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
    public static final String ANNOTATION_RULE_ALLOWED_CONNECTION = "org.kie.workbench.common.stunner.core.rule.annotation.AllowedConnections";
    public static final String ANNOTATION_RULE_ALLOWED_OCCS = "org.kie.workbench.common.stunner.core.rule.annotation.AllowedOccurrences";
    public static final String ANNOTATION_RULE_ALLOWED_EDGE_OCCURRS = "org.kie.workbench.common.stunner.core.rule.annotation.AllowedEdgeOccurrences";

    public static final String ANNOTATION_SHAPE_SET = "org.kie.workbench.common.stunner.core.client.annotation.ShapeSet";
    public static final String ANNOTATION_SHAPE = "org.kie.workbench.common.stunner.core.client.annotation.Shape";

    public static final String MORPH_DEFINITION_CLASSNAME = "MorphDefinition";
    public static final String MORPH_PROPERTY_DEFINITION_CLASSNAME = "MorphPropertyDefinition";
    public static final String MORPH_PROVIDER_CLASSNAME = "MorphDefinitionProvider";

    public static final String RULE_CONTAINMENT_SUFFIX_CLASSNAME = "ContainmentRule";
    public static final String RULE_DOCKING_SUFFIX_CLASSNAME = "DockingRule";
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

    public static final String SHAPE_FACTORY_CLASSNAME = "ShapeFactory";

    private final ProcessingContext processingContext = ProcessingContext.getInstance();
    private final ContainmentRuleGenerator containmentRuleGenerator;
    private final ConnectionRuleGenerator connectionRuleGenerator;
    private final CardinalityRuleGenerator cardinalityRuleGenerator;
    private final EdgeCardinalityRuleGenerator edgeCardinalityRuleGenerator;
    private final DockingRuleGenerator dockingRuleGenerator;
    private BindableDefinitionSetAdapterGenerator definitionSetAdapterGenerator;
    private BindableDefinitionAdapterGenerator definitionAdapterGenerator;
    private BindablePropertySetAdapterGenerator propertySetAdapterGenerator;
    private BindablePropertyAdapterGenerator propertyAdapterGenerator;
    private BindableDefinitionSetRuleAdapterGenerator ruleAdapterGenerator;
    private DefinitionSetProxyGenerator definitionSetProxyGenerator;
    private MorphDefinitionGenerator morphDefinitionGenerator;
    private MorphPropertyDefinitionGenerator morphPropertyDefinitionGenerator;
    private MorphDefinitionProviderGenerator morphDefinitionProviderGenerator;
    private BindableShapeSetGenerator shapeSetGenerator;
    private ModelFactoryGenerator generatedDefinitionFactoryGenerator;
    private BindableShapeFactoryGenerator shapeFactoryGenerator;

    public MainProcessor() {
        ContainmentRuleGenerator ruleGenerator = null;
        ConnectionRuleGenerator connectionRuleGenerator = null;
        CardinalityRuleGenerator cardinalityRuleGenerator = null;
        EdgeCardinalityRuleGenerator edgeCardinalityRuleGenerator = null;
        DockingRuleGenerator dockingRuleGenerator = null;
        BindableDefinitionSetAdapterGenerator definitionSetAdapterGenerator = null;
        BindableDefinitionAdapterGenerator definitionAdapterGenerator = null;
        BindablePropertySetAdapterGenerator propertySetAdapterGenerator = null;
        BindablePropertyAdapterGenerator propertyAdapter = null;
        MorphDefinitionGenerator morphDefinitionGenerator = null;
        MorphPropertyDefinitionGenerator morphPropertyDefinitionGenerator = null;
        MorphDefinitionProviderGenerator morphDefinitionProviderGenerator = null;
        BindableDefinitionSetRuleAdapterGenerator ruleAdapter = null;
        DefinitionSetProxyGenerator definitionSetProxyGenerator = null;
        BindableShapeSetGenerator shapeSetGenerator = null;
        BindableShapeFactoryGenerator shapeFactoryGenerator = null;
        ModelFactoryGenerator generatedDefinitionFactoryGenerator = null;
        try {
            ruleGenerator = new ContainmentRuleGenerator();
            propertyAdapter = new BindablePropertyAdapterGenerator();
            ruleAdapter = new BindableDefinitionSetRuleAdapterGenerator();
            connectionRuleGenerator = new ConnectionRuleGenerator();
            cardinalityRuleGenerator = new CardinalityRuleGenerator();
            edgeCardinalityRuleGenerator = new EdgeCardinalityRuleGenerator();
            dockingRuleGenerator = new DockingRuleGenerator();
            definitionAdapterGenerator = new BindableDefinitionAdapterGenerator();
            definitionSetAdapterGenerator = new BindableDefinitionSetAdapterGenerator();
            propertySetAdapterGenerator = new BindablePropertySetAdapterGenerator();
            definitionSetProxyGenerator = new DefinitionSetProxyGenerator();
            morphDefinitionGenerator = new MorphDefinitionGenerator();
            morphPropertyDefinitionGenerator = new MorphPropertyDefinitionGenerator();
            morphDefinitionProviderGenerator = new MorphDefinitionProviderGenerator();
            shapeSetGenerator = new BindableShapeSetGenerator();
            generatedDefinitionFactoryGenerator = new ModelFactoryGenerator();
            shapeFactoryGenerator = new BindableShapeFactoryGenerator();

        } catch ( Throwable t ) {
            rememberInitializationError( t );
        }
        this.containmentRuleGenerator = ruleGenerator;
        this.connectionRuleGenerator = connectionRuleGenerator;
        this.cardinalityRuleGenerator = cardinalityRuleGenerator;
        this.edgeCardinalityRuleGenerator = edgeCardinalityRuleGenerator;
        this.dockingRuleGenerator = dockingRuleGenerator;
        this.definitionSetAdapterGenerator = definitionSetAdapterGenerator;
        this.definitionAdapterGenerator = definitionAdapterGenerator;
        this.propertySetAdapterGenerator = propertySetAdapterGenerator;
        this.propertyAdapterGenerator = propertyAdapter;
        this.ruleAdapterGenerator = ruleAdapter;
        this.definitionSetProxyGenerator = definitionSetProxyGenerator;
        this.morphDefinitionGenerator = morphDefinitionGenerator;
        this.morphPropertyDefinitionGenerator = morphPropertyDefinitionGenerator;
        this.morphDefinitionProviderGenerator = morphDefinitionProviderGenerator;
        this.shapeSetGenerator = shapeSetGenerator;
        this.generatedDefinitionFactoryGenerator = generatedDefinitionFactoryGenerator;
        this.shapeFactoryGenerator = shapeFactoryGenerator;

    }

    public static String toValidId( String id ) {
        return StringUtils.uncapitalize( id );
    }

    public static String toClassMemberId( String className ) {
        int i = className.lastIndexOf( "." );
        String s = i > -1 ? className.substring( i + 1, className.length() ) : className;
        return toValidId( s );
    }

    @Override
    protected boolean processWithExceptions( Set<? extends TypeElement> set, RoundEnvironment roundEnv ) throws Exception {
        if ( roundEnv.processingOver() ) {
            return processLastRound( set, roundEnv );

        }
        //If prior processing threw an error exit
        if ( roundEnv.errorRaised() ) {
            return false;
        }
        final Elements elementUtils = processingEnv.getElementUtils();
        for ( Element e : roundEnv.getElementsAnnotatedWith( elementUtils.getTypeElement( ANNOTATION_DEFINITION_SET ) ) ) {
            processDefinitionSets( set, e, roundEnv );
        }
        for ( Element e : roundEnv.getElementsAnnotatedWith( elementUtils.getTypeElement( ANNOTATION_DEFINITION ) ) ) {
            processDefinitions( set, e, roundEnv );
        }
        for ( Element e : roundEnv.getElementsAnnotatedWith( elementUtils.getTypeElement( ANNOTATION_PROPERTY_SET ) ) ) {
            processPropertySets( set, e, roundEnv );
        }
        final Set<? extends Element> propertyElements = new LinkedHashSet<Element>() {{
            addAll( roundEnv.getElementsAnnotatedWith( elementUtils.getTypeElement( ANNOTATION_PROPERTY ) ) );
            addAll( roundEnv.getElementsAnnotatedWith( elementUtils.getTypeElement( ANNOTATION_NAME_PROPERTY ) ) );
        }};
        for ( Element e : propertyElements ) {
            processProperties( set, e, roundEnv );
        }
        for ( Element e : roundEnv.getElementsAnnotatedWith( elementUtils.getTypeElement( ANNOTATION_RULE_CAN_CONTAIN ) ) ) {
            processContainmentRules( set, e, roundEnv );
        }
        for ( Element e : roundEnv.getElementsAnnotatedWith( elementUtils.getTypeElement( ANNOTATION_RULE_CAN_DOCK ) ) ) {
            processDockingRules( set, e, roundEnv );
        }
        for ( Element e : roundEnv.getElementsAnnotatedWith( elementUtils.getTypeElement( ANNOTATION_RULE_ALLOWED_OCCS ) ) ) {
            processCardinalityRules( set, e, roundEnv );
        }
        for ( Element e : roundEnv.getElementsAnnotatedWith( elementUtils.getTypeElement( ANNOTATION_RULE_ALLOWED_EDGE_OCCURRS ) ) ) {
            processEdgeCardinalityRules( set, e, roundEnv );
        }
        for ( Element e : roundEnv.getElementsAnnotatedWith( elementUtils.getTypeElement( ANNOTATION_RULE_ALLOWED_CONNECTION ) ) ) {
            processConnectionRules( set, e, roundEnv );
        }
        return true;
    }

    private boolean processDefinitionSets( Set<? extends TypeElement> set, Element e, RoundEnvironment roundEnv ) throws Exception {
        final Messager messager = processingEnv.getMessager();
        final boolean isClass = e.getKind() == ElementKind.CLASS;
        if ( isClass ) {
            TypeElement classElement = ( TypeElement ) e;
            PackageElement packageElement = ( PackageElement ) classElement.getEnclosingElement();
            messager.printMessage( Diagnostic.Kind.NOTE, "Discovered definition set class [" + classElement.getSimpleName() + "]" );
            final String packageName = packageElement.getQualifiedName().toString();
            final String className = classElement.getSimpleName().toString();
            processingContext.setDefinitionSet( packageName, className );
            String propertyClassName = packageName + "." + className;
            // Description fields.
            processFieldName( classElement,
                    propertyClassName,
                    ANNOTATION_DESCRIPTION,
                    processingContext.getDefSetAnnotations().getDescriptionFieldNames(),
                    true );
            // Definitions identifiers.
            DefinitionSet definitionSetAnn = e.getAnnotation( DefinitionSet.class );
            List<? extends TypeMirror> mirrors = null;
            try {
                Class<?>[] defsClasses = definitionSetAnn.definitions();
            } catch ( MirroredTypesException mte ) {
                mirrors = mte.getTypeMirrors();
            }
            if ( null == mirrors ) {
                throw new RuntimeException( "No graph class class specifyed for the @DefinitionSet." );
            }
            Set<String> defIds = new LinkedHashSet<>();
            for ( TypeMirror mirror : mirrors ) {
                String fqcn = mirror.toString();
                defIds.add( fqcn );
            }
            processingContext.getDefSetAnnotations().getDefinitionIds().addAll( defIds );
            // Builder class.
            processDefinitionSetModelBuilder( e, propertyClassName,
                    processingContext.getDefSetAnnotations().getBuilderFieldNames() );
            // Graph factory type.
            TypeMirror mirror = null;
            try {
                Class<?> graphClass = definitionSetAnn.graphFactory();
            } catch ( MirroredTypeException mte ) {
                mirror = mte.getTypeMirror();
            }
            if ( null == mirror ) {
                throw new RuntimeException( "No graph factory class specifyed for the @DefinitionSet." );
            }
            String fqcn = mirror.toString();
            processingContext.getDefSetAnnotations().getGraphFactoryTypes().put( propertyClassName, fqcn );
            ShapeSet shapeSetAnn = e.getAnnotation( ShapeSet.class );
            if ( null != shapeSetAnn ) {
                processingContext.getDefSetAnnotations().setHasShapeSet( true );
            }

        }
        return true;
    }

    private boolean processDefinitions( Set<? extends TypeElement> set, Element e, RoundEnvironment roundEnv ) throws Exception {
        final boolean isClass = e.getKind() == ElementKind.CLASS;
        if ( isClass ) {
            TypeElement classElement = ( TypeElement ) e;
            PackageElement packageElement = ( PackageElement ) classElement.getEnclosingElement();
            String propertyClassName = packageElement.getQualifiedName().toString() + "." + classElement.getSimpleName();
            Map<String, String> baseTypes = processingContext.getDefinitionAnnotations().getBaseTypes();
            TypeElement parentElement = getDefinitionInheritedType( classElement );
            if ( null != parentElement && !baseTypes.containsKey( propertyClassName ) ) {
                PackageElement basePackageElement = ( PackageElement ) parentElement.getEnclosingElement();
                String baseClassName = basePackageElement.getQualifiedName().toString() + "." + parentElement.getSimpleName();
                baseTypes.put( propertyClassName, baseClassName );
            }
            // Category fields.
            processFieldName( classElement,
                    propertyClassName,
                    ANNOTATION_DEFINITION_CATEGORY,
                    processingContext.getDefinitionAnnotations().getCategoryFieldNames(),
                    true );
            // Title fields.
            processFieldName( classElement,
                    propertyClassName,
                    ANNOTATION_DEFINITION_TITLE,
                    processingContext.getDefinitionAnnotations().getTitleFieldNames(),
                    true );
            // Description fields.
            processFieldName( classElement,
                    propertyClassName,
                    ANNOTATION_DESCRIPTION,
                    processingContext.getDefinitionAnnotations().getDescriptionFieldNames(),
                    true );
            // Labels fields.
            processFieldName( classElement,
                    propertyClassName,
                    ANNOTATION_DEFINITION_LABELS,
                    processingContext.getDefinitionAnnotations().getLabelsFieldNames(),
                    true );
            // Builder class.
            processDefinitionModelBuilder( e, propertyClassName,
                    processingContext.getDefinitionAnnotations().getBuilderFieldNames() );
            // Graph element.
            Definition definitionAnn = e.getAnnotation( Definition.class );
            TypeMirror mirror = null;
            try {
                Class<?> graphClass = definitionAnn.graphFactory();
            } catch ( MirroredTypeException mte ) {
                mirror = mte.getTypeMirror();
            }
            if ( null == mirror ) {
                throw new RuntimeException( "No graph factory class specified for the @Definition." );
            }
            String fqcn = mirror.toString();
            processingContext.getDefinitionAnnotations().getGraphFactoryFieldNames().put( propertyClassName, fqcn );
            // PropertySets fields.
            processFieldNames( classElement, propertyClassName, ANNOTATION_PROPERTY_SET, processingContext.getDefinitionAnnotations().getPropertySetFieldNames() );
            // Properties fields.
            processFieldNames( classElement, propertyClassName, ANNOTATION_PROPERTY, processingContext.getDefinitionAnnotations().getPropertyFieldNames() );
            // -- Morphing annotations --
            // MorphBase - defaultType
            MorphBase morphBaseAnn = e.getAnnotation( MorphBase.class );
            if ( null != morphBaseAnn ) {
                TypeElement superElement = getAnnotationInTypeInheritance( classElement, MorphBase.class.getName() );
                final String packageName = packageElement.getQualifiedName().toString();
                String morphBaseClassName = packageName + "." + superElement.getSimpleName().toString();
                Map<String, String> defaultTypesMap = processingContext.getMorphingAnnotations().getBaseDefaultTypes();
                if ( null == defaultTypesMap.get( morphBaseClassName ) ) {
                    TypeMirror morphDefaultTypeMirror = null;
                    try {
                        Class<?> defaultTypeClass = morphBaseAnn.defaultType();
                    } catch ( MirroredTypeException mte ) {
                        morphDefaultTypeMirror = mte.getTypeMirror();
                    }
                    if ( null == morphDefaultTypeMirror ) {
                        throw new RuntimeException( "No default type class specifyed for the @MorphBase." );
                    }
                    String morphDefaultTypeClassName = morphDefaultTypeMirror.toString();
                    processingContext.getMorphingAnnotations().getBaseDefaultTypes().put( morphBaseClassName, morphDefaultTypeClassName );
                    // MorphBase - targets
                    List<? extends TypeMirror> morphTargetMirrors = null;
                    try {
                        Class<?>[] defsClasses = morphBaseAnn.targets();
                    } catch ( MirroredTypesException mte ) {
                        morphTargetMirrors = mte.getTypeMirrors();
                    }
                    if ( null != morphTargetMirrors ) {
                        Set<String> morphTargetMirrorClasses = new LinkedHashSet<>();
                        for ( TypeMirror morphTargetMirror : morphTargetMirrors ) {
                            String morphTargetMirrorClassName = morphTargetMirror.toString();
                            morphTargetMirrorClasses.add( morphTargetMirrorClassName );
                        }
                        processingContext.getMorphingAnnotations().getBaseTargets().put( morphBaseClassName, morphTargetMirrorClasses );

                    }
                    // Morph Properties.
                    processMorphProperties( superElement, morphBaseClassName );

                }

            }
            // Morph - baseType
            Morph morphAnn = e.getAnnotation( Morph.class );
            if ( null != morphAnn ) {
                TypeMirror morphBaseTypeMirror = null;
                try {
                    Class<?> defaultTypeClass = morphAnn.base();
                } catch ( MirroredTypeException mte ) {
                    morphBaseTypeMirror = mte.getTypeMirror();
                }
                if ( null == morphBaseTypeMirror ) {
                    throw new RuntimeException( "No base type class specifyed for the @MorphBase." );
                }
                String morphBaseTypeClassName = morphBaseTypeMirror.toString();
                Set<String> currentTargets = processingContext.getMorphingAnnotations().getBaseTargets().get( morphBaseTypeClassName );
                if ( null == currentTargets ) {
                    currentTargets = new LinkedHashSet<>();
                    processingContext.getMorphingAnnotations().getBaseTargets().put( morphBaseTypeClassName, currentTargets );
                }
                currentTargets.add( propertyClassName );

            }
            // Shape Definitions Factory.
            Shape shapeAnn = e.getAnnotation( Shape.class );
            if ( null != shapeAnn ) {
                TypeMirror sfm = null;
                try {
                    Class<?> graphClass = shapeAnn.factory();
                } catch ( MirroredTypeException mte ) {
                    sfm = mte.getTypeMirror();
                }
                if ( null == sfm ) {
                    throw new RuntimeException( "No ShapeDef Factory class class specifyed for the Definition ["
                            + propertyClassName + "]" );
                }
                String sfmfqcn = sfm.toString();
                TypeMirror sm = null;
                try {
                    Class<?> graphClass = shapeAnn.def();
                } catch ( MirroredTypeException mte ) {
                    sm = mte.getTypeMirror();
                }
                if ( null == sm ) {
                    throw new RuntimeException( "No Shape Def class class specifyed for the @Definition." );
                }
                String smfqcn = sm.toString();
                if ( !processingContext.getDefinitionAnnotations().getShapeDefinitions().containsKey( propertyClassName ) ) {
                    processingContext.getDefinitionAnnotations()
                            .getShapeDefinitions().put( propertyClassName, new String[]{ sfmfqcn, smfqcn } );

                }

            }

        }
        return false;

    }

    private TypeElement getAnnotationInTypeInheritance( TypeElement classElement, String annotation ) {
        TypeElement c = classElement;
        while ( null != c &&
                !hasAnnotation( c, annotation ) &&
                !classElement.getQualifiedName().toString().equals( Object.class.getName() ) ) {
            c = getParent( c );

        }
        return c;
    }

    private boolean hasAnnotation( TypeElement classElement, String annotation ) {
        Element actionElement = processingEnv.getElementUtils().getTypeElement(
                annotation );
        TypeMirror actionType = actionElement.asType();
        if ( null != classElement ) {
            List<? extends AnnotationMirror> mirrors = classElement.getAnnotationMirrors();
            if ( null != mirrors && !mirrors.isEmpty() ) {
                for ( AnnotationMirror m : mirrors ) {
                    if ( m.getAnnotationType().equals( actionType ) ) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void processMorphProperties( TypeElement classElement,
                                         String definitionClassName ) {
        final Messager messager = processingEnv.getMessager();
        final Elements elementUtils = processingEnv.getElementUtils();
        List<VariableElement> variableElements = ElementFilter.fieldsIn( classElement.getEnclosedElements() );
        for ( VariableElement variableElement : variableElements ) {
            if ( GeneratorUtils.getAnnotation( elementUtils, variableElement, ANNOTATION_MORPH_PROPERTY ) != null ) {
                final TypeMirror fieldReturnType = variableElement.asType();
                final String fieldReturnTypeName = GeneratorUtils.getTypeMirrorDeclaredName( fieldReturnType );
                final String fieldName = variableElement.getSimpleName().toString();
                messager.printMessage( Diagnostic.Kind.WARNING, "Discovered Morph Property " +
                        "for class [" + classElement.getSimpleName() + "] " +
                        "at field [" + fieldName + "] " +
                        "of return type [" + fieldReturnTypeName + "]" );
                // MorphBase - defaultType
                MorphProperty morphBaseAnn = variableElement.getAnnotation( MorphProperty.class );
                TypeMirror morphDefaultTypeMirror = null;
                try {
                    Class<?> defaultTypeClass = morphBaseAnn.binder();
                } catch ( MirroredTypeException mte ) {
                    morphDefaultTypeMirror = mte.getTypeMirror();
                }
                if ( null == morphDefaultTypeMirror ) {
                    throw new RuntimeException( "No binder class specifyed for the @MorphProperty." );
                }
                String binderClassName = morphDefaultTypeMirror.toString();
                ProcessingMorphProperty morphProperty =
                        new ProcessingMorphProperty( fieldReturnTypeName, StringUtils.capitalize( fieldName ), binderClassName );
                List<ProcessingMorphProperty> morphProperties =
                        processingContext.getMorphingAnnotations().getBaseMorphProperties().get( definitionClassName );
                if ( null == morphProperties ) {
                    morphProperties = new LinkedList<>();
                    processingContext.getMorphingAnnotations().getBaseMorphProperties().put( definitionClassName, morphProperties );
                }
                morphProperties.add( morphProperty );

            }

        }

    }

    private TypeElement getDefinitionInheritedType( TypeElement classElement ) {
        final Elements elementUtils = processingEnv.getElementUtils();
        classElement = getParent( classElement );
        while ( !classElement.toString().equals( Object.class.getName() ) ) {
            List<VariableElement> variableElements = ElementFilter.fieldsIn( classElement.getEnclosedElements() );
            for ( VariableElement variableElement : variableElements ) {
                for ( String annotation : DEFINITION_ANNOTATIONS ) {
                    if ( GeneratorUtils.getAnnotation( elementUtils, variableElement, annotation ) != null ) {
                        return classElement;
                    }

                }

            }
            classElement = getParent( classElement );

        }
        return null;
    }

    private TypeElement getParent( TypeElement classElement ) {
        return ( TypeElement ) processingEnv.getTypeUtils().asElement( classElement.getSuperclass() );
    }

    private void processDefinitionModelBuilder( Element e, String className, Map<String, String> processingContextMap ) {
        Definition definitionAnn = e.getAnnotation( Definition.class );
        TypeMirror bMirror = null;
        try {
            Class<?> builderClass = definitionAnn.builder();
        } catch ( MirroredTypeException mte ) {
            bMirror = mte.getTypeMirror();
        }
        if ( null != bMirror && !VoidBuilder.class.getName().equals( bMirror.toString() ) ) {
            String fqcn = bMirror.toString();
            processingContextMap.put( className, fqcn );
        }

    }

    private void processDefinitionSetModelBuilder( Element e, String className, Map<String, String> processingContextMap ) {
        DefinitionSet definitionAnn = e.getAnnotation( DefinitionSet.class );
        TypeMirror bMirror = null;
        try {
            Class<?> builderClass = definitionAnn.builder();
        } catch ( MirroredTypeException mte ) {
            bMirror = mte.getTypeMirror();
        }
        if ( null != bMirror && !VoidBuilder.class.getName().equals( bMirror.toString() ) ) {
            String fqcn = bMirror.toString();
            processingContextMap.put( className, fqcn );
        }

    }

    private boolean processPropertySets( Set<? extends TypeElement> set, Element e, RoundEnvironment roundEnv ) throws Exception {
        final boolean isClass = e.getKind() == ElementKind.CLASS;
        if ( isClass ) {
            TypeElement classElement = ( TypeElement ) e;
            PackageElement packageElement = ( PackageElement ) classElement.getEnclosingElement();
            String propertyClassName = packageElement.getQualifiedName().toString() + "." + classElement.getSimpleName();
            // Name fields.
            processFieldName( classElement,
                    propertyClassName,
                    ANNOTATION_NAME,
                    processingContext.getPropertySetAnnotations().getNameFieldNames(),
                    true );
            // Properties fields.
            processFieldNames( classElement, propertyClassName, ANNOTATION_PROPERTY, processingContext.getPropertySetAnnotations().getPropertiesFieldNames() );

        }
        return false;

    }

    private boolean processProperties( Set<? extends TypeElement> set, Element e, RoundEnvironment roundEnv ) throws Exception {
        final boolean isClass = e.getKind() == ElementKind.CLASS;
        if ( isClass ) {
            TypeElement classElement = ( TypeElement ) e;
            PackageElement packageElement = ( PackageElement ) classElement.getEnclosingElement();
            String propertyClassName = packageElement.getQualifiedName().toString() + "." + classElement.getSimpleName();
            if ( null != e.getAnnotation( NameProperty.class ) ) {
                processingContext.setNamePropertyClass( propertyClassName + ".class" );
            }
            // Value fields.
            processFieldName( classElement,
                    propertyClassName,
                    ANNOTATION_PROPERTY_VALUE,
                    processingContext.getPropertyAnnotations().getValueFieldNames(),
                    true );
            // Default Value fields.
            processFieldName( classElement,
                    propertyClassName,
                    ANNOTATION_PROPERTY_DEFAULT_VALUE,
                    processingContext.getPropertyAnnotations().getDefaultValueFieldNames(),
                    true );
            // Allowed Values fields.
            processFieldName( classElement,
                    propertyClassName,
                    ANNOTATION_PROPERTY_ALLOWED_VALUES,
                    processingContext.getPropertyAnnotations().getAllowedValuesFieldNames(),
                    false );
            // Caption fields.
            processFieldName( classElement,
                    propertyClassName,
                    ANNOTATION_PROPERTY_CAPTION,
                    processingContext.getPropertyAnnotations().getCaptionFieldNames(),
                    true );
            // Description fields.
            processFieldName( classElement,
                    propertyClassName,
                    ANNOTATION_DESCRIPTION,
                    processingContext.getPropertyAnnotations().getDescriptionFieldNames(),
                    true );
            // Type fields.
            processFieldName( classElement,
                    propertyClassName,
                    ANNOTATION_PROPERTY_TYPE,
                    processingContext.getPropertyAnnotations().getTypeFieldNames(),
                    true );
            // Read only fields.
            processFieldName( classElement,
                    propertyClassName,
                    ANNOTATION_PROPERTY_READONLY,
                    processingContext.getPropertyAnnotations().getReadOnlyFieldNames(),
                    true );
            // Optional fields.
            processFieldName( classElement,
                    propertyClassName,
                    ANNOTATION_PROPERTY_OPTIONAL,
                    processingContext.getPropertyAnnotations().getOptionalFieldNames(),
                    true );

        }
        return false;

    }

    private void processFieldName( TypeElement classElement,
                                   String propertyClassName,
                                   String annotation,
                                   Map<String, String> ctxMap,
                                   boolean mandatory ) {
        Collection<String> fieldNames = getFieldNames( classElement, annotation );
        boolean empty = fieldNames.isEmpty();
        if ( mandatory && empty ) {
            throw new RuntimeException( "No annotation of type [" + annotation + "] for Property of class [" + classElement + "]" );

        }
        if ( !empty ) {
            ctxMap.put( propertyClassName, fieldNames.iterator().next() );

        }

    }

    protected void processMethodName( TypeElement classElement,
                                      String propertyClassName,
                                      String annotation,
                                      Map<String, String> ctxMap,
                                      boolean mandatory ) {
        Collection<String> methodNames = getMethodNames( classElement, propertyClassName, annotation );
        boolean empty = methodNames == null || methodNames.isEmpty();
        if ( mandatory && empty ) {
            throw new RuntimeException( "No annotation of type [" + annotation + "] for Definition of class [" + classElement + "]" );

        }
        if ( !empty ) {
            ctxMap.put( propertyClassName, methodNames.iterator().next() );

        }

    }

    private void processFieldNames( TypeElement classElement,
                                    String propertyClassName,
                                    String annotation,
                                    Map<String, Set<String>> ctxMap ) {
        Collection<String> fieldNames = getFieldNames( classElement, annotation );
        ctxMap.put( propertyClassName, new LinkedHashSet<>( fieldNames ) );

    }

    private Collection<String> getFieldNames( TypeElement classElement,
                                              String annotation ) {
        final Messager messager = processingEnv.getMessager();
        final Elements elementUtils = processingEnv.getElementUtils();
        Set<String> result = new LinkedHashSet<>();
        while ( !classElement.toString().equals( Object.class.getName() ) ) {
            List<VariableElement> variableElements = ElementFilter.fieldsIn( classElement.getEnclosedElements() );
            for ( VariableElement variableElement : variableElements ) {
                if ( GeneratorUtils.getAnnotation( elementUtils, variableElement, annotation ) != null ) {
                    final TypeMirror fieldReturnType = variableElement.asType();
                    final String fieldReturnTypeName = GeneratorUtils.getTypeMirrorDeclaredName( fieldReturnType );
                    final String fieldName = variableElement.getSimpleName().toString();
                    result.add( fieldName );
                    messager.printMessage( Diagnostic.Kind.WARNING, "Discovered property value " +
                            "for class [" + classElement.getSimpleName() + "] " +
                            "at field [" + fieldName + "] " +
                            "of return type [" + fieldReturnTypeName + "]" );

                }

            }
            classElement = getParent( classElement );

        }
        return result;

    }

    private Collection<String> getMethodNames( TypeElement classElement,
                                               String className,
                                               String annotation ) {
        final String name = GeneratorUtils.getTypedMethodName( classElement, annotation, className, processingEnv );
        if ( null == name ) {
            return null;

        }
        return new ArrayList<String>() {{
            add( name );
        }};
    }

    private boolean processContainmentRules( Set<? extends TypeElement> set, Element e, RoundEnvironment roundEnv ) throws Exception {
        final Messager messager = processingEnv.getMessager();
        final boolean isIface = e.getKind() == ElementKind.INTERFACE;
        final boolean isClass = e.getKind() == ElementKind.CLASS;
        if ( isIface || isClass ) {
            TypeElement classElement = ( TypeElement ) e;
            PackageElement packageElement = ( PackageElement ) classElement.getEnclosingElement();
            messager.printMessage( Diagnostic.Kind.NOTE, "Discovered containment rule for class [" + classElement.getSimpleName() + "]" );
            final String packageName = packageElement.getQualifiedName().toString();
            final String classNameActivity = classElement.getSimpleName() + RULE_CONTAINMENT_SUFFIX_CLASSNAME;
            try {
                //Try generating code for each required class
                messager.printMessage( Diagnostic.Kind.NOTE, "Generating code for [" + classNameActivity + "]" );
                containmentRuleGenerator.generate( packageName,
                        packageElement,
                        classNameActivity,
                        classElement,
                        processingEnv );

            } catch ( GenerationException ge ) {
                final String msg = ge.getMessage();
                processingEnv.getMessager().printMessage( Diagnostic.Kind.ERROR, msg, classElement );
            }

        }
        return true;

    }

    private boolean processDockingRules( Set<? extends TypeElement> set, Element e, RoundEnvironment roundEnv ) throws Exception {
        final Messager messager = processingEnv.getMessager();
        final boolean isIface = e.getKind() == ElementKind.INTERFACE;
        final boolean isClass = e.getKind() == ElementKind.CLASS;
        if ( isIface || isClass ) {
            TypeElement classElement = ( TypeElement ) e;
            PackageElement packageElement = ( PackageElement ) classElement.getEnclosingElement();
            messager.printMessage( Diagnostic.Kind.NOTE, "Discovered docking rule for class [" + classElement.getSimpleName() + "]" );
            final String packageName = packageElement.getQualifiedName().toString();
            final String classNameActivity = classElement.getSimpleName() + RULE_DOCKING_SUFFIX_CLASSNAME;
            try {
                //Try generating code for each required class
                messager.printMessage( Diagnostic.Kind.NOTE, "Generating code for [" + classNameActivity + "]" );
                dockingRuleGenerator.generate( packageName,
                        packageElement,
                        classNameActivity,
                        classElement,
                        processingEnv );

            } catch ( GenerationException ge ) {
                final String msg = ge.getMessage();
                processingEnv.getMessager().printMessage( Diagnostic.Kind.ERROR, msg, classElement );
            }

        }
        return true;

    }

    private boolean processEdgeCardinalityRules( Set<? extends TypeElement> set, Element e, RoundEnvironment roundEnv ) throws Exception {
        final Messager messager = processingEnv.getMessager();
        final boolean isIface = e.getKind() == ElementKind.INTERFACE;
        final boolean isClass = e.getKind() == ElementKind.CLASS;
        if ( isIface || isClass ) {
            TypeElement classElement = ( TypeElement ) e;
            PackageElement packageElement = ( PackageElement ) classElement.getEnclosingElement();
            messager.printMessage( Diagnostic.Kind.NOTE, "Discovered edge cardinality rule for class [" + classElement.getSimpleName() + "]" );
            final String packageName = packageElement.getQualifiedName().toString();
            final String classNameActivity = classElement.getSimpleName() + RULE_EDGE_CARDINALITY_SUFFIX_CLASSNAME;
            try {
                //Try generating code for each required class
                messager.printMessage( Diagnostic.Kind.NOTE, "Generating code for [" + classNameActivity + "]" );
                edgeCardinalityRuleGenerator.generate( packageName,
                        packageElement,
                        classNameActivity,
                        classElement,
                        processingEnv );

            } catch ( GenerationException ge ) {
                final String msg = ge.getMessage();
                processingEnv.getMessager().printMessage( Diagnostic.Kind.ERROR, msg, classElement );
            }

        }
        return true;

    }

    private boolean processCardinalityRules( Set<? extends TypeElement> set, Element e, RoundEnvironment roundEnv ) throws Exception {
        final Messager messager = processingEnv.getMessager();
        final boolean isIface = e.getKind() == ElementKind.INTERFACE;
        final boolean isClass = e.getKind() == ElementKind.CLASS;
        if ( isIface || isClass ) {
            TypeElement classElement = ( TypeElement ) e;
            PackageElement packageElement = ( PackageElement ) classElement.getEnclosingElement();
            messager.printMessage( Diagnostic.Kind.NOTE, "Discovered cardinality rule for class [" + classElement.getSimpleName() + "]" );
            final String packageName = packageElement.getQualifiedName().toString();
            final String classNameActivity = classElement.getSimpleName() + RULE_CARDINALITY_SUFFIX_CLASSNAME;
            try {
                //Try generating code for each required class
                messager.printMessage( Diagnostic.Kind.NOTE, "Generating code for [" + classNameActivity + "]" );
                cardinalityRuleGenerator.generate( packageName,
                        packageElement,
                        classNameActivity,
                        classElement,
                        processingEnv );

            } catch ( GenerationException ge ) {
                final String msg = ge.getMessage();
                processingEnv.getMessager().printMessage( Diagnostic.Kind.ERROR, msg, classElement );
            }

        }
        return true;

    }

    private boolean processConnectionRules( Set<? extends TypeElement> set, Element element, RoundEnvironment roundEnv ) throws Exception {
        final Messager messager = processingEnv.getMessager();
        final boolean isIface = element.getKind() == ElementKind.INTERFACE;
        final boolean isClass = element.getKind() == ElementKind.CLASS;
        if ( isIface || isClass ) {
            TypeElement classElement = ( TypeElement ) element;
            PackageElement packageElement = ( PackageElement ) classElement.getEnclosingElement();
            messager.printMessage( Diagnostic.Kind.NOTE, "Discovered connection rule for class [" + classElement.getSimpleName() + "]" );
            final String packageName = packageElement.getQualifiedName().toString();
            final String classNameActivity = classElement.getSimpleName() + RULE_CONNECTION_SUFFIX_CLASSNAME;
            try {
                //Try generating code for each required class
                messager.printMessage( Diagnostic.Kind.NOTE, "Generating code for [" + classNameActivity + "]" );
                connectionRuleGenerator.generate( packageName,
                        packageElement,
                        classNameActivity,
                        classElement,
                        processingEnv );

            } catch ( GenerationException ge ) {
                final String msg = ge.getMessage();
                processingEnv.getMessager().printMessage( Diagnostic.Kind.ERROR, msg, classElement );
            }

        }
        return true;

    }

    private boolean processLastRound( Set<? extends TypeElement> set, RoundEnvironment roundEnv ) throws Exception {
        processLastRoundDefinitionSetProxyAdapter( set, roundEnv );
        processLastRoundDefinitionSetAdapter( set, roundEnv );
        processLastRoundPropertySetAdapter( set, roundEnv );
        processLastRoundDefinitionFactory( set, roundEnv );
        processLastRoundDefinitionAdapter( set, roundEnv );
        processLastRoundPropertyAdapter( set, roundEnv );
        processLastRoundRuleAdapter( set, roundEnv );
        processLastRoundMorphing( set, roundEnv );
        processLastRoundShapesStuffGenerator( set, roundEnv );
        return true;
    }

    private boolean processLastRoundMorphing( Set<? extends TypeElement> set, RoundEnvironment roundEnv ) throws Exception {
        final Messager messager = processingEnv.getMessager();
        try {
            // Ensure visible on both backend and client sides.
            final String packageName = getGeneratedPackageName() + ".definition.morph";
            final Set<String> generatedDefinitionClasses = new LinkedHashSet<>();
            // MORPH DEFINITIONS GENERATION.
            Map<String, Set<String>> baseTargets = processingContext.getMorphingAnnotations().getBaseTargets();
            if ( null != baseTargets && !baseTargets.isEmpty() ) {
                for ( Map.Entry<String, Set<String>> entry : baseTargets.entrySet() ) {
                    String baseType = entry.getKey();
                    Set<String> targets = entry.getValue();
                    final String className = getMorphDefinitionClassName( packageName, baseType, MORPH_DEFINITION_CLASSNAME )[ 0 ];
                    final String classFQName = getMorphDefinitionClassName( packageName, baseType, MORPH_DEFINITION_CLASSNAME )[ 1 ];
                    String defaultType = processingContext.getMorphingAnnotations().getBaseDefaultTypes().get( baseType );
                    messager.printMessage( Diagnostic.Kind.WARNING, "Starting MorphDefinition generation for class named " + classFQName );
                    final StringBuffer ruleClassCode = morphDefinitionGenerator.generate( packageName, className,
                            baseType, targets, defaultType, messager );
                    writeCode( packageName,
                            className,
                            ruleClassCode );
                    generatedDefinitionClasses.add( classFQName );

                }

            }
            // MORPH PROPERTY DEFINITIONS GENERATION.
            Map<String, List<ProcessingMorphProperty>> morphProperties =
                    processingContext.getMorphingAnnotations().getBaseMorphProperties();
            if ( null != morphProperties && !morphProperties.isEmpty() ) {
                for ( Map.Entry<String, List<ProcessingMorphProperty>> entry : morphProperties.entrySet() ) {
                    String baseType = entry.getKey();
                    List<ProcessingMorphProperty> properties = entry.getValue();
                    final String className = getMorphDefinitionClassName( packageName, baseType, MORPH_PROPERTY_DEFINITION_CLASSNAME )[ 0 ];
                    final String classFQName = getMorphDefinitionClassName( packageName, baseType, MORPH_PROPERTY_DEFINITION_CLASSNAME )[ 1 ];
                    String defaultType = processingContext.getMorphingAnnotations().getBaseDefaultTypes().get( baseType );
                    messager.printMessage( Diagnostic.Kind.WARNING, "Starting MorphPropertyDefinition generation for class named " + classFQName );
                    final StringBuffer ruleClassCode = morphPropertyDefinitionGenerator.generate( packageName, className,
                            baseType, properties, defaultType, messager );
                    writeCode( packageName,
                            className,
                            ruleClassCode );
                    generatedDefinitionClasses.add( classFQName );

                }

            }
            // MORPH DEFINITIONS PROVIDER GENERATION.
            if ( !generatedDefinitionClasses.isEmpty() ) {
                final String className = getSetClassPrefix() + MORPH_PROVIDER_CLASSNAME;
                final String classFQName = packageName + "." + className;
                messager.printMessage( Diagnostic.Kind.WARNING, "Starting MorphDefinitionProvider generation for class named " + classFQName );
                final StringBuffer ruleClassCode = morphDefinitionProviderGenerator.generate( packageName, className,
                        generatedDefinitionClasses, messager );
                writeCode( packageName,
                        className,
                        ruleClassCode );

            }

        } catch ( GenerationException ge ) {
            final String msg = ge.getMessage();
            processingEnv.getMessager().printMessage( Diagnostic.Kind.ERROR, msg );
        }
        return true;

    }

    private String[] getMorphDefinitionClassName( String packageName, String baseType, String suffix ) {
        String baseTypeName = baseType.substring( baseType.lastIndexOf( "." ) + 1, baseType.length() );
        final String className = baseTypeName + suffix;
        String fqcn = packageName + "." + className;
        return new String[]{ className, fqcn };
    }

    private boolean processLastRoundRuleAdapter( Set<? extends TypeElement> set, RoundEnvironment roundEnv ) throws Exception {
        final Messager messager = processingEnv.getMessager();
        try {
            // Ensure visible on both backend and client sides.
            final String packageName = getGeneratedPackageName() + ".definition.adapter.binding";
            final String className = getSetClassPrefix() + RULE_ADAPTER_CLASSNAME;
            final String classFQName = packageName + "." + className;
            messager.printMessage( Diagnostic.Kind.WARNING, "Starting RuleAdapter generation for class named " + classFQName );
            final StringBuffer ruleClassCode = ruleAdapterGenerator.generate( packageName, className,
                    processingContext.getDefinitionSet().getClassName(), processingContext.getRules(), messager );
            writeCode( packageName,
                    className,
                    ruleClassCode );

        } catch ( GenerationException ge ) {
            final String msg = ge.getMessage();
            processingEnv.getMessager().printMessage( Diagnostic.Kind.ERROR, msg );
        }
        return true;

    }

    private boolean processLastRoundDefinitionSetAdapter( Set<? extends TypeElement> set, RoundEnvironment roundEnv ) throws Exception {
        final Messager messager = processingEnv.getMessager();
        try {
            // Ensure visible on both backend and client sides.
            final String packageName = getGeneratedPackageName() + ".definition.adapter.binding";
            final String className = getSetClassPrefix() + DEFINITIONSET_ADAPTER_CLASSNAME;
            final String classFQName = packageName + "." + className;
            messager.printMessage( Diagnostic.Kind.WARNING, "Starting ErraiBinderAdapter generation named " + classFQName );
            final StringBuffer ruleClassCode = definitionSetAdapterGenerator.generate( packageName, className,
                    processingContext.getDefSetAnnotations(), messager );
            writeCode( packageName,
                    className,
                    ruleClassCode );

        } catch ( GenerationException ge ) {
            final String msg = ge.getMessage();
            processingEnv.getMessager().printMessage( Diagnostic.Kind.ERROR, msg );
        }
        return true;

    }

    private boolean processLastRoundDefinitionSetProxyAdapter( Set<? extends TypeElement> set, RoundEnvironment roundEnv ) throws Exception {
        final Messager messager = processingEnv.getMessager();
        try {
            // Ensure visible on both backend and client sides.
            final String packageName = getGeneratedPackageName() + ".definition.adapter.binding";
            final String className = getSetClassPrefix() + DEFINITIONSET_PROXY_CLASSNAME;
            final String classFQName = packageName + "." + className;
            messager.printMessage( Diagnostic.Kind.WARNING, "Starting DefinitionSetProxyAdapter generation for class named " + classFQName );
            final StringBuffer ruleClassCode = definitionSetProxyGenerator.
                    generate( packageName,
                            className,
                            processingContext.getDefinitionSet(),
                            processingContext.getDefSetAnnotations().getBuilderFieldNames(),
                            messager );
            writeCode( packageName,
                    className,
                    ruleClassCode );

        } catch ( GenerationException ge ) {
            final String msg = ge.getMessage();
            processingEnv.getMessager().printMessage( Diagnostic.Kind.ERROR, msg );
        }
        return true;

    }

    private boolean processLastRoundPropertySetAdapter( Set<? extends TypeElement> set, RoundEnvironment roundEnv ) throws Exception {
        final Messager messager = processingEnv.getMessager();
        try {
            // Ensure visible on both backend and client sides.
            final String packageName = getGeneratedPackageName() + ".definition.adapter.binding";
            final String className = getSetClassPrefix() + PROPERTYSET_ADAPTER_CLASSNAME;
            final String classFQName = packageName + "." + className;
            messager.printMessage( Diagnostic.Kind.WARNING, "Starting ErraiBinderAdapter generation named " + classFQName );
            final StringBuffer ruleClassCode = propertySetAdapterGenerator.generate( packageName, className,
                    processingContext.getPropertySetAnnotations(), messager );
            writeCode( packageName,
                    className,
                    ruleClassCode );

        } catch ( GenerationException ge ) {
            final String msg = ge.getMessage();
            processingEnv.getMessager().printMessage( Diagnostic.Kind.ERROR, msg );
        }
        return true;

    }

    private boolean processLastRoundDefinitionFactory( Set<? extends TypeElement> set, RoundEnvironment roundEnv ) throws Exception {
        final Messager messager = processingEnv.getMessager();
        try {
            final int size = processingContext.getDefinitionAnnotations().getBuilderFieldNames().size() +
                    processingContext.getDefSetAnnotations().getBuilderFieldNames().size();
            if ( size > 0 ) {
                final Map<String, String> buildersMap = new LinkedHashMap<>();
                if ( !processingContext.getDefinitionAnnotations().getBuilderFieldNames().isEmpty() ) {
                    buildersMap.putAll( processingContext.getDefinitionAnnotations().getBuilderFieldNames() );
                }
                if ( !processingContext.getDefSetAnnotations().getBuilderFieldNames().isEmpty() ) {
                    buildersMap.putAll( processingContext.getDefSetAnnotations().getBuilderFieldNames() );
                }
                // Ensure visible on both backend and client sides.
                final String packageName = getGeneratedPackageName() + ".definition.factory";
                final String className = getSetClassPrefix() + DEFINITION_FACTORY_CLASSNAME;
                final String classFQName = packageName + "." + className;
                messager.printMessage( Diagnostic.Kind.WARNING, "Starting ModelFactory generation for class named " + classFQName );
                final StringBuffer ruleClassCode = generatedDefinitionFactoryGenerator.
                        generate( packageName,
                                className,
                                buildersMap,
                                messager );
                writeCode( packageName,
                        className,
                        ruleClassCode );

            }

        } catch ( GenerationException ge ) {
            final String msg = ge.getMessage();
            processingEnv.getMessager().printMessage( Diagnostic.Kind.ERROR, msg );
        }
        return true;

    }

    private boolean processLastRoundDefinitionAdapter( Set<? extends TypeElement> set, RoundEnvironment roundEnv ) throws Exception {
        final Messager messager = processingEnv.getMessager();
        try {
            // Ensure visible on both backend and client sides.
            final String packageName = getGeneratedPackageName() + ".definition.adapter.binding";
            final String className = getSetClassPrefix() + DEFINITION_ADAPTER_CLASSNAME;
            final String classFQName = packageName + "." + className;
            messager.printMessage( Diagnostic.Kind.WARNING, "Starting ErraiBinderAdapter generation named " + classFQName );
            final StringBuffer ruleClassCode = definitionAdapterGenerator.generate( packageName, className,
                    processingContext.getDefinitionAnnotations(), processingContext.getNamePropertyClass(), messager );
            writeCode( packageName,
                    className,
                    ruleClassCode );

        } catch ( GenerationException ge ) {
            final String msg = ge.getMessage();
            processingEnv.getMessager().printMessage( Diagnostic.Kind.ERROR, msg );
        }
        return true;

    }

    private boolean processLastRoundPropertyAdapter( Set<? extends TypeElement> set, RoundEnvironment roundEnv ) throws Exception {
        final Messager messager = processingEnv.getMessager();
        try {
            // Ensure visible on both backend and client sides.
            final String packageName = getGeneratedPackageName() + ".definition.adapter.binding";
            final String className = getSetClassPrefix() + PROPERTY_ADAPTER_CLASSNAME;
            final String classFQName = packageName + "." + className;
            messager.printMessage( Diagnostic.Kind.WARNING, "Starting ErraiBinderAdapter generation named " + classFQName );
            final StringBuffer ruleClassCode = propertyAdapterGenerator.generate( packageName, className,
                    processingContext.getPropertyAnnotations(), messager );
            writeCode( packageName,
                    className,
                    ruleClassCode );

        } catch ( GenerationException ge ) {
            final String msg = ge.getMessage();
            processingEnv.getMessager().printMessage( Diagnostic.Kind.ERROR, msg );
        }
        return true;

    }

    private boolean processLastRoundShapesStuffGenerator( Set<? extends TypeElement> set, RoundEnvironment roundEnv ) throws Exception {
        final Messager messager = processingEnv.getMessager();
        try {
            String shapeFactoryClassname = null;
            // Generate the Shape Factory, if exist shape definitions.
            if ( processingContext.getDefSetAnnotations().hasShapeSet() &&
                    !processingContext.getDefinitionAnnotations().getShapeDefinitions().isEmpty() ) {
                // Ensure only visible on client side.
                final String packageName = getGeneratedPackageName() + ".client.shape";
                final String className = getSetClassPrefix() + SHAPE_FACTORY_CLASSNAME;
                shapeFactoryClassname = packageName + "." + className;
                messager.printMessage( Diagnostic.Kind.WARNING, "Starting Shape Factory bean generation " +
                        "[" + shapeFactoryClassname + "]" );
                final StringBuffer sfClassCode =
                        shapeFactoryGenerator.generate(
                                packageName,
                                className,
                                processingContext.getDefinitionAnnotations(),
                                messager );
                writeCode( packageName,
                        className,
                        sfClassCode );
                // Generate the Shape Set if annotation present ( Ensure only visible on client side. ).
                final String packageName2 = getGeneratedPackageName() + ".client.shape";
                final String className2 = getSetClassPrefix() + BindableAdapterUtils.SHAPE_SET_SUFFIX;
                final String classFQName2 = packageName2 + "." + className2;
                messager.printMessage( Diagnostic.Kind.WARNING, "Starting Shape Set bean generation " +
                        "[" + classFQName2 + "]" );
                final String defSetClassName = processingContext.getDefinitionSet().getClassName();
                final StringBuffer ssClassCode = shapeSetGenerator.generate(
                        packageName2,
                        className2,
                        defSetClassName,
                        shapeFactoryClassname,
                        messager );
                writeCode( packageName2,
                        className2,
                        ssClassCode );
            }
        } catch ( GenerationException ge ) {
            final String msg = ge.getMessage();
            processingEnv.getMessager().printMessage( Diagnostic.Kind.ERROR, msg );
        }
        return true;
    }

    private String getGeneratedPackageName() {
        final String s = processingContext.getDefinitionSet().getClassName();
        return s.substring( 0, s.lastIndexOf( "." ) );
    }

    private String getSetClassPrefix() {
        return processingContext.getDefinitionSet().getId();
    }

    private void log( String message ) {
        final Messager messager = processingEnv.getMessager();
        messager.printMessage( Diagnostic.Kind.ERROR, message );
    }
}
