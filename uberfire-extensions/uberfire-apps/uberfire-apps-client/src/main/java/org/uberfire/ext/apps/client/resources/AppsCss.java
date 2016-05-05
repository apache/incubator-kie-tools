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

package org.uberfire.ext.apps.client.resources;

import com.google.gwt.resources.client.CssResource;

public interface AppsCss extends CssResource {

    @ClassName("tile")
    String tile();

    @ClassName( "iconPanel" )
    String iconPanel();

    @ClassName( "deleteIcon" )
    String deleteIcon();

    @ClassName( "deletePanel" )
    String deletePanel();

    @ClassName("blueTile")
    String blueTile();

    @ClassName("redTile")
    String redTile();

    @ClassName("greenTile")
    String greenTile();

    @ClassName("tileContent")
    String tileContent();

    @ClassName("tileIcon")
    String tileIcon();

    @ClassName("tileStatus")
    String tileStatus();

    @ClassName("tileName")
    String tileName();

    @ClassName("breadCrumbs")
    String breadCrumbs();


}
