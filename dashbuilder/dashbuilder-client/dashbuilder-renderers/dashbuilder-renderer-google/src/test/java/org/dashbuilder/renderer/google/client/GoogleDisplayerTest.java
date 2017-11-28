/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dashbuilder.renderer.google.client;

import org.dashbuilder.common.client.widgets.FilterLabelSet;
import org.dashbuilder.displayer.DisplayerSettings;
import org.dashbuilder.displayer.client.AbstractDisplayerTest;

import static org.mockito.Mockito.*;

public abstract class GoogleDisplayerTest extends AbstractDisplayerTest {

    public GoogleBarChartDisplayer createBarChartDisplayer(DisplayerSettings settings) {
        return initDisplayer(new GoogleBarChartDisplayer(mock(GoogleBarChartDisplayer.View.class), mock(FilterLabelSet.class)), settings);
    }

    public GoogleTableDisplayer createTableDisplayer(DisplayerSettings settings) {
        return initDisplayer(new GoogleTableDisplayer(mock(GoogleTableDisplayer.View.class), mock(FilterLabelSet.class)), settings);
    }
}