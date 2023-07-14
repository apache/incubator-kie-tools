package org.gwtbootstrap3.extras.select.client.ui.constants;

/*
 * #%L
 * GwtBootstrap3
 * %%
 * Copyright (C) 2016 GwtBootstrap3
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
 * Select drop-down menu size.
 *
 * @author Xiaodong Sun
 */
public enum MenuSize {

    /**
     * Show as many items as the window will allow without being cut off.
     */
    AUTO("auto"),

    /**
     * Always show all items.
     */
    ALL("false"),

    ;

    private String value;

    private MenuSize(String size) {
        this.value = size;
    }

    /**
     * Returns the value of the drop-down menu size.
     *
     * @return
     */
    public String getValue() {
        return value;
    }

}
