/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.uberfire.client.mvp;

import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchPopup;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.security.authz.RuntimeResource;

/**
 * Common top-level interface for all Workbench Activity classes. No concrete class implements this interface directly;
 * see the subinterfaces for specific activity types that do get implemented.
 * <p>
 * Also, implementations of this interface and its subinterfaces are typically not written by hand; instead, they are
 * generated from classes annotated with {@link WorkbenchScreen}, {@link WorkbenchEditor}, {@link WorkbenchPopup}, and
 * others by an UberFire annotation processor.
 * <p>
 * Developers of UberFire applications will not typically come into direct contact with things that implement Activity
 * or its subinterfaces; instead, they will work with a {@link PlaceManager} to manipulate activities at arm's length.
 * <p>
 * If you do need to get your hands on a particular {@code Activity} instance, do so using an {@link ActivityManager}.
 * 
 * <h3>Activity Lifecycle</h3>
 * Activities have the following lifecycle, which is normally driven by an {@link ActivityManager}:
 * <ol>
 *  <li>The activity starts off in the <i>uninitialized</i> state.
 *  <li>{@link #onStartup(PlaceRequest)} is called with the the PlaceRequest that caused it to be created.
 *       The activity is "associated" with this PlaceRequest until the onShutdown method is invoked. This puts the activity
 *       in the <i>started</i> state.
 *  <li>{@link #onOpen()} is called to notify the Activity that its view has been added to the UI, and its associated
 *       place is considered "open." This puts the activity in the <i>open</i> state.
 *  <li>{@link #onClose()} is called to notify the Activity that its view has been removed from the UI, and its associated
 *       place is considered "closed." This puts the activity back in the <i>started</i> state.
 *  <li>{@link #onShutdown()} is called to notify the Activity that it is no longer associated with the PlaceRequest.
 *      This puts the activity back in the <i>uninitialized</i> state.
 * </ol>
 * 
 * An activity will never receive a call to {@link #onStartup(PlaceRequest)} when it is started or open, but it may be
 * restarted (perhaps with a different PlaceRequest) after a call to {@link #onShutdown()}.
 * <p>
 * An activity will never receive a call to {@link #onOpen()} when it is uninitialized or open, but it may be reopened after a call
 * to {@link #onClose()}.
 * 
 * @see PlaceManager
 * @see ActivityManager
 */
public interface Activity extends RuntimeResource {

    /**
     * Called by the framework to notify this activity that it is now associated with the given PlaceRequest.
     * When this lifecycle method is invoked, the activity's widget has not yet been added to the GUI.
     * 
     * @param place
     *            The place that resolved to this activity
     */
    void onStartup( final PlaceRequest place );

    /**
     * Called by the framework to notify this activity that its Widget has been added to the live GUI.
     */
    void onOpen();

    /**
     * Called by the framework to notify this activity that its Widget has been removed from the live GUI.
     */
    void onClose();

    /**
     * Called by the framework to notify this activity that it is no longer associated with the PlaceRequest that was
     * passed to {@link #onStartup(PlaceRequest)}.
     */
    void onShutdown();

}
