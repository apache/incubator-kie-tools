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

package org.kie.workbench.common.stunner.basicset.shape.def.icon.dynamics;

import org.kie.workbench.common.stunner.basicset.definition.icon.dynamics.DynamicIcon;
import org.kie.workbench.common.stunner.core.definition.shape.AbstractShapeDef;
import org.kie.workbench.common.stunner.shapes.def.icon.dynamics.IconShapeDef;

public abstract class AbstractDynamicIconShapeDef<I extends DynamicIcon>
        extends AbstractShapeDef<I>
        implements IconShapeDef<I> {

    @Override
    public double getWidth( final I element ) {
        return element.getWidth().getValue();
    }

    @Override
    public double getHeight( final I element ) {
        return element.getHeight().getValue();
    }

    @Override
    public String getBackgroundColor( final I element ) {
        return element.getBackgroundSet().getBgColor().getValue();
    }

    @Override
    public String getBorderColor( final I element ) {
        return element.getBackgroundSet().getBorderColor().getValue();
    }

    @Override
    public double getBorderSize( final I element ) {
        return element.getBackgroundSet().getBorderSize().getValue();
    }

    @Override
    public double getBackgroundAlpha( final I element ) {
        return 1;
    }

    @Override
    public double getBorderAlpha( final I element ) {
        return 1;
    }
}
