package org.uberfire.client.workbench.widgets.menu;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.client.workbench.events.PerspectiveChange;
import org.uberfire.workbench.model.menu.Menus;

@ApplicationScoped
public class PerspectiveContextMenusPresenter {

    public interface View
            extends
            IsWidget {

        void buildMenu( final Menus menus );

        void clear();
    }

    @Inject
    private View view;

    void onPerspectiveChange( @Observes PerspectiveChange perspectiveChange ) {
        buildMenu( perspectiveChange.getMenus() );
    }

    private void buildMenu( final Menus menus ) {
        if ( menus == null || menus.getItems() == null || menus.getItems().isEmpty() ) {
            view.clear();
            return;
        }
        view.buildMenu( menus );
    }

    public View getView() {
        return view;
    }
}
