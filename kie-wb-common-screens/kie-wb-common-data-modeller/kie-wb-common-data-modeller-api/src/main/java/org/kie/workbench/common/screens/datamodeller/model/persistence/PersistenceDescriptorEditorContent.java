/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.datamodeller.model.persistence;

import org.guvnor.common.services.shared.metadata.model.Overview;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.backend.vfs.Path;

@Portable
public class PersistenceDescriptorEditorContent {

    private PersistenceDescriptorModel descriptorModel;

    private Overview overview;

    private String source;

    //not include this field in hashCode
    private Path path;

    //not include this field in hashCode
    private boolean created = false;

    public PersistenceDescriptorEditorContent() {
    }

    public PersistenceDescriptorModel getDescriptorModel() {
        return descriptorModel;
    }

    public void setDescriptorModel( PersistenceDescriptorModel descriptorModel ) {
        this.descriptorModel = descriptorModel;
    }

    public Overview getOverview() {
        return overview;
    }

    public void setOverview( Overview overview ) {
        this.overview = overview;
    }

    public String getSource() {
        return source;
    }

    public void setSource( String source ) {
        this.source = source;
    }

    public Path getPath() {
        return path;
    }

    public void setPath( Path path ) {
        this.path = path;
    }

    public boolean isCreated() {
        return created;
    }

    public void setCreated( boolean created ) {
        this.created = created;
    }

    @Override public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }

        PersistenceDescriptorEditorContent content = ( PersistenceDescriptorEditorContent ) o;

        if ( descriptorModel != null ? !descriptorModel.equals( content.descriptorModel ) : content.descriptorModel != null ) {
            return false;
        }
        if ( overview != null ? !overview.equals( content.overview ) : content.overview != null ) {
            return false;
        }
        return !( source != null ? !source.equals( content.source ) : content.source != null );

    }

    @Override public int hashCode() {
        int result = descriptorModel != null ? descriptorModel.hashCode() : 0;
        result = ~~result;
        result = 31 * result + ( overview != null ? overview.hashCode() : 0 );
        result = ~~result;
        result = 31 * result + ( source != null ? source.hashCode() : 0 );
        result = ~~result;
        return result;
    }
}
