package org.uberfire.ext.plugin.client.editor;

import java.util.Collection;
import java.util.Map;

import javax.inject.Inject;

import org.uberfire.ext.editor.commons.client.BaseEditorViewImpl;
import org.uberfire.ext.plugin.client.widget.plugin.GeneralPluginEditor;
import org.uberfire.ext.plugin.model.*;
import org.uberfire.mvp.ParameterizedCommand;

public abstract class RuntimePluginBaseView extends BaseEditorViewImpl {

    @Inject
    protected GeneralPluginEditor editor;

    public void setupContent( final PluginContent response,
                              final ParameterizedCommand<Media> parameterizedCommand ) {
        editor.setupContent( response, parameterizedCommand );
    }

    public PluginSimpleContent getContent() {
        return editor.getContent();
    }

    public String getTemplate() {
        return editor.getTemplate();
    }

    public String getCss() {
        return editor.getCss();
    }

    public Map<CodeType, String> getCodeMap() {
        return editor.getCodeMap();
    }

    protected abstract void setFramework( Collection<Framework> frameworks );

    protected abstract Collection<Framework> getFrameworks();
}
