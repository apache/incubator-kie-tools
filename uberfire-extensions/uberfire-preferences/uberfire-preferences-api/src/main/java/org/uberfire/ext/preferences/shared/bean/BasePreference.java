/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.ext.preferences.shared.bean;

import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;

/**
 * Interface that all preference beans should implement. It allows load and save operations
 * when injecting it through CDI. The methods in this class are only really implemented by
 * a generated preference bean, that will be the one injected by CDI.
 * @param <T> The preference bean type implementing the interface.
 */
public interface BasePreference<T> extends Preference {

    /**
     * Loads the preference content recursively through its properties.
     */
    default void load() {
        throw new UnsupportedOperationException( "You should call this method only for default qualified injected instances." );
    }

    /**
     * Loads the preference content recursively through its properties.
     * @param errorCallback Error callback that returns the exception that occurred (if any).
     */
    default void load( final ParameterizedCommand<Throwable> errorCallback ) {
        throw new UnsupportedOperationException( "You should call this method only for default qualified injected instances." );
    }

    /**
     * Loads the preference content recursively through its properties.
     * @param successCallback Success callback that returns the loaded preference.
     * @param errorCallback Error callback that returns the exception that occurred (if any).
     */
    default void load( final ParameterizedCommand<T> successCallback,
                       final ParameterizedCommand<Throwable> errorCallback ) {
        throw new UnsupportedOperationException( "You should call this method only for default qualified injected instances." );
    }

    /**
     * Saves the preference content recursively through its properties.
     */
    default void save() {
        throw new UnsupportedOperationException( "You should call this method only for default qualified injected instances." );
    }

    /**
     * Saves the preference content recursively through its properties.
     * @param errorCallback Error callback that returns the exception that occurred (if any).
     */
    default void save( final ParameterizedCommand<Throwable> errorCallback ) {
        throw new UnsupportedOperationException( "You should call this method only for default qualified injected instances." );
    }

    /**
     * Saves the preference content recursively through its properties.
     * @param successCallback Success callback that indicates that the preference was saved.
     * @param errorCallback Error callback that returns the exception that occurred (if any).
     */
    default void save( final Command successCallback,
                       final ParameterizedCommand<Throwable> errorCallback ) {
        throw new UnsupportedOperationException( "You should call this method only for default qualified injected instances." );
    }
}
