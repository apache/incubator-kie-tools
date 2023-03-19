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
package org.dashbuilder.dataset.engine.index.stats;


public class MemSizeFormatter {

    public static final String SIZE_UNITS[] = new String[] {"bytes", "Kb", "Mb", "Gb", "Tb", "Pb"};

    public static String formatSize(long bytes) {
        for (int exp=SIZE_UNITS.length-1; exp>=0; exp--) {
            String sizeUnit = SIZE_UNITS[exp];
            double size = bytes / Math.pow(1024, exp);
            if (((long) size) > 0) {
                return size + " " + sizeUnit;
            }
        }
        return bytes + " bytes";
    }

}
