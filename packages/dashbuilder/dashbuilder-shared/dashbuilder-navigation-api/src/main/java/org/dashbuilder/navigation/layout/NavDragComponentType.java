/*
 * Copyright 2017 JBoss, by Red Hat, Inc
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
package org.dashbuilder.navigation.layout;

public enum NavDragComponentType {

    CAROUSEL("org.dashbuilder.client.navigation.layout.editor.NavCarouselDragComponent"),
    MENUBAR("org.dashbuilder.client.navigation.layout.editor.NavMenuBarDragComponent"),
    TABLIST("org.dashbuilder.client.navigation.layout.editor.NavTabListDragComponent"),
    TREE("org.dashbuilder.client.navigation.layout.editor.NavTreeDragComponent"),
    TILES("org.dashbuilder.client.navigation.layout.editor.NavTilesDragComponent");

    private String className;

    NavDragComponentType(String className) {
        this.className = className;
    }

    public String getFQClassName() {
        return className;
    }

    public static NavDragComponentType getByClassName(String className) {
        for (NavDragComponentType type : values()) {
            if (type.className.equals(className)) {
                return type;
            }
        }
        return null;
    }
}
