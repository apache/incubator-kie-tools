package org.uberfire.client.views.pfly.maximize;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.constants.ButtonSize;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.uberfire.client.resources.i18n.WorkbenchConstants;
import org.uberfire.client.workbench.panels.MaximizeToggleButtonPresenter;
import org.uberfire.client.workbench.panels.MaximizeToggleButtonPresenter.View;
import org.uberfire.mvp.Command;

import static org.uberfire.commons.validation.PortablePreconditions.*;

public class MaximizeToggleButton extends Button implements View {

    private MaximizeToggleButtonPresenter presenter;
    private boolean maximized;
    private Command maximizeCommand;
    private Command unmaximizeCommand;

    public MaximizeToggleButton() {
        setIcon( IconType.CHEVRON_UP );
        setSize( ButtonSize.SMALL );
        setTitle( WorkbenchConstants.INSTANCE.maximizePanel() );
        addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                presenter.handleClick();
            }
        } );
    }

    @Override
    public void init( MaximizeToggleButtonPresenter presenter ) {
        this.presenter = checkNotNull( "presenter", presenter );
    }

    /**
     * Normally invoked automatically when this button gets clicked. Exposed for testing purposes.
     */
    public void click() {
        final boolean wasMaximized = maximized;
        setMaximized( !wasMaximized );
        if ( wasMaximized ) {
            if ( unmaximizeCommand != null ) {
                unmaximizeCommand.execute();
                setTitle( WorkbenchConstants.INSTANCE.maximizePanel() );
            }
        } else {
            if ( maximizeCommand != null ) {
                maximizeCommand.execute();
                setTitle( WorkbenchConstants.INSTANCE.minimizePanel() );
            }
        }
    }

    /**
     * Returns the currently registered maximize command. Can be used to check if there is currently a maximize command registered.
     */
    public Command getMaximizeCommand() {
        return maximizeCommand;
    }

    /**
     * Sets the command to invoke upon each transition from unmaximized to maximized.
     */
    public void setMaximizeCommand( Command maximizeCommand ) {
        this.maximizeCommand = maximizeCommand;
    }

    /**
     * Returns the currently registered unmaximize command. Can be used to check if there is currently an unmaximize command registered.
     */
    public Command getUnmaximizeCommand() {
        return unmaximizeCommand;
    }

    /**
     * Sets the command to invoke upon each transition from maximized to unmaximized.
     */
    public void setUnmaximizeCommand( Command unmaximizeCommand ) {
        this.unmaximizeCommand = unmaximizeCommand;
    }

    /**
     * Reports whether this button is currently in the maximized state. If true, the next click will return to the
     * normal unmaximized state. If false, the next click will transition to the maximized state.
     */
    public boolean isMaximized() {
        return maximized;
    }

    /**
     * Changes the maximized state of this button <i>without</i> calling the commands. This can be used to notify the
     * button that some external process has already maximized the thing in question. It is permissible but not necessary
     * to call this method from the maximizeCommadn and unmaximizeCommand.
     *
     * @param maximized the new maximized state to set.
     */
    public void setMaximized( boolean maximized ) {
        this.maximized = maximized;
        if ( maximized ) {
            setIcon( IconType.CHEVRON_DOWN );
            setTitle( WorkbenchConstants.INSTANCE.minimizePanel() );
        } else {
            setIcon( IconType.CHEVRON_UP );
            setTitle( WorkbenchConstants.INSTANCE.maximizePanel() );
        }
    }
}