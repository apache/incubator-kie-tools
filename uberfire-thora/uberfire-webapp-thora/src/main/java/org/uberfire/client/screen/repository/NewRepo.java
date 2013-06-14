package org.uberfire.client.screen.repository;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.TextBox;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.widgets.gravatar.GravatarImage;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.security.Identity;
import org.uberfire.shared.repository.RepositoryAppService;
import org.uberfire.shared.repository.RepositoryInfo;

@WorkbenchScreen(identifier = "NewRepo")
@Templated("new-repo.html")
@ApplicationScoped
public class NewRepo extends Composite {

    @Inject
    private PlaceManager placeManager;

    @Inject
    private Caller<RepositoryAppService> repositoryAppService;

    @Inject
    private Identity identity;

    @DataField
    @Inject
    private Image userImage;

    @Inject
    @DataField
    private CheckBox initRepo;

    @Inject
    @DataField
    private TextBox repoName;

    @Inject
    @DataField
    private TextBox description;

    @Inject
    @DataField
    private TextBox origin;

    @Inject
    @DataField
    private Button createRepo;

    @DataField
    private Element errorMessage = DOM.createSpan();

    @DataField
    private Element repoGroup = DOM.createDiv();

    @WorkbenchPartTitle
    public String getTitle() {
        return "New Repository";
    }

    @PostConstruct
    public void setup() {
        String _email = identity.getProperty( "email", "--" );
        userImage.setUrl( new GravatarImage( _email, 30 ).getUrl() );
        repoName.addKeyDownHandler( new KeyDownHandler() {
            @Override
            public void onKeyDown( KeyDownEvent event ) {
                errorMessage.setInnerText( "" );
                repoGroup.removeClassName( "error" );
            }
        } );
    }

    @EventHandler("createRepo")
    public void createRepo( ClickEvent e ) {

        repositoryAppService.call( new RemoteCallback<Boolean>() {
            @Override
            public void callback( Boolean exists ) {
                if ( exists ) {
                    errorMessage.setInnerText( "Repository already exists." );
                    repoGroup.addClassName( "error" );
                    repoName.setFocus( true );
                    return;
                }
                final RemoteCallback callback = new RemoteCallback<RepositoryInfo>() {
                    @Override
                    public void callback( RepositoryInfo o ) {
                        errorMessage.setInnerText( "" );
                        repoGroup.removeClassName( "error" );
                        initRepo.setValue( false );
                        description.setText( "" );
                        origin.setText( "" );
                        placeManager.closePlace( new DefaultPlaceRequest( "NewRepo" ) );
                        placeManager.goTo( new DefaultPlaceRequest( "RepoBrowser" ).addParameter( "repo", repoName.getText() ) );
                        repoName.setText( "" );
                    }
                };

                if ( origin.getText().trim().isEmpty() ) {
                    repositoryAppService.call( callback ).createRepository( identity.getName(), repoName.getText(), description.getText(), initRepo.getValue() );
                } else {
                    repositoryAppService.call( callback ).mirrorRepository( identity.getName(), repoName.getText(), description.getText(), origin.getText() );
                }

            }
        } ).repositoryAlreadyExists( repoName.getText() );

    }

}
