/*
 * Copyright 2016 JBoss Inc
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
package org.kie.workbench.common.services.shared.whitelist;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class WhiteList
        implements Set<String> {

    private Set<String> whiteList = new HashSet<String>();

    public WhiteList() {

    }

    public WhiteList( final Collection<String> packageNames ) {
        whiteList.addAll( packageNames );
    }

    @Override
    public int size() {
        return whiteList.size();
    }

    @Override
    public boolean isEmpty() {
        return whiteList.isEmpty();
    }

    @Override
    public boolean contains( final Object o ) {
        return whiteList.contains( o );
    }

    @Override
    public Iterator<String> iterator() {
        return whiteList.iterator();
    }

    @Override
    public Object[] toArray() {
        return whiteList.toArray();
    }

    @Override
    public <T> T[] toArray( final T[] ts ) {
        return whiteList.toArray( ts );
    }

    @Override
    public boolean add( final String s ) {
        return whiteList.add( s );
    }

    @Override
    public boolean remove( final Object o ) {
        return whiteList.remove( o );
    }

    @Override
    public boolean containsAll( final Collection<?> collection ) {
        return whiteList.containsAll( collection );
    }

    @Override
    public boolean addAll( final Collection<? extends String> collection ) {
        return whiteList.addAll( collection );
    }

    @Override
    public boolean retainAll( final Collection<?> collection ) {
        return whiteList.retainAll( collection );
    }

    @Override
    public boolean removeAll( final Collection<?> collection ) {
        return whiteList.removeAll( collection );
    }

    @Override
    public void clear() {
        whiteList.clear();
    }

    public boolean containsAny( final Collection<String> packages ) {
        for ( String aPackage : packages ) {
            if ( contains( aPackage ) ) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return whiteList.hashCode();
    }
}
