/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
package org.guvnor.common.services.backend.test;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.guvnor.common.services.shared.test.TestResultMessage;
import org.guvnor.common.services.shared.test.TestRunnerService;
import org.guvnor.common.services.shared.test.TestService;
import org.uberfire.backend.vfs.Path;

@ApplicationScoped
public class TestRunnerServiceImpl
        implements TestRunnerService {

    private Event<TestResultMessage> defaultTestResultMessageEvent;

    private Instance<TestService> testServices;

    public TestRunnerServiceImpl() {
    }

    @Inject
    public TestRunnerServiceImpl(final @Any Instance<TestService> testServices,
                                 final Event<TestResultMessage> defaultTestResultMessageEvent) {
        this.testServices = testServices;
        this.defaultTestResultMessageEvent = defaultTestResultMessageEvent;
    }

    @Override
    public void runAllTests(final String identifier,
                            final Path path) {
        runAllTests(identifier,
                    path,
                    defaultTestResultMessageEvent);
    }

    @Override
    public void runAllTests(final String identifier, Path path,
                            final Event<TestResultMessage> customTestResultEvent) {
        final TestResultMessageAggregator testResultEvent = new TestResultMessageAggregator();

        for (final TestService testService : testServices) {
            for (final TestResultMessage testResultMessage : testService.runAllTests(identifier,
                                                                                     path)) {
                testResultEvent.add(testResultMessage);
            }
        }

        customTestResultEvent.fire(testResultEvent.getSummary(identifier));
    }
}
