/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser;

import org.kie.workbench.common.stunner.client.yaml.mapper.api.exception.YAMLDeserializationException;

/**
 * Context for the deserialization process.
 *
 * @author Nicolas Morel
 * @version $Id: $
 */
public class DefaultYAMLDeserializationContext implements YAMLDeserializationContext {

  /*
   * Deserialization options
   */
  private final boolean failOnUnknownProperties;
  private final boolean readUnknownEnumValuesAsNull;

  private DefaultYAMLDeserializationContext(
      boolean failOnUnknownProperties, boolean readUnknownEnumValuesAsNull) {
    this.failOnUnknownProperties = failOnUnknownProperties;
    this.readUnknownEnumValuesAsNull = readUnknownEnumValuesAsNull;
  }

  /**
   * builder
   *
   * @return a {@link DefaultYAMLDeserializationContext.Builder} object.
   */
  public static Builder builder() {
    return new DefaultBuilder();
  }

  /**
   * {@inheritDoc}
   *
   * <p>isReadUnknownEnumValuesAsNull
   *
   * @see Builder#readUnknownEnumValuesAsNull(boolean)
   */
  @Override
  public boolean isReadUnknownEnumValuesAsNull() {
    return readUnknownEnumValuesAsNull;
  }

  @Override
  public boolean isFailOnUnknownProperties() {
    return failOnUnknownProperties;
  }

  public static class Builder {

    protected boolean failOnUnknownProperties = true;

    protected boolean readUnknownEnumValuesAsNull = false;

    private Builder() {}

    /**
     * Determines whether encountering of unknown properties (ones that do not map to a property,
     * and there is no "any setter" or handler that can handle it) should result in a failure (by
     * throwing a {@link YAMLDeserializationException}) or not. This setting only takes effect after
     * all other handling methods for unknown properties have been tried, and property remains
     * unhandled.
     *
     * <p>Feature is enabled by default (meaning that a {@link YAMLDeserializationException} will be
     * thrown if an unknown property is encountered).
     *
     * @param failOnUnknownProperties true if should fail on unknown properties
     * @return the builder
     */
    public Builder failOnUnknownProperties(boolean failOnUnknownProperties) {
      this.failOnUnknownProperties = failOnUnknownProperties;
      return this;
    }

    /**
     * Feature that determines whether marshaller should return null for unknown enum values.
     * Default is false which will throw {@link IllegalArgumentException} when unknown enum value is
     * found.
     *
     * @param readUnknownEnumValuesAsNull true if should readUnknownEnumValuesAsNull
     * @return the builder
     */
    public Builder readUnknownEnumValuesAsNull(boolean readUnknownEnumValuesAsNull) {
      this.readUnknownEnumValuesAsNull = readUnknownEnumValuesAsNull;
      return this;
    }

    public final YAMLDeserializationContext build() {
      return new DefaultYAMLDeserializationContext(
          failOnUnknownProperties, readUnknownEnumValuesAsNull);
    }
  }

  public static class DefaultBuilder extends Builder {

    private DefaultBuilder() {}
  }
}
