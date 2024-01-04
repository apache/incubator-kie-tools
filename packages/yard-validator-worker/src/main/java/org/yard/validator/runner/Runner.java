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
package org.yard.validator.runner;

import org.yard.validator.Issue;
import org.yard.validator.ReportBus;
import org.yard.validator.checks.Check;
import org.yard.validator.util.Logger;

import java.util.List;
import java.util.Optional;

public class Runner {

    private ReportBus reportBus;

    public Runner(ReportBus reportBus) {
        this.reportBus = reportBus;
    }

    public void run(final List<Check> checks) {
        try {

            for (final Check check : checks) {
                final Optional<Issue> issue = check.check();
                issue.ifPresent(value -> reportBus.report(value));
            }
        } catch (final Exception e) {
            Logger.log("Failed to run checks: " + e.getMessage());
        }
    }
}
