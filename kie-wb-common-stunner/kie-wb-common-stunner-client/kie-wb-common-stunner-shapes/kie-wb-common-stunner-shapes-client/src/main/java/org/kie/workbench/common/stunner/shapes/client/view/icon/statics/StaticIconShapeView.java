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

package org.kie.workbench.common.stunner.shapes.client.view.icon.statics;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.wires.WiresLayoutContainer;
import com.ait.lienzo.client.core.types.BoundingBox;
import org.kie.workbench.common.stunner.shapes.client.view.BasicShapeView;
import org.kie.workbench.common.stunner.shapes.def.icon.statics.Icons;

public class StaticIconShapeView<T extends StaticIconShapeView>
        extends BasicShapeView<T> {

    private Icons icon;
    private double width;
    private double height;
    private Group iconGroup;

    public StaticIconShapeView( final Icons icon ) {
        super( new MultiPath()
                .setStrokeWidth( 0 )
                .setStrokeAlpha( 0 ) );
        this.setIcon( icon );

    }

    public T setIcon( final Icons icon ) {
        this.icon = icon;
        this.buildIconView();
        return ( T ) this;
    }

    private void buildIconView() {
        getPath().clear();
        if ( null != iconGroup ) {
            this.removeChild( iconGroup );
        }
        iconGroup = StaticIconsBuilder.build( icon );
        if ( null != iconGroup ) {
            final BoundingBox bb = iconGroup.getBoundingBox();
            final double w = bb.getWidth();
            final double h = bb.getHeight();
            this.width = w;
            this.height = h;
            getPath().rect( 0, 0, w, h );
            this.addChild( iconGroup, WiresLayoutContainer.Layout.CENTER );

        }
        refresh();

    }

    @Override
    protected void doDestroy() {
        super.doDestroy();
        if ( null != iconGroup ) {
            iconGroup.removeFromParent();
            iconGroup = null;
        }
        icon = null;

    }

}
