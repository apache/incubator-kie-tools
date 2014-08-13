package org.kie.workbench.common.widgets.configresource.client.widget.bound;

import java.util.List;

import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.models.datamodel.imports.Import;
import org.drools.workbench.models.datamodel.imports.Imports;
import org.kie.uberfire.client.common.HasBusyIndicator;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.metadata.client.KieEditorView;
import org.uberfire.client.mvp.UberView;

public interface ImportsWidgetView
        extends KieEditorView,
                UberView<ImportsWidgetView.Presenter> {

    interface Presenter {

        void setContent( final AsyncPackageDataModelOracle dmo,
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
