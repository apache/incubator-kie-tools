/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.basicset.shape.def;

import org.kie.workbench.common.stunner.basicset.definition.BasicConnector;
import org.kie.workbench.common.stunner.core.client.shape.view.HasTitle;
import org.kie.workbench.common.stunner.core.definition.shape.GlyphDef;
import org.kie.workbench.common.stunner.shapes.def.ConnectorGlyphDef;
import org.kie.workbench.common.stunner.shapes.def.ConnectorShapeDef;

public final class BasicConnectorDefImpl
        implements ConnectorShapeDef<BasicConnector> {

    @Override
    public double getAlpha(final BasicConnector element) {
        return 1d;
    }

    @Override
    public String getBackgroundColor(final BasicConnector element) {
        return element.getBackgroundSet().getBgColor().getValue();
    }

    @Override
    public double getBackgroundAlpha(final BasicConnector element) {
        return 1;
    }

    @Override
    public String getBorderColor(final BasicConnector element) {
        return element.getBackgroundSet().getBorderColor().getValue();
    }

    @Override
    public double getBorderSize(final BasicConnector element) {
        return element.getBackgroundSet().getBorderSize().getValue();
    }

    @Override
    public double getBorderAlpha(final BasicConnector element) {
        return 1;
    }

    @Override
    public String getNamePropertyValue(final BasicConnector element) {
        return element.getName().getValue();
    }

    @Override
    public String getFontFamily(final BasicConnector element) {
        return null;
    }

    @Override
    public String getFontColor(final BasicConnector element) {
        return null;
    }

    @Override
    public String getFontBorderColor(final BasicConnector element) {
        return null;
    }

    @Override
    public double getFontSize(final BasicConnector element) {
        return 0;
    }

    @Override
    public double getFontBorderSize(final BasicConnector element) {
        return 0;
    }

    @Override
    public HasTitle.Position getFontPosition(final BasicConnector element) {
        return HasTitle.Position.BOTTOM;
    }

    @Override
    public double getFontRotation(final BasicConnector element) {
        return 0;
    }

    @Override
    public GlyphDef<BasicConnector> getGlyphDef() {
        return new BasicConnectorGlyphDef();
    }

    private static class BasicConnectorGlyphDef extends ConnectorGlyphDef<BasicConnector> {

        @Override
        public String getColor() {
            return "#000000";
        }

        @Override
        public String getGlyphDescription(final BasicConnector element) {
            return element.getTitle();
        }
    }
}
