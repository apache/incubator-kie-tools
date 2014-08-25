package org.kie.workbench.common.screens.social.hp.client.userpage.main;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.kie.uberfire.social.activities.client.widgets.timeline.regular.model.SocialTimelineWidgetModel;
import org.kie.uberfire.social.activities.client.widgets.timeline.simple.model.SimpleSocialTimelineWidgetModel;

@Dependent
public class MainPresenter {

    public View getView() {
        return view;
    }

    @Inject
    private View view;

    public interface View extends IsWidget {
        void setup( SimpleSocialTimelineWidgetModel model );
    }

    public void setup( SimpleSocialTimelineWidgetModel model ) {
        view.setup(model);
    }


}
