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
