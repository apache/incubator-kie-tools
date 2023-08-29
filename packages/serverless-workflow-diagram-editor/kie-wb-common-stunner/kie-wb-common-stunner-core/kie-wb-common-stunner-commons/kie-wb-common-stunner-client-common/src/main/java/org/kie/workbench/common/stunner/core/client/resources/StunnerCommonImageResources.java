/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.stunner.core.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundleWithLookup;
import com.google.gwt.resources.client.ImageResource;

public interface StunnerCommonImageResources extends ClientBundleWithLookup {

    StunnerCommonImageResources INSTANCE = GWT.create(StunnerCommonImageResources.class);

    @Source("images/edit.png")
    ImageResource edit();

    @Source("images/delete.png")
    ImageResource delete();

    @Source("images/gears.png")
    ImageResource gears();

    @Source("images/form.png")
    ImageResource form();

    @Source("images/drd.png")
    ImageResource drd();

    // Sprite - png 16x16px, 5px, vertical
    @Source("images/common-icons-sprite.png")
    ImageResource commonIconsSprite();

    @ClientBundle.Source("images/common-icons-sprite.css")
    StunnerCommonCssResource commonIconsSpriteCss();
}
