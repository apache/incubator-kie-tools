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

package org.kie.workbench.common.services.datamodeller.parser.descr;

public class PackageDescr extends ElementDescriptor {

    public PackageDescr( ) {
        super( ElementType.PACKAGE );
    }

    public PackageDescr( String text, int start, int line, int position ) {
        this( text, start, -1, line, position );
    }

    public PackageDescr( String text, int start, int stop ) {
        this( text, start, stop, -1, -1 );
    }

    public PackageDescr( String text, int start, int stop, int line, int position ) {
        super( ElementType.PACKAGE, text, start, stop, line, position );
    }

    public QualifiedNameDescr getQualifiedName( ) {
        return ( QualifiedNameDescr ) getElements( ).getFirst( ElementType.QUALIFIED_NAME );
    }

    public void setQualifiedName( QualifiedNameDescr qualifiedName ) {
        getElements( ).removeFirst( ElementType.QUALIFIED_NAME );
        getElements( ).add( qualifiedName );
    }

    public JavaTokenDescr getPackageToken( ) {
        return ( JavaTokenDescr ) getElements( ).getFirst( ElementType.JAVA_PACKAGE );
    }

    public void setPackageToken( JavaTokenDescr packageToken ) {
        getElements( ).removeFirst( ElementType.JAVA_PACKAGE );
        getElements( ).add( packageToken );
    }

    public String getPackageName( ) {
        return getQualifiedName( ) != null ? getQualifiedName( ).getName( ) : null;
    }

    public JavaTokenDescr getEndSemiColon( ) {
        return ( JavaTokenDescr ) getElements( ).getLast( ElementType.JAVA_SEMI_COLON );
    }

    public PackageDescr setEndSemiColon( JavaTokenDescr element ) {
        getElements( ).removeFirst( ElementType.JAVA_SEMI_COLON );
        getElements( ).add( element );
        return this;
    }

}
