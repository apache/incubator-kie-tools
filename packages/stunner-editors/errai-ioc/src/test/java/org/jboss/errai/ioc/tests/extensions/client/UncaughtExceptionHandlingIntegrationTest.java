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


package org.jboss.errai.ioc.tests.extensions.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;
import org.jboss.errai.ioc.client.IOCUtil;
import org.jboss.errai.ioc.client.container.ErraiUncaughtExceptionHandler;
import org.jboss.errai.ioc.client.test.AbstractErraiIOCTest;
import org.jboss.errai.ioc.tests.extensions.client.res.DependentUncaughtExceptionHandler;
import org.jboss.errai.ioc.tests.extensions.client.res.ExceptionForAppScopedHandler;
import org.jboss.errai.ioc.tests.extensions.client.res.ExceptionForDependentHandler;
import org.jboss.errai.ioc.tests.extensions.client.res.ExceptionForPrivateHandler;
import org.jboss.errai.ioc.tests.extensions.client.res.UncaughtExceptionTestLogger;

/**
 * Tests support for handling uncaught exceptions.
 *
 * @author Christian Sadilek <csadilek@redhat.com>
 * @author Max Barkley <mbarkley@redhat.com>
 */
public class UncaughtExceptionHandlingIntegrationTest extends AbstractErraiIOCTest {

  private final List<Throwable> testHandlerLog = new ArrayList<>();
  private final UncaughtExceptionHandler testHandler = e -> {
    testHandlerLog.add(e);
  };

  private UncaughtExceptionTestLogger uncaughtHandlersLogger;

  @Override
  public String getModuleName() {
    return "org.jboss.errai.ioc.tests.extensions.IOCExtensionTests";
  }

  @Override
  protected void gwtSetUp() throws Exception {
    testHandlerLog.clear();
    GWT.setUncaughtExceptionHandler(testHandler);
    super.gwtSetUp();
    uncaughtHandlersLogger = IOCUtil.getInstance(UncaughtExceptionTestLogger.class);
  }

  public void testErraiUncaughtExceptionHandlerIsSet() throws Exception {
    assertNotNull("Exception handler should not be null.", GWT.getUncaughtExceptionHandler());
    assertEquals("Exception handler should be instance of " + ErraiUncaughtExceptionHandler.class.getSimpleName(),
            ErraiUncaughtExceptionHandler.class.getName(), GWT.getUncaughtExceptionHandler().getClass().getName());
  }

  public void testErraiUncaughtExceptionHandlerInvokesReplacedHandler() throws Exception {
    final ErraiUncaughtExceptionHandler erraiHandler = assertErraiHandlerSet();
    assertTrue(testHandlerLog.isEmpty());
    final Throwable t = new Throwable();
    erraiHandler.onUncaughtException(t);
    assertEquals(1, testHandlerLog.size());
    assertSame(t, testHandlerLog.get(0));
  }

  public void testAppScopedExceptionHandler() throws Exception {
    final ErraiUncaughtExceptionHandler erraiHandler = assertErraiHandlerSet();
    assertTrue(uncaughtHandlersLogger.getLogged().isEmpty());
    final ExceptionForAppScopedHandler e = new ExceptionForAppScopedHandler();

    erraiHandler.onUncaughtException(e);
    assertEquals(1, uncaughtHandlersLogger.getLogged().size());
    assertSame(e, uncaughtHandlersLogger.getLogged().get(0));
  }

  public void testDependentExceptionHandler() throws Exception {
    final ErraiUncaughtExceptionHandler erraiHandler = assertErraiHandlerSet();
    assertTrue(uncaughtHandlersLogger.getLogged().isEmpty());
    final ExceptionForDependentHandler e = new ExceptionForDependentHandler();

    erraiHandler.onUncaughtException(e);
    assertTrue(uncaughtHandlersLogger.getLogged().isEmpty());

    final DependentUncaughtExceptionHandler handler = IOCUtil.getInstance(DependentUncaughtExceptionHandler.class);

    erraiHandler.onUncaughtException(e);
    assertEquals(1, uncaughtHandlersLogger.getLogged().size());
    assertSame(e, uncaughtHandlersLogger.getLogged().get(0));

    IOCUtil.destroy(handler);
    uncaughtHandlersLogger.reset();
    erraiHandler.onUncaughtException(e);
    assertTrue(uncaughtHandlersLogger.getLogged().isEmpty());
  }

  public void testPrivateExceptionHandler() throws Exception {
    final ErraiUncaughtExceptionHandler erraiHandler = assertErraiHandlerSet();
    assertTrue(uncaughtHandlersLogger.getLogged().isEmpty());
    final ExceptionForPrivateHandler e = new ExceptionForPrivateHandler();
    erraiHandler.onUncaughtException(e);
    assertEquals(1, uncaughtHandlersLogger.getLogged().size());
    assertSame(e, uncaughtHandlersLogger.getLogged().get(0));
  }

  private ErraiUncaughtExceptionHandler assertErraiHandlerSet() throws Exception, AssertionError {
    try {
      testErraiUncaughtExceptionHandlerIsSet();
    } catch (final AssertionError ae) {
      throw new AssertionError("Precondition failed.", ae);
    }

    final ErraiUncaughtExceptionHandler erraiHandler = (ErraiUncaughtExceptionHandler) GWT.getUncaughtExceptionHandler();
    return erraiHandler;
  }
}
