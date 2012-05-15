package org.drools.guvnor.client.perspective.workspace;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.IsWidget;
import org.drools.guvnor.client.common.content.multi.MultiActivityManager;
import org.drools.guvnor.client.common.content.multi.MultiContentPanel;

@ApplicationScoped
public class WorkspacePerspectivePresenter extends AbstractActivity {

    public interface MyView extends IsWidget {

        void setUserName(String userName);

        MultiContentPanel getContentPanel();
    }

    @Inject MyView view;

    @Inject private MultiActivityManager contentActivityManager;

    @Override
    public void start(final AcceptsOneWidget acceptsOneWidget, final EventBus eventBus) {
        acceptsOneWidget.setWidget(view);
        contentActivityManager.setTabbedPanel(view.getContentPanel());
    }
}
