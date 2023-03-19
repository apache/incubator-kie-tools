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

package org.jboss.errai.ui.shared;

import java.util.Arrays;

import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.DataField.AttributeRule;
import org.jboss.errai.ui.shared.api.annotations.DataField.ConflictStrategy;

/**
 * Used to store meta-data from a {@link DataField} in a templated bean for runtime.
 *
 * @author Max Barkley <mbarkley@redhat.com>
 */
public class DataFieldMeta {

  private static final AttributeRule[] defaultAttrs = new AttributeRule[0];
  private static final ConflictStrategy globalDefaultStrategy = ConflictStrategy.USE_TEMPLATE;

  private final AttributeRule[] rules;
  private final ConflictStrategy defaultStrategy;

  public DataFieldMeta() {
    this(defaultAttrs, globalDefaultStrategy);
  }

  public DataFieldMeta(final AttributeRule[] rules, final ConflictStrategy defaultStrategy) {
    this.rules = rules;
    this.defaultStrategy = defaultStrategy;
  }

  public ConflictStrategy getStrategy(final String attributeName) {
    return Arrays
      .stream(rules)
      .filter(attr -> attr.name().equals(attributeName))
      .map(attr -> attr.strategy())
      .findFirst()
      .orElse(defaultStrategy);
  }

}
