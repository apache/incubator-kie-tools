package org.kie.workbench.widgets.configresource.client.widget.unbound;

import com.google.gwt.user.client.ui.IsWidget;
import org.drools.guvnor.models.commons.shared.imports.Imports;

public interface ImportsWidgetView
        extends IsWidget {

    interface Presenter {

        void onAddImport();

        void onRemoveImport();

        void setContent( final Imports resourceImports,
                         final boolean isReadOnly );

    }

    void addImport( final String type );

    String getSelected();

    void removeImport( final String selected );

    void setReadOnly( final boolean isReadOnly );

    void setPresenter( final Presenter presenter );

    void showPleaseSelectAnImport();
}
