/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.client.shape.common;

import java.util.Arrays;

import org.kie.workbench.common.stunner.core.util.HashUtil;

public class DashArray {

    private double dash;
    private double dashes[];

    public DashArray(final double dash,
                     final double dashes[]) {
        this.dash = dash;
        this.dashes = dashes;
    }

    public static DashArray create(final double dash,
                                   final double... dashes) {
        return new DashArray(dash,
                             dashes);
    }

    public double getDash() {
        return dash;
    }

    public void setDash(double dash) {
        this.dash = dash;
    }

    public double[] getDashes() {
        return dashes;
    }

    public void setDashes(double[] dashes) {
        this.dashes = dashes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DashArray other = (DashArray) o;
        return Double.compare(dash,
                              other.dash) == 0 && Arrays.equals(dashes,
                                                                other.dashes);
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(Double.hashCode(dash),
                                         Arrays.hashCode(dashes));
    }
}
