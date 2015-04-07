package org.uberfire.ext.layout.editor.client;

import org.jboss.errai.common.client.api.RemoteCallback;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.plugin.model.LayoutEditorModel;
import org.uberfire.ext.plugin.model.PluginType;
import org.uberfire.mvp.ParameterizedCommand;

public interface LayoutEditorPluginAPI extends LayoutEditorAPI {

    void load( PluginType pluginType,
               Path currentPath,
               ParameterizedCommand<LayoutEditorModel> loadCallBack );

    void save( Path path,
               RemoteCallback<Path> saveSuccessCallback );

    int getCurrentModelHash();

}
