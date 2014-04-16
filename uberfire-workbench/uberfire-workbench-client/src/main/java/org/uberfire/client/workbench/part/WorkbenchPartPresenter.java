package org.uberfire.client.workbench.part;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RequiresResize;
import org.uberfire.client.mvp.UberView;
import org.uberfire.workbench.model.PartDefinition;
import org.uberfire.workbench.model.menu.Menus;

public interface WorkbenchPartPresenter {

    PartDefinition getDefinition();

    void setDefinition( PartDefinition definition );

    View getPartView();

    void setWrappedWidget( IsWidget widget );

    String getTitle();

    void setTitle( String title );

    Menus getMenus();

    void setMenus( Menus menus );

    IsWidget getTitleDecoration();

    void setTitleDecoration( IsWidget titleDecoration );

    String getContextId();

    void setContextId( String contextId );

    public interface View
            extends
            UberView<WorkbenchPartPresenter>,
            RequiresResize {

        WorkbenchPartPresenter getPresenter();

        void setWrappedWidget( IsWidget widget );

        IsWidget getWrappedWidget();
    }
}
