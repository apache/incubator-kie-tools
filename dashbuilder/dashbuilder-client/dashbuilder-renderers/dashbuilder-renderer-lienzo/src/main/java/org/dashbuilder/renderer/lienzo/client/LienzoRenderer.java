/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.renderer.lienzo.client;

import org.dashbuilder.displayer.DisplayerSettings;
import org.dashbuilder.displayer.DisplayerSubType;
import org.dashbuilder.displayer.DisplayerType;
import org.dashbuilder.displayer.client.AbstractRendererLibrary;
import org.dashbuilder.displayer.client.Displayer;

import javax.enterprise.context.ApplicationScoped;
import java.util.Arrays;
import java.util.List;

import static org.dashbuilder.displayer.DisplayerSubType.*;
import static org.dashbuilder.displayer.DisplayerType.*;

/**
 * Lienzo renderer.
 */
@ApplicationScoped
public class LienzoRenderer extends AbstractRendererLibrary {

    public static final String UUID = "lienzo";

    @Override
    public String getUUID() {
        return UUID;
    }

    @Override
    public String getName() {
        return "Lienzo";
    }

    @Override
    public List<DisplayerType> getSupportedTypes() {
        return Arrays.asList(
                BARCHART,
                PIECHART,
                LINECHART);
    }

    @Override
    public List<DisplayerSubType> getSupportedSubtypes(DisplayerType displayerType) {
        switch (displayerType) {
            case BARCHART:
                return Arrays.asList(BAR, COLUMN);
            case PIECHART:
                return Arrays.asList(PIE);
            case LINECHART:
                return Arrays.asList(LINE);
            default:
                return Arrays.asList();
        }
    }

    @Override
    public Displayer lookupDisplayer(DisplayerSettings displayerSettings) {
        DisplayerType type = displayerSettings.getType();
        if (BARCHART.equals(type)) {
            return new LienzoBarChartDisplayer();
        }
        if (PIECHART.equals(type)) {
            return new LienzoPieChartDisplayer();
        }
        if (LINECHART.equals(type)) {
            return new LienzoLineChartDisplayer();
        }
        return null;
    }
}
