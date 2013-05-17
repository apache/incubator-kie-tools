package org.kie.workbench.common.widgets.configresource.client.widget.bound;

import com.google.gwt.user.client.ui.IsWidget;
import org.drools.workbench.models.commons.shared.imports.Imports;
import org.kie.workbench.common.services.datamodel.oracle.PackageDataModelOracle;

public interface ImportsWidgetView
        extends IsWidget {

    interface Presenter {

        void onAddImport();

        void onRemoveImport();

        void setContent( final PackageDataModelOracle oracle,
                         final Imports resourceImports,
                         final boolean isReadOnly );

    }

    void addImport( final String type );

    String getSelected();

    void removeImport( final String selected );

    void setReadOnly( final boolean isReadOnly );

    void setPresenter( final Presenter presenter );

    void showPleaseSelectAnImport();
}
