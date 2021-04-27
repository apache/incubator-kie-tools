/*
 * Copyright (C) 2011 Red Hat, Inc. and/or its affiliates.
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

package org.jboss.errai.databinding.client;

import java.lang.annotation.Annotation;

import javax.inject.Singleton;

import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.ioc.client.api.ContextualTypeProvider;
import org.jboss.errai.ioc.client.api.IOCProvider;

/**
 * {@link IOCProvider} to make {@link DataBinder} instances injectable.
 * 
 * @author Christian Sadilek <csadilek@redhat.com>
 */
@IOCProvider @Singleton
@SuppressWarnings("rawtypes")
public class DataBinderProvider implements ContextualTypeProvider<DataBinder> {
  
  @Override
  @SuppressWarnings("all")
  public DataBinder provide(Class<?>[] typeargs, Annotation[] qualifiers) {
    return DataBinder.forType(typeargs[0]);
  }
}
