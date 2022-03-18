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

package org.jboss.errai.ioc.async.test.beanmanager.client.res;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.Disposer;
import org.jboss.errai.ioc.client.api.LoadAsync;

/**
 * @author Mike Brock
 */
@ApplicationScoped @LoadAsync
public class Foo {
  @Inject Bar bar;
  @Inject Bar2 bar2;
  @Inject Disposer<Bar> barDisposer;
  @Inject BazTheSingleton bazTheSingleton;

  public Bar getBar() {
    return bar;
  }

  public Bar2 getBar2() {
    return bar2;
  }

  public Disposer<Bar> getBarDisposer() {
    return barDisposer;
  }

  public BazTheSingleton getBazTheSingleton() {
    return bazTheSingleton;
  }
}
