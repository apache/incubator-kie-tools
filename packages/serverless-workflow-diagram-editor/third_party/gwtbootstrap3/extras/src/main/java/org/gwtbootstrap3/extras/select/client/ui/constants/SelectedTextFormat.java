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
 * Selected text format.
 *
 * @author Xiaodong Sun
 */
public enum SelectedTextFormat {

    /**
     * A comma delimited list of selected values (default)
     */
    VALUES("values"),

    /**
     * Always show the select title (placeholder), regardless of selection
     */
    STATIC("static"),

    /**
     * If one item is selected, then the option value is shown. If more than
     * one is selected then the number of selected items is displayed, e.g.
     * <code># items selected</code>
     */
    COUNT("count"),
    ;

    private String format;

    private SelectedTextFormat(String format) {
        this.format = format;
    }

    /**
     * Returns the basic format.
     *
     * @return
     */
    public String getFormat() {
        return format;
    }

    /**
     * Returns <code>count > x</code> if the format is
     * {@link SelectedTextFormat#COUNT}, or the basic format otherwise.
     *
     * @param minCount
     * @return
     */
    public String getFormat(int minCount) {
        return getFormat() + (this == COUNT ? " > " + minCount : "");
    }

}
