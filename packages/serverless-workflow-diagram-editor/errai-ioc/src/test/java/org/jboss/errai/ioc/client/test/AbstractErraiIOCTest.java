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


package org.jboss.errai.ioc.client.test;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.junit.client.GWTTestCase;
import org.jboss.errai.common.client.logging.LoggingHandlerConfigurator;
import org.jboss.errai.common.client.logging.handlers.ErraiSystemLogHandler;
import org.jboss.errai.ioc.client.Container;
import org.jboss.errai.ioc.client.container.IOC;

/**
 * @author Mike Brock
 */
public abstract class AbstractErraiIOCTest extends GWTTestCase {

  @Override
  protected void gwtSetUp() throws Exception {
    super.gwtSetUp();
    // Only setup handlers for first test.
    if (LoggingHandlerConfigurator.get() == null) {
      GWT.log("Initializing Logging for tests.");
      // Cannot use console logger in non-production compiled tests.
      new LoggingHandlerConfigurator().onModuleLoad();
      if (!GWT.isScript()) {
        GWT.log("Tests not running as a compiled script: disabling all but system handler.");
        final Handler[] handlers = Logger.getLogger("").getHandlers();
        for (final Handler handler : handlers) {
          handler.setLevel(Level.OFF);
        }
        LoggingHandlerConfigurator.get().getHandler(ErraiSystemLogHandler.class).setLevel(Level.ALL);
      }
    }

    new Container().bootstrapContainer();
  }

  @Override
  protected void gwtTearDown() throws Exception {
    IOC.reset();
    Container.reset();
  }

  protected void $(final Runnable runnable) {
    delayTestFinish(30000);
    Container.$(runnable);
  }
}

