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
 * <p>A client side viewer.</p>
 * @param <T> The entity type.
 *
 * @since 0.8.0
 */
public interface Viewer<T> {

    /**
     * Initialize the viewer hierarchy and display the instance to show. 
     * @param instance The instance.
     */
    void show(T instance);
}
