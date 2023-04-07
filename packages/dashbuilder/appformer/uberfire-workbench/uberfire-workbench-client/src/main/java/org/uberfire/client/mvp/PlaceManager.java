/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.client.mvp;

import java.util.List;

import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsType;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.client.annotations.WorkbenchPopup;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.mvp.BiParameterizedCommand;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PartDefinition;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

/**
 * A Workbench-centric abstraction over the browser's history mechanism. Allows the application to initiate navigation
 * to any displayable thing: a {@link WorkbenchPerspective}, a {@link WorkbenchScreen}, a {@link WorkbenchPopup}, a
 * a {@link WorkbenchPart} within a screen or editor, or the editor associated with a VFS file
 * located at a particular {@link Path}.
 */
@JsType
public interface PlaceManager {

    @JsMethod(name = "goToId")
    void goTo(final String identifier);

    @JsMethod(name = "goToPlace")
    void goTo(final PlaceRequest place);


    @JsMethod(name = "goToPartWithPanel")
    void goTo(final PartDefinition part,
              final PanelDefinition panel);


    @JsMethod(name = "goToPlaceWithPanel")
    void goTo(final PlaceRequest place,
              final PanelDefinition panel);

    /**
     * Finds the <i>currently open</i> activity that handles the given PlaceRequest by ID. No attempt is made to match
     * by path, but see {@link ActivityManagerImpl#resolveExistingParts(PlaceRequest)} for a variant that does.
     *
     * @param place the PlaceRequest whose activity to search for
     * @return the activity that currently exists in service of the given PlaceRequest's ID. Null if no current activity
     * handles the given PlaceRequest.
     */
    Activity getActivity(final PlaceRequest place);

    @JsMethod(name = "getStatusByPlaceRequest")
    PlaceStatus getStatus(final PlaceRequest place);

    default void executeOnOpenCallbacks(final PlaceRequest place) {
        checkNotNull("place",
                     place);

        final List<Command> callbacks = getOnOpenCallbacks(place);
        if (callbacks != null) {
            callbacks.forEach(Command::execute);
        }
    }

    default void executeOnCloseCallbacks(final PlaceRequest place) {
        checkNotNull("place",
                     place);

        final List<Command> callbacks = getOnCloseCallbacks(place);
        if (callbacks != null) {
            callbacks.forEach(Command::execute);
        }
    }

    List<Command> getOnOpenCallbacks(PlaceRequest place);

    List<Command> getOnCloseCallbacks(PlaceRequest place);

    @JsMethod(name = "closePlaceById")
    void closePlace(final String id);

    void closePlace(final PlaceRequest placeToClose);

    @JsMethod(name = "closePlaceWithCallback")
    void closePlace(final PlaceRequest placeToClose,
                    final Command doAfterClose);

    void tryClosePlace(final PlaceRequest placeToClose,
                       final Command onAfterClose);

    @JsMethod(name = "forceCloseById")
    void forceClosePlace(final String id);

    @JsMethod(name = "forceCloseByPlaceRequest")
    void forceClosePlace(final PlaceRequest place);

    void closeAllPlaces();

    void forceCloseAllPlaces();

    boolean closeAllPlacesOrNothing();

    boolean canClosePlace(PlaceRequest place);

    boolean canCloseAllPlaces();

    /**
     * @return All opened PlaceRequests that cannot be closed (@onMayClose method returns false).
     */
    List<PlaceRequest> getUncloseablePlaces();

    void registerOnOpenCallback(PlaceRequest place,
                                Command callback);

    void unregisterOnOpenCallbacks(PlaceRequest place);

    void registerOnCloseCallback(PlaceRequest place,
                                 Command callback);

    void unregisterOnCloseCallbacks(PlaceRequest place);

    /**
     * Registers a callback interceptor that uses a chain approach to execute code before a PlaceRequest is closed,
     * if the perspective passed as a parameter is currently opened. It will not be executed in the case of a forced close.
     * @param perspectiveIdentifier Perspective identifier for which the close chain must be called when it is being closed.
     * @param closeChain Callback to be called when a PlaceRequest is being closed. The callback command must invoke the chain
     * to proceed with the closing operation.
     */
    void registerPerspectiveCloseChain(String perspectiveIdentifier,
                                       BiParameterizedCommand<Command, PlaceRequest> closeChain);

}
