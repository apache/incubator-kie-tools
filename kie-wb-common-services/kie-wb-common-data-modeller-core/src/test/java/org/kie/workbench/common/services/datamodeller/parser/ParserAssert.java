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
        if ( mod1 != null && mod2 != null ) {
            assertEquals( mod1.getName( ), mod2.getName( ) );
            //TODO add this comparation assertEquals(mod1.getName(), ParserUtil.readElement(buffer, mod2));
        }
        if ( mod1 == null ) {
            assertNull( mod2 );
        }
        if ( mod2 == null ) {
            assertNull( mod1 );
        }
    }

    public static void assertEqualsModifiers( StringBuffer buffer, ModifierListDescr modifiers1, ModifierListDescr modifiers2 ) {
        if ( modifiers1 != null && modifiers2 != null ) {
            assertEquals( modifiers1.getModifiers( ).size( ), modifiers2.getModifiers( ).size( ) );
            for ( int i = 0; i < modifiers1.getModifiers( ).size( ); i++ ) {
                assertEqualsModifier( buffer, modifiers1.getModifiers( ).get( i ), modifiers2.getModifiers( ).get( i ) );
            }
        }
        if ( modifiers1 == null ) {
            assertNull( modifiers2 );
        }
        if ( modifiers2 == null ) {
            assertNull( modifiers1 );
        }
    }

    public static void assertEqualsPrimitiveType( StringBuffer buffer, PrimitiveTypeDescr type1, PrimitiveTypeDescr type2 ) {
        if ( type1 != null && type2 != null ) {
            assertEquals( type1.getName( ), type2.getName( ) );
        }
        if ( type1 == null ) {
            assertNull( type2 );
        }
        if ( type2 == null ) {
            assertNull( type1 );
        }
    }

    public static void assertEqualsClassType( StringBuffer buffer, ClassOrInterfaceTypeDescr class1, ClassOrInterfaceTypeDescr class2 ) {
        if ( class1 != null && class2 != null ) {
            assertEquals( class1.getClassName( ), class2.getClassName( ) );
        }
        if ( class1 == null ) {
            assertNull( class2 );
        }
        if ( class2 == null ) {
            assertNull( class1 );
        }
    }

    public static void assertEqualsVariableInitializer( StringBuffer buffer, VariableInitializerDescr var1, VariableInitializerDescr var2 ) {
        if ( var1 != null && var2 != null ) {
            assertEquals( var1.getInitializerExpr( ), ParserUtil.readElement( buffer, var2 ) );
        }
        if ( var1 == null ) {
            assertNull( var2 );
        }
        if ( var2 == null ) {
            assertNull( var1 );
        }
    }

    public static void assertEqualsVariableDeclaration( StringBuffer buffer, VariableDeclarationDescr var1, VariableDeclarationDescr var2 ) {
        if ( var1 != null && var2 != null ) {
            assertEqualsIdentifier( buffer, var1.getIdentifier( ), var2.getIdentifier( ) );
            assertEquals( var1.getDimensionsCount( ), var2.getDimensionsCount( ) );
            assertEqualsVariableInitializer( buffer, var1.getVariableInitializer( ), var2.getVariableInitializer( ) );
        }
        if ( var1 == null ) {
            assertNull( var2 );
        }
        if ( var2 == null ) {
            assertNull( var1 );
        }
    }

    public static void assertEqualsFieldDeclaration( StringBuffer buffer, FieldDescr field1, FieldDescr field2 ) {
        if ( field1 != null && field2 != null ) {
            assertEqualsModifiers( buffer, field1.getModifiers( ), field2.getModifiers( ) );
            assertEquals( field1.getVariableDeclarations( ).size( ), field2.getVariableDeclarations( ).size( ) );
            for ( int i = 0; i < field1.getVariableDeclarations( ).size( ); i++ ) {
                assertEqualsVariableDeclaration( buffer, field1.getVariableDeclarations( ).get( i ), field2.getVariableDeclarations( ).get( i ) );
            }
            assertEqualsType( buffer, field1.getType( ), field2.getType( ) );
        }
        if ( field1 == null ) {
            assertNull( field2 );
        }
        if ( field2 == null ) {
            assertNull( field1 );
        }
    }

    public static void assertEqualsType( StringBuffer buffer, TypeDescr type1, TypeDescr type2 ) {

        if ( type1 != null && type2 != null ) {
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
        }
        if ( type1 == null ) {
            assertNull( type2 );
        }
        if ( type2 == null ) {
            assertNull( type1 );
        }
    }

    public static void assertEqualsMethodDeclaration( StringBuffer buffer, MethodDescr method1, MethodDescr method2 ) {
        if ( method1 != null && method2 != null ) {
            assertEqualsModifiers( buffer, method1.getModifiers( ), method2.getModifiers( ) );
            assertEqualsType( buffer, method1.getType( ), method2.getType( ) );
            assertEqualsParameterList( buffer, method1.getParamsList( ), method2.getParamsList( ) );
            assertEquals( method1.getDimensionsCount( ), method2.getDimensionsCount( ) );
        }
        if ( method1 == null ) {
            assertNull( method2 );
        }
        if ( method2 == null ) {
            assertNotNull( method1 );
        }
    }

    public static void assertEqualsParameterList( StringBuffer buffer, ParameterListDescr params1, ParameterListDescr params2 ) {
        if ( params1 != null && params2 != null ) {
            List<ParameterDescr> l1 = params1.getParameters( );
            List<ParameterDescr> l2 = params2.getParameters( );
            assertEquals( l1.size( ), l2.size( ) );

            for ( int i = 0; i < l1.size( ); i++ ) {
                assertEqualsParameter( buffer, l1.get( i ), l2.get( i ) );
            }
        }

        if ( params1 == null ) {
            assertNull( params2 );
        }
        if ( params2 == null ) {
            assertNull( params1 );
        }
    }

    public static void assertEqualsParameter( StringBuffer buffer, ParameterDescr param1, ParameterDescr param2 ) {
        if ( param1 != null && param2 != null ) {
            assertEqualsIdentifier( buffer, param1.getIdentifier( ), param2.getIdentifier( ) );
            assertEqualsType( buffer, param1.getType( ), param2.getType( ) );
            assertEqualsModifiers( buffer, param1.getModifiers( ), param2.getModifiers( ) );
        }
        if ( param1 == null ) {
            assertNull( param2 );
        }
        if ( param2 == null ) {
            assertNull( param1 );
        }
    }

    public static void assertEqualsIdentifier( StringBuffer buffer, IdentifierDescr id1, IdentifierDescr id2 ) {
        if ( id1 != null && id2 != null ) {
            assertEquals( id1.getIdentifier( ), id2.getIdentifier( ) );
        }
        if ( id1 == null ) {
            assertNull( id2 );
        }
        if ( id2 == null ) {
            assertNull( id1 );
        }
    }

}
