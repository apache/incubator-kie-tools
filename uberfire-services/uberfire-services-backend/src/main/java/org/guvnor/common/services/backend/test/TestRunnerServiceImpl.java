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

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.guvnor.common.services.shared.test.Failure;
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
    public void runAllTests(final String identifier,
                            final Path path,
                            final Event<TestResultMessage> customTestResultEvent) {

        final TestRunEventCollection testResultEvent = new TestRunEventCollection();

        for (final TestService testService : testServices) {
            testService.runAllTests(identifier,
                                    path,
                                    testResultEvent);
        }
        customTestResultEvent.fire(new TestResultMessage(identifier,
                                                         testResultEvent.getRunCountSum(),
                                                         testResultEvent.getRuntimeSum(),
                                                         testResultEvent.getFailures()));
    }

    private class TestRunEventCollection
            implements Event<TestResultMessage> {

        private List<TestResultMessage> resultMessages = new ArrayList<>();

        @Override
        public void fire(TestResultMessage testResultMessage) {
            resultMessages.add(testResultMessage);
        }

        @Override
        public Event<TestResultMessage> select(Annotation... annotations) {
            return null;
        }

        @Override
        public <U extends TestResultMessage> Event<U> select(Class<U> aClass, Annotation... annotations) {
            return null;
        }

        public int getRunCountSum() {
            int result = 0;
            for (final TestResultMessage message : resultMessages) {
                result += message.getRunCount();
            }
            return result;
        }

        public long getRuntimeSum() {
            long result = 0;
            for (final TestResultMessage message : resultMessages) {
                result += message.getRunTime();
            }
            return result;
        }

        public List<Failure> getFailures() {
            List<Failure> result = new ArrayList<>();
            for (final TestResultMessage message : resultMessages) {
                result.addAll(message.getFailures());
            }
            return result;
        }
    }
}
