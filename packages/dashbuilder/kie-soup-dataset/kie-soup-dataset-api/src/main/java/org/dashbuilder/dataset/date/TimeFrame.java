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
package org.dashbuilder.dataset.date;

/**
 * A time frame defines a time period between two time instants where these two instants depends on the current time.
 * <p>Some examples of time frame expressions are:
 * <ul>
 * <li>&quot;<i>now till 10second</i>&quot; => next 10 seconds</li>
 * <li>&quot;<i>begin[minute] till 10second</i>&quot; => first 10 seconds of current minute</li>
 * <li>&quot;<i>begin[minute] till now</i>&quot; => past seconds within current minute</li>
 * <li>&quot;<i>begin[minute] till 60seconds</i>&quot; => the current minute</li>
 * <li>&quot;<i>now till 1year</i>&quot; => 1 year since now</li>
 * <li>&quot;<i>now till end[year March] 1year</i>&quot; => this year + next (year starting on March)</li>
 * <li>&quot;<i>end[year March] till 1year</i>&quot; => next year</li>
 * <li>&quot;<i>begin[year March] -1year till now</i>&quot; => begin of last year till now</li>
 * <li>&quot;<i>end[year March] +2quarter till 1quarter</i>&quot; => 3rd quarter of next year</li>
 * <li>&quot;<i>begin[year March] -7day till begin[year March]</i>&quot; => Last week of last year</li>
 * </ul>
 * </p>
 */
public class TimeFrame {

    private TimeInstant from = null;
    private TimeInstant to = null;

    public TimeFrame() {
    }

    public TimeFrame(TimeInstant from, TimeInstant to) {
        this.from = from;
        this.to = to;
    }

    public TimeInstant getFrom() {
        return from;
    }

    public void setFrom(TimeInstant from) {
        this.from = from;
    }

    public TimeInstant getTo() {
        return to;
    }

    public void setTo(TimeInstant to) {
        this.to = to;
    }

    /**
     * Parses a time frame expression.
     *
     * @param timeFrameExpr A valid time instant expression (<i>see TimeFrame class javadoc</i>)
     * @return A TimeFrame instance
     * @throws IllegalArgumentException If the expression is not valid
     */
    public static TimeFrame parse(String timeFrameExpr) {
        if (timeFrameExpr == null || timeFrameExpr.length() == 0) {
            throw new IllegalArgumentException("Empty time frame expression");
        }
        String expr = timeFrameExpr.toLowerCase().trim();
        int sep = expr.indexOf("till");

        TimeFrame timeFrame = new TimeFrame();
        if (sep == -1) {
            TimeInstant instant = TimeInstant.parse(expr);
            TimeInstant now = TimeInstant.now();
            if (instant.getTimeInstant().equals(now.getTimeInstant())) {
                throw new IllegalArgumentException("Time frame limits are equals: " + instant);
            }
            if (instant.getTimeInstant().before(now.getTimeInstant())) {
                timeFrame.setFrom(instant);
                timeFrame.setTo(now);
            } else {
                timeFrame.setFrom(now);
                timeFrame.setTo(instant);
            }
        } else {
            String fromExpr = expr.substring(0, sep);
            String toExpr = expr.substring(sep + 4);
            TimeInstant from = TimeInstant.parse(fromExpr);
            TimeInstant to = TimeInstant.parse(toExpr);
            timeFrame.setFrom(from);
            timeFrame.setTo(to);

            // Process relative time instants
            if (to.getTimeMode() == null) {
                to.setStartTime(from.getTimeInstant());
            }
        }
        return timeFrame;
    }

    public String toString() {
        StringBuilder out = new StringBuilder();
        if (from != null && to != null) {
            out.append(from.toString());
            out.append(" till ");
            out.append(to.toString());
        }
        else if (from != null) {
            out.append(from.toString());
        }
        else if (to != null) {
            out.append(to.toString());
        }
        return out.toString();
    }

}