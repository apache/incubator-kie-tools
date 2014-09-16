package org.kie.workbench.common.screens.social.hp.client.userpage.main.header;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import org.kie.uberfire.social.activities.client.widgets.userbox.UserBoxView;
import org.kie.uberfire.social.activities.model.SocialUser;
import org.uberfire.mvp.ParameterizedCommand;

@Dependent
public class HeaderPresenter {

    public View getView() {
        return view;
    }

    @Inject
    private View view;

    public void clear() {
        view.clear();
    }

    public void noConnection() {
        view.noConnection();
    }

    public interface View extends IsWidget {

        void addConnection( SocialUser follower,
                            UserBoxView.RelationType relationType,
                            Image connection,
                            ParameterizedCommand<String> clickCommand,
                            ParameterizedCommand<String> followUnfollowCommand );

        void clear();

        void noConnection();
    }

    @PostConstruct
    public void setup() {
    }

    public void addConnection( SocialUser follower,
                               UserBoxView.RelationType relationType,
                               Image connection,
                               ParameterizedCommand<String> clickCommand,
                               ParameterizedCommand<String> followUnfollowCommand ) {
        view.addConnection( follower, relationType, connection, clickCommand, followUnfollowCommand );
    }

}
