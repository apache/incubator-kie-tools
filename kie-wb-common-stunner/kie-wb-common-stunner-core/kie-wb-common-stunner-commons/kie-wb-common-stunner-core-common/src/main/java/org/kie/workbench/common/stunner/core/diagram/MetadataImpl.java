/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.diagram;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.NonPortable;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.ShapeManager;
import org.kie.workbench.common.stunner.core.client.ShapeSet;

import java.util.Collection;

@Portable
public final class MetadataImpl extends AbstractMetadata {

    public MetadataImpl() {
    }

    private MetadataImpl( @MapsTo( "definitionSetId" ) String definitionSetId ) {
        super( definitionSetId );
    }

    @NonPortable
    public static class MetadataImplBuilder {

        private final String defSetId;
        private final DefinitionManager definitionManager;
        private final ShapeManager shapeManager;
        private String title;

        public MetadataImplBuilder( final String defSetId ) {
            this( defSetId, null );
        }

        public MetadataImplBuilder( final String defSetId,
                                    final DefinitionManager definitionManager ) {
            this( defSetId, definitionManager, null );
        }

        public MetadataImplBuilder( final String defSetId,
                                    final DefinitionManager definitionManager,
                                    final ShapeManager shapeManager ) {
            this.defSetId = defSetId;
            this.definitionManager = definitionManager;
            this.shapeManager = shapeManager;
        }

        public MetadataImplBuilder setTitle( final String t ) {
            this.title = t;
            return this;
        }

        public MetadataImpl build() {

            final MetadataImpl result = new MetadataImpl( defSetId );
            if ( null != definitionManager ) {
                final Object defSet = definitionManager.definitionSets().getDefinitionSetById( defSetId );
                if ( null != defSet ) {
                    result.setTitle( null != title ? title :
                            definitionManager.adapters().forDefinitionSet().getDescription( defSet ) );
                    final ShapeSet<?> shapeSet = getShapeSet();
                    if ( null != shapeSet ) {
                        result.setShapeSetId( shapeSet.getId() );
                    }
                }
            }

            return result;
        }

        private ShapeSet<?> getShapeSet() {
            if ( null != shapeManager ) {
                final Collection<ShapeSet<?>> sets = shapeManager.getShapeSets();
                if ( null != sets && !sets.isEmpty() ) {
                    for ( final ShapeSet<?> set : sets ) {
                        if ( set.getDefinitionSetId().equals( defSetId ) ) {
                            return set;
                        }
                    }
                }
            }

            return null;
        }

    }
}
