package org.kie.workbench.projectimportsscreen.client.forms;

import com.google.gwt.user.client.ui.IsWidget;
import org.kie.workbench.widgets.common.client.widget.HasBusyIndicator;
import org.kie.workbench.widgets.configresource.client.widget.unbound.ImportsWidgetPresenter;
import org.kie.guvnor.services.metadata.model.Metadata;

public interface ProjectImportsScreenView extends HasBusyIndicator,
        IsWidget {

    interface Presenter {

        void onShowMetadata();

    }

    void setPresenter(final Presenter presenter);

    void setMetadata(final Metadata metadata);

    Metadata getMetadata();

    void setImports(ImportsWidgetPresenter importsWidgetPresenter);

    boolean confirmClose();

    void alertReadOnly();

}
