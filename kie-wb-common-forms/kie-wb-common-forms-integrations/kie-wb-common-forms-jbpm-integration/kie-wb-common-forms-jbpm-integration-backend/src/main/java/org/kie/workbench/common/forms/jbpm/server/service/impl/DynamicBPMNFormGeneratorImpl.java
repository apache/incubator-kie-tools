/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.kie.workbench.common.forms.jbpm.server.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.drools.workbench.models.commons.backend.oracle.ProjectDataModelOracleImpl;
import org.drools.workbench.models.datamodel.oracle.DataType;
import org.drools.workbench.models.datamodel.oracle.ModelField;
import org.drools.workbench.models.datamodel.oracle.ProjectDataModelOracle;
import org.drools.workbench.models.datamodel.oracle.TypeSource;
import org.kie.workbench.common.forms.commons.layout.Dynamic;
import org.kie.workbench.common.forms.commons.layout.FormLayoutTemplateGenerator;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.EntityRelationField;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.IsCRUDDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.TableColumnMeta;
import org.kie.workbench.common.forms.jbpm.model.authoring.JBPMFormModel;
import org.kie.workbench.common.forms.jbpm.model.authoring.process.BusinessProcessFormModel;
import org.kie.workbench.common.forms.jbpm.model.authoring.task.TaskFormModel;
import org.kie.workbench.common.forms.jbpm.service.bpmn.DynamicBPMNFormGenerator;
import org.kie.workbench.common.forms.jbpm.service.bpmn.util.BPMNVariableUtils;
import org.kie.workbench.common.forms.model.FieldDataType;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.kie.workbench.common.forms.model.PortableJavaModel;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.HasPlaceHolder;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.HasNestedForm;
import org.kie.workbench.common.forms.service.FieldManager;
import org.kie.workbench.common.services.datamodel.backend.server.builder.projects.ClassFactBuilder;
import org.kie.workbench.common.services.datamodel.backend.server.builder.projects.FactBuilder;
import org.kie.workbench.common.services.datamodel.backend.server.builder.projects.ProjectDataModelOracleBuilder;

@Dependent
public class DynamicBPMNFormGeneratorImpl implements DynamicBPMNFormGenerator {

    private FieldManager fieldManager;

    private FormLayoutTemplateGenerator layoutTemplateGenerator;

    @Inject
    public DynamicBPMNFormGeneratorImpl( FieldManager fieldManager,
                                         @Dynamic FormLayoutTemplateGenerator layoutTemplateGenerator ) {
        this.fieldManager = fieldManager;
        this.layoutTemplateGenerator = layoutTemplateGenerator;
    }

    @Override
    public Collection<FormDefinition> generateProcessForms( BusinessProcessFormModel model, ClassLoader classLoader ) {
        return createFormDefinition( model.getProcessId(), model.getProcessId(), model, classLoader );
    }

    @Override
    public Collection<FormDefinition> generateTaskForms( TaskFormModel model, ClassLoader classLoader ) {
        return createFormDefinition( model.getFormName(), model.getFormName(), model, classLoader );
    }

    protected Collection<FormDefinition> createFormDefinition( String id,
                                                               String name,
                                                               JBPMFormModel model,
                                                               ClassLoader classLoader ) {
        Map<String, FormDefinition> allForms = new HashMap<>();

        FormDefinition form = new FormDefinition( model );

        form.setId( id );
        form.setName( name + BPMNVariableUtils.TASK_FORM_SUFFIX );

        allForms.put( form.getId(), form );

        GenerationContext context = new GenerationContext( allForms, classLoader );

        model.getVariables().forEach( variable -> {

            if ( !BPMNVariableUtils.isValidInputName( variable.getName() ) ) {
                return;
            }

            FieldDefinition field = generateFieldDefinition( variable.getName(),
                                                             new FieldDataType( BPMNVariableUtils.getRealTypeForInput(
                                                                     variable.getType() ) ),
                                                             context );
            if ( field != null ) {
                form.getFields().add( field );
            }
        } );

        layoutTemplateGenerator.generateLayoutTemplate( form );


        return allForms.values();
    }


