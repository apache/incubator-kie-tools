/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.util.IsConflicting;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.util.IsOverlapping;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.util.IsRedundant;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.util.IsSubsuming;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.util.RelationResolver;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.keys.Key;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.keys.UUIDKey;

public class InspectorList<InspectorType extends HasUUID>
        extends ArrayList<InspectorType>
        implements IsOverlapping,
                   IsSubsuming<InspectorList>,
                   IsRedundant<InspectorList>,
                   IsConflicting<InspectorList>,
                   HasKeys {



    private final UUIDKey uuidKey = new UUIDKey( this );

    private final RelationResolver relationResolver;

    public InspectorList() {
        this( false );
    }

    public InspectorList( final boolean record ) {
        this.relationResolver = new RelationResolver( this,
                                                      record );
    }

    @Override
    public boolean overlaps( final Object other ) {
        return false;
    }

    @Override
    public UUIDKey getUuidKey() {
        return uuidKey;
    }

    @Override
    public Key[] keys() {
        return new Key[]{
                uuidKey
        };
    }


    @Override
    public boolean conflicts( final InspectorList other ) {
        return relationResolver.isConflicting( other );
    }

    @Override
    public boolean isRedundant( final InspectorList other ) {
        return relationResolver.isRedundant( other );
    }

    @Override
    public boolean subsumes( final InspectorList other ) {
        return relationResolver.subsumes( other );
    }

    @Override
    public boolean add( final InspectorType inspector ) {
        return super.add( inspector );
    }
}
