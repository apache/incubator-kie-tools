package org.kie.workbench.common.screens.projecteditor.client.forms;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.event.ShownEvent;
import com.github.gwtbootstrap.client.ui.event.ShownHandler;

import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.jboss.errai.ioc.client.container.IOC;
import org.uberfire.client.mvp.LockRequiredEvent;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.mvp.ParameterizedCommand;

@Dependent
public class DependencySelectorPopupViewImpl
        extends BaseModal
        implements DependencySelectorPopupView {

    private DependencySelectorPresenter presenter;
    private DependencyListWidget dependencyPagedJarTable;
    
    @Inject
    private javax.enterprise.event.Event<LockRequiredEvent> lockRequired;

    @AfterInitialization
    public void init() {
        dependencyPagedJarTable = IOC.getBeanManager().lookupBean( DependencyListWidget.class ).getInstance();

        dependencyPagedJarTable.addOnSelect( new ParameterizedCommand<String>() {
            @Override
            public void execute( String parameter ) {
                presenter.onPathSelection( parameter );
                lockRequired.fire( new LockRequiredEvent() );
            }
        } );

        setTitle( "Artifacts" );
        add( dependencyPagedJarTable );
        setPixelSize( 800,
                      500 );

        //Need to refresh the grid to load content after the popup is shown
        addShownHandler( new ShownHandler() {

            @Override
            public void onShown( final ShownEvent shownEvent ) {
                dependencyPagedJarTable.refresh();
            }

        } );
    }

    @Override
    public void init( final DependencySelectorPresenter presenter ) {
        this.presenter = presenter;
    }

}
