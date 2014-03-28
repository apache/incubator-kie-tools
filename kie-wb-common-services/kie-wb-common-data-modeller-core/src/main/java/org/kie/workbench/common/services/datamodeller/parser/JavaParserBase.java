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

import java.util.Stack;

import org.antlr.runtime.CommonToken;
import org.antlr.runtime.Parser;
import org.antlr.runtime.RecognizerSharedState;
import org.antlr.runtime.Token;
import org.antlr.runtime.TokenStream;
import org.kie.workbench.common.services.datamodeller.parser.descr.ClassDescr;
import org.kie.workbench.common.services.datamodeller.parser.descr.ClassOrInterfaceTypeDescr;
import org.kie.workbench.common.services.datamodeller.parser.descr.ElementDescriptor;
import org.kie.workbench.common.services.datamodeller.parser.descr.ElementDescriptor.ElementType;
import org.kie.workbench.common.services.datamodeller.parser.descr.EllipsisParameterDescr;
import org.kie.workbench.common.services.datamodeller.parser.descr.FieldDescr;
import org.kie.workbench.common.services.datamodeller.parser.descr.FileDescr;
import org.kie.workbench.common.services.datamodeller.parser.descr.HasClassOrInterfaceType;
import org.kie.workbench.common.services.datamodeller.parser.descr.HasModifiers;
import org.kie.workbench.common.services.datamodeller.parser.descr.HasPrimitiveType;
import org.kie.workbench.common.services.datamodeller.parser.descr.HasType;
import org.kie.workbench.common.services.datamodeller.parser.descr.HasTypeArguments;
import org.kie.workbench.common.services.datamodeller.parser.descr.IdentifierWithTypeArgumentsDescr;
import org.kie.workbench.common.services.datamodeller.parser.descr.ImportDescr;
import org.kie.workbench.common.services.datamodeller.parser.descr.JavaTokenDescr;
import org.kie.workbench.common.services.datamodeller.parser.descr.MethodDescr;
import org.kie.workbench.common.services.datamodeller.parser.descr.ModifierListDescr;
import org.kie.workbench.common.services.datamodeller.parser.descr.NormalParameterDescr;
import org.kie.workbench.common.services.datamodeller.parser.descr.PackageDescr;
import org.kie.workbench.common.services.datamodeller.parser.descr.ParameterDescr;
import org.kie.workbench.common.services.datamodeller.parser.descr.ParameterListDescr;
import org.kie.workbench.common.services.datamodeller.parser.descr.QualifiedNameDescr;
import org.kie.workbench.common.services.datamodeller.parser.descr.TypeArgumentDescr;
import org.kie.workbench.common.services.datamodeller.parser.descr.TypeArgumentListDescr;
import org.kie.workbench.common.services.datamodeller.parser.descr.TypeDescr;

public class JavaParserBase extends Parser {

    public static enum ParserMode {
        PARSE_CLASS,
        PARSE_METHOD,
        PARSE_FIELD,
        PARSE_ANNOTATION,
        PARSE_PACKAGE, PARSE_QUALIFIED_NAME, PARSE_TYPE
    }

    public JavaParserBase( TokenStream input, RecognizerSharedState state ) {
        super( input, state );
    }

    protected FileDescr fileDescr = new FileDescr( );

    protected MethodDescr methodDescr;

    protected FieldDescr fieldDescr;

    protected Stack<ElementDescriptor> context = new Stack<ElementDescriptor>( );

    protected boolean declaringMethodReturnType = false;

    protected boolean declaringSuperClass = false;

    protected int classLevel = 0;

    protected ParserMode mode = ParserMode.PARSE_CLASS;

    protected StringBuilder sourceBuffer;

    public FileDescr getFileDescr( ) {
        return fileDescr;
    }

    public ParserMode getMode( ) {
        return mode;
    }

    public MethodDescr getMethodDescr( ) {
        return methodDescr;
    }

