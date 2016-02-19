package org.kie.workbench.common.screens.server.management.client.widget.card;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.kie.workbench.common.screens.server.management.client.widget.card.body.BodyPresenter;
import org.kie.workbench.common.screens.server.management.client.widget.card.footer.FooterPresenter;
import org.kie.workbench.common.screens.server.management.client.widget.card.title.TitlePresenter;
import org.uberfire.client.mvp.UberView;

@Dependent
public class CardPresenter {

    public interface View extends UberView<CardPresenter> {

        void add( IsWidget view );
    }

    private final View view;

    @Inject
    public CardPresenter( final View view ) {
        this.view = view;
    }

    @PostConstruct
    public void init() {
        view.init( this );
    }

    public View getView() {
        return view;
    }

    public void addTitle( final TitlePresenter titlePresenter ) {
        view.add( titlePresenter.getView() );
    }

    public void addBody( final BodyPresenter bodyPresenter ) {
        view.add( bodyPresenter.getView() );
    }

    public void addFooter( final FooterPresenter footerPresenter ) {
        view.add( footerPresenter.getView() );
    }

}
