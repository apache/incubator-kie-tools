/*
 * Copyright (C) 2015 Red Hat, Inc. and/or its affiliates.
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

package org.jboss.errai.ioc.client;
/*
 * Copyright 2011 JBoss, by Red Hat, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



import org.jboss.errai.common.client.api.extension.InitVotes;
import org.jboss.errai.ioc.client.container.IOC;

import com.google.gwt.junit.client.GWTTestCase;

/**
 * @author Mike Brock <cbrock@redhat.com>
 */
public abstract class IOCClientTestCase extends GWTTestCase {
  private ContainerBootstrapper initializer = new ContainerBootstrapper() {

    @Override
    public void bootstrap() {
      try {
        new Container().bootstrapContainer();
      }
      catch (final Throwable t) {
        throw new RuntimeException("failed to bootstrap container", t);
      }
    }
  };

  protected IOCClientTestCase() {
  }

  protected void bootstrapContainer() {
    initializer.bootstrap();
  }

  public void setInitializer(final ContainerBootstrapper initializer) {
    this.initializer = initializer;
  }

  public String getModulePackage() {
    return getModuleName().substring(0, getModuleName().lastIndexOf('.'));
  }

  @Override
  public void gwtSetUp() throws Exception {
    try {
      bootstrapContainer();
    }
    catch (final Exception t) {
      t.printStackTrace();
      throw t;
    }
  }

  @Override
  protected void gwtTearDown() throws Exception {
    IOC.reset();
    InitVotes.reset();

    super.gwtTearDown();
  }

  public static interface ContainerBootstrapper {
    public void bootstrap();
  }
}