    public FieldDescr getFieldDescr( ) {
        return fieldDescr;
    }

    public void setMode( ParserMode mode ) {
        this.mode = mode;
    }

    public StringBuilder getSourceBuffer( ) {
        return sourceBuffer;
    }

    public void setSourceBuffer( StringBuilder sourceBuffer ) {
        this.sourceBuffer = sourceBuffer;
    }

    private ClassDescr getClassDescr( ) {
        return fileDescr.getClassDescr( );
    }

    protected void initContext( ) {
        context.push( fileDescr );
        if ( sourceBuffer != null && sourceBuffer.length( ) > 0 ) {
            fileDescr.setStop( sourceBuffer.length( ) - 1 );
        }
    }

    protected void log( String message ) {
        //TODO setup log stuff
        System.out.println( message + " : " + new java.util.Date( ) );
    }

    protected boolean isFieldOnTop( ) {
        return isOnTop( ElementType.FIELD );
    }

    protected boolean isMethodOnTop( ) {
        return isOnTop( ElementType.METHOD );
    }

    protected boolean isTypeOnTop( ) {
        return isOnTop( ElementType.TYPE );
    }

    protected boolean isClassOrInterfaceTypeOnTop( ) {
        return isOnTop( ElementType.CLASS_OR_INTERFACE_TYPE );
    }

    protected boolean isTypeArgumentOnTop( ) {
        return isOnTop( ElementType.TYPE_ARGUMENT );
    }

    protected boolean isParameterOnTop( ) {
        return isOnTop( ElementType.NORMAL_PARAMETER ) || isOnTop( ElementType.ELLIPSIS_PARAMETER );
    }

    protected boolean isNormalParameterOnTop( ) {
        return isOnTop( ElementType.NORMAL_PARAMETER );
    }

    protected boolean isEllipsisParameterOnTop( ) {
        return isOnTop( ElementType.ELLIPSIS_PARAMETER );
    }

    protected boolean isModifierListOnTop( ) {
        return isOnTop( ElementType.MODIFIER_LIST );
    }

    protected boolean isClassOnTop( ) {
        return isOnTop( ElementType.CLASS );
    }

    protected boolean isFileOnTop( ) {
        return isOnTop( ElementType.FILE );
    }

    protected boolean isQualifiedNameOnTop( ) {
        return isOnTop( ElementType.QUALIFIED_NAME );
    }

    protected boolean isPackageOnTop( ) {
        return isOnTop( ElementType.PACKAGE );
    }

    protected boolean isImportOnTop( ) {
        return isOnTop( ElementType.IMPORT );
    }

    protected boolean isIdentifierWithTypeArgumentsOnTop( ) {
        return isOnTop( ElementType.IDENTIFIER_WITH_TYPE_ARGUMENTS );
    }

    protected boolean isTypeArgumentListOnTop( ) {
        return isOnTop( ElementType.TYPE_ARGUMENT_LIST );
    }

    protected boolean isOnTop( ElementType elementType ) {
        return !context.empty( ) && context.peek( ).isElementType( elementType );
    }

    protected MethodDescr popMethod( ) {
        return isMethodOnTop( ) ? ( MethodDescr ) context.pop( ) : null;
    }

    protected MethodDescr peekMethod( ) {
        return isMethodOnTop( ) ? ( MethodDescr ) context.peek( ) : null;
    }

    protected FieldDescr popField( ) {
        return isFieldOnTop( ) ? ( FieldDescr ) context.pop( ) : null;
    }

    protected FieldDescr peekField( ) {
        return isFieldOnTop( ) ? ( FieldDescr ) context.peek( ) : null;
    }

    protected TypeDescr popType( ) {
        return isTypeOnTop( ) ? ( TypeDescr ) context.pop( ) : null;
    }

    protected TypeDescr peekType( ) {
        return isTypeOnTop( ) ? ( TypeDescr ) context.peek( ) : null;
    }

