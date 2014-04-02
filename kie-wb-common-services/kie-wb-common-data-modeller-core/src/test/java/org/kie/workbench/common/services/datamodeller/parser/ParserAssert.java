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

import org.kie.workbench.common.services.datamodeller.parser.descr.*;
import org.kie.workbench.common.services.datamodeller.parser.util.ParserUtil;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class ParserAssert {

    public static void assertEqualsModifier( StringBuffer buffer, final ModifierDescr mod1, final ModifierDescr mod2 ) {
        if ( mod1 != null ) {
            assertNotNull( mod2 );
            assertEquals( mod1.getName( ), mod2.getName( ) );
            //TODO add this comparation assertEquals(mod1.getName(), ParserUtil.readElement(buffer, mod2));
        } else {
            assertNull( mod2 );
        }
    }

    public static void assertEqualsAnnotation( StringBuffer buffer, final AnnotationDescr annotation1, final AnnotationDescr annotation2) {

        if (annotation1 != null) {

            assertNotNull( annotation2 );

            //assertNotNull( annotation1.getStartAt() );
            //assertNotNull( annotation2.getStartAt() );

            assertQualifiedName( buffer, annotation1.getQualifiedName(), annotation2.getQualifiedName() );

            if (annotation1.hasElementValue()) {
                assertEquals( true, annotation2.hasElementValue() );
                assertEqualsElementValue( buffer, annotation1.getElementValue(), annotation2.getElementValue() );
            } else if ( annotation1.hasElementValuePairs() ) {
                assertEquals(true, annotation2.hasElementValuePairs() );
                assertEquals( annotation1.getElementValuePairs().size(), annotation2.getElementValuePairs().size());
                for (int i = 0; i < annotation1.getElementValuePairs().size(); i++) {
                    assertEqualsIdentifier(buffer, annotation1.getElementValuePairs().getValuePairs().get( i ).getIdentifier(), annotation2.getElementValuePairs().getValuePairs().get( i ).getIdentifier() );
                    assertEqualsElementValue( buffer, annotation1.getElementValuePairs().getValuePairs().get( i ).getValue(), annotation2.getElementValuePairs().getValuePairs().get( i ).getValue() );
                }
            } else {
                assertEquals( false, annotation2.hasElementValue() );
                assertEquals( false, annotation2.hasElementValuePairs() );
            }

        } else {
            assertNull( annotation2 );
        }
    }

    private static void assertEqualsElementValue( StringBuffer buffer, ElementValueDescr value1, ElementValueDescr value2 ) {
        if (value1 != null) {
            assertNotNull( value2 );
            assertEquals( value1.getValue(), value2.getValue() );
        } else {
            assertNull( value2 );
        }
    }

    public static void assertEqualsModifiers( StringBuffer buffer, ModifierListDescr modifiers1, ModifierListDescr modifiers2 ) {
        if ( modifiers1 != null ) {
            assertNotNull( modifiers2 );
            assertEquals( modifiers1.getAllModifiers().size(), modifiers2.getAllModifiers().size() );
            for ( int i = 0; i < modifiers1.getAllModifiers().size(); i++ ) {
                if (modifiers1.getAllModifiers().get(i).getElementType() == ElementDescriptor.ElementType.MODIFIER) {
                    assertEquals( ElementDescriptor.ElementType.MODIFIER, modifiers2.getAllModifiers().get( i ).getElementType() );
                    assertEqualsModifier( buffer, (ModifierDescr)modifiers1.getAllModifiers().get( i ), (ModifierDescr)modifiers2.getAllModifiers().get( i ));
                } else {
                    assertEquals( ElementDescriptor.ElementType.ANNOTATION, modifiers2.getAllModifiers().get( i ).getElementType() );
                    assertEqualsAnnotation( buffer,  ( AnnotationDescr )modifiers1.getAllModifiers().get( i ), (AnnotationDescr)modifiers2.getAllModifiers().get( i ) );
                }
            }
        } else {
            assertNull( modifiers2 );
        }
    }

    public static void assertEqualsPrimitiveType( StringBuffer buffer, PrimitiveTypeDescr type1, PrimitiveTypeDescr type2 ) {
        if ( type1 != null ) {
            assertNotNull( type2 );
            assertEquals( type1.getName( ), type2.getName( ) );
        } else {
            assertNull( type2 );
        }
    }

    public static void assertEqualsClassType( StringBuffer buffer, ClassOrInterfaceTypeDescr class1, ClassOrInterfaceTypeDescr class2 ) {
        if ( class1 != null ) {
            assertNotNull( class2 );
            assertEquals( class1.getClassName( ), class2.getClassName( ) );
        } else {
            assertNull( class2 );
        }
    }

    public static void assertEqualsVariableInitializer( StringBuffer buffer, VariableInitializerDescr var1, VariableInitializerDescr var2 ) {
        if ( var1 != null ) {
            assertNotNull( var2 );
            assertEquals( var1.getInitializerExpr( ), ParserUtil.readElement( buffer, var2 ) );
        } else {
            assertNull( var2 );
        }
    }

    public static void assertEqualsVariableDeclaration( StringBuffer buffer, VariableDeclarationDescr var1, VariableDeclarationDescr var2 ) {
        if ( var1 != null ) {
            assertNotNull( var2 );
            assertEqualsIdentifier( buffer, var1.getIdentifier( ), var2.getIdentifier( ) );
            assertEquals( var1.getDimensionsCount( ), var2.getDimensionsCount( ) );
            assertEqualsVariableInitializer( buffer, var1.getVariableInitializer( ), var2.getVariableInitializer( ) );
        } else {
            assertNull( var2 );
        }
    }

    public static void assertEqualsFieldDeclaration( StringBuffer buffer, FieldDescr field1, FieldDescr field2 ) {
        if ( field1 != null ) {
            assertNotNull( field2 );
            assertEqualsModifiers( buffer, field1.getModifiers( ), field2.getModifiers( ) );
            assertEquals( field1.getVariableDeclarations( ).size( ), field2.getVariableDeclarations( ).size( ) );
            for ( int i = 0; i < field1.getVariableDeclarations( ).size( ); i++ ) {
                assertEqualsVariableDeclaration( buffer, field1.getVariableDeclarations( ).get( i ), field2.getVariableDeclarations( ).get( i ) );
            }
            assertEqualsType( buffer, field1.getType( ), field2.getType( ) );
        } else {
            assertNull( field2 );
        }
    }

    public static void assertEqualsType( StringBuffer buffer, TypeDescr type1, TypeDescr type2 ) {

        if ( type1 != null ) {
            assertNotNull( type2 );
            if ( type1.isClassOrInterfaceType( ) ) {
                assertEquals( true, type2.isClassOrInterfaceType( ) );
                assertEqualsClassType( buffer, type1.getClassOrInterfaceType( ), type2.getClassOrInterfaceType( ) );
            } else if ( type1.isPrimitiveType( ) ) {
                assertEquals( true, type2.isPrimitiveType( ) );
                assertEqualsPrimitiveType( buffer, type1.getPrimitiveType( ), type2.getPrimitiveType( ) );
            } else if ( type1.isVoidType( ) ) {
                assertEquals( true, type2.isVoidType( ) );
            } else {
                assertNull( type1 );
                assertNull( type2 );
            }
        } else {
            assertNull( type2 );
        }
    }

    public static void assertEqualsMethodDeclaration( StringBuffer buffer, MethodDescr method1, MethodDescr method2 ) {
        if ( method1 != null ) {
            assertNotNull( method2 );
            assertEqualsIdentifier( buffer, method1.getIdentifier(), method2.getIdentifier() );
            assertEqualsModifiers( buffer, method1.getModifiers( ), method2.getModifiers( ) );
            assertEqualsType( buffer, method1.getType( ), method2.getType( ) );
            assertEqualsParameterList( buffer, method1.getParamsList( ), method2.getParamsList( ) );
            assertEquals( method1.getDimensionsCount( ), method2.getDimensionsCount( ) );
        } else {
            assertNull( method2 );
        }
    }

    public static void assertEqualsParameterList( StringBuffer buffer, ParameterListDescr params1, ParameterListDescr params2 ) {
        if ( params1 != null ) {
            assertNotNull( params2 );
            List<ParameterDescr> l1 = params1.getParameters( );
            List<ParameterDescr> l2 = params2.getParameters( );
            assertEquals( l1.size( ), l2.size( ) );

            for ( int i = 0; i < l1.size( ); i++ ) {
                assertEqualsParameter( buffer, l1.get( i ), l2.get( i ) );
            }
        } else {
            assertNull( params2 );
        }
    }

    public static void assertEqualsParameter( StringBuffer buffer, ParameterDescr param1, ParameterDescr param2 ) {
        if ( param1 != null ) {
            assertNotNull( param2 );
            assertEqualsIdentifier( buffer, param1.getIdentifier( ), param2.getIdentifier( ) );
            assertEqualsType( buffer, param1.getType( ), param2.getType( ) );
            assertEqualsModifiers( buffer, param1.getModifiers( ), param2.getModifiers( ) );
        } else {
            assertNull( param2 );
        }
    }

    public static void assertEqualsIdentifier( StringBuffer buffer, IdentifierDescr id1, IdentifierDescr id2 ) {
        if ( id1 != null ) {
            assertNotNull( id2 );
            assertEquals( id1.getIdentifier( ), id2.getIdentifier( ) );
        } else {
            assertNull(id2);
        }
    }

    public static void assertQualifiedName( StringBuffer buffer, QualifiedNameDescr qname1, QualifiedNameDescr qname2) {
        if (qname1 != null) {
            assertNotNull( qname2 );
            assertEquals( qname1.getName(), qname2.getName() );
        } else {
            assertNull( qname2 );
        }
    }

}
