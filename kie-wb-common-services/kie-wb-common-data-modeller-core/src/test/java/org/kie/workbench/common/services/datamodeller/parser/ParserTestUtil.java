/*
 * Copyright 2014 JBoss Inc
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

package org.kie.workbench.common.services.datamodeller.parser;

import org.kie.workbench.common.services.datamodeller.parser.descr.AnnotationDescr;
import org.kie.workbench.common.services.datamodeller.parser.descr.ClassOrInterfaceTypeDescr;
import org.kie.workbench.common.services.datamodeller.parser.descr.ElementDescriptor;
import org.kie.workbench.common.services.datamodeller.parser.descr.ElementValueDescr;
import org.kie.workbench.common.services.datamodeller.parser.descr.ElementValuePairDescr;
import org.kie.workbench.common.services.datamodeller.parser.descr.ElementValuePairListDescr;
import org.kie.workbench.common.services.datamodeller.parser.descr.FieldDescr;
import org.kie.workbench.common.services.datamodeller.parser.descr.IdentifierDescr;
import org.kie.workbench.common.services.datamodeller.parser.descr.JavaTokenDescr;
import org.kie.workbench.common.services.datamodeller.parser.descr.ModifierDescr;
import org.kie.workbench.common.services.datamodeller.parser.descr.ModifierListDescr;
import org.kie.workbench.common.services.datamodeller.parser.descr.PrimitiveTypeDescr;
import org.kie.workbench.common.services.datamodeller.parser.descr.QualifiedNameDescr;
import org.kie.workbench.common.services.datamodeller.parser.descr.TypeDescr;
import org.kie.workbench.common.services.datamodeller.parser.descr.VariableDeclarationDescr;
import org.kie.workbench.common.services.datamodeller.parser.descr.VariableInitializerDescr;
import org.kie.workbench.common.services.datamodeller.util.NamingUtils;

public class ParserTestUtil {

    public static AnnotationDescr createAnnotation(String className, String defaultValue, String[][] paramValues) {
        AnnotationDescr annotationDescr = new AnnotationDescr(  );
        annotationDescr.setQualifiedName( new QualifiedNameDescr( className, -1, -1, -1, -1 ) );
        if (defaultValue != null) {
            annotationDescr.setParamsStartParen( new JavaTokenDescr( ElementDescriptor.ElementType.JAVA_LBRACE, "(", -1, -1, -1, -1 ) );
            annotationDescr.setElementValue( new ElementValueDescr( defaultValue, -1, -1, -1, -1 ) );
            annotationDescr.setParamsStopParen( new JavaTokenDescr( ElementDescriptor.ElementType.JAVA_RBRACE, ")", -1, -1, -1, -1 ) );
        } else if (paramValues != null) {
            annotationDescr.setParamsStartParen( new JavaTokenDescr( ElementDescriptor.ElementType.JAVA_LBRACE, "(", -1, -1, -1, -1 ) );
            if (paramValues.length > 0) {

                ElementValuePairListDescr valuePairs = new ElementValuePairListDescr(  );
                for (int i = 0; i < paramValues.length; i++) {
                    ElementValuePairDescr valuePair = new ElementValuePairDescr();
                    valuePair.setIdentifier( new IdentifierDescr( paramValues[i][0], -1, -1, -1, -1 ) );
                    valuePair.setValue( new ElementValueDescr( paramValues[i][1], -1, -1, -1, -1 ) );
                    valuePairs.addValuePair( valuePair );
                }
                annotationDescr.setElementValuePairs( valuePairs  );

            }
            annotationDescr.setParamsStopParen( new JavaTokenDescr( ElementDescriptor.ElementType.JAVA_RBRACE, ")", -1, -1, -1, -1 ) );
        }
        return annotationDescr;
    }

    public static FieldDescr createField( String[] modifiers, String name, String type, String initializer ) {
        FieldDescr fieldDescr = new FieldDescr(  );
        if (modifiers != null) {
            fieldDescr.setModifiers( new ModifierListDescr(  ) );
            for (int i = 0; i < modifiers.length; i++) {
                fieldDescr.addModifier( new ModifierDescr( modifiers[i], -1, -1, -1, -1, modifiers[i] ) );
            }
        }
        if ( NamingUtils.isPrimitiveTypeId( type )) {
            fieldDescr.setType( new TypeDescr(  ) );
            fieldDescr.getType().setPrimitiveType( new PrimitiveTypeDescr( type, -1, -1, -1, -1, type ) );
        } else {
            fieldDescr.setType( new TypeDescr(  ) );
            fieldDescr.getType().setClassOrInterfaceType( new ClassOrInterfaceTypeDescr( type, -1, -1, -1, -1  ) );
        }

        VariableDeclarationDescr variableDecl = new VariableDeclarationDescr( );
        variableDecl.setIdentifier( new IdentifierDescr( name, -1, -1, -1 ) );
        fieldDescr.addVariableDeclaration( variableDecl );
        if (initializer != null) {
            variableDecl.setVariableInitializer( new VariableInitializerDescr( initializer, -1, -1, -1, -1, initializer ) );
        }
        fieldDescr.setEndSemiColon( new JavaTokenDescr( ElementDescriptor.ElementType.JAVA_SEMI_COLON, ";", -1, -1, -1, -1 ) );
        return fieldDescr;
    }

}
