package org.kie.workbench.common.screens.projectimportsscreen.client.forms;

import javax.inject.Inject;

import com.google.gwt.user.client.Window;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.kie.workbench.common.widgets.configresource.client.resources.i18n.ImportConstants;
import org.kie.workbench.common.widgets.configresource.client.widget.unbound.ImportsWidgetPresenter;
import org.kie.workbench.common.widgets.metadata.client.resources.i18n.MetadataConstants;
import org.kie.workbench.common.widgets.metadata.client.widget.MetadataWidget;
import org.kie.uberfire.client.common.BusyPopup;
import org.kie.uberfire.client.common.MultiPageEditorView;
import org.kie.uberfire.client.common.Page;

public class ProjectImportsScreenViewImpl
        extends MultiPageEditorView
        implements ProjectImportsScreenView {

    private MetadataWidget metadataWidget;

    private Presenter presenter;

    @Inject
    public ProjectImportsScreenViewImpl( final MetadataWidget metadataWidget ) {
        super();
        this.metadataWidget = metadataWidget;

    }

    @Override
    public void setPresenter( final Presenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void setImports( ImportsWidgetPresenter importsWidgetPresenter ) {
        addPage( new Page( importsWidgetPresenter,
                           ImportConstants.INSTANCE.Imports() ) {
            @Override
            public void onFocus() {
            }

            @Override
            public void onLostFocus() {
            }
        } );

        addPage(new Page(metadataWidget,
                MetadataConstants.INSTANCE.Metadata()) {
            @Override
            public void onFocus() {
                presenter.onShowMetadata();
            }

            @Override
            public void onLostFocus() {
            }
        });
    }

    @Override
    public void setMetadata( final Metadata metadata ) {
        metadataWidget.setContent( metadata,
                                   false );
    }

    @Override
    public boolean confirmClose() {
        return Window.confirm( CommonConstants.INSTANCE.DiscardUnsavedData() );
    }

    @Override
    public void alertReadOnly() {
        Window.alert( CommonConstants.INSTANCE.CantSaveReadOnly() );
    }

    @Override
    public Metadata getMetadata() {
        return metadataWidget.getContent();
    }

    @Override
    public void showBusyIndicator( final String message ) {
        BusyPopup.showMessage( message );
    }

    @Override
    public void hideBusyIndicator() {
        BusyPopup.close();
    }

}