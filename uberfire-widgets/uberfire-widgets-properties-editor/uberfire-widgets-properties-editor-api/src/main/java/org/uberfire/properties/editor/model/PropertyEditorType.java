package org.uberfire.properties.editor.model;

import java.util.ArrayList;
import java.util.List;

import org.uberfire.properties.editor.model.validators.LongValidator;
import org.uberfire.properties.editor.model.validators.PropertyFieldValidator;
import org.uberfire.properties.editor.model.validators.TextValidator;

public enum PropertyEditorType {

    TEXT {
        @Override
        public boolean isType( Class<?> type ) {
            return isString( type ) || isFloat( type ) || isDouble( type );
        }

        private boolean isFloat( Class<?> type ) {
            return ( type.equals( Float.class ) || ( type.toString().equalsIgnoreCase( "float" ) ) );
        }

        private boolean isDouble( Class<?> type ) {
            return ( type.equals( Double.class ) || ( type.toString().equalsIgnoreCase( "double" ) ) );
        }

        private boolean isString( Class<?> type ) {
            return type.equals( String.class );
        }

        @Override
        public List<PropertyFieldValidator> getValidators() {
            ArrayList validators = new ArrayList();
            validators.add( new TextValidator() );
            return validators;
        }

    }, BOOLEAN {
        @Override
        public boolean isType( Class<?> type ) {
            return ( type.equals( Boolean.class ) || ( type.toString().equalsIgnoreCase( "boolean" ) ) );
        }
    }, NATURAL_NUMBER {
        @Override
        public List<PropertyFieldValidator> getValidators() {
            ArrayList validators = new ArrayList();
            validators.add( new LongValidator() );
            return validators;
        }

        @Override
        public boolean isType( Class<?> type ) {
            return isInteger( type ) || isLong( type ) || isShort( type );
        }

        private boolean isShort( Class<?> type ) {
            return ( type.equals( Short.class ) || ( type.toString().equalsIgnoreCase( "short" ) ) );
        }

        private boolean isLong( Class<?> type ) {
            return ( type.equals( Long.class ) || ( type.toString().equalsIgnoreCase( "long" ) ) );
        }

        private boolean isInteger( Class<?> type ) {
            return ( type.equals( Integer.class ) || ( type.toString().equalsIgnoreCase( "int" ) ) );
        }
    }, COMBO {
        @Override
        public boolean isType( Class<?> type ) {
            return type.isEnum();
        }
    }, SECRET_TEXT {

    };

    public boolean isType( Class<?> type ) {
        return false;
    }

    public List<PropertyFieldValidator> getValidators() {
        return new ArrayList();
    }

    public static PropertyEditorType getFromType( Class<?> type ) {
        for ( PropertyEditorType candidate : PropertyEditorType.values() ) {
            if ( candidate.isType( type ) ) {
                return candidate;
            }
        }
        return null;
    }

}
