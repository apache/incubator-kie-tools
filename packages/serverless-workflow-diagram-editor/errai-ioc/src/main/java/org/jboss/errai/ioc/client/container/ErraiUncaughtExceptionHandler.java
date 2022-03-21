/*
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

package org.jboss.errai.ioc.client.container;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;

/**
 * <p>
 * An {@link UncaughtExceptionHandler} implementation used to dispatch to
 * {@link org.jboss.errai.ioc.client.api.UncaughtExceptionHandler @UncaughtExceptionHandler}.methods.
 *
 * <p>
 * Before the Errai IOC container starts, an {@link ErraiUncaughtExceptionHandler} is set as the global exception
 * handler with {@link GWT#setUncaughtExceptionHandler(UncaughtExceptionHandler)}.
 *
 * @author Max Barkley <mbarkley@redhat.com>
 */
public class ErraiUncaughtExceptionHandler implements UncaughtExceptionHandler {

  private final Optional<UncaughtExceptionHandler> oReplaced;
  private final List<Consumer<Throwable>> userHandlers = new ArrayList<>();

  public ErraiUncaughtExceptionHandler(final UncaughtExceptionHandler replaced) {
    oReplaced = Optional.ofNullable(replaced);
  }

  @Override
  public void onUncaughtException(final Throwable e) {
    userHandlers.forEach(h -> h.accept(e));
    oReplaced.ifPresent(h -> h.onUncaughtException(e));
  }

  public Runnable addHandler(final Consumer<Throwable> handler) {
    userHandlers.add(handler);
    return () -> userHandlers.remove(handler);
  }

}
