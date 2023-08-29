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


package org.jboss.errai.ioc.client.container.async;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jboss.errai.common.client.api.Assert;
import org.jboss.errai.common.client.util.CreationalCallback;
import org.jboss.errai.ioc.client.container.IOC;

/**
 * An <tt>AsyncBeanQuery</tt> is used for querying more than one bean at a time, and then orchestrating
 * a unit of work to happen only after all of the beans in the query have been successfully loaded.
 * <p/>
 * For instance, you may need to load two or more beans dynamically from the bean manager in order to
 * accomplish some task. Instead of chaining a series of callbacks, which can become difficult to understand
 * and maintain, you can instead construct a query for the set of beans you need.
 * <p/>
 * Example:
 * <pre><code>
 * AsyncBeanQuery beanQuery = new AsyncBeanQuery();
 * final AsyncBeanFuture<BeanA> beanAFuture = beanQuery.load(BeanA.class);
 * final AsyncBeanFuture<BeanB> beanBFuture = beanQuery.load(BeanB.class);
 * <p/>
 * beanQuery.query(new Runnable() {
 *    public void run() {
 *     BeanA beanA = beanAFuture.get();
 *     BeanB beanB = beanBFuture.get();
 *     // do some work.
 *   }
 * });
 * </code></pre>
 * <p/>
 * In the above example, the <tt>Runnable</tt> passed to {@link #query(Runnable)} will be executed only after
 * all the beans requested with the previous {@link #load(AsyncBeanDef)} calls have been satisfied.
 *
 * @author Mike Brock
 * @since 3.0
 */
public class AsyncBeanQuery {
  private boolean cancelled = false;
  private Runnable finishCallback;

  private final List<LoadStrategy> loadStrategies
      = new ArrayList<LoadStrategy>();

  private final Set<LoadStrategy> loaded
      = new HashSet<LoadStrategy>();

  /**
   * Requests that the specified bean of the specified {@param type} is loaded.
   *
   * @param type
   *     the type of the bean to be loaded. See: {@link AsyncBeanManagerImpl#lookupBean(Class, java.lang.annotation.Annotation...)}
   * @param <T>
   *     the type of bean to be loaded.
   *
   * @return an {@link AsyncBeanFuture} which will house the instance of the bean once it is loaded.
   */
  public <T> AsyncBeanFuture<T> load(final Class<T> type) {
    return load(IOC.getAsyncBeanManager().lookupBean(type));
  }

  /**
   * Requests that the specified bean of the specified {@param type} and {@param qualifiers} is loaded.
   *
   * @param type
   *     the type of the bean to be loaded. See: {@link AsyncBeanManagerImpl#lookupBean(Class, java.lang.annotation.Annotation...)}
   * @param qualifiers
   *     the qualifiers for the bean to be loaded.
   * @param <T>
   *     the type of bean to be loaded.
   *
   * @return an {@link AsyncBeanFuture} which will house the instance of the bean once it is loaded.
   */
  public <T> AsyncBeanFuture<T> load(final Class<T> type, final Annotation... qualifiers) {
    return load(IOC.getAsyncBeanManager().lookupBean(type, qualifiers));
  }

  /**
   * Requests that the bean described by the specified {@link AsyncBeanDef} is loaded.
   *
   * @param beanDef
   *     the {@link AsyncBeanDef} describing the bean to be loaded.
   * @param <T>
   *     the type of bean to be loaded.
   *
   * @return an {@link AsyncBeanFuture} which will house the instance of the bean once it is loaded.
   */
  public <T> AsyncBeanFuture<T> load(final AsyncBeanDef<T> beanDef) {
    return load(beanDef, false);
  }

  /**
   * Requests that a new instance specified bean of the specified {@param type} is loaded.
   *
   * @param type
   *     the type of the bean to be loaded. See: {@link AsyncBeanManagerImpl#lookupBean(Class, java.lang.annotation.Annotation...)}
   * @param <T>
   *     the type of bean to be loaded.
   *
   * @return an {@link AsyncBeanFuture} which will house the instance of the bean once it is loaded.
   */
  public <T> AsyncBeanFuture<T> loadNew(final Class<T> type) {
    return loadNew(IOC.getAsyncBeanManager().lookupBean(type));
  }

