/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.widgets.decoratedgrid.client.widget;

import static org.kie.workbench.common.widgets.decoratedgrid.client.resources.GridResources.*;

/**
 * Class to calculate the height of a cell in a VerticalMergedGrid for Safari.
 */
public class CellHeightCalculatorImplSafari extends CellHeightCalculatorImpl {

    public int calculateHeight( int rowSpan ) {
        int divHeight = INSTANCE.style().rowHeight() * rowSpan + ( ( rowSpan - 1 ) * INSTANCE.style().borderWidth() );
        return divHeight;
    }

}
