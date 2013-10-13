/*
 * Copyright 2012 JBoss Inc
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

package org.uberfire.metadata.backend.lucene;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexableField;
import org.uberfire.metadata.engine.MetaIndexEngine;
import org.uberfire.metadata.engine.MetaModelStore;
import org.uberfire.metadata.model.KObject;
import org.uberfire.metadata.model.KObjectKey;
import org.uberfire.metadata.model.KProperty;
import org.uberfire.metadata.model.schema.MetaObject;
import org.uberfire.metadata.model.schema.MetaProperty;
import org.uberfire.metadata.model.schema.MetaType;

import static org.uberfire.commons.validation.Preconditions.*;

public class LuceneIndexEngine implements MetaIndexEngine {

    private final LuceneSetup lucene;
    private final FieldFactory fieldFactory;
    private final MetaModelStore metaModelStore;
    private int batchMode = 0;

    public LuceneIndexEngine( final MetaModelStore metaModelStore,
                              final LuceneSetup lucene,
                              final FieldFactory fieldFactory ) {
        this.metaModelStore = checkNotNull( "metaModelStore", metaModelStore );
        this.lucene = checkNotNull( "lucene", lucene );
        this.fieldFactory = checkNotNull( "fieldFactory", fieldFactory );
    }

    @Override
    public boolean freshIndex() {
        return lucene.freshIndex();
    }

    @Override
    public synchronized void startBatchMode() {
        if ( batchMode < 0 ) {
            batchMode = 1;
        } else {
            batchMode++;
        }
    }

    @Override
    public void index( final KObject object ) {
        updateMetaModel( object );

        lucene.indexDocument( object.getId(), newDocument( object ) );

        commitIfNotBatchMode();
    }

    private Document newDocument( final KObject object ) {
        final Document doc = new Document();

        doc.add( new StringField( "id", object.getId(), Field.Store.YES ) );
        doc.add( new StringField( "type", object.getType().getName(), Field.Store.YES ) );
        doc.add( new TextField( "key", object.getKey(), Field.Store.YES ) );
        doc.add( new StringField( "cluster.id", object.getClusterId(), Field.Store.YES ) );
        doc.add( new StringField( "segment.id", object.getSegmentId(), Field.Store.YES ) );

        final StringBuilder allText = new StringBuilder( object.getKey() ).append( '\n' );

        for ( final KProperty<?> property : object.getProperties() ) {
            final IndexableField[] fields = fieldFactory.build( property );
            for ( final IndexableField field : fields ) {
                doc.add( field );
                if ( field instanceof TextField && !( property.getValue() instanceof Boolean ) ) {
                    allText.append( field.stringValue() ).append( '\n' );
                }
            }
        }

        doc.add( new TextField( FULL_TEXT_FIELD, allText.toString().toLowerCase(), Field.Store.NO ) );

        return doc;
    }

    @Override
    public void index( final KObject... objects ) {
        for ( final KObject object : objects ) {
            index( object );
        }
    }

    @Override
    public void rename( final KObjectKey from,
                        final KObjectKey to ) {
        lucene.rename( from.getId(), to.getId() );

        commitIfNotBatchMode();
    }

    @Override
    public void delete( final KObjectKey objectKey ) {
        lucene.deleteIfExists( objectKey.getId() );
    }

    @Override
    public void delete( final KObjectKey... objectsKey ) {
        final String[] ids = new String[ objectsKey.length ];
        for ( int i = 0; i < ids.length; i++ ) {
            ids[ i ] = objectsKey[ i ].getId();
        }
        lucene.deleteIfExists( ids );
    }

    private synchronized void commitIfNotBatchMode() {
        if ( batchMode <= 0 ) {
            commit();
        }
    }

    @Override
    public synchronized void commit() {
        batchMode--;
        if ( batchMode <= 0 ) {
            lucene.commit();
        }
    }

    @Override
    public void dispose() {
        metaModelStore.dispose();
        lucene.dispose();
    }

    private void updateMetaModel( final KObject object ) {
        final MetaObject metaObject = metaModelStore.getMetaObject( object.getType().getName() );
        if ( metaObject == null ) {
            metaModelStore.add( newMetaObect( object ) );
        } else {
            for ( final KProperty property : object.getProperties() ) {
                final MetaProperty metaProperty = metaObject.getProperty( property.getName() );
                if ( metaProperty == null ) {
                    metaObject.addProperty( newMetaProperty( property ) );
                } else {
                    metaProperty.addType( property.getValue().getClass() );
                    if ( property.isSearchable() ) {
                        metaProperty.setAsSearchable();
                    }
                }
            }
            metaModelStore.update( metaObject );
        }
    }

    private MetaObject newMetaObect( final KObject object ) {
        final Set<MetaProperty> properties = new HashSet<MetaProperty>();
        for ( final KProperty<?> property : object.getProperties() ) {
            properties.add( newMetaProperty( property ) );
        }

        return new MetaObject() {

            private final Map<String, MetaProperty> propertyMap = new ConcurrentHashMap<String, MetaProperty>() {{
                for ( final MetaProperty property : properties ) {
                    put( property.getName(), property );
                }
            }};

            @Override
            public MetaType getType() {
                return object.getType();
            }

            @Override
            public Collection<MetaProperty> getProperties() {
                return propertyMap.values();
            }

            @Override
            public MetaProperty getProperty( final String name ) {
                return propertyMap.get( name );
            }

            @Override
            public void addProperty( final MetaProperty metaProperty ) {
                if ( !propertyMap.containsKey( metaProperty.getName() ) ) {
                    propertyMap.put( metaProperty.getName(), metaProperty );
                }
            }
        };
    }

    private MetaProperty newMetaProperty( final KProperty<?> property ) {
        return new MetaProperty() {

            private boolean isSearchable = property.isSearchable();
            private Set<Class<?>> types = new CopyOnWriteArraySet<Class<?>>() {{
                add( property.getValue().getClass() );
            }};

            @Override
            public String getName() {
                return property.getName();
            }

            @Override
            public Set<Class<?>> getTypes() {
                return types;
            }

            @Override
            public boolean isSearchable() {
                return isSearchable;
            }

            @Override
            public void setAsSearchable() {
                this.isSearchable = true;
            }

            @Override
            public void addType( final Class<?> type ) {
                types.add( type );
            }

            @Override
            public boolean equals( final Object obj ) {
                if ( obj == null ) {
                    return false;
                }
                if ( !( obj instanceof MetaProperty ) ) {
                    return false;
                }
                return ( (MetaProperty) obj ).getName().equals( getName() );
            }

            @Override
            public int hashCode() {
                return getName().hashCode();
            }
        };
    }
}
