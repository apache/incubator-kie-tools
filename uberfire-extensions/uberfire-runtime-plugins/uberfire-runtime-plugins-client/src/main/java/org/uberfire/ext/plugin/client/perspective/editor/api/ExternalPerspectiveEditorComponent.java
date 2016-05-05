/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

package org.uberfire.ext.plugin.client.perspective.editor.api;

import java.util.Map;

import com.google.gwt.user.client.ui.IsWidget;
import org.gwtbootstrap3.client.ui.Modal;

/**
 * An interface for implementing custom Perspective Editor's component. Any components implementing this interface
 * will be added to the Perspective Editor's widget palette.
 *
 * <p>Components must provide a Workbench place name plus a widget for editing and configuring the place request
 * parameters. The Perspective Editor will persist the place request created by this component along with the
 * perspective definition. During perspective rendering, the workbench will process every place request found within
 * the perspective.</p>
 */
public interface ExternalPerspectiveEditorComponent {

    /**
     * Initialize the editor component from an existing place request definition.
     * @param placeName The place name
     * @param parameters The place parameters
     */
    void setup( final String placeName,
                final Map<String, String> parameters );

    /**
     * The name of the Workbench Place tied to this external component.
     * @return An existing place name.
     */
    String getPlaceName();

    /**
     * The place request parameters.
     */
    Map<String,String> getParametersMap();

    /**
     * The widget in charge of editing the place request parameters.
     * <p>It will be added and displayed inside the perspective editor popup panel.</p>
     */
    IsWidget getConfig();

    /**
     * Provides a quick preview representation of the Place being configured.
     * @param parameters The Place request parameters.
     */
    IsWidget getPreview( final Map<String, String> parameters );

    /**
     * Change the popup widget settings to fit the component editor needs. Basically,
     * the common use of this method is to set the modal's width & height.
     */
    void modalSettings( Modal popup );
}
