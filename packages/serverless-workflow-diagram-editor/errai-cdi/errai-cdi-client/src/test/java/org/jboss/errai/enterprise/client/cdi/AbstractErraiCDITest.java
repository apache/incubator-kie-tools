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


package org.jboss.errai.enterprise.client.cdi;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.Timer;
import org.jboss.errai.common.client.api.extension.InitVotes;
import org.jboss.errai.enterprise.client.cdi.api.CDI;
import org.jboss.errai.ioc.client.Container;
import org.jboss.errai.ioc.client.QualifierUtil;
import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.client.container.SyncBeanDef;

/**
 * Abstract base class of all Errai CDI integration tests,
 * used to bootstrap our IOC container and CDI module.
 *
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public abstract class AbstractErraiCDITest extends GWTTestCase {

  protected boolean disableBus = false;

  @Override
  protected void gwtSetUp() throws Exception {
    if (disableBus) {
      setRemoteCommunicationEnabled(false);
    }
    InitVotes.setTimeoutMillis(60000);

    // Unfortunately, GWTTestCase does not call our inherited module's onModuleLoad() methods
    // http://code.google.com/p/google-web-toolkit/issues/detail?id=3791
    new CDI().__resetSubsystem();
    new Container().onModuleLoad();
    new CDIClientBootstrap().onModuleLoad();

    InitVotes.startInitPolling();

    super.gwtSetUp();
  }

  @Override
  protected void gwtTearDown() throws Exception {
    setRemoteCommunicationEnabled(true);
    InitVotes.reset();
    IOC.reset();
    Container.reset();
    super.gwtTearDown();
  }

  public static boolean annotationSetMatches(final Set<Annotation> annotations,
                                             final Class<? extends Annotation>... annos) {

    final Set<Class<? extends Annotation>> annoClassCompareFrom
        = new HashSet<>(Arrays.asList(annos));

    final Set<Class<? extends Annotation>> annoClassCompareTo
        = new HashSet<>();

    for (final Annotation a : annotations) {
      annoClassCompareTo.add(a.annotationType());
    }

    return annoClassCompareFrom.equals(annoClassCompareTo);
  }

  public native void setRemoteCommunicationEnabled(boolean enabled) /*-{
    $wnd.erraiBusRemoteCommunicationEnabled = enabled;
  }-*/;

  protected <T> Collection<SyncBeanDef<T>> getBeans(final Class<T> type,
                                                   final Annotation... annotations) {
    return IOC.getBeanManager().lookupBeans(type, annotations);
  }

  protected boolean assertContains(final Collection<Annotation> annotationCollection,
                                   final Annotation toCompare) {
    for (final Annotation a : annotationCollection) {
      if (QualifierUtil.isEqual(a, toCompare)) return true;
    }
    return false;
  }

  protected void asyncTest() {
    delayTestFinish(90000);
  }

  protected void asyncTest(final Runnable runnable) {
    asyncTest();
    InitVotes.registerOneTimeInitCallback(new Runnable() {

      @Override
      public void run() {
        new Timer() {
          @Override
          public void run() {
            runnable.run();
          }
        }.schedule(100);
      }
    });
  }
}
