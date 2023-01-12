/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.sw.client.shapes;

import com.ait.lienzo.client.core.shape.MultiPath;

public class EndShapeView extends ServerlessWorkflowBasicShape<EndShapeView> {

    private final static int END_SHAPE_SIZE = 46;

    public EndShapeView() {
        super(new MultiPath()
                      .rect(0, 0, END_SHAPE_SIZE, END_SHAPE_SIZE)
                      .setID("end"),
              "End");
        setTitleBoundaries(END_SHAPE_SIZE, END_SHAPE_SIZE);
        setTitleXOffsetPosition(0.0); // Looks like a bug in horizontal aligning without OffsetPosition 0
    }
}
