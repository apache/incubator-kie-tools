package org.kie.workbench.common.screens.server.management.client.header;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;

@Dependent
public class HeaderPresenter {

    public View getView() {
        return view;
    }

    @Inject
    private View view;

    public interface View extends IsWidget {

        void displayDeleteContainer();

        void displayStopContainer();

        void displayStartContainer();

        void hideDeleteContainer();

        void hideStopContainer();

        void hideStartContainer();

        void setOnFilterChange( ParameterizedCommand<String> command );

        void setOnClearSelection( Command command );

        void setOnRegisterServer( final Command onConnectToServerSelection );

        void setOnSelectAll( Command command );

        void setOnDelete( final Command onDelete );

        void setOnStart( Command onStart );

        void setOnStop( Command onStop );

        void setOnRefresh( Command onStop );

        void filter( final String content );

        void clearFilter();
    }

    @PostConstruct
    public void setup() {
    }

    public void setOnFilterChange( final ParameterizedCommand<String> command ) {
        view.setOnFilterChange( command );
    }

    public void setOnClearSelection( final Command command ) {
        view.setOnClearSelection( command );
    }

    public void setOnSelectAll( final Command command ) {
        view.setOnSelectAll( command );
    }

    public void setOnDelete( final Command command ) {
        view.setOnDelete( command );
    }

    public void setOnRefresh( final Command command ) {
        view.setOnRefresh( command );
    }

    public void setOnRegisterServer( final Command onRegisterServer ) {
        view.setOnRegisterServer( onRegisterServer );
    }

    public void setOnStart( final Command onStart ) {
        view.setOnStart( onStart );
    }

    public void setOnStop( final Command onStop ) {
        view.setOnStop( onStop );
    }

    public void displayDeleteContainer() {
        view.displayDeleteContainer();
    }

    public void displayStopContainer() {
        view.displayStopContainer();
    }

    public void displayStartContainer() {
        view.displayStartContainer();
    }

    public void hideStartContainer() {
        view.hideStartContainer();
    }

    public void hideStopContainer() {
        view.hideStopContainer();
    }

    public void hideDeleteContainer() {
        view.hideDeleteContainer();
    }

}
