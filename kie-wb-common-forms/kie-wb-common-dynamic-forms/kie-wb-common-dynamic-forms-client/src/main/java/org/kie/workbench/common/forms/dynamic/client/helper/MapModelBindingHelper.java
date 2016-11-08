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

package org.kie.workbench.common.forms.dynamic.client.helper;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import org.jboss.errai.databinding.client.MapPropertyType;
import org.jboss.errai.databinding.client.PropertyType;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.ioc.client.container.IOC;
import org.kie.workbench.common.forms.dynamic.service.shared.impl.MapModelRenderingContext;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.kie.workbench.common.forms.model.impl.basic.slider.IntegerSliderDefinition;
import org.kie.workbench.common.forms.model.impl.relations.MultipleSubFormFieldDefinition;
import org.kie.workbench.common.forms.model.impl.relations.SubFormFieldDefinition;

@ApplicationScoped
public class MapModelBindingHelper {

    protected Map<Class<? extends FieldDefinition>, PropertyGenerator> propertiesGenerator = new HashMap<>();

    protected Map<String, Class> basicProperties = new HashMap<>();

    @PostConstruct
    public void initialize() {

        basicProperties.put( String.class.getName(), String.class );
        basicProperties.put( Integer.class.getName(), Integer.class );
        basicProperties.put( int.class.getName(), Integer.class );
        basicProperties.put( Double.class.getName(), Double.class );
        basicProperties.put( double.class.getName(), Double.class );
        basicProperties.put( Long.class.getName(), Long.class );
        basicProperties.put( long.class.getName(), Long.class );
        basicProperties.put( Float.class.getName(), Float.class );
        basicProperties.put( float.class.getName(), Float.class );
        basicProperties.put( Boolean.class.getName(), Boolean.class );
        basicProperties.put( boolean.class.getName(), Boolean.class );
        basicProperties.put( Character.class.getName(), Character.class );
        basicProperties.put( char.class.getName(), Character.class );
        basicProperties.put( Date.class.getName(), Date.class );
        basicProperties.put( BigInteger.class.getName(), BigInteger.class );
        basicProperties.put( BigDecimal.class.getName(), BigDecimal.class );


        IOC.getBeanManager().lookupBeans( PropertyGenerator.class ).forEach( beanDef -> {
            PropertyGenerator generator = beanDef.getInstance();
            propertiesGenerator.put( generator.getType(), generator );
        } );
    }

    public void initContext( MapModelRenderingContext context ) {

        FormDefinition form = context.getRootForm();

        Map<String, PropertyType> modelProperties = generateModelDefinition( form, context );

        Map<String, Object> content = DataBinder.forMap( modelProperties ).getModel();

        Map<String, Object> contextModel = context.getModel() != null ? context.getModel() : new HashMap<>();

        prepareMapContent( content, form, contextModel, context );

        context.setModel( content );
    }

    public void prepareMapContent( Map<String, Object> formData, FormDefinition form, Map<String, Object> contextData, MapModelRenderingContext context ) {
        form.getFields().forEach( field -> {

            if ( field.getBinding() == null || field.getBinding().isEmpty() ) {
                return;
            }

            if ( formData.containsKey( field.getBinding() ) ) {
                return;
            }

            Object fieldValue = contextData.get( field.getBinding() );

            if ( field instanceof SubFormFieldDefinition ) {
                prepareMapContentFor( (SubFormFieldDefinition) field, fieldValue, formData, context );
            } else if ( field instanceof MultipleSubFormFieldDefinition ) {
                prepareMapContentFor( (MultipleSubFormFieldDefinition) field, fieldValue, formData, context );
            } else {
                formData.put( field.getBinding(), fieldValue );
            }
        } );
    }

    protected void prepareMapContentFor( SubFormFieldDefinition field, Object fieldValue, Map<String, Object> formData, MapModelRenderingContext context ) {
        FormDefinition subForm = context.getAvailableForms().get( field.getNestedForm() );

        formData.put( field.getBinding(), new HashMap<>() );
        Map<String, Object> nestedFormValues = (Map<String, Object>) formData.get( field.getBinding() );

        Map<String, Object> nestedValues = (Map<String, Object>) fieldValue;

        if ( nestedValues == null ) {
            nestedValues = new HashMap<>();
        }

        prepareMapContent( nestedFormValues, subForm, nestedValues, context );
    }

