package org.kie.workbench.common.widgets.configresource.client.widget.unbound;

import com.google.gwt.user.client.ui.IsWidget;
import org.kie.workbench.common.widgets.client.widget.HasBusyIndicator;

public interface ImportsWidgetView
        extends IsWidget, HasBusyIndicator {

    interface Presenter {

        void onAddImport();

        void onRemoveImport();

    }

    void addImport(final String type);

    String getSelected();

    void removeImport(final String selected);

    void setReadOnly(final boolean isReadOnly);

    void setPresenter(final Presenter presenter);

    void showPleaseSelectAnImport();

    void showBusyIndicator(String text);

    void hideBusyIndicator();
}
