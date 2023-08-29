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

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Default;

import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import org.kie.workbench.common.stunner.core.client.shape.ImageStrip;

@Default
@ApplicationScoped
public class StunnerCommonIconsStrip implements ImageStrip {

    @Override
    public ImageResource getImage() {
        return StunnerCommonImageResources.INSTANCE.commonIconsSprite();
    }

    @Override
    public StripCssResource getCss() {
        return new StripCssResource() {
            @Override
            public CssResource getCssResource() {
                return StunnerCommonImageResources.INSTANCE.commonIconsSpriteCss();
            }

            @Override
            public String getClassName() {
                return StunnerCommonImageResources.INSTANCE.commonIconsSpriteCss().commonIconsSpriteClass();
            }
        };
    }

    @Override
    public int getWide() {
        return 16;
    }

    @Override
    public int getHigh() {
        return 16;
    }

    @Override
    public int getPadding() {
        return 5;
    }

    @Override
    public Orientation getOrientation() {
        return Orientation.VERTICAL;
    }
}
