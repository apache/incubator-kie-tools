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

import java.util.ArrayList;
import java.util.List;

public class ModifierListDescr extends ElementDescriptor {

    public ModifierListDescr( ) {
        super( ElementType.MODIFIER_LIST );
    }

    public ModifierListDescr( String text, int start, int line, int position ) {
        this( text, start, -1, line, position );
    }

    public ModifierListDescr( String text, int start, int stop ) {
        this( text, start, stop, -1, -1 );
    }

    public ModifierListDescr( String text, int start, int stop, int line, int position ) {
        super( ElementType.MODIFIER_LIST, text, start, stop, line, position );
    }

    public void add( ModifierDescr modifierDescr ) {
        getElements( ).add( modifierDescr );
    }

    public void add( AnnotationDescr annotationDescr ) {
        getElements( ).add( annotationDescr );
    }

    public int size() {
        List<ElementDescriptor> modifiers = getAllModifiers();
        return modifiers != null ? modifiers.size() : 0;

    }

    public List<ModifierDescr> getModifiers( ) {
        List<ModifierDescr> modifiers = new ArrayList<ModifierDescr>( );
        for ( ElementDescriptor modifier : getElements( ).getElementsByType( ElementType.MODIFIER ) ) {
            modifiers.add( ( ModifierDescr ) modifier );
        }
        return modifiers;
    }


    public List<AnnotationDescr> getAnnotations( ) {
        List<AnnotationDescr> modifiers = new ArrayList<AnnotationDescr>( );
        for ( ElementDescriptor modifier : getElements( ).getElementsByType( ElementType.ANNOTATION ) ) {
            modifiers.add( ( AnnotationDescr ) modifier );
        }
        return modifiers;
    }


    public List<ElementDescriptor> getAllModifiers( ) {
        List<ElementDescriptor> modifiers = new ArrayList<ElementDescriptor>( );
        for ( ElementDescriptor modifier : getElements( ) ) {
            if ( ElementType.MODIFIER == modifier.getElementType() || ElementType.ANNOTATION == modifier.getElementType()) {
                modifiers.add( modifier );
            }
        }
        return modifiers;
    }
}
