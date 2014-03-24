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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class ElementDescrList implements List<ElementDescriptor> {

    private ArrayList<ElementDescriptor> elements = new ArrayList<ElementDescriptor>( );

    public ElementDescrList( ) {
    }

    public void addElementAfter( ElementDescriptor element, ElementDescriptor newElement ) {
        int index = element != null ? elements.indexOf( element ) : -1;
        index = index < 0 ? elements.size( ) : ( index + 1 );
        elements.add( index, newElement );
    }

    public void addMemberBefore( ElementDescriptor element, ElementDescriptor newElement ) {
        int index = element != null ? elements.indexOf( element ) : -1;
        index = index < 0 ? 0 : index;
        elements.add( index, newElement );
    }

    public List<ElementDescriptor> getElementsByType( ElementDescriptor.ElementType type ) {
        List<ElementDescriptor> result = new ArrayList<ElementDescriptor>( );
        for ( ElementDescriptor element : elements ) {
            if ( type == element.getElementType( ) ) {
                result.add( element );
            }
        }
        return result;
    }

    public ElementDescriptor removeFirst( ElementDescriptor.ElementType type ) {
        int index = indexOf( type );
        return ( index >= 0 ) ? elements.remove( index ) : null;
    }

    public ElementDescriptor getFirst( ElementDescriptor.ElementType type ) {
        int index = indexOf( type );
        return ( index >= 0 ) ? elements.get( index ) : null;
    }

    public ElementDescriptor getLast( ElementDescriptor.ElementType type ) {
        int index = lastIndexOf( type );
        return ( index >= 0 ) ? elements.get( index ) : null;
    }

    public int indexOf( ElementDescriptor.ElementType type ) {
        int index = -1;
        for ( ElementDescriptor element : elements ) {
            index++;
            if ( type == element.getElementType( ) ) {
                return index;
            }
        }
        return -1;
    }

    public int lastIndexOf( ElementDescriptor.ElementType type ) {
        int index = -1;
        int i = 0;
        for ( ElementDescriptor element : elements ) {
            if ( type == element.getElementType( ) ) {
                index = i;
            }
            i++;
        }
        return index >= elements.size( ) ? -1 : index;
    }

    @Override
    public int size( ) {
        return elements.size( );
    }

    @Override
    public boolean isEmpty( ) {
        return elements.isEmpty( );
    }

    @Override
    public boolean contains( Object o ) {
        return elements.contains( o );
    }

    @Override
    public Iterator<ElementDescriptor> iterator( ) {
        return elements.iterator( );
    }

    @Override
    public Object[] toArray( ) {
        return elements.toArray( );
    }

    @Override
    public <T> T[] toArray( T[] a ) {
        return elements.toArray( a );
    }

    @Override
    public boolean add( ElementDescriptor element ) {
        return elements.add( element );
    }

    @Override
    public boolean remove( Object o ) {
        return elements.remove( o );
    }

    @Override
    public boolean containsAll( Collection<?> c ) {
        return elements.containsAll( c );
    }

    @Override
    public boolean addAll( Collection<? extends ElementDescriptor> c ) {
        return elements.addAll( c );
    }

    @Override
    public boolean addAll( int index, Collection<? extends ElementDescriptor> c ) {
        return elements.addAll( index, c );
    }

    @Override
    public boolean removeAll( Collection<?> c ) {
        return elements.removeAll( c );
    }

    @Override
    public boolean retainAll( Collection<?> c ) {
        return elements.retainAll( c );
    }

    @Override
    public void clear( ) {
        elements.clear( );
    }

    @Override
    public ElementDescriptor get( int index ) {
        return elements.get( index );
    }

    @Override
    public ElementDescriptor set( int index, ElementDescriptor element ) {
        return elements.set( index, element );
    }

    @Override
    public void add( int index, ElementDescriptor element ) {
        elements.add( index, element );
    }

    @Override
    public ElementDescriptor remove( int index ) {
        return elements.remove( index );
    }

    @Override
    public int indexOf( Object o ) {
        return elements.indexOf( o );
    }

    @Override
    public int lastIndexOf( Object o ) {
        return elements.lastIndexOf( o );
    }

    @Override
    public ListIterator<ElementDescriptor> listIterator( ) {
        return elements.listIterator( );
    }

    @Override
    public ListIterator<ElementDescriptor> listIterator( int index ) {
        return elements.listIterator( index );
    }

    @Override
    public List<ElementDescriptor> subList( int fromIndex, int toIndex ) {
        return subList( fromIndex, toIndex );
    }
}