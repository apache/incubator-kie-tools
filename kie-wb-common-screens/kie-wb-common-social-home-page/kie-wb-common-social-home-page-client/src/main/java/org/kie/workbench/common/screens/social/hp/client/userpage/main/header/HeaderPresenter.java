package org.kie.workbench.common.screens.social.hp.client.userpage.main.header;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;

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

    public interface View extends IsWidget {

        void addConnection( Image connections );

        void clear();
    }

    @PostConstruct
    public void setup() {
    }

    public void addConnection( Image connection ) {
        view.addConnection( connection );
    }

}