    protected void prepareMapContentFor( MultipleSubFormFieldDefinition field, Object fieldValue, Map<String, Object> formData, MapModelRenderingContext context ) {

        formData.put( field.getBinding(), new ArrayList<Map<String, Object>>() );

        final List<Map<String, Object>> nestedFormValues = (List<Map<String, Object>>) formData.get( field.getBinding() );

        final List<Map<String, Object>> nestedValues = (List<Map<String, Object>>) fieldValue;

        if ( nestedValues != null ) {
            nestedValues.forEach( nestedValue -> {
                Map<String, Object> nestedFormValue = new HashMap<>();
                FormDefinition creationForm = context.getAvailableForms().get( field.getCreationForm() );
                prepareMapContent( nestedFormValue, creationForm, nestedValue, context );
                FormDefinition editionForm = context.getAvailableForms().get( field.getEditionForm() );
                prepareMapContent( nestedFormValue, editionForm, nestedValue, context );
                nestedFormValues.add( nestedFormValue );
            } );
        }
    }

    protected Map<String, PropertyType> generateModelDefinition( FormDefinition form,
                                                                 MapModelRenderingContext context ) {
        return generateModelDefinition( form, context, new HashMap<>() );
    }

    protected Map<String, PropertyType> generateModelDefinition( FormDefinition form,
                                                                 MapModelRenderingContext context,
                                                                 Map<String, Map<String, PropertyType>> availableModels ) {

        if ( availableModels.containsKey( form.getId() ) ) {
            return availableModels.get( form.getId() );
        }

        Map<String, PropertyType> formTypes = new HashMap<>();
        availableModels.put( form.getId(), formTypes );

        form.getFields().forEach( field -> {
            PropertyType propertyType = null;
            if ( basicProperties.containsKey( field.getStandaloneClassName() ) ) {
                propertyType = new PropertyType( basicProperties.get( field.getStandaloneClassName() ) );
            } else if ( propertiesGenerator.containsKey( field.getClass() ) ){
                PropertyGenerator generator = propertiesGenerator.get( field.getClass() );
                propertyType = generator.generatePropertyType( field );
            } else if ( field instanceof SubFormFieldDefinition ) {
                propertyType = getModeldefinitionFor( (SubFormFieldDefinition) field, context, availableModels );
            } else if ( field instanceof MultipleSubFormFieldDefinition ) {
                propertyType = new PropertyType( List.class );
            }
            if ( propertyType != null ) {
                formTypes.put( field.getBinding(), propertyType );
            }
        } );

        return formTypes;
    }

    public MapPropertyType getModeldefinitionFor( SubFormFieldDefinition subFormField, MapModelRenderingContext context, Map<String, Map<String, PropertyType>> availableModels ) {
        FormDefinition subForm = context.getAvailableForms().get( subFormField.getNestedForm() );

        return new MapPropertyType( generateModelDefinition( subForm, context, availableModels ) );
    }

    public MapPropertyType getModeldefinitionFor( MultipleSubFormFieldDefinition subFormField, MapModelRenderingContext context, Map<String, Map<String, PropertyType>> availableModels ) {
        FormDefinition creationForms = context.getAvailableForms().get( subFormField.getCreationForm() );

        Map<String, PropertyType> modelDefinition = generateModelDefinition( creationForms, context, availableModels );

        FormDefinition editionForm = context.getAvailableForms().get( subFormField.getCreationForm() );

        modelDefinition.putAll( generateModelDefinition( editionForm, context, availableModels ) );

        modelDefinition.put( MapModelRenderingContext.FORM_ENGINE_OBJECT_IDX, new PropertyType( Integer.class ) );
        modelDefinition.put( MapModelRenderingContext.FORM_ENGINE_EDITED_OBJECT, new PropertyType( Boolean.class ) );

        return new MapPropertyType( modelDefinition );
    }

}
