/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.drools.completion.fixtures;

/**
 * Reflection fixture carrying statics alongside instance members, so the two
 * views can be shown not to leak into each other.
 */
public class Rounding {

    public static final int SCALE = 2;
    public static final String MODE = "HALF_EVEN";

    public int applied;
    private String label;

    public static int roundHalfUp(double value) {
        return (int) Math.round(value);
    }

    public static String describe(int scale, String mode) {
        return scale + mode;
    }

    public String getLabel() {
        return label;
    }
}
