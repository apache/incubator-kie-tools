package org.uberfire.client.workbench.part;

import org.uberfire.client.mvp.UberView;
import org.uberfire.client.workbench.panels.impl.MultiTabWorkbenchPanelPresenter;
import org.uberfire.workbench.model.PartDefinition;
import org.uberfire.workbench.model.menu.Menus;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RequiresResize;

public interface WorkbenchPartPresenter {

    PartDefinition getDefinition();

    void setDefinition( PartDefinition definition );

    View getPartView();

    void setWrappedWidget( IsWidget widget );

    /**
     * Returns the text that a panel may choose to display beside this part's title. For example,
     * {@link MultiTabWorkbenchPanelPresenter} uses this text for the tab's label.
     *
     * @return The title of this part; never null.
     */
    String getTitle();

    /**
     * Sets the text that a panel may choose to display beside this part's title. For example,
     * {@link MultiTabWorkbenchPanelPresenter} uses this text for the tab's label.
     *
     * @param title
     *            The title of this part. Null is not permitted.
     */
    void setTitle( String title );

    /**
     * Returns the menus associated with this part.
     *
     * @return the menus associated with this part. Null if this part does not have menus.
     */
    Menus getMenus();

    /**
     * Sets the menus associated with this part.
     *
     * @param menus
     *            the menus associated with this part. Can be null, which means this part does not have menus.
     */
    void setMenus( Menus menus );

    /**
     * Returns the widget that a panel may choose to display beside this part's title.
     *
     * @return The title decoration widget to use. Null means no title decoration.
     */
    IsWidget getTitleDecoration();

    /**
     * Sets the widget that a panel may choose to display beside this part's title.
     * <p>
     * NOTE: presently, none of the built-in panel types display a part's title decoration.
     *
     * @param titleDecoration
     *            The title decoration widget to use. Null is permitted, and means no title decoration.
     */
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
