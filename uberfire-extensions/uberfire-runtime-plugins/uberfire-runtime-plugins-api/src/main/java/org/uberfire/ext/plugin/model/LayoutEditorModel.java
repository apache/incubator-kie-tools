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

package org.uberfire.ext.plugin.model;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.backend.vfs.Path;

@Portable
public class LayoutEditorModel extends Plugin {

    private String layoutEditorModel;
    private boolean emptyLayout;

    public LayoutEditorModel() {
    }

    public LayoutEditorModel( final String name,
                              final PluginType type,
                              final Path path,
                              final String layoutEditorModel) {
        super( name, type, path );
        this.layoutEditorModel = layoutEditorModel;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof LayoutEditorModel ) ) {
            return false;
        }
        if ( !super.equals( o ) ) {
            return false;
        }

        LayoutEditorModel that = (LayoutEditorModel) o;

        if ( layoutEditorModel != null ? !layoutEditorModel.equals( that.layoutEditorModel ) : that.layoutEditorModel != null ) {
            return false;
        }

        return true;
    }


    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = ~~result;
        if ( layoutEditorModel != null ) {
            result = 31 * result + layoutEditorModel.hashCode();
        }
        return result;
    }

    public String getLayoutEditorModel() {
        return layoutEditorModel;
    }

    public LayoutEditorModel emptyLayout() {
        this.emptyLayout=true;
        return this;
    }

    public boolean isEmptyLayout() {
        return emptyLayout;
    }
}
