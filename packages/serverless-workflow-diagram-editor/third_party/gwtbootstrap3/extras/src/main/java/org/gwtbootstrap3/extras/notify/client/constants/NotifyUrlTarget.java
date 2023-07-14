package org.gwtbootstrap3.extras.notify.client.constants;

/*
 * #%L
 * GwtBootstrap3
 * %%
 * Copyright (C) 2013 - 2015 GwtBootstrap3
 * %%
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
 * #L%
 */

import org.gwtbootstrap3.client.ui.constants.Type;

/**
 * Enumeration of Notify's URL target types.
 *
 * @author Xiaodong SUN
 */
public enum NotifyUrlTarget implements Type {

    BLANK("_blank"),
    SELF("_self"),
    PARENT("_parent"),
    TOP("_top"),
    ;

    private final String target;

    private NotifyUrlTarget(final String target) {
        this.target = target;
    }

    /**
     * Returns the string representation of URL target.
     *
     * @return the string representation of URL target
     */
    public String getTarget() {
        return target;
    }

}
