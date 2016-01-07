/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.security.management.client.editor;

/**
 * <p>A client side editor's driver.</p>
 * 
 * <p>Drivers are used to isolate the editors hierarchy from the edition logic for an instance of type <code>T</code>.</p>
 * <p>So do not matter the editors' concrete implementations, same driver can be used agains different client side editors reusing the edition logic.</p>
 *
 * @since 0.8.0
 */
public interface Driver<T, E extends Editor<T, T>> {

    /**
     * Show the instance using the given editor.
     * @param instance The instance to show.
     * @param viewer The viewer for the instance.
     */
    void show(T instance, E viewer);

    /**
     * Edit the instance using the given editor.
     * @param instance The instance to show.
     * @param editor The editor for the instance.
     */
    void edit(T instance, E editor);

    /**
     * Flush the editors hierarchy states and perform the validations.
     * @return The validation violations, if any.
     */
    boolean flush();

    /**
     * The instance after being flush with against the editors hierarchy. 
     * @return The instance.
     */
    T getValue();
    
}
