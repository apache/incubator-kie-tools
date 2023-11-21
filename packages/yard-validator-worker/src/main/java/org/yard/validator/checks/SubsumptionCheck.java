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

import org.yard.validator.Issue;
import org.yard.validator.key.Key;
import org.yard.validator.key.KeyParent;
import org.yard.validator.key.OperatorValueKey;

import java.util.Objects;
import java.util.Optional;

import static org.yard.model.Operators.*;

public class SubsumptionCheck
        implements Check {

    private final String hitPolicy;
    private final CheckItem checkItemA;
    private final CheckItem checkItemB;

    public SubsumptionCheck(
            final String hitPolicy,
            final CheckItem checkItemA,
            final CheckItem checkItemB) {
        this.hitPolicy = hitPolicy;
        this.checkItemA = checkItemA;
        this.checkItemB = checkItemB;
    }

    @Override
    public Optional<Issue> check() {
        final Optional<Issue> aToB = getIssue(checkItemA, checkItemB);

        if (isFirstHitPolicy()) {
            if (aToB.isPresent()) {
                // No need to check for redundancy. The first row masks the rest.
                return Optional.of(new Issue(
                        "Masking row. The higher row prevents the activation of the other row.",
                        checkItemA.getLocation(),
                        checkItemB.getLocation()));
            } else {
                // No need to check the other row for subsumption,
                // since subsumption is likely there by rule design
                return Optional.empty();
            }
        } else {
            final Optional<Issue> bToA = getIssue(checkItemB, checkItemA);

            // If the counterpart in the table is subsumptant, meaning the other item subsumes this, we have redundancy.
            if (aToB.isPresent() && bToA.isPresent()) {
                return Optional.of(new Issue(
                        getRedundancyMessage(),
                        checkItemA.getLocation(),
                        checkItemB.getLocation()));
            } else if (aToB.isPresent()) {
                return aToB;
            } else {
                return bToA;
            }
        }
    }

    private String getRedundancyMessage() {
        if (Objects.equals("UNIQUE", hitPolicy)) {
            return "Redundancy found. Unique hit policy fails when more than one row returns results.";
        } else {
            return "Redundancy found. If both rows return the same result, the other can be removed. If they return different results, the table fails to return a value.";
        }
    }

    private String getSubsumptionMessage() {
        if (Objects.equals("UNIQUE", hitPolicy)) {
            return "Subsumption found. Unique hit policy fails when more than one row returns results.";
        } else {
            return "Subsumption found. If both rows return the same result, the other can be removed. If they return different results, the table fails to return a value.";
        }
    }

    private boolean isFirstHitPolicy() {
        return Objects.equals("FIRST", hitPolicy)
                // PRIORITY can not be set since the column header does not support listing it.
                // For this reason it acts the same way as FIRST
                || Objects.equals("PRIORITY", hitPolicy);
    }

    private Optional<Issue> getIssue(
            final CheckItem checkItemA,
            final CheckItem checkItemB) {
        // All values and ranges in A are covered by B
        int coveredBIndex = 0;
        for (int aIndex = 0; aIndex < checkItemA.getKeys().length; aIndex++) {
            final Key key = checkItemA.getKeys()[aIndex];
            final KeyParent parent = key.getParent();

            for (int bIndex = coveredBIndex; bIndex < checkItemB.getKeys().length; bIndex++) {
                try {
                    final Key other = findCounterPartParent(checkItemB, parent, bIndex);
                    coveredBIndex = bIndex + 1;
                    if (!subsumes((OperatorValueKey) key, (OperatorValueKey) other)) {
                        return Optional.empty();
                    }
                    break;
                } catch (NotFoundException e) {
                    // All good for now, the other has "any" marked for this parent.
                }
            }
        }
        return Optional.of(
                new Issue(
                        getSubsumptionMessage(),
                        checkItemA.getLocation(),
                        checkItemB.getLocation()));
    }

    private Key findCounterPartParent(final CheckItem checkItem,
            final KeyParent parent,
            final int i) throws NotFoundException {
        if (checkItem.getKeys()[i].getParent().equals(parent)) {
            return checkItem.getKeys()[i];
        }
        throw new NotFoundException();
    }

    public boolean subsumes(final OperatorValueKey keyA,
            final OperatorValueKey keyB) {
        switch (keyA.getOperator()) {
            case NOT_EQUALS:
                switch (keyB.getOperator()) {
                    case NOT_EQUALS:
                        return valueIsEqualTo(keyA.getValue(), keyB.getValue());
                    case EQUALS:
                        boolean valueIsEqualTo = valueIsEqualTo(keyA.getValue(), keyB.getValue());
                        boolean covers = covers(keyA, keyB.getValue());
                        return !valueIsEqualTo && !covers;
                    default:
                        return false;
                }
            case EQUALS:
                switch (keyB.getOperator()) {
                    case NOT_EQUALS:
                        boolean valueIsEqualTo = valueIsEqualTo(keyA.getValue(), keyB.getValue());
                        boolean covers = covers(keyA, keyB.getValue());
                        return !valueIsEqualTo && !covers;
                    default:
                        return covers(keyA, keyB.getValue());
                }
            case GREATER_OR_EQUAL:
                switch (keyB.getOperator()) {
                    case GREATER_OR_EQUAL:
                    case GREATER_THAN:
                        return covers(keyA, keyB.getValue());
                    case NOT_EQUALS:
                        return valueIsGreaterThan(keyA, keyB.getValue());
                    default:
                        return false;
                }

            case LESS_OR_EQUAL:
                switch (keyB.getOperator()) {
                    case LESS_OR_EQUAL:
                    case LESS_THAN:
                        return covers(keyA, keyB.getValue());
                    case NOT_EQUALS:
                        return valueIsLessThan(keyA, keyB.getValue());
                    default:
                        return false;
                }
            case LESS_THAN:
                switch (keyB.getOperator()) {
                    case LESS_OR_EQUAL:
                        return covers(keyA, keyB.getValue());
                    case LESS_THAN:
                    case NOT_EQUALS:
                        return valueIsLessThanOrEqualTo(keyA, keyB.getValue());
                    default:
                        return false;
                }
            case GREATER_THAN:
                switch (keyB.getOperator()) {
                    case GREATER_OR_EQUAL:
                        return covers(keyA, keyB.getValue());
                    case GREATER_THAN:
                    case NOT_EQUALS:
                        return valueIsGreaterThanOrEqualTo(keyA, keyB.getValue());
                    default:
                        return false;
                }
            default:
                return false;
        }
    }

    protected boolean valueIsEqualTo(final Comparable valueA,
            final Comparable valueB) {
        if (valueA == null) {
            return valueB == null;
        } else {
            if (valueB == null) {
                return false;
            } else {
                return valueA.compareTo(valueB) == 0;
            }
        }
    }

    public boolean covers(final OperatorValueKey key,
            final Comparable otherValue) {
        switch (key.getOperator()) {
            case EQUALS:
                return valueIsEqualTo(key.getValue(), otherValue);
            case NOT_EQUALS:
                return !valueIsEqualTo(key.getValue(), otherValue);
            case GREATER_OR_EQUAL:
                return valueIsGreaterThanOrEqualTo(key, otherValue);
            case LESS_OR_EQUAL:
                return valueIsLessThanOrEqualTo(key, otherValue);
            case LESS_THAN:
                return valueIsLessThan(key, otherValue);
            case GREATER_THAN:
                return valueIsGreaterThan(key, otherValue);
            default:
                return false;
        }
    }

    protected boolean valueIsGreaterThanOrEqualTo(final OperatorValueKey key,
            final Comparable otherValue) {
        return valueIsEqualTo(key.getValue(), otherValue) || valueIsGreaterThan(key, otherValue);
    }

    protected boolean valueIsLessThanOrEqualTo(final OperatorValueKey key,
            final Comparable otherValue) {
        return valueIsEqualTo(key.getValue(), otherValue) || valueIsLessThan(key, otherValue);
    }

    protected boolean valueIsGreaterThan(final OperatorValueKey key,
            final Comparable otherValue) {
        return otherValue.compareTo(key.getValue()) > 0;
    }

    protected boolean valueIsLessThan(final OperatorValueKey key,
            final Comparable otherValue) {
        return otherValue.compareTo(key.getValue()) < 0;
    }

    private class NotFoundException extends Throwable {
    }
}
