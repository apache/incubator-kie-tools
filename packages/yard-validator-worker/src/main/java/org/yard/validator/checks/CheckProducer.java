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
package org.yard.validator.checks;

import org.yard.validator.CustomTreeSet;
import org.yard.validator.ParserResult;
import org.yard.validator.key.ColumnKey;
import org.yard.validator.key.Location;
import org.yard.validator.key.RowLocation;
import org.yard.validator.util.Logger;

import java.util.*;
import java.util.stream.Collectors;

public class CheckProducer {

    public static List<Check> getChecks(final ParserResult parse) {
        final List<Check> result = new ArrayList<>();
        try {
            final Set<ColumnKey> bundleKeys = formBundleKeys(parse.getResult());
            final Map<Integer, RunBundle> bundles = formBundles(parse.getResult(), bundleKeys);

            for (final RunBundle value : bundles.values()) {
                result.addAll(getCheckList(parse.getHitPolicy(), value.getMap()));
            }

        } catch (final Exception e) {
            Logger.log("Failed to create checks: " + e.getMessage());
        }
        return result;
    }

    private static Set<ColumnKey> formBundleKeys(final Map<RowLocation, CustomTreeSet> parse) {
        final Set<ColumnKey> result = new HashSet<>();
        for (final CustomTreeSet value : parse.values()) {
            if (value != null) {
                if (result.isEmpty()) {
                    result.addAll(value.getEqualColumns());
                } else {
                    final Set<ColumnKey> removeKeys = result.stream().filter(x -> !value.getEqualColumns().contains(x)).collect(Collectors.toSet());
                    result.removeAll(removeKeys);
                }
            }
        }
        return result;
    }

    private static Map<Integer, RunBundle> formBundles(final Map<RowLocation, CustomTreeSet> parse,
            final Set<ColumnKey> bundleKeys) {
        final Map<Integer, RunBundle> result = new HashMap<>();
        for (Map.Entry<RowLocation, CustomTreeSet> entry : parse.entrySet()) {
            int hash = entry.getValue().getHash(bundleKeys);
            if (!result.containsKey(hash)) {
                result.put(hash, new RunBundle());
            }
            result.get(hash).put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    private static List<Check> getCheckList(
            final String hitPolicy,
            final Map<RowLocation, CustomTreeSet> parse) {
        final List<Check> result = new ArrayList<>();

        // Dual row
        int locationIndex = 0;
        final RowLocation[] locations = new RowLocation[parse.keySet().size()];
        for (RowLocation location : parse.keySet()) {
            locations[locationIndex++] = location;
        }

        for (int i = 0; i < parse.entrySet().size(); i++) {
            for (int j = i; j < parse.entrySet().size(); j++) {
                if (i == j) {
                    continue;
                }
                final RowLocation locationA = getHigher(locations, i, j);
                final RowLocation locationB = getLower(locations, i, j);

                final CheckItem checkItemA = new CheckItem(
                        locationA.getTableRowNumber(),
                        locationA,
                        parse.get(locationA));
                final CheckItem checkItemB = new CheckItem(
                        locationB.getTableRowNumber(),
                        locationB,
                        parse.get(locationB));

                result.add(new SubsumptionCheck(
                        hitPolicy,
                        checkItemA,
                        checkItemB));
            }
        }
        return result;
    }

    private static RowLocation getHigher(
            final RowLocation[] locations,
            final int i,
            final int j) {
        if (locations[i].getTableRowNumber() > locations[j].getTableRowNumber()) {
            return locations[i];
        } else {
            return locations[j];
        }
    }

    private static RowLocation getLower(
            final RowLocation[] locations,
            final int i,
            final int j) {
        if (locations[i].getTableRowNumber() < locations[j].getTableRowNumber()) {
            return locations[i];
        } else {
            return locations[j];
        }
    }
}
