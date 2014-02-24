package org.kie.workbench.common.screens.defaulteditor.client.editor;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.kie.workbench.common.screens.defaulteditor.client.editor.resources.i18n.GuvnorDefaultEditorConstants;
import org.kie.workbench.common.widgets.client.handlers.DefaultNewResourceHandler;
import org.kie.workbench.common.widgets.client.handlers.NewResourcePresenter;
import org.uberfire.client.editors.defaulteditor.DefaultEditorNewFileUpload;
import org.uberfire.commons.data.Pair;
import org.uberfire.workbench.type.AnyResourceTypeDefinition;
import org.uberfire.workbench.type.ResourceTypeDefinition;

@ApplicationScoped
public class NewFileUploader
        extends DefaultNewResourceHandler {

    @Inject
    private DefaultEditorNewFileUpload options;

    @Inject
    private AnyResourceTypeDefinition resourceType;

    @PostConstruct
    private void setupExtensions() {
        extensions.add( new Pair<String, DefaultEditorNewFileUpload>( GuvnorDefaultEditorConstants.INSTANCE.Options(),
                                                                      options ) );
    }

    @Override
    public String getDescription() {
        return GuvnorDefaultEditorConstants.INSTANCE.NewFileDescription();
    }

    @Override
    public IsWidget getIcon() {
        return null;
    }

    @Override
    public ResourceTypeDefinition getResourceType() {
        return resourceType;
    }

    @Override
    public void create( org.guvnor.common.services.project.model.Package pkg,
                        String baseFileName,
                        NewResourcePresenter presenter ) {

        options.setFolderPath( pkg.getPackageMainResourcesPath() );
        options.setFileName( baseFileName );

        options.upload();

        presenter.complete();
    }
}
