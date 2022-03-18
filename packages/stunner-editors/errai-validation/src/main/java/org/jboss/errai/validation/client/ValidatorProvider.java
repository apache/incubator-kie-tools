/*
 * Copyright (C) 2013 Red Hat, Inc. and/or its affiliates.
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

package org.jboss.errai.validation.client;

import javax.inject.Provider;
import javax.inject.Singleton;
import javax.validation.Validation;
import javax.validation.Validator;

import org.jboss.errai.ioc.client.api.IOCProvider;

/**
 * {@link IOCProvider} to make {@link Validator} instances injectable.
 * 
 * @author Christian Sadilek <csadilek@redhat.com>
 */
@IOCProvider
@Singleton
public class ValidatorProvider implements Provider<Validator> {

  @Override
  public Validator get() {
    return Validation.buildDefaultValidatorFactory().getValidator();
  }
}


