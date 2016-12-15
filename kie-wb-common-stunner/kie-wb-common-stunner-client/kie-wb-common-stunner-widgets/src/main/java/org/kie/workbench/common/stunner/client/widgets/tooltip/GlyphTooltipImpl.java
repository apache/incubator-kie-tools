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

package org.kie.workbench.common.stunner.client.widgets.tooltip;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.stunner.core.client.components.glyph.GlyphTooltip;
import org.kie.workbench.common.stunner.core.client.shape.view.glyph.Glyph;
import org.uberfire.client.mvp.UberView;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
public class GlyphTooltipImpl implements GlyphTooltip<Group>, IsWidget {

    public interface View extends UberView<GlyphTooltipImpl> {

        View show( String text, double x, double y, Direction direction );

        View show( IPrimitive<?> glyph, String text, double x, double y, double width, double height, Direction direction );

        View hide();

        View remove();

    }

    View view;

    @Inject
    public GlyphTooltipImpl( final View view ) {
        this.view = view;
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    @PostConstruct
    public void init() {
        view.init( this );
    }

    @PreDestroy
    public void finishHim() {
        remove();
    }

    @Override
    public void show( final String text,
                      final double x,
                      final double y,
                      final Direction direction ) {
        view.show( text, x, y, direction );
    }

    @Override
    public void show( final Glyph<Group> glyph, final String text, final double x, final double y, final Direction direction ) {
        view.show( glyph.getGroup(), text, x, y, glyph.getWidth(), glyph.getHeight(), direction );
    }

    @Override
    public void hide() {
        view.hide();
    }

    @Override
    public void remove() {
        view.remove();
    }

}
