package org.kie.workbench.common.screens.projecteditor.client.forms;

import javax.inject.Inject;

import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextHeader;
import com.google.gwt.user.client.ui.Widget;
import org.guvnor.m2repo.client.widgets.AbstractPagedJarTable;
import org.guvnor.m2repo.model.JarListPageRow;
import org.guvnor.m2repo.service.M2RepoService;
import org.jboss.errai.common.client.api.Caller;
import org.uberfire.client.common.Popup;

public class DependencySelectorPopupViewImpl
        extends Popup
        implements DependencySelectorPopupView {

    private final Caller<M2RepoService> m2RepoService;
    private Presenter presenter;

    @Inject
    public DependencySelectorPopupViewImpl( final Caller<M2RepoService> m2RepoService ) {
        this.m2RepoService = m2RepoService;
    }

    @Override
    public Widget getContent() {
        final AbstractPagedJarTable pagedJarTable = new DependencyPagedJarTable( m2RepoService );

        final Column<JarListPageRow, String> selectColumn = new Column<JarListPageRow, String>( new ButtonCell() ) {
            public String getValue( JarListPageRow row ) {
                return "Select";
            }
        };
        selectColumn.setFieldUpdater( new FieldUpdater<JarListPageRow, String>() {
            public void update( final int index,
                                final JarListPageRow row,
                                final String value ) {
                presenter.onPathSelection( row.getPath() );
            }
        } );

        pagedJarTable.addColumn( selectColumn,
                                 new TextHeader( "" ) );

        return pagedJarTable;
    }

    @Override
    public void setPresenter( Presenter presenter ) {
        this.presenter = presenter;
    }

}
