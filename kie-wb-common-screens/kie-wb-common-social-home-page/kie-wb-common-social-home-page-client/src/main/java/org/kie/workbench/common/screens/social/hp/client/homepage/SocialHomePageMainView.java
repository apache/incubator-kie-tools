package org.kie.workbench.common.screens.social.hp.client.homepage;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import org.kie.workbench.common.screens.social.hp.client.homepage.header.HeaderPresenter;
import org.kie.workbench.common.screens.social.hp.client.homepage.main.MainPresenter;

@Dependent
public class SocialHomePageMainView extends Composite implements SocialHomePageMainPresenter.View {

    private final FlowPanel panel = new FlowPanel();
    private SocialHomePageMainPresenter presenter = null;

    private HeaderPresenter header  = null;
    private MainPresenter main = null;

    @PostConstruct
    public void setup() {
        initWidget( panel );
    }

    @Override
    public void init( final SocialHomePageMainPresenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void setHeader( HeaderPresenter header ) {
        if ( this.header == null ) {
            this.header = header;
            panel.clear();
            panel.add( header.getView() );
        }
    }

    @Override
    public void setMain( MainPresenter main ) {
        if ( this.main == null ) {
            this.main = main;
            panel.add( main.getView() );
        }
    }

}