    protected ClassOrInterfaceTypeDescr popClassOrInterfaceType( ) {
        return isClassOrInterfaceTypeOnTop( ) ? ( ClassOrInterfaceTypeDescr ) context.pop( ) : null;
    }

    protected ClassOrInterfaceTypeDescr peekClassOrInterfaceType( ) {
        return isClassOrInterfaceTypeOnTop( ) ? ( ClassOrInterfaceTypeDescr ) context.peek( ) : null;
    }

    protected TypeArgumentDescr popTypeArgument( ) {
        return isTypeArgumentOnTop( ) ? ( TypeArgumentDescr ) context.pop( ) : null;
    }

    protected ParameterDescr popParameter( ) {
        return isParameterOnTop( ) ? ( ParameterDescr ) context.pop( ) : null;
    }

    protected ParameterDescr peekParameter( ) {
        return isParameterOnTop( ) ? ( ParameterDescr ) context.peek( ) : null;
    }

    protected NormalParameterDescr popNormalParameter( ) {
        return isNormalParameterOnTop( ) ? ( NormalParameterDescr ) context.pop( ) : null;
    }

    protected NormalParameterDescr peekNormalParameter( ) {
        return isNormalParameterOnTop( ) ? ( NormalParameterDescr ) context.peek( ) : null;
    }

    protected EllipsisParameterDescr popEllipsisParameter( ) {
        return isEllipsisParameterOnTop( ) ? ( EllipsisParameterDescr ) context.pop( ) : null;
    }

    protected EllipsisParameterDescr peekEllipsisParameter( ) {
        return isEllipsisParameterOnTop( ) ? ( EllipsisParameterDescr ) context.peek( ) : null;
    }

    protected TypeArgumentListDescr popTypeArgumentList( ) {
        return isTypeArgumentListOnTop( ) ? ( TypeArgumentListDescr ) context.pop( ) : null;
    }

    protected TypeArgumentListDescr peekTypeArgumentList( ) {
        return isTypeArgumentListOnTop( ) ? ( TypeArgumentListDescr ) context.peek( ) : null;
    }

    protected ModifierListDescr popModifierList( ) {
        return isModifierListOnTop( ) ? ( ModifierListDescr ) context.pop( ) : null;
    }

    protected ModifierListDescr peekModifierList( ) {
        return isModifierListOnTop( ) ? ( ModifierListDescr ) context.peek( ) : null;
    }

    protected ClassDescr popClass( ) {
        return isClassOnTop( ) ? ( ClassDescr ) context.pop( ) : null;
    }

    protected ClassDescr peekClass( ) {
        return isClassOnTop( ) ? ( ClassDescr ) context.peek( ) : null;
    }

    protected FileDescr popFile( ) {
        return isFileOnTop( ) ? ( FileDescr ) context.pop( ) : null;
    }

    protected FileDescr peekFile( ) {
        return isFileOnTop( ) ? ( FileDescr ) context.peek( ) : null;
    }

    protected PackageDescr popPackage( ) {
        return isPackageOnTop( ) ? ( PackageDescr ) context.pop( ) : null;
    }

    protected PackageDescr peekPackage( ) {
        return isPackageOnTop( ) ? ( PackageDescr ) context.peek( ) : null;
    }

    protected ImportDescr popImport( ) {
        return isImportOnTop( ) ? ( ImportDescr ) context.pop( ) : null;
    }

    protected ImportDescr peekImport( ) {
        return isImportOnTop( ) ? ( ImportDescr ) context.peek( ) : null;
    }

    protected QualifiedNameDescr popQualifiedName( ) {
        return isQualifiedNameOnTop( ) ? ( QualifiedNameDescr ) context.pop( ) : null;
    }

    protected QualifiedNameDescr peekQualifiedName( ) {
        return isQualifiedNameOnTop( ) ? ( QualifiedNameDescr ) context.peek( ) : null;
    }

