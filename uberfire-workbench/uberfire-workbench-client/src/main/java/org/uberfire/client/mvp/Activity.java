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
import org.uberfire.mvp.Command;
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
 * @see PlaceManager
 * @see ActivityManager
 */
public interface Activity extends RuntimeResource {

    void launch( final PlaceRequest place,
                 final Command callback );

    void onOpen();

}
