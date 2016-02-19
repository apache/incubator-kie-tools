package org.kie.workbench.common.screens.server.management.client.navigation.template.copy;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.uberfire.client.mvp.UberView;
import org.uberfire.mvp.ParameterizedCommand;

@ApplicationScoped
public class CopyPopupPresenter {

    public interface View extends UberView<CopyPopupPresenter> {

        void clear();

        void display();

        String getNewTemplateName();

        void errorOnTemplateNameFromGroup();

        void hide();

        void errorOnTemplateNameFromGroup( String message );
    }

    private final View view;

    private ParameterizedCommand<String> command = new ParameterizedCommand<String>() {
        @Override
        public void execute( final String parameter ) {
        }
    };

    @Inject
    public CopyPopupPresenter( final View view ) {
        this.view = view;
    }

    @PostConstruct
    public void init() {
        view.init( this );
    }

    public View getView() {
        return view;
    }

    public void copy( final ParameterizedCommand<String> command ) {
        view.clear();
        view.display();
        this.command = command;
    }

    public void errorDuringProcessing( final String message ) {
        view.errorOnTemplateNameFromGroup( message );
    }

    public void save() {
        if ( view.getNewTemplateName().trim().isEmpty() ) {
            view.errorOnTemplateNameFromGroup();
            return;
        }
        command.execute( view.getNewTemplateName() );
    }

    public void hide() {
        view.hide();
    }

}
