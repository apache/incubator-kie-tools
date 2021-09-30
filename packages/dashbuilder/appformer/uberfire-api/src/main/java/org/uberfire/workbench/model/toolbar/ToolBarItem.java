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
package org.uberfire.workbench.model.toolbar;

import jsinterop.annotations.JsType;
import org.uberfire.mvp.Command;
import org.uberfire.security.authz.RuntimeFeatureResource;

/**
 * Meta-data for a Workbench Tool Bar Item including permissions. The default is
 * that all users have permission to access a Tool BarItem Item and that it is
 * enabled.
 */
@JsType
public interface ToolBarItem
        extends RuntimeFeatureResource {

    /**
     * @return the caption
     */
    String getTooltip();

    /**
     * @return the toolbar icon information
     */
    ToolBarIcon getIcon();

    /**
     * @return is the Tool Bar Item enabled
     */
    boolean isEnabled();

    /**
     * @param enabled the enabled to set
     */
    void setEnabled(boolean enabled);

    /**
     * @return The command associated with the Tool Bar Item
     */
    Command getCommand();
}
