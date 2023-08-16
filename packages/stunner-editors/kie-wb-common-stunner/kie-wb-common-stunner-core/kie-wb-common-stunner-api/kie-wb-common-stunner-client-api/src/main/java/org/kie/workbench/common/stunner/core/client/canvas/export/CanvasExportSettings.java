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


package org.kie.workbench.common.stunner.core.client.canvas.export;

import java.util.OptionalInt;

public class CanvasExportSettings {

    private final OptionalInt wide;
    private final OptionalInt high;

    public static CanvasExportSettings build() {
        return new CanvasExportSettings(OptionalInt.empty(),
                                        OptionalInt.empty());
    }

    public static CanvasExportSettings build(final int wide,
                                             final int high) {
        return new CanvasExportSettings(OptionalInt.of(wide),
                                        OptionalInt.of(high));
    }

    protected CanvasExportSettings(final OptionalInt wide,
                                   final OptionalInt high) {
        this.wide = wide;
        this.high = high;
    }

    public boolean hasSize() {
        return wide.isPresent() && high.isPresent();
    }

    public int getWide() {
        return wide.orElse(0);
    }

    public int getHigh() {
        return high.orElse(0);
    }
}
