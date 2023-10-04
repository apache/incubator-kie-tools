package org.gwtbootstrap3.client.ui.base.helper;

/*
 * #%L
 * GwtBootstrap3
 * %%
 * Copyright (C) 2013 GwtBootstrap3
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
 * Helper methods regarding CSS styling of UIObjects.
 *
 * @author Sven Jacobs
 * @author Joshua Godi
 */
public final class StyleHelper {

    /**
     * Returns {@code true} if specified style is contained in space-separated list of styles
     *
     * @param styleNames Space-separated list of styles
     * @param style      Style to look for
     * @return True if contains style
     */
    public static boolean containsStyle(final String styleNames,
                                        final String style) {

        if (styleNames == null || style == null) {
            return false;
        }

        final String[] styles = styleNames.split("\\s");

        for (final String s : styles) {
            if (style.equals(s)) {
                return true;
            }
        }

        return false;
    }

    private StyleHelper() {
    }
}
