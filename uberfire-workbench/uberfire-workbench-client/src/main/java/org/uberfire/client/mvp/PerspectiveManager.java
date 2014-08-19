package org.uberfire.client.mvp;

import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.client.workbench.PanelManager;
import org.uberfire.client.workbench.WorkbenchServicesProxy;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.Commands;
import org.uberfire.workbench.model.PerspectiveDefinition;

/**
 * The PerspectiveManager is responsible for the process of starting up a perspective, shutting down the current
 * perspective, and switching between perspectives. This includes a sequence of asynchronous operations such as fetching
 * any stored definition the current user has for the new perspective, and saving the definition when the user has
 * modified it (for example, by dragging and dropping components, launching new activities, and so on).
 * <p>
 * Structurally, the PerspectiveManager performs actions at the request of the {@link PlaceManager}, and it accomplishes
 * these actions by delegating to the {@link PanelManager} and {@link WorkbenchServicesProxy}.
 */
public interface PerspectiveManager {

    /**
     * Returns the current perspective's {@link PerspectiveActivity}. When the app first starts, this will be null. Once
     * the workbench has bootstrapped, the return value will not be null again.
     */
    PerspectiveActivity getCurrentPerspective();

    /**
     * Returns a description of the current perspective which reflects the current visible state of the user interface.
     */
    PerspectiveDefinition getLivePerspectiveDefinition();

    /**
     * Saves the current live perspective definition to the server. Saved perspective definitions override the default
     * definition produced by the {@link PerspectiveActivity#getDefaultPerspectiveLayout()} when switching to a new
     * perspective. This method has no effect if the current perspective is transient.
     *
     * @param doWhenFinished
     *            action to execute after the save is complete (or immediately in the case of a transient perspective).
     *            Must not be null. To do nothing, use {@link Commands#DO_NOTHING}.
     *
     * @see WorkbenchPerspective#isTransient()
     * @see PerspectiveActivity#isTransient()
     */
    void savePerspectiveState( final Command doWhenFinished );

    /**
     * This method should only be invoked by PlaceManager. To launch a perspective within an UberFire app, pass a
     * PlaceRequest for that perspective to {@link PlaceManager#goTo(org.uberfire.mvp.PlaceRequest)}.
     * <p>
     * Closes all current panels in the PanelManager, then builds up the new panel arrangement based on the
     * {@link PerspectiveDefinition} associated with the given perspective activity. If the given perspective is
     * transient, its default perspective definition will always be used. Otherwise, the PerspectiveManager will first
     * attempt to retrieve the current user's saved PerspectiveDefinition from the server, falling back on the default
     * if none is found.
     *
     * @param perspective
     *            the perspective to switch to. Must not be null.
     * @param doWhenFinished
     *            The command to execute once the new perspective's panels and parts have been launched. Must not be
     *            null.
     */
    void switchToPerspective( final PerspectiveActivity perspective,
                              final Command doWhenFinished );

}
