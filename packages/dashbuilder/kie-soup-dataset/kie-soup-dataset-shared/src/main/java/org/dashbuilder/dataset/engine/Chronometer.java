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

package org.dashbuilder.dataset.engine;

/**
 * The interface <code>Chronometer</code> permits to measure the time elapsed between two time snapshots.
 */
public interface Chronometer {

    /**
     * Start the timer.
     */
    long start();

    /**
     * Stop the timer.
     */
    long stop();

    /**
     * Return the elapsed time measured in nanoseconds since the very start.
     * @return  long, the time.
     */
    long elapsedTime();

    /**
     * Return the time specified in human readable format.
     * @param millis The time to format in milliseconds.
     * @return Examples: <i>2d 3h 44m 2s<i>  or  <i>20 weeks 3h 3s</i>
     */
    String formatElapsedTime(long millis);
}
