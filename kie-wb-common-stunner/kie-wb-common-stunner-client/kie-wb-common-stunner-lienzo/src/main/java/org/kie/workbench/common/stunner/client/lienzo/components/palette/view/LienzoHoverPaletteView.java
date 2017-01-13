/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.client.lienzo.components.palette.view;

import javax.enterprise.context.Dependent;

import org.kie.workbench.common.stunner.client.lienzo.components.palette.view.element.LienzoPaletteElementView;
import org.kie.workbench.common.stunner.lienzo.palette.HoverPalette;

@Dependent
public class LienzoHoverPaletteView
        extends AbstractLienzoPaletteView<LienzoHoverPaletteView>
        implements LienzoPaletteView<LienzoHoverPaletteView, LienzoPaletteElementView> {

    @Override
    protected HoverPalette buildPalette() {
        return new HoverPalette().setTimeout(1500);
    }

    @Override
    protected void initPaletteCallbacks() {
        super.initPaletteCallbacks();
        if (null != getHoverPalette()) {
            getHoverPalette().setCloseCallback(() -> presenter.onClose());
        }
    }

    @Override
    public void destroy() {
        if (null != getHoverPalette()) {
            getHoverPalette().setCloseCallback(null);
        }
        super.destroy();
    }

    public void startTimeOut() {
        if (null != getHoverPalette()) {
            getHoverPalette().startTimeout();
        }
    }

    public void clearTimeOut() {
        if (null != getHoverPalette()) {
            getHoverPalette().stopTimeout();
        }
    }

    private HoverPalette getHoverPalette() {
        return null != getPalette() ? (HoverPalette) getPalette() : null;
    }
}
