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

package org.drools.completion;

import java.util.Map;

/**
 * Holder for the accumulate-function result-type map (function name → simple
 * result type), used by {@link LhsBindingResolver} and the inlay-hint helper to
 * resolve accumulate result bindings ({@code $count : count()}).
 *
 * <p>Seeded with the complete set of Drools' built-in accumulate functions. The
 * names and result types are transcribed from the Drools engine itself —
 * {@code META-INF/kie.default.properties.conf} (function name → impl class) and
 * each impl's {@code getResultType()}.
 * Baking them in avoids a runtime dependency on {@code drools-core};
 * the trade-off is they don't auto-track future Drools versions.
 *
 * <p>{@link #set} remains as an override hook (e.g. for tests, or a future
 * registry that loads the map from the Drools dependency directly); {@code null}
 * resets to the built-in defaults.
 */
public final class AccumulateFunctionTypes {

    /** Drools built-in accumulate functions → result type (simple name). */
    private static final Map<String, String> DEFAULTS = Map.ofEntries(
            Map.entry("max", "Comparable"),
            Map.entry("maxN", "Number"),
            Map.entry("maxI", "Integer"),
            Map.entry("maxL", "Long"),
            Map.entry("min", "Comparable"),
            Map.entry("minN", "Number"),
            Map.entry("minI", "Integer"),
            Map.entry("minL", "Long"),
            Map.entry("count", "Long"),
            Map.entry("collectList", "List"),
            Map.entry("collectSet", "Set"),
            Map.entry("average", "Double"),
            Map.entry("averageBD", "BigDecimal"),
            Map.entry("sum", "Double"),
            Map.entry("sumI", "Integer"),
            Map.entry("sumL", "Long"),
            Map.entry("sumBI", "BigInteger"),
            Map.entry("sumBD", "BigDecimal"),
            Map.entry("variance", "Double"),
            Map.entry("standardDeviation", "Double"));

    private static volatile Map<String, String> resultTypes = DEFAULTS;

    private AccumulateFunctionTypes() {
    }

    /** Installs the result-type map (copied defensively). {@code null} resets to defaults. */
    public static void set(Map<String, String> map) {
        resultTypes = (map == null) ? DEFAULTS : Map.copyOf(map);
    }

    /** The current function-name → result-type map (never null). */
    public static Map<String, String> get() {
        return resultTypes;
    }
}
