package org.kie.workbench.common.screens.social.hp.client.homepage.main;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.kie.uberfire.social.activities.client.widgets.timeline.regular.SocialTimelineWidget;

@Dependent
public class MainPresenter {

    public View getView() {
        return view;
    }

    @Inject
    private View view;

    public interface View extends IsWidget {

        void setSocialWidget( SocialTimelineWidget socialTimelineWidget );
    }

    public void setSocialWidget( SocialTimelineWidget socialTimelineWidget ) {
        view.setSocialWidget(socialTimelineWidget);
    }

    @PostConstruct
    public void setup() {
    }


}
