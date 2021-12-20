/*
 * Copyright (c) 2017 Ahome' Innovation Technologies. All rights reserved.
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

package com.ait.lienzo.test;

import java.util.Collection;

import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;

/**
 * The main JUnit test runner class.
 * <p>
 * By annotation your test class using <code>@RunWith( LienzoMockitoTestRunner.class )</code> the lienzo testing
 * framework comes into scene and its being loaded on your testing classpath.
 *
 * @author Roger Martinez
 * @See com.ait.lienzo.test.loader.LienzoMockitoClassLoader
 * @since 1.0
 */
public class LienzoMockitoTestRunner extends GwtMockitoTestRunner {

    public LienzoMockitoTestRunner(final Class<?> unitTestClass) throws InitializationError {
        super(init(unitTestClass));
    }

    private static Class<?> init(final Class<?> unitTestClass) {
        try {
            return LienzoMockito.init(unitTestClass);
        } catch (final Exception e) {
            throw new RuntimeException("Error initializing Lienzo Mockito.", e);
        }
    }

    @Override
    public void run(final RunNotifier notifier) {
        final RunNotifier wrapperNotifier = new RunNotifier();

        wrapperNotifier.addListener(new RunListener() {
            @Override
            public void testAssumptionFailure(final Failure failure) {
                notifier.fireTestAssumptionFailed(failure);
            }

            @Override
            public void testFailure(final Failure failure) throws Exception {
                notifier.fireTestFailure(failure);
            }

            @Override
            public void testFinished(final Description description) throws Exception {
                notifier.fireTestFinished(description);
            }

            @Override
            public void testIgnored(final Description description) throws Exception {
                notifier.fireTestIgnored(description);
            }

            @Override
            public void testRunFinished(final Result result) throws Exception {
                notifier.fireTestRunFinished(result);
            }

            @Override
            public void testRunStarted(final Description description) throws Exception {
                notifier.fireTestRunStarted(description);
            }

            @Override
            public void testStarted(final Description description) throws Exception {
                //Class<?> testClass = description.getTestClass();
                notifier.fireTestStarted(description);
            }
        });
        super.run(wrapperNotifier);
    }

    /**
     * Additional classes those methods needs to be no-op stubbed.
     */
    @Override
    protected Collection<Class<?>> getClassesToStub() {
        final Collection<Class<?>> toStub = super.getClassesToStub();

        toStub.add(RootPanel.class);

        return toStub;
    }
}
