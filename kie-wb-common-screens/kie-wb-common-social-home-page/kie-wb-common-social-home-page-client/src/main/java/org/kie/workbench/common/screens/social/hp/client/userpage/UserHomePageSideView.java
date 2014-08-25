package org.kie.workbench.common.screens.social.hp.client.userpage;

import java.util.List;
import javax.enterprise.context.ApplicationScoped;

import com.github.gwtbootstrap.client.ui.Accordion;
import com.github.gwtbootstrap.client.ui.AccordionGroup;
import com.github.gwtbootstrap.client.ui.Paragraph;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle;
import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.kie.workbench.common.screens.social.hp.client.userpage.side.SideUserInfoPresenter;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;

@ApplicationScoped
public class UserHomePageSideView extends Composite implements UserHomePageSidePresenter.View {

    private final Accordion accordion = GWT.create( Accordion.class );
    private final AccordionGroup allPeople = GWT.create( AccordionGroup.class );
    private final AccordionGroup user = GWT.create( AccordionGroup.class );

    private UserHomePageSidePresenter presenter = null;

    @AfterInitialization
    public void setup() {
        initWidget( accordion );
        accordion.add( allPeople );
        user.setDefaultOpen( true );
        accordion.add( user );
    }

    @Override
    public void init( final UserHomePageSidePresenter presenter ) {
        this.presenter = presenter;
    }


    @Override
    public void setupUserMenu( String userName,
                               SideUserInfoPresenter sideUserInfoPresenter ) {
        user.clear();
        user.setHeading( userName );
        user.setDefaultOpen( true );
        user.add( sideUserInfoPresenter.getView() );
    }

    @Override
    public void setupAllPeopleMenu( List<String> users,
                                    final ParameterizedCommand onSelect ) {
        allPeople.clear();
        allPeople.setHeading( "All People" );
        allPeople.add( new Paragraph( "Search:" ) );
        MultiWordSuggestOracle oracle = new MultiWordSuggestOracle();
        oracle.addAll( users );
        SuggestBox box = new SuggestBox( oracle );
        box.addSelectionHandler( new SelectionHandler<SuggestOracle.Suggestion>() {
            @Override
            public void onSelection( SelectionEvent<SuggestOracle.Suggestion> event ) {
                onSelect.execute(event.getSelectedItem().getReplacementString());
            }
        } );
        allPeople.add( box );
    }

}
