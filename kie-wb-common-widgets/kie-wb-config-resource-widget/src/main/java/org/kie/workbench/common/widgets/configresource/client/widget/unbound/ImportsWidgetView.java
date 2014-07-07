package org.kie.workbench.common.widgets.configresource.client.widget.unbound;

import java.util.List;

import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.models.datamodel.imports.Import;
import org.guvnor.common.services.project.model.ProjectImports;
import org.kie.uberfire.client.common.HasBusyIndicator;
import org.uberfire.client.mvp.UberView;

public interface ImportsWidgetView
        extends HasBusyIndicator,
                UberView<ImportsWidgetView.Presenter> {

    interface Presenter {

        void setContent( final ProjectImports importTypes,
                         final boolean isReadOnly );

        Widget asWidget();

        boolean isDirty();

        void setNotDirty();

    }

    void setContent( final List<Import> importTypes,
                     final boolean isReadOnly );

    boolean isDirty();

    void setNotDirty();

}
