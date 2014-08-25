package org.kie.workbench.common.screens.social.hp.client.userpage.side;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.Image;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.IsWidget;
import org.kie.uberfire.social.activities.model.SocialUser;

@Dependent
public class SideUserInfoPresenter {

    public interface View extends IsWidget {

        void setUserPanel( Image userImage );

        void setUserInfo( SocialUser socialUser );

        void setupLink( Anchor anchor );

        void clear();
    }

    @Inject
    private View view;

    public void setup( SocialUser socialUser,
                       Image userImage,
                       Anchor anchor ) {
        view.clear();
        view.setUserPanel( userImage );
        view.setUserInfo( socialUser );
        view.setupLink( anchor );
    }

    public View getView() {
        return view;
    }


}
