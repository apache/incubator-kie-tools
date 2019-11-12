/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.workbench.client.error;

public enum TimeAmount {
    TEN_MINUTES(10 * 60 * 1000L),
    THIRTY_MINUTES(30 * 60 * 1000L),
    ONE_HOUR(60 * 60 * 1000L),
    TWO_HOURS(2 * 60 * 60 * 1000L);

    private long timeAmountInMS;

    TimeAmount(long timeAmountInMS) {
        this.timeAmountInMS = timeAmountInMS;
    }

    public long getTimeAmount() {
        return timeAmountInMS;
    }
}