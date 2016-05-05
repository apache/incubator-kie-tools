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
package org.uberfire.ext.layout.editor.client;

import java.util.List;

import com.google.gwt.user.client.ui.Widget;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;
import org.uberfire.ext.layout.editor.client.components.LayoutDragComponent;
import org.uberfire.ext.layout.editor.client.components.LayoutDragComponentGroup;

public interface LayoutEditor {

    void init(String layoutName, List<LayoutDragComponent> layoutDragComponents);

    Widget asWidget();

    void loadLayout(LayoutTemplate layoutTemplate);

    void loadDefaultLayout(String layoutName);

    LayoutTemplate getLayout();

    void addLayoutProperty(String key, String value);

    String getLayoutProperty(String key);

    void addDraggableComponentGroup( LayoutDragComponentGroup group );

    void addDraggableComponentToGroup( String groupId, String componentId, LayoutDragComponent component );

    void removeDraggableComponentGroup ( String groupId );

    void removeDraggableGroupComponent (String groupId, String componentId);
}
