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

package org.uberfire.ext.metadata.backend.lucene.index;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexableField;
import org.uberfire.commons.lifecycle.PriorityDisposableRegistry;
import org.uberfire.ext.metadata.backend.lucene.fields.FieldFactory;
import org.uberfire.ext.metadata.engine.Index;
import org.uberfire.ext.metadata.engine.MetaIndexEngine;
import org.uberfire.ext.metadata.engine.MetaModelStore;
import org.uberfire.ext.metadata.model.KCluster;
import org.uberfire.ext.metadata.model.KObject;
import org.uberfire.ext.metadata.model.KObjectKey;
import org.uberfire.ext.metadata.model.KProperty;
import org.uberfire.ext.metadata.model.schema.MetaObject;
import org.uberfire.ext.metadata.model.schema.MetaProperty;
import org.uberfire.ext.metadata.model.schema.MetaType;

import static org.uberfire.commons.validation.Preconditions.*;

public class LuceneIndexEngine implements MetaIndexEngine {

    private final FieldFactory fieldFactory;
    private final MetaModelStore metaModelStore;
    private final LuceneIndexManager indexManager;
    private final Map<KCluster, AtomicInteger> batchMode = new ConcurrentHashMap<KCluster, AtomicInteger>();
    private final Collection<Runnable> beforeDispose = new ArrayList<Runnable>();

    public LuceneIndexEngine( final FieldFactory fieldFactory,
                              final MetaModelStore metaModelStore,
                              final LuceneIndexManager indexManager ) {
        this.fieldFactory = checkNotNull( "fieldFactory", fieldFactory );
        this.metaModelStore = checkNotNull( "metaModelStore", metaModelStore );
        this.indexManager = checkNotNull( "indexManager", indexManager );
        PriorityDisposableRegistry.register( this );
    }

    @Override
    public boolean freshIndex( final KCluster cluster ) {
        final Index index = indexManager.get( cluster );
        return (index == null || index.freshIndex()) && !batchMode.containsKey( cluster );
    }

    @Override
    public void startBatch( final KCluster cluster ) {
        final AtomicInteger batchStack = batchMode.get( cluster );
        if ( batchStack == null ) {
            batchMode.put( cluster, new AtomicInteger() );
        } else {
            if ( batchStack.get() < 0 ) {
                batchStack.set( 1 );
            } else {
                batchStack.incrementAndGet();
            }
        }
    }

    @Override
    public void index( final KObject object ) {
        updateMetaModel( object );

        final LuceneIndex index = indexManager.indexOf( object );
        index.indexDocument( object.getId(),
                             newDocument( object ) );

        commitIfNotBatchMode( index.getCluster() );
    }

    private Document newDocument( final KObject object ) {
        final Document doc = new Document();

        doc.add( new StringField( "id",
                                  object.getId(),
                                  Field.Store.YES ) );
        doc.add( new StringField( "type",
                                  object.getType().getName(),
                                  Field.Store.YES ) );
        doc.add( new TextField( "key",
                                object.getKey(),
                                Field.Store.YES ) );
        doc.add( new StringField( "cluster.id",
                                  object.getClusterId(),
                                  Field.Store.YES ) );
        doc.add( new StringField( "segment.id",
                                  object.getSegmentId(),
                                  Field.Store.YES ) );

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

        //Only create a "full text" entry if required
        if ( object.fullText() ) {
            doc.add( new TextField( FULL_TEXT_FIELD,
                                    allText.toString().toLowerCase(),
                                    Field.Store.NO ) );
        }

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
                        final KObject to ) {
        checkNotNull( "from",
                      from );
        checkNotNull( "to",
                      to );
        checkCondition( "renames are allowed only from same cluster",
                        from.getClusterId().equals( to.getClusterId() ) );
        final LuceneIndex index = indexManager.indexOf( from );
        index.rename( from.getId(),
                      newDocument( to ) );

        commitIfNotBatchMode( index.getCluster() );
    }

    @Override
    public void delete( KCluster cluster ) {
        indexManager.delete( cluster );
    }

    @Override
    public void delete( final KObjectKey objectKey ) {
        final LuceneIndex index = indexManager.indexOf( objectKey );
        index.deleteIfExists( objectKey.getId() );
        commitIfNotBatchMode( index.getCluster() );
    }

    @Override
    public void delete( final KObjectKey... objectsKey ) {
        final Map<LuceneIndex, List<String>> execution = new HashMap<LuceneIndex, List<String>>();
        for ( final KObjectKey key : objectsKey ) {
            final LuceneIndex index = indexManager.indexOf( key );

            final List<String> ids = execution.get( index );
            if ( ids == null ) {
                execution.put( index, new ArrayList<String>() {{
                    add( key.getId() );
                }} );
            } else {
                ids.add( key.getId() );
            }
        }

        for ( final Map.Entry<LuceneIndex, List<String>> entry : execution.entrySet() ) {
            entry.getKey().deleteIfExists( entry.getValue().toArray( new String[ entry.getValue().size() ] ) );
        }
    }

    @Override
    public void commit( final KCluster cluster ) {
        final Index index = indexManager.get( cluster );
        if ( index == null ) {
            return;
        }
        final AtomicInteger batchStack = batchMode.get( cluster );
        if ( batchStack != null ) {
            int value = batchStack.decrementAndGet();
            if ( value <= 0 ) {
                index.commit();
                batchMode.remove( cluster );
            }
        } else {
            index.commit();
        }
    }

    private synchronized void commitIfNotBatchMode( final KCluster cluster ) {
        final AtomicInteger batchStack = batchMode.get( cluster );
        if ( batchStack == null || batchStack.get() <= 0 ) {
            commit( cluster );
        }
    }

    @Override
    public void dispose() {
        if ( !beforeDispose.isEmpty() ) {
            for ( final Runnable activeDispose : beforeDispose ) {
                activeDispose.run();
            }
        }
    }

    @Override
    public int priority() {
        return 50;
    }

    @Override
    public void beforeDispose( final Runnable callback ) {
        this.beforeDispose.add( checkNotNull( "callback", callback ) );
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
