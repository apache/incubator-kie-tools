package org.kie.workbench.common.screens.projecteditor.client.forms;

import javax.enterprise.context.Dependent;

import com.google.gwt.user.client.Timer;
import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.jboss.errai.ioc.client.container.IOC;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.mvp.ParameterizedCommand;

@Dependent
public class DependencySelectorPopupViewImpl
        extends BaseModal
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
        super.show();

        /*
           We need to give time for the popup to show up,
           before loading the content.
         */
        new Timer() {
            @Override
            public void run() {
                dependencyPagedJarTable.refresh();
            }
        }.schedule( 1000 );
    }
}
