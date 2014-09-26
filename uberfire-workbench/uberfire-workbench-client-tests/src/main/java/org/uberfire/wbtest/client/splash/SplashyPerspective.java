package org.uberfire.wbtest.client.splash;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.panels.impl.MultiTabWorkbenchPanelPresenter;
import org.uberfire.wbtest.client.api.AbstractTestPerspectiveActivity;
import org.uberfire.wbtest.client.resize.ResizeTestScreenActivity;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.impl.PerspectiveDefinitionImpl;

@Dependent
@Named("org.uberfire.wbtest.client.splash.SplashyPerspective")
public class SplashyPerspective extends AbstractTestPerspectiveActivity {

    @Inject
    public SplashyPerspective( PlaceManager placeManager ) {
        super( placeManager );
    }

    @Override
    public PerspectiveDefinition getDefaultPerspectiveLayout() {
        PerspectiveDefinition pd = new PerspectiveDefinitionImpl( MultiTabWorkbenchPanelPresenter.class.getName() );

        // this can be any screen EXCEPT DefaultScreenActivity because that one is used in the selenium
        // tests for detecting when the transition back to the default perspective is complete.
        pd.getRoot().addPart( ResizeTestScreenActivity.class.getName() );

        return pd;
    }

}
