/*
 * Copyright 2013 JBoss Inc
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

package org.uberfire.metadata.backend.lucene.util;

import org.apache.lucene.document.Document;
import org.uberfire.metadata.model.KObject;
import org.uberfire.metadata.model.KProperty;
import org.uberfire.metadata.model.schema.MetaType;

/**
 *
 */
public final class KObjectUtil {

    public static KObject toKObject( final Document document ) {
        return new KObject() {

            @Override
            public String getId() {
                return document.get( "id" );
            }

            @Override
            public MetaType getType() {
                return new MetaType() {
                    @Override
                    public String getName() {
                        return document.get( "type" );
                    }
                };
            }

            @Override
            public String getClusterId() {
                return document.get( "cluster.id" );
            }

            @Override
            public String getSegmentId() {
                return document.get( "segment.id" );
            }

            @Override
            public String getKey() {
                return document.get( "key" );
            }

            @Override
            public boolean equals( final Object obj ) {
                if ( obj == null ) {
                    return false;
                }
                if ( !( obj instanceof KObject ) ) {
                    return false;
                }
                final KObject kobj = (KObject) obj;
                return getClusterId().equals( kobj.getClusterId() ) &&
                        getId().equals( kobj.getId() ) &&
                        getKey().equals( kobj.getKey() ) &&
                        getType().getName().equals( kobj.getType().getName() );
            }

            @Override
            public int hashCode() {
                int result = getId().hashCode();
                result = 31 * result + getClusterId().hashCode();
                result = 31 * result + getKey().hashCode();
                result = 31 * result + getType().getName().hashCode();
                return result;

            }

            @Override
            public Iterable<KProperty<?>> getProperties() {
//                for ( final IndexableField indexableField : document ) {
//                    if ( !KEY_FIELDS.contains( indexableField.name() ) ) {
//                        indexableField.stringValue();
//                    }
//                }

                return null;
            }
        };
    }
}
