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

package org.drools.workbench.screens.guided.dtable.model;

import java.util.HashSet;
import java.util.Set;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.backend.vfs.Path;
import org.uberfire.commons.validation.PortablePreconditions;

@Portable
public class GuidedDecisionTableEditorGraphModel {

    private Set<GuidedDecisionTableGraphEntry> entries = new HashSet<>();

    public Set<GuidedDecisionTableGraphEntry> getEntries() {
        return entries;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }

        GuidedDecisionTableEditorGraphModel that = (GuidedDecisionTableEditorGraphModel) o;

        return entries.equals( that.entries );
    }

    @Override
    public int hashCode() {
        return entries.hashCode();
    }

    @Portable
    public static class GuidedDecisionTableGraphEntry {

        private Path pathHead;
        private Path pathVersion;
        private Double x;
        private Double y;

        public GuidedDecisionTableGraphEntry( final Path pathHead,
                                              final Path pathVersion ) {
            this( pathHead,
                  pathVersion,
                  null,
                  null );
        }

        public GuidedDecisionTableGraphEntry( final @MapsTo("pathHead") Path pathHead,
                                              final @MapsTo("pathVersion") Path pathVersion,
                                              final @MapsTo("x") Double x,
                                              final @MapsTo("y") Double y ) {
            this.pathHead = PortablePreconditions.checkNotNull( "pathHead",
                                                                pathHead );
            this.pathVersion = PortablePreconditions.checkNotNull( "pathVersion",
                                                                   pathVersion );
            this.x = x;
            this.y = y;
        }

        public Path getPathHead() {
            return pathHead;
        }

        public void setPathHead( final Path pathHead ) {
            this.pathHead = pathHead;
        }

        public Path getPathVersion() {
            return pathVersion;
        }

        public void setPathVersion( final Path pathVersion ) {
            this.pathVersion = pathVersion;
        }

        public Double getX() {
            return x;
        }

        public Double getY() {
            return y;
        }

        @Override
        public boolean equals( Object o ) {
            if ( this == o ) {
                return true;
            }
            if ( o == null || getClass() != o.getClass() ) {
                return false;
            }

            GuidedDecisionTableGraphEntry that = (GuidedDecisionTableGraphEntry) o;

            if ( !pathHead.equals( that.pathHead ) ) {
                return false;
            }
            if ( !pathVersion.equals( that.pathVersion ) ) {
                return false;
            }
            if ( x != null ? !x.equals( that.x ) : that.x != null ) {
                return false;
            }
            return !( y != null ? !y.equals( that.y ) : that.y != null );

        }

        @Override
        public int hashCode() {
            int result = pathHead.hashCode();
            result = ~~result;
            result = 31 * result + pathVersion.hashCode();
            result = ~~result;
            result = 31 * result + ( x != null ? x.hashCode() : 0 );
            result = ~~result;
            result = 31 * result + ( y != null ? y.hashCode() : 0 );
            result = ~~result;
            return result;
        }
    }

}
