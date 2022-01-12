/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.forms.commons.rendering.shared.util.masks;

/**
 * Defines a fragment of a Mask which has been processed on a {@link MaskInterpreter}
 */
public interface MaskSection {

    /**
     * Returns the section type
     */
    MaskSectionType getType();

    /**
     * Returns the content of the section, it can be a propertyName or a text depending on the type
     */
    String getText();
}
