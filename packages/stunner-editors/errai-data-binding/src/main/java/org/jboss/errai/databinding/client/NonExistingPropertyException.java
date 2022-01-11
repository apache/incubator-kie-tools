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

import org.jboss.errai.databinding.client.api.Bindable;

/**
 * Thrown to indicate that a JavaBean property does not exist on the object it was referred to. This exception is thrown
 * by implementations of {@link HasProperties} (e.g proxies for {@link Bindable} types).
 *
 * @author Christian Sadilek <csadilek@redhat.com>
 */
@SuppressWarnings("serial")
public class NonExistingPropertyException extends RuntimeException {

  public NonExistingPropertyException(final String type, final String property) {
    super("No property [" + property + "] in bindable type [" + type + "].");
  }

}
