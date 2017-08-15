/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.client.workbench.widgets.menu.megamenu.brand;

/**
 * Implement this to define some customizations on the Mega Menu header.
 */
public interface MegaMenuBrand {

    /**
     * Defines the image to be shown on the left side of the mega menu header.
     * If not provided, none will be used.
     * @return URL to the logo image. Null or empty value if none should be used.
     */
    String brandImageUrl();

    /**
     * Defines the text shown when the mouse goes over the logo image.
     * @return Text to be shown. Null or empty value if none should be used.
     */
    String brandImageLabel();

    /**
     * Defines the label of the dropdown button that shows the mega menu.
     * If not provided, "Menu" will be used.
     * @return Label of the dropdown button. Null or empty value if the default value
     * should be used.
     */
    String menuAccessorLabel();
}
