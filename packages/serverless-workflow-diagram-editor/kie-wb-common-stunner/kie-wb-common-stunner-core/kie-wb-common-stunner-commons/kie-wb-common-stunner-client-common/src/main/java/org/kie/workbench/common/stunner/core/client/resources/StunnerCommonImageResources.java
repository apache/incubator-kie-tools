/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.client.resources;

import org.gwtproject.resources.client.ClientBundle;
import org.gwtproject.resources.client.ClientBundleWithLookup;
import org.gwtproject.resources.client.ImageResource;

public interface StunnerCommonImageResources extends ClientBundleWithLookup {

    StunnerCommonImageResources INSTANCE = new StunnerCommonImageResources_default_InlineClientBundleGenerator();

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
