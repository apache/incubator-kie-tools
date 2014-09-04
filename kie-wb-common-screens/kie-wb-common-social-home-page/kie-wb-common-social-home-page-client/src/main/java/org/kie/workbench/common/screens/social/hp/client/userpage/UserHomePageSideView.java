package org.kie.workbench.common.screens.social.hp.client.userpage;

import java.util.List;
import javax.enterprise.context.ApplicationScoped;

import com.github.gwtbootstrap.client.ui.Paragraph;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle;
import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.kie.workbench.common.screens.social.hp.client.userpage.side.SideUserInfoPresenter;
import org.uberfire.mvp.ParameterizedCommand;

@ApplicationScoped
public class UserHomePageSideView extends Composite implements UserHomePageSidePresenter.View {

    private final FlowPanel userInfoPanel = GWT.create( FlowPanel.class );

    private UserHomePageSidePresenter presenter = null;

    @AfterInitialization
    public void setup() {
        initWidget( userInfoPanel );
    }

    @Override
    public void init( final UserHomePageSidePresenter presenter ) {
        this.presenter = presenter;
    }


    @Override
    public void setupUserInfo( String userName,
                               SideUserInfoPresenter sideUserInfoPresenter ) {
        userInfoPanel.add( sideUserInfoPresenter.getView() );
    }


    @Override
    public void setupSearchPeopleMenu( List<String> users,
                                       final ParameterizedCommand<String> onSelect,
                                       String suggestText ) {

        MultiWordSuggestOracle oracle = new MultiWordSuggestOracle();
        oracle.addAll( users );
        SuggestBox box = new SuggestBox( oracle );
        box.setText( suggestText );
        box.addSelectionHandler( new SelectionHandler<SuggestOracle.Suggestion>() {
            @Override
            public void onSelection( SelectionEvent<SuggestOracle.Suggestion> event ) {
                onSelect.execute(event.getSelectedItem().getReplacementString());
            }
        } );
        userInfoPanel.add( box );
    }

    @Override
    public void setupHomeLink( Anchor anchor ) {
        Paragraph p = GWT.create(Paragraph.class);
        p.add( anchor );
        userInfoPanel.add( p );
    }

    @Override
    public void clear() {
        userInfoPanel.clear();
    }

}
