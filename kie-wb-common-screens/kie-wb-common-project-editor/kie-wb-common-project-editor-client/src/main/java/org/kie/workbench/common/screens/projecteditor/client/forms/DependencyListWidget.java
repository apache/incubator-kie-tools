package org.kie.workbench.common.screens.projecteditor.client.forms;

import javax.enterprise.context.Dependent;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ButtonCell;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.resources.ButtonSize;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import org.guvnor.m2repo.client.widgets.ArtifactListPresenter;
import org.guvnor.m2repo.model.JarListPageRow;
import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.jboss.errai.ioc.client.container.IOC;
import org.uberfire.mvp.ParameterizedCommand;

@Dependent
public class DependencyListWidget
        extends Composite {

    interface Binder
            extends
            UiBinder<Widget, DependencyListWidget> {

    }

    private static Binder uiBinder = GWT.create( Binder.class );

    @UiField
    FlowPanel panel;

    @UiField
    TextBox filter;

    @UiField
    Button search;

    private ArtifactListPresenter dependencyPagedJarTable;
    private ParameterizedCommand<String> onPathSelect;

    public DependencyListWidget() {
        initWidget( uiBinder.createAndBindUi( this ) );
        search.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                dependencyPagedJarTable.search( filter.getText() );
            }
        } );
    }

    @AfterInitialization
    public void init() {
        dependencyPagedJarTable = IOC.getBeanManager().lookupBean( ArtifactListPresenter.class ).getInstance();

        final Column<JarListPageRow, String> selectColumn = new Column<JarListPageRow, String>( new ButtonCell() {{
            setSize( ButtonSize.MINI );
        }} ) {
            public String getValue( JarListPageRow row ) {
                return "Select";
            }
        };
        selectColumn.setFieldUpdater( new FieldUpdater<JarListPageRow, String>() {
            public void update( final int index,
                                final JarListPageRow row,
                                final String value ) {
                onPathSelect.execute( row.getPath() );
            }
        } );
        dependencyPagedJarTable.getView().addColumn( selectColumn, null, "Select" );

        dependencyPagedJarTable.getView().setContentHeight( "300px" );

        panel.add( dependencyPagedJarTable.getView() );
    }

    public void addOnSelect( final ParameterizedCommand<String> onPathSelect ) {
        this.onPathSelect = onPathSelect;
    }

    public void refresh() {
        dependencyPagedJarTable.refresh();
    }

}
