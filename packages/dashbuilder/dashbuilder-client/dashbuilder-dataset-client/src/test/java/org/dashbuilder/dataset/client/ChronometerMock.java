/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dashbuilder.dataset.client;

import org.dashbuilder.dataset.engine.Chronometer;

public class ChronometerMock implements Chronometer {

    @Override
    public long start() {
        return 0;
    }

    @Override
    public long stop() {
        return 0;
    }

    @Override
    public long elapsedTime() {
        return 0;
    }

    @Override
    public String formatElapsedTime(long millis) {
        return String.valueOf(millis);
    }
}
