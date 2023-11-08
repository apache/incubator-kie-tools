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

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Default;
import org.kie.workbench.common.stunner.core.client.shape.ImageStrip;
import org.treblereel.j2cl.processors.common.resources.ImageResource;

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
            public String getCssResource() {
                return StunnerCommonImageResources.INSTANCE.commonIconsSpriteCss().getText();
            }

            @Override
            public String getClassName() {
                return StunnerCommonImageResources.INSTANCE.commonIconsSpriteCss().getName();
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
