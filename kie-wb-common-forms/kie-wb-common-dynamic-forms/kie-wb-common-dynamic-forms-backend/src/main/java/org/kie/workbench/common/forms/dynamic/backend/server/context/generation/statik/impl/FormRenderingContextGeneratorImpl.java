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

package org.kie.workbench.common.forms.dynamic.backend.server.context.generation.statik.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.drools.workbench.models.datamodel.oracle.Annotation;
import org.drools.workbench.models.datamodel.oracle.DataType;
import org.drools.workbench.models.datamodel.oracle.ModelField;
import org.kie.workbench.common.forms.commons.layout.Dynamic;
import org.kie.workbench.common.forms.commons.layout.FormLayoutTemplateGenerator;
import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.statik.impl.fieldInitializers.FormAwareFieldInitializer;
import org.kie.workbench.common.forms.dynamic.service.context.generation.DynamicGenerator;
import org.kie.workbench.common.forms.dynamic.service.context.generation.FormRenderingContextGenerator;
import org.kie.workbench.common.forms.dynamic.service.context.generation.StaticGenerator;
import org.kie.workbench.common.forms.dynamic.service.shared.impl.StaticModelFormRenderingContext;
import org.kie.workbench.common.forms.metaModel.FieldDef;
import org.kie.workbench.common.forms.model.DefaultFieldTypeInfo;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.kie.workbench.common.forms.service.FieldManager;
import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.statik.impl.fieldInitializers.FieldInitializer;
import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.statik.impl.processors.FieldAnnotationProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@StaticGenerator
@DynamicGenerator
@Dependent
public class FormRenderingContextGeneratorImpl implements FormRenderingContextGenerator<DMOBasedTransformerContext, StaticModelFormRenderingContext> {

    private static final Logger logger = LoggerFactory.getLogger( FormRenderingContextGeneratorImpl.class );

    private FieldManager fieldManager;

    private FormLayoutTemplateGenerator layoutGenerator;

    private List<FieldAnnotationProcessor<? extends FieldDefinition>> processors = new ArrayList<>();

    private List<FieldInitializer<? extends FieldDefinition>> fieldInitializers = new ArrayList<>();

    @Inject
    public FormRenderingContextGeneratorImpl( Instance<FieldAnnotationProcessor<? extends FieldDefinition>> installedProcessors,
                                              Instance<FieldInitializer<? extends FieldDefinition>> installedInitializers,
                                              @Dynamic FormLayoutTemplateGenerator layoutGenerator,
                                              FieldManager fieldManager ) {
        this.layoutGenerator = layoutGenerator;
        this.fieldManager = fieldManager;

        for ( FieldAnnotationProcessor processor : installedProcessors ) {
            processors.add( processor );
        }

        for( FieldInitializer initializer : installedInitializers ) {
            if ( initializer instanceof FormAwareFieldInitializer ) {
                ( (FormAwareFieldInitializer) initializer ).setTransformerService( this );
            }
            fieldInitializers.add( initializer );
        }
    }

    @Override
    public StaticModelFormRenderingContext createContext( Object model ) {

        try {
            DMOBasedTransformerContext context = DMOBasedTransformerContext.getTransformerContextFor( model );

            FormDefinition form = generateFormDefinition( context );
            context.getRenderingContext().setRootForm( form );

            return context.getRenderingContext();
        } catch ( IOException e ) {
            logger.warn( "Error creating context: ", e );
        }
        return null;
    }


