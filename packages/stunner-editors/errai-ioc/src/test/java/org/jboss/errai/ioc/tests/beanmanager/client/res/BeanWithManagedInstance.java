/**
 * Copyright (C) 2016 Red Hat, Inc. and/or its affiliates.
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

package org.jboss.errai.ioc.tests.beanmanager.client.res;

import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Default;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;

/**
 *
 * @author Max Barkley <mbarkley@redhat.com>
 */
@Dependent
public class BeanWithManagedInstance {

  private boolean destroyed;

  @Inject
  public ManagedInstance<DestructableClass> defaultOnly;

  @Inject
  @Any
  public ManagedInstance<DestructableClass> any;

  @Inject
  @A
  public ManagedInstance<DestructableClass> aOnly;

  @Inject
  @A @Default
  public ManagedInstance<DestructableClass> unsatisfied;

  @PreDestroy
  private void destroy() {
    destroyed = true;
  }

  public boolean isDestroyed() {
    return destroyed;
  }

}
