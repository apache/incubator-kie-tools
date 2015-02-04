package org.uberfire.ext.plugin.model;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.plugin.editor.PerspectiveEditor;

@Portable
public class PerspectiveEditorModel extends Plugin {

    private PerspectiveEditor perspectiveModel;

    public PerspectiveEditorModel() {
    }

    public PerspectiveEditorModel( final String name,
                                   final PluginType type,
                                   final Path path,
                                   final PerspectiveEditor perspectiveModel ) {
        super( name, type, path );
        this.perspectiveModel = perspectiveModel;
    }

    public PerspectiveEditor getPerspectiveModel() {
        return perspectiveModel;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof PerspectiveEditorModel ) ) {
            return false;
        }
        if ( !super.equals( o ) ) {
            return false;
        }

        PerspectiveEditorModel that = (PerspectiveEditorModel) o;

        if ( perspectiveModel != null ? !perspectiveModel.equals( that.perspectiveModel ) : that.perspectiveModel != null ) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = ~~result;
        if ( perspectiveModel != null ) {
            result = 31 * result + perspectiveModel.hashCode();
        }
        return result;
    }

}