    public FormDefinition generateFormDefinition( DMOBasedTransformerContext context ) {
        FormDefinition form = new FormDefinition();

        final String modelType = context.getType();

        form.setId( modelType );

        Set<FieldSetting> settings = getClassFieldSettings( modelType, context );

        for( FieldSetting setting : settings ) {

            FieldDefinition field = null;

            for( Annotation annotation : setting.getAnnotations() ) {
                for ( FieldAnnotationProcessor processor : processors ) {
                    if ( processor.supportsAnnotation( annotation ) ) {
                        field = processor.getFieldDefinition( setting, annotation, context );
                    }
                }
            }

            if ( field == null ) {
                field = fieldManager.getDefinitionByValueType( setting.getTypeInfo() );
                if ( field != null ) {
                    field.setId( setting.getFieldName() );
                    field.setName( setting.getFieldName() );
                    field.setLabel( setting.getLabel() );

                    String binding = setting.getFieldName();

                    if ( !StringUtils.isEmpty( setting.getProperty() ) ) {
                        binding += "." + setting.getProperty();
                    }

                    field.setBinding( binding );
                }
            }

            if ( field == null ) {
                continue;
            }

            for ( FieldInitializer initializer : fieldInitializers ) {
                if ( initializer.supports( field ) ) {
                    initializer.initializeField( field, setting, context );
                }
            }

            form.getFields().add( field );
        }

        layoutGenerator.generateLayoutTemplate( form );

        return form;
    }

    @Override
    public FormDefinition generateFormDefinitionForType( String type, DMOBasedTransformerContext context ) {

        if ( !context.getType().equals( type ) ) {
            context = context.copyFor( type );
        }

        return generateFormDefinition( context );
    }

    protected Set<FieldSetting> getClassFieldSettings( String modelType, DMOBasedTransformerContext context ) {
        TreeSet<FieldSetting> settings = new TreeSet<FieldSetting>();

        ModelField[] modelFields = context.getOracle().getProjectModelFields().get( modelType );

        for( ModelField modelField : modelFields ) {

            final String fieldName = modelField.getName();

            if ( fieldName.equals( "this" ) ) {
                continue;
            }

            Set<Annotation> annotations = context.getOracle().getProjectTypeFieldsAnnotations().get( modelType ).get( fieldName );

            if ( annotations == null || annotations.isEmpty() ) {
                continue;
            }

            for ( Annotation annotation : annotations ) {
                if ( annotation.getQualifiedTypeName().equals( FieldDef.class.getName() ) ) {

                    String fieldModelType = modelType;
                    String fieldClassName = modelField.getClassName();

                    boolean isEnunm = context.getOracle().getProjectJavaEnumDefinitions().get( modelType + "#" + modelField.getName() ) != null;

                    // here we store final field we want to generate the Form Field for.
                    ModelField finalModelField = modelField;

                    // If field is collection check for the generic type
                    if ( isCollection( modelField ) ) {
                        fieldModelType = modelType;
                    } else {

                        String property = annotation.getParameters().get( "property" ).toString();

                        /*
                            if the annotation has property let's get the nested field and overwrite the finalField value
                            in order to generate a Form Field with the right type.
                        */
                        if ( property != null && !property.isEmpty() ) {
                            ModelField[] fields = context.getOracle().getProjectModelFields().get( fieldClassName );

                            for ( int i = 0; i < fields.length; i++ ) {
                                if ( fields[i].getName().equals( property ) ) {
                                    finalModelField = fields[i];

                                    // check if the nested value is an Enum
                                    isEnunm = context.getOracle().getProjectJavaEnumDefinitions().get( fieldClassName + "#" + property ) != null;

                                    /*
                                     if field isn't a collection let's get the real className, if it is a collection
                                      we'll use the type to get the generic type later.
                                    */
                                    if ( !isCollection( finalModelField ) )  {
                                        fieldClassName = finalModelField.getClassName();
                                    } else {
                                        fieldModelType = fieldClassName;
                                    }
                                }
                            }
                        }
                    }

                    boolean isCollection = isCollection( finalModelField );

                    if ( isCollection ) {
                        // if field is a collection let's get the generic type of it.
                        fieldClassName = context.getOracle().getProjectFieldParametersType().get( fieldModelType + "#" + finalModelField.getName() );
                    }

                    if ( fieldClassName != null ) {
                        FieldSetting setting = new FieldSetting( fieldName, new DefaultFieldTypeInfo( fieldClassName, isCollection, isEnunm ), annotation, annotations );
                        settings.add( setting );
                    }
                }
            }
        }

        return settings;
    }

    private boolean isCollection( ModelField modelField ) {
        return DataType.TYPE_COLLECTION.equals( modelField.getType() );
    }
}
