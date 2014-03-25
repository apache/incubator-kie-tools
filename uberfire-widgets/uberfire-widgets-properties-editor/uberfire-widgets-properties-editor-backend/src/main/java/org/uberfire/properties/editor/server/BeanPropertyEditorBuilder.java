package org.uberfire.properties.editor.server;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.Dependent;

import org.jboss.errai.bus.server.annotations.Service;
import org.uberfire.properties.editor.model.PropertyEditorCategory;
import org.uberfire.properties.editor.model.PropertyEditorFieldInfo;
import org.uberfire.properties.editor.model.PropertyEditorType;
import org.uberfire.properties.editor.service.BeanPropertyEditorBuilderService;

@Service
@Dependent
public class BeanPropertyEditorBuilder implements BeanPropertyEditorBuilderService {

    @Override
    public PropertyEditorCategory extract( String fqcn ) {
        return extractOnlyBeanInfo( fqcn );
    }

    @Override
    public PropertyEditorCategory extract( String fqcn,
                                           Object instance ) {
        return extractBeanInfoAndValues( fqcn, instance );
    }

    private PropertyEditorCategory extractOnlyBeanInfo( String fqcn ) {
        return extractBeanInfoAndValues( fqcn, null );
    }

    private PropertyEditorCategory extractBeanInfoAndValues( String fqcn,
                                                             Object instance ) {
        Class targetClass;
        try {
            targetClass = Class.forName( fqcn );
        } catch ( Exception e ) {
            throw new NullBeanException();
        }

        PropertyEditorCategory beanCategory = new PropertyEditorCategory( targetClass.getSimpleName() );
        extractFieldInformationAndValues( targetClass, beanCategory, instance );
        return beanCategory;
    }

    private void extractFieldInformationAndValues( Class targetClass,
                                                   PropertyEditorCategory beanCategory,
                                                   Object instance ) throws ErrorReadingFieldInformationAndValues {
        for ( Field declaredField : targetClass.getDeclaredFields() ) {
            PropertyEditorType type = PropertyEditorType.getFromType( declaredField.getType() );
            if ( isAHandledType( type ) ) {
                PropertyEditorFieldInfo field = createPropertyEditorInfo( instance, declaredField, type );
                if ( isACombo( field ) ) {
                    generateComboValues( declaredField, field );
                }
                beanCategory.withField( field );
            }
        }
    }

    private PropertyEditorFieldInfo createPropertyEditorInfo( Object instance,
                                                              Field declaredField,
                                                              PropertyEditorType type ) {
        if ( needToExtractValues( instance ) ) {
            return new PropertyEditorFieldInfo( declaredField.getName(), extractFieldValue( instance, declaredField ), type );
        } else {
            return new PropertyEditorFieldInfo( declaredField.getName(), type );
        }
    }

    private boolean needToExtractValues( Object instance ) {
        return instance != null;
    }

    private boolean isACombo( PropertyEditorFieldInfo field ) {
        return field.getType().equals( PropertyEditorType.COMBO );
    }

    private String extractFieldValue( Object instance,
                                      Field field ) {
        try {
            return extractStringValue( instance, field );
        } catch ( IllegalAccessException e ) {
            throw new ErrorReadingFieldInformationAndValues();
        }
    }

    private String extractStringValue( Object instance,
                                       Field field ) throws IllegalAccessException {
        field.setAccessible( true );
        Object value = field.get( instance );
        if ( value != null ) {
            return value.toString();
        } else {
            return "";
        }
    }

    private void generateComboValues( Field declaredField,
                                      PropertyEditorFieldInfo field ) {
        List<String> values = new ArrayList<String>();
        for ( Object constant : declaredField.getType().getEnumConstants() ) {
            values.add( constant.toString() );
        }
        field.withComboValues( values );

    }

    public boolean isAHandledType( PropertyEditorType type ) {
        return type != null;
    }

    public class NullBeanException extends RuntimeException {

    }

    private class ErrorReadingFieldInformationAndValues extends RuntimeException {

    }
}
