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
package org.dashbuilder.dataset.client.engine;

import javax.enterprise.context.Dependent;

import com.google.gwt.core.client.Duration;
import org.dashbuilder.dataset.client.resources.i18n.CommonConstants;
import org.dashbuilder.dataset.engine.Chronometer;

@Dependent
public final class ClientChronometer implements Chronometer {

    protected Double startTime;
    protected Double stopTime;

    public long start() {
        stopTime = null;
        startTime = Duration.currentTimeMillis();
        return startTime.longValue() * 1000000;
    }

    public long stop() {
        stopTime = Duration.currentTimeMillis();
        return stopTime.longValue() * 1000000;
    }

    public long elapsedTime() {
        long stop = stopTime != null ? stopTime.longValue() : System.currentTimeMillis();
        return (stop - startTime.longValue()) * 1000000;
    }

    public String formatElapsedTime(long millis) {
        long milliseconds = millis;
        long seconds = milliseconds / 1000; milliseconds %= 1000;
        long minutes = seconds / 60; seconds %= 60;
        long hours = minutes / 60; minutes %= 60;
        long days = hours / 24; hours %= 24;
        long weeks = days / 7; days %= 7;
        double secondsd = (double) (seconds * 1000 + milliseconds) / 1000;

        StringBuilder buf = new StringBuilder();
        if (weeks > 0) buf.append(weeks).append(" ").append( CommonConstants.INSTANCE.weeks()).append(" ");
        if (days > 0) buf.append(days).append("d ");
        if (hours > 0) buf.append(hours).append("h ");
        if (minutes > 0) buf.append(minutes).append("m ");
        if (secondsd > 0) buf.append(secondsd).append("s");
        if (buf.length() == 0) return "0s";
        return buf.toString();
    }
}
