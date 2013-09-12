package org.kie.workbench.common.widgets.configresource.client.widget.bound;

import java.util.List;

import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.models.commons.shared.imports.Import;
import org.drools.workbench.models.commons.shared.imports.Imports;
import org.drools.workbench.models.commons.shared.oracle.PackageDataModelOracle;
import org.kie.workbench.common.widgets.client.widget.HasBusyIndicator;
import org.uberfire.client.mvp.UberView;

public interface ImportsWidgetView
        extends HasBusyIndicator,
                UberView<ImportsWidgetView.Presenter> {

    interface Presenter {

        void setContent( final PackageDataModelOracle oracle,
                         final Imports resourceImports,
                         final boolean isReadOnly );

        void onAddImport( final Import importType );

        void onRemoveImport( final Import importType );

        Widget asWidget();

        boolean isDirty();

        void setNotDirty();

    }

    void setContent( final List<Import> allAvailableImportTypes,
                     final List<Import> importTypes,
                     final boolean isReadOnly );

    boolean isDirty();

    void setNotDirty();

}
