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

package org.uberfire.ext.wires.client.preferences.form;

/**
 * View mode to define what scopes options the user will be able to choose.
 */
public enum ViewMode {

    /**
     * No scope is shown.
     */
    GLOBAL,

    /**
     * The USER and ALL_USERS scopes can be chosen.
     */
    USER,

    /**
     * The COMPONENT and ENTIRE_APPLICATION scopes can be chosen.
     */
    COMPONENT,

    /**
     * THE USER, ALL_USERS, COMPONENT and ENTIRE_APPLICATION scopes can be chosen.
     */
    USER_COMPONENT
}
