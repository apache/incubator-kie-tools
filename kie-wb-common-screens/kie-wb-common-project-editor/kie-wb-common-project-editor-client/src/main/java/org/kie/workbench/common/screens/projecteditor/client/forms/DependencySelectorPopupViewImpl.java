package org.kie.workbench.common.screens.projecteditor.client.forms;

import javax.enterprise.context.Dependent;

import com.github.gwtbootstrap.client.ui.Modal;
import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.jboss.errai.ioc.client.container.IOC;
import org.uberfire.mvp.ParameterizedCommand;

@Dependent
public class DependencySelectorPopupViewImpl
        extends Modal
        implements DependencySelectorPopupView {

    private DependencySelectorPresenter presenter;
    private DependencyListWidget dependencyPagedJarTable;

    @AfterInitialization
    public void init() {
        dependencyPagedJarTable = IOC.getBeanManager().lookupBean( DependencyListWidget.class ).getInstance();

        dependencyPagedJarTable.addOnSelect( new ParameterizedCommand<String>() {
            @Override
            public void execute( String parameter ) {
                presenter.onPathSelection( parameter );
            }
        } );

        setTitle( "Artifacts" );
        add( dependencyPagedJarTable );
        setPixelSize( 800,
                      500 );
    }

    @Override
    public void init( final DependencySelectorPresenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void show() {
        dependencyPagedJarTable.refresh();
        super.show();
    }
}
