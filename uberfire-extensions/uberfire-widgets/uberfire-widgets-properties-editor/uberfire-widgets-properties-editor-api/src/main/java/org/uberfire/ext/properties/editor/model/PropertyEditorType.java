/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

package org.uberfire.ext.properties.editor.model;

import java.util.ArrayList;
import java.util.List;

import org.uberfire.ext.properties.editor.model.validators.ColorValidator;
import org.uberfire.ext.properties.editor.model.validators.LongValidator;
import org.uberfire.ext.properties.editor.model.validators.PropertyFieldValidator;

/**
 * PropertyEditorType define the type of a PropertyEditorField
 */
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

    }, COLOR {
        public List<PropertyFieldValidator> getValidators() {
            ArrayList validators = new ArrayList();
            validators.add( new ColorValidator() );
            return validators;
        }
    }, CUSTOM {

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