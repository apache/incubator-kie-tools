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

import javax.validation.ConstraintViolation;
import java.util.Set;

/**
 * <p>A client side editor.</p>
 * 
 * <p>Editors should implement this interface in order to isolate the editor components from the editing logic (provided by drivers) and the instance edited.</p>
 * <p>Editor sub-types should add more interface methods to describe the whole editor hierarchy for the instance of type <code>T</code>.</p>
 * 
 * <p>The most common edition workflow consist of:</p>
 * <ol>
 *     <li>Create the editor implementation instance and call <code>edit()</code> using the edited instance as argument.</li>
 *     <li>Let the user interact with the editors and update their states.</li>
 *     <li>Flush the whole editors hierarchy to ensure editor's states are up to date.</li>
 *     <li>Obtain the edited instance member values from each editor in the hierarchy by calling <code>getValue()</code>.</li>
 *     <li>Validate the edited instance using any JSR303 validators for it.</li>
 *     <li>Set the resulting violations, if any, into the editors hierarchy to display the validation errors.</li>
 * </ol>
 * 
 * @param <T> The edited entity type.
 * @param <A> The edited member type for this editor/sub-editor.
 *
 * @since 0.8.0
 */
public interface Editor<T, A> extends Viewer<T> {

    /**
     * Initialize the editor hierarchy using the instance given.
     * @param instance The instance used to initialize editors.
     */
    void edit(T instance);

    /**
     * Flush editor's state.
     */
    void flush();

    /**
     * Get edited value. This method must be called after flush.
     * @return
     */
    A getValue();

    /**
     * Set validation violations into the editor hierarchy.
     * @param violations The validation violations.
     */
    void setViolations(Set<ConstraintViolation<T>> violations);
    
}
