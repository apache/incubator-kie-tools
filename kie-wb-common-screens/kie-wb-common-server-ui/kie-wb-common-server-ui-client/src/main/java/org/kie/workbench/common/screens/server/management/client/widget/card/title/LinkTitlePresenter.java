package org.kie.workbench.common.screens.server.management.client.widget.card.title;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.uberfire.client.mvp.UberView;
import org.uberfire.mvp.Command;

import static org.uberfire.commons.validation.PortablePreconditions.*;

@Dependent
public class LinkTitlePresenter implements TitlePresenter {

    public interface View extends UberView<LinkTitlePresenter> {

        void setText( final String value );
    }

    private final View view;
    private Command command;

    @Inject
    public LinkTitlePresenter( final View view ) {
        this.view = view;
    }

    @PostConstruct
    public void init() {
        this.view.init( this );
    }

    @Override
    public View getView() {
        return view;
    }

    public void setup( final String title,
                       final Command command ) {
        this.command = checkNotNull( "command", command );
        view.setText( title );
    }

    public void onSelect() {
        command.execute();
    }
}