    protected IdentifierWithTypeArgumentsDescr popIdentifierWithTypeArguments( ) {
        return isIdentifierWithTypeArgumentsOnTop( ) ? ( IdentifierWithTypeArgumentsDescr ) context.pop( ) : null;
    }

    protected IdentifierWithTypeArgumentsDescr peekIdentifierWithTypeArguments( ) {
        return isIdentifierWithTypeArgumentsOnTop( ) ? ( IdentifierWithTypeArgumentsDescr ) context.peek( ) : null;
    }

    protected TypeArgumentDescr peekTypeArgument( ) {
        return isTypeArgumentOnTop( ) ? ( TypeArgumentDescr ) context.peek( ) : null;
    }

    protected HasModifiers peekHasModifiers( ) {
        return !context.empty( ) && ( context.peek( ) instanceof HasModifiers ) ? ( HasModifiers ) context.peek( ) : null;
    }

    protected HasType peekHasType( ) {
        return !context.empty( ) && ( context.peek( ) instanceof HasType ) ? ( HasType ) context.peek( ) : null;
    }

    protected HasClassOrInterfaceType peekHasClassOrInterfaceType( ) {
        return !context.empty( ) && ( context.peek( ) instanceof HasClassOrInterfaceType ) ? ( HasClassOrInterfaceType ) context.peek( ) : null;
    }

    protected HasPrimitiveType peekHasPrimitiveType( ) {
        return !context.empty( ) && ( context.peek( ) instanceof HasPrimitiveType ) ? ( HasPrimitiveType ) context.peek( ) : null;
    }

    protected HasTypeArguments peekHasTypeArguments( ) {
        return !context.empty( ) && ( context.peek( ) instanceof HasTypeArguments ) ? ( HasTypeArguments ) context.peek( ) : null;
    }

    protected int start( CommonToken token ) {
        return token != null ? token.getStartIndex( ) : -1;
    }

    protected int stop( CommonToken token ) {
        return token != null ? token.getStopIndex( ) : -1;
    }

    protected int line( Token token ) {
        return token != null ? token.getLine( ) : -1;
    }

    protected int position( Token token ) {
        return token != null ? token.getCharPositionInLine( ) : -1;
    }

    protected int calcStart( String text, Token token ) {
        return 1;
    }

    protected int calcStop( String text, Token token ) {
        return 1;
    }

    protected int calcStop( ) {
        return 0;
    }

    protected boolean isBacktracking( ) {
        return state.backtracking > 0;
    }

    protected void updateOnAfter( ElementDescriptor element, String text, CommonToken stop ) {
        element.setText( text );
        element.setStop( stop( stop ) );
    }

    protected void updateOnAfter( ElementDescriptor element, String text, CommonToken start, CommonToken stop ) {
        element.setText( text );
        element.setStart( start( start ) );
        element.setLine( line( start ) );
        element.setPosition( position( start ) );
        element.setStop( stop( stop ) );
    }

    protected boolean isDeclaringMethodReturnType( ) {
        return declaringMethodReturnType;
    }

    protected void setDeclaringMethodReturnType( boolean declaringMethodReturnType ) {
        this.declaringMethodReturnType = declaringMethodReturnType;
    }

    public void setDeclaringSuperClass( boolean declaringSuperClass ) {
        this.declaringSuperClass = declaringSuperClass;
    }

    public boolean isDeclaringMainClass( ) {
        return classLevel == 1 && mode == ParserMode.PARSE_CLASS;
    }

    public int increaseClassLevel( ) {
        return ++classLevel;
    }

    public int decreaseClassLevel( ) {
        return --classLevel;
    }

