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

package org.uberfire.java.nio.base;

import java.util.HashMap;
import java.util.Map;

import org.uberfire.java.nio.file.attribute.AttributeView;
import org.uberfire.java.nio.file.attribute.BasicFileAttributeView;

import static java.util.Collections.*;

public class AttrsStorageImpl implements AttrsStorage {

    final Properties                   content        = new Properties();
    final Map<String, AttributeView>   viewsNameIndex = new HashMap<String, AttributeView>();
    final Map<Class<?>, AttributeView> viewsTypeIndex = new HashMap<Class<?>, AttributeView>();

    @Override
    public AttrsStorage getAttrStorage() {
        return this;
    }

    @Override
    public <V extends AttributeView> void addAttrView( final V view ) {
        viewsNameIndex.put( view.name(), view );
        if ( view instanceof ExtendedAttributeView ) {
            final ExtendedAttributeView extendedView = (ExtendedAttributeView) view;
            for ( Class<? extends BasicFileAttributeView> type : extendedView.viewTypes() ) {
                viewsTypeIndex.put( type, view );
            }
        } else {
            viewsTypeIndex.put( view.getClass(), view );
        }
    }

    @Override
    public <V extends AttributeView> V getAttrView( final Class<V> type ) {
        return (V) viewsTypeIndex.get( type );
    }

    @Override
    public <V extends AttributeView> V getAttrView( final String name ) {
        return (V) viewsNameIndex.get( name );
    }

    @Override
    public void clear() {
        viewsNameIndex.clear();
        viewsTypeIndex.clear();
        content.clear();
    }

    @Override
    public Properties toProperties() {
        return buildProperties( false );
    }

    @Override
    public void loadContent( final Properties properties ) {
        content.clear();
        for ( final Map.Entry<String, Object> attr : properties.entrySet() ) {
            content.put( attr.getKey(), attr.getValue() );
        }
    }

    @Override
    public Map<String, Object> getContent() {
        return unmodifiableMap( buildProperties( false ) );
    }

    @Override
    public Map<String, Object> getAllContent() {
        return unmodifiableMap( buildProperties( true ) );
    }

    private synchronized Properties buildProperties( boolean includesNonSerializable ) {
        final Properties properties = new Properties( content );

        for ( final Map.Entry<String, AttributeView> view : viewsNameIndex.entrySet() ) {
            if ( includesNonSerializable ||
                    view.getValue() instanceof ExtendedAttributeView && ( (ExtendedAttributeView) view.getValue() ).isSerializable() ) {
                final ExtendedAttributeView extendedView = (ExtendedAttributeView) view.getValue();
                for ( final Map.Entry<String, Object> attr : extendedView.readAllAttributes().entrySet() ) {
                    properties.put( attr.getKey(), attr.getValue() );
                }
            }
        }

        return properties;
    }
}

