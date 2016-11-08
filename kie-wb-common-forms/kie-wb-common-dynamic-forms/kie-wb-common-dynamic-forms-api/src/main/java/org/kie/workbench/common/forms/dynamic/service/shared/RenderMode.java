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

package org.kie.workbench.common.forms.dynamic.service.shared;

import org.jboss.errai.common.client.api.annotations.Portable;

/**
 * Determines how the form has to be displayed.
 */
@Portable
public enum RenderMode {
    /**
     * Displays the form using the regular form inputs.
     */
    EDIT_MODE,

    /**
     * Displays the form using the regular widgets on read only mode.
     */
    READ_ONLY_MODE,

    /**
     * Displays the form showing the flat values.
     */
    PRETTY_MODE;
}