    protected void processType( TypeDescr type ) {
        //if we are processing a method declaration return type, or a method parameter, or a field type
        if ( isDeclaringMainClass( ) || mode == ParserMode.PARSE_FIELD || mode == ParserMode.PARSE_METHOD ) {
            if ( isTypeArgumentOnTop( ) ) {
                peekTypeArgument( ).setType( type );
            } else if ( isFieldOnTop( ) ) {
                peekField( ).setType( type );
            } else if ( isMethodOnTop( ) && declaringMethodReturnType ) {
                peekMethod( ).setType( type );
            } else if ( isParameterOnTop( ) ) {
                peekParameter( ).setType( type );
            } else if ( isClassOnTop( ) && declaringSuperClass ) {
                peekClass( ).setSuperClass( type );
            }
        }
    }

    protected void processModifiers( ModifierListDescr modifiers ) {
        if ( isDeclaringMainClass( ) || mode == ParserMode.PARSE_FIELD || mode == ParserMode.PARSE_METHOD ) {
            if ( isTypeArgumentOnTop( ) || isMethodOnTop( ) || isFieldOnTop( ) || isParameterOnTop( ) || isClassOnTop( ) ) {
                peekHasModifiers( ).setModifiers( modifiers );
            }
        }
    }

    protected void processMethod( MethodDescr methodDescr ) {
        if ( isDeclaringMainClass( ) ) {
            getClassDescr( ).addMember( methodDescr );
        } else if ( mode == ParserMode.PARSE_METHOD ) {
            this.methodDescr = methodDescr;
        }
    }

    protected void setFormalParamsStart( ElementType type, String text, int start, int stop, int line, int position ) {
        if ( isDeclaringMainClass( ) || mode == ParserMode.PARSE_METHOD ) {
            if ( isMethodOnTop( ) ) {
                peekMethod( ).setParamsStartParen( new JavaTokenDescr( type, text, start, stop, line, position ) );
            }
        }
    }

    protected void setFormalParamsStop( ElementType type, String text, int start, int stop, int line, int position ) {
        if ( isDeclaringMainClass( ) || mode == ParserMode.PARSE_METHOD ) {
            if ( isMethodOnTop( ) ) {
                peekMethod( ).setParamsStopParen( new JavaTokenDescr( type, text, start, stop, line, position ) );
            }
        }
    }

    protected void processParameterList( ParameterListDescr params ) {
        if ( isDeclaringMainClass( ) || mode == ParserMode.PARSE_METHOD ) {
            if ( isMethodOnTop( ) ) {
                peekMethod( ).setParamsList( params );
            }
        }
    }

    protected void processField( FieldDescr fieldDescr ) {
        if ( isDeclaringMainClass( ) ) {
            getClassDescr( ).addMember( fieldDescr );
        } else if ( mode == ParserMode.PARSE_FIELD ) {
            this.fieldDescr = fieldDescr;
        }
    }

    protected void processClass( ClassDescr classDescr ) {
        if ( isDeclaringMainClass( ) ) {
            fileDescr.setClassDescr( classDescr );
            context.push( classDescr );
        }
    }

    protected void processQualifiedName( QualifiedNameDescr nameDescr ) {
        /**
         if (isPackageOnTop()) {
         PackageDescr packageDescr = peekPackage();
         packageDescr.setQualifiedName(nameDescr);
         }
         **/
    }

    protected void processPackage( PackageDescr packageDescr ) {
        fileDescr.setPackageDescr( packageDescr );
    }

    protected void processImport( ImportDescr importDescr ) {
        fileDescr.addImport( importDescr );
    }

    protected void processTypeArgumentList( TypeArgumentListDescr arguments ) {
        if ( isDeclaringMainClass( ) ) {
            if ( isIdentifierWithTypeArgumentsOnTop( ) ) {
                peekIdentifierWithTypeArguments( ).setArguments( arguments );
            }
        }
    }

    protected void processClassBodyStart( JavaTokenDescr bodyStart ) {
        if (isDeclaringMainClass()) {
            peekClass().setBodyStartBrace( bodyStart );
        }
    }

    protected void processClassBodyStop( JavaTokenDescr bodyStop ) {
        if (isDeclaringMainClass()) {
            peekClass().setBodyStopBrace( bodyStop );
        }
    }

}