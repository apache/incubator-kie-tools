package org.gwtbootstrap3.extras.notify.client.constants;

import org.gwtbootstrap3.client.ui.constants.Type;

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

/**
 * Enumeration of possible Notify's position to the container element.
 *
 * @author Xiaodong SUN
 */
public enum NotifyPosition implements Type {

    STATIC("static"),
    FIXED("fixed"),
    RELATIVE("relative"),
    ABSOLUTE("absolute"),
    ;

    private final String position;

    private NotifyPosition(final String position) {
        this.position = position;
    }

    /**
     * Returns the string representation of position.
     *
     * @return the string representation of position
     */
    public String getPosition() {
        return position;
    }

}
