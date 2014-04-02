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

public class ModifiersContainerDescr extends ElementDescriptor implements HasModifiers {

    public ModifiersContainerDescr( ElementType elementType ) {
        super( elementType );
    }

    public ModifiersContainerDescr( ElementType elementType, String text, int start, int line, int position ) {
        super( elementType, text, start, line, position );
    }

    public ModifiersContainerDescr( ElementType elementType, String text, int start, int stop ) {
        super( elementType, text, start, stop );
    }

    public ModifiersContainerDescr( ElementType elementType, String text, int start, int stop, int line, int position ) {
        super( elementType, text, start, stop, line, position );
    }

    @Override
    public ModifierListDescr getModifiers( ) {
        return ( ModifierListDescr ) getElements( ).getFirst( ElementType.MODIFIER_LIST );
    }

    @Override
    public void setModifiers( ModifierListDescr modifiers ) {
        getElements( ).add( modifiers );
    }

    @Override
    public void addModifier( ModifierDescr modifier ) {
        ModifierListDescr modifierListDescr = getModifiers( );
        if ( modifierListDescr != null ) {
            modifierListDescr.add( modifier );
        }
    }

    @Override public void addAnnotation( AnnotationDescr annotation ) {
        ModifierListDescr modifierListDescr = getModifiers( );
        if ( modifierListDescr != null ) {
            modifierListDescr.add( annotation );
        }
    }
}