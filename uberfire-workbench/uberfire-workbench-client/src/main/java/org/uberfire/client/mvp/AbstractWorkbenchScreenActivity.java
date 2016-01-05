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

import org.uberfire.client.annotations.WorkbenchScreen;

/**
 * Implementation of behaviour common to all workbench screen activities. Concrete implementations are typically
 * generated from classes annotated with {@link WorkbenchScreen}, but it is permissible for applications to extend this
 * class directly instead of using the {@code @WorkbenchScreen} annotation.
 * <p>
 * When implementing a Screen by extending this class, you must follow three rules:
 * <ol>
 *  <li>mark it as a {@code @Dependent} bean;
 *  <li>specify its place ID via the {@code @Named} annotation;
 *  <li>include an {@code @Inject} constructor that passes the {@code PlaceManager} up to
 *      the super constructor.
 * </ol>
 */
public abstract class AbstractWorkbenchScreenActivity extends AbstractWorkbenchActivity implements WorkbenchScreenActivity {

    /**
     * Passes the given PlaceManager up to the superclass.
     * <p>
     * In order to make the {@code super()} call to this constructor, subclasses should declare their own constructor
     * that takes a {@code PlaceManager} plus any other dependencies required by the screen, and annotate that
     * constructor with {@code @Inject}.
     *
     * @param placeManager The PlaceManager in force for the current application. Must not be null.
     */
    public AbstractWorkbenchScreenActivity( final PlaceManager placeManager ) {
        super( placeManager );
    }

}