    protected FieldDefinition generateFieldDefinition( String fieldName,
                                                       FieldDataType typeInfo,
                                                       GenerationContext context ) {
        FieldDefinition field = fieldManager.getDefinitionByDataType( typeInfo );

        if ( field == null ) {
            return null;
        }

        String label = fieldName.substring( 0, 1 ).toUpperCase() + fieldName.substring( 1 );
        field.setName( fieldName );
        field.setLabel( label );
        field.setStandaloneClassName( typeInfo.getType() );
        field.setBinding( fieldName );

        if ( field instanceof HasPlaceHolder ) {
            ( (HasPlaceHolder) field ).setPlaceHolder( label );
        }
        if ( field instanceof EntityRelationField ) {
            FormDefinition nestedForm = context.getContextForms().get( field.getStandaloneClassName() );

            if ( nestedForm == null ) {
                nestedForm = createFormDefinition( field.getStandaloneClassName(), context );
                context.getContextForms().put( nestedForm.getId(), nestedForm );
            }

            if ( nestedForm != null ) {
                if ( field instanceof HasNestedForm ) {
                    ( (HasNestedForm) field ).setNestedForm( nestedForm.getId() );
                } else {
                    IsCRUDDefinition multipleSubForm = (IsCRUDDefinition) field;

                    multipleSubForm.setCreationForm( nestedForm.getId() );
                    multipleSubForm.setEditionForm( nestedForm.getId() );

                    List<TableColumnMeta> tableColumnMetas = new ArrayList<>();

                    nestedForm.getFields().forEach( nestedField -> {
                        tableColumnMetas.add( new TableColumnMeta( nestedField.getLabel(), nestedField.getBinding() ) );
                    } );

                    multipleSubForm.setColumnMetas( tableColumnMetas );
                }
            }

        }

        return field;
    }

    protected FormDefinition createFormDefinition( final String modelType,
                                                   GenerationContext context ) {

        if ( context.getOracle() == null ) {
            Class clazz = null;
            try {
                clazz = context.getClassLoader().loadClass( modelType );
                if ( clazz == null ) {
                    clazz = getClass().forName( modelType );
                }
            } catch ( ClassNotFoundException e ) {
                return null;
            }
            if ( clazz != null ) {
                initOracle( clazz, context );
            }
        }

        if ( context.getOracle() != null ) {
            PortableJavaModel portableJavaModel = new PortableJavaModel( modelType );

            FormDefinition form = new FormDefinition( portableJavaModel );

            form.setId( modelType );
            form.setName( modelType );

            ProjectDataModelOracle oracle = context.getOracle();

            ModelField[] fields = oracle.getProjectModelFields().get( modelType );

            Arrays.stream( fields ).forEach( modelField -> {
                if ( modelField.getName().equals( "this" ) ) {
                    return;
                }
                FieldDataType info;
                String fieldType = modelField.getClassName();
                boolean isEnunm = oracle.getProjectJavaEnumDefinitions().get( modelType + "#" + modelField.getName() ) != null;
                boolean isList = DataType.TYPE_COLLECTION.equals( modelField.getType() );

                if ( isList ) {
                    fieldType = oracle.getProjectFieldParametersType().get( modelType + "#" + modelField.getName() );
                }

                info = new FieldDataType( fieldType, isList, isEnunm );

                FieldDefinition field = generateFieldDefinition( modelField.getName(),
                                                                 info,
                                                                 context );
                if ( field != null ) {
                    form.getFields().add( field );
                }
            } );

            layoutTemplateGenerator.generateLayoutTemplate( form );

            return form;
        }
        return null;
    }

    private void initOracle( Class clazz, GenerationContext context ) {
        try {
            final ProjectDataModelOracleBuilder builder = ProjectDataModelOracleBuilder.newProjectOracleBuilder();

            final ClassFactBuilder modelFactBuilder = new ClassFactBuilder( builder,
                                                                            clazz,
                                                                            false,
                                                                            TypeSource.JAVA_PROJECT );

            ProjectDataModelOracle oracle = modelFactBuilder.getDataModelBuilder().build();

            Map<String, FactBuilder> builders = new HashMap<>();

            for ( FactBuilder factBuilder : modelFactBuilder.getInternalBuilders().values() ) {
                if ( factBuilder instanceof ClassFactBuilder ) {
                    builders.put( ( (ClassFactBuilder) factBuilder ).getType(), factBuilder );
                    factBuilder.build( (ProjectDataModelOracleImpl) oracle );
                }
            }
            builders.put( modelFactBuilder.getType(), modelFactBuilder );

            modelFactBuilder.build( (ProjectDataModelOracleImpl) oracle );

            context.setOracle( oracle );
        } catch ( IOException ex ) {

        }
    }

    private class GenerationContext {
        private Map<String, FormDefinition> contextForms;

        private ClassLoader classLoader;

        private ProjectDataModelOracle oracle;

        public GenerationContext( Map<String, FormDefinition> contextForms, ClassLoader classLoader ) {
            this.contextForms = contextForms;
            this.classLoader = classLoader;
        }

        public Map<String, FormDefinition> getContextForms() {
            return contextForms;
        }

        public ClassLoader getClassLoader() {
            return classLoader;
        }

        public ProjectDataModelOracle getOracle() {
            return oracle;
        }

        public void setOracle( ProjectDataModelOracle oracle ) {
            this.oracle = oracle;
        }
    }
}