  /**
   * Requests that a new instance of  specified bean of the specified {@param type} and {@param qualifiers} is loaded.
   *
   * @param type
   *     the type of the bean to be loaded. See: {@link AsyncBeanManagerImpl#lookupBean(Class, java.lang.annotation.Annotation...)}
   * @param qualifiers
   *     the qualifiers for the bean to be loaded.
   * @param <T>
   *     the type of bean to be loaded.
   *
   * @return an {@link AsyncBeanFuture} which will house the instance of the bean once it is loaded.
   */
  public <T> AsyncBeanFuture<T> loadNew(final Class<T> type, final Annotation... qualifiers) {
    return loadNew(IOC.getAsyncBeanManager().lookupBean(type, qualifiers));
  }

  /**
   * Requests that a new instance of the bean described by the specified {@link AsyncBeanDef} is loaded.
   *
   * @param beanDef
   *     the {@link AsyncBeanDef} describing the bean to be loaded.
   * @param <T>
   *     the type of bean to be loaded.
   *
   * @return an {@link AsyncBeanFuture} which will house the instance of the bean once it is loaded.
   */
  public <T> AsyncBeanFuture<T> loadNew(final AsyncBeanDef<T> beanDef) {
    return load(beanDef, true);
  }

  private <T> AsyncBeanFuture<T> load(final AsyncBeanDef<T> beanDef, final boolean loadNew) {
    final LoadStrategy<T> loadStrategy = new LoadStrategy<T>(beanDef, loadNew);
    loadStrategies.add(loadStrategy);
    return loadStrategy.getFuture();
  }

  /**
   * Initiates the constructed query based on {@link #load(AsyncBeanDef)}  and {@link #loadNew(AsyncBeanDef)} calls
   * made prior to calling this method. The specified <tt>Runnable</tt> is invoked only after all the requested
   * beans have been successfully loaded.
   *
   * @param finishCallback
   *     the <tt>Runnable</tt> to be invoked once all of the request beans have been loaded.
   */
  public void query(final Runnable finishCallback) {
    Assert.notNull(finishCallback);

    this.finishCallback = finishCallback;

    for (final LoadStrategy strategy : loadStrategies) {
      strategy.load();
    }
  }

  /**
   * Cancels the query if it has not yet returned. Already loaded beans are destroyed.
   */
  public void cancelQuery() {
    cancelled = true;

    for (final LoadStrategy strategy : loaded) {
      final AsyncBeanFuture future = strategy.getFuture();
      IOC.getAsyncBeanManager().destroyBean(future.get());
    }

    loaded.clear();
    loadStrategies.clear();
  }

  private void vote(LoadStrategy<?> strategy) {
    if (cancelled) {
      IOC.getAsyncBeanManager().destroyBean(strategy.getFuture().get());
      return;
    }

    loaded.add(strategy);

    if (loaded.containsAll(loadStrategies)) {
      finishCallback.run();
    }
  }

  private class LoadStrategy<T> {
    private final AsyncBeanDef<T> beanDef;
    private final AsyncBeanFuture<T> future;
    private final boolean loadNew;

    private LoadStrategy(final AsyncBeanDef<T> beanDef, final boolean loadNew) {
      this.beanDef = beanDef;
      this.loadNew = loadNew;

      this.future = new AsyncBeanFuture<T>(new CreationalCallback<T>() {
        @Override
        public void callback(T beanInstance) {
          vote(LoadStrategy.this);
        }
      });
    }


    AsyncBeanDef<T> getBeanDef() {
      return beanDef;
    }

    AsyncBeanFuture<T> getFuture() {
      return future;
    }

    boolean isLoadNew() {
      return loadNew;
    }

    void load() {

      if (loadNew) {
        beanDef.newInstance(future.getCreationalCallback());
      }
      else {
        beanDef.getInstance(future.getCreationalCallback());
      }
    }
  }
}
