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

package org.kie.workbench.common.stunner.client.yaml.mapper.api;

import java.util.Date;
import java.util.Map;

import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.ser.YAMLSerializationContext;

/**
 * Context for the serialization process.
 *
 * @author Nicolas Morel
 * @version $Id: $
 */
public class DefaultYAMLSerializationContext implements YAMLSerializationContext {

  private final boolean serializeNulls;
  private final boolean writeDatesAsTimestamps;
  private final boolean writeEmptyYAMLArrays;

  private DefaultYAMLSerializationContext(
      boolean serializeNulls, boolean writeDatesAsTimestamps, boolean writeEmptyYAMLArrays) {
    this.serializeNulls = serializeNulls;
    this.writeDatesAsTimestamps = writeDatesAsTimestamps;
    this.writeEmptyYAMLArrays = writeEmptyYAMLArrays;
  }

  /**
   * builder
   *
   * @return a {@link DefaultYAMLSerializationContext.Builder} object.
   */
  public static Builder builder() {
    return new DefaultBuilder();
  }

  /**
   * {@inheritDoc}
   *
   * <p>isSerializeNulls
   *
   * @see Builder#serializeNulls(boolean)
   */
  @Override
  public boolean isSerializeNulls() {
    return serializeNulls;
  }

  /**
   * {@inheritDoc}
   *
   * <p>isWriteDatesAsTimestamps
   *
   * @see Builder#writeDatesAsTimestamps(boolean)
   */
  @Override
  public boolean isWriteDatesAsTimestamps() {
    return writeDatesAsTimestamps;
  }

  /**
   * {@inheritDoc}
   *
   * <p>isWriteEmptyYAMLArrays
   *
   * @see Builder#writeEmptyYAMLArrays(boolean)
   */
  @Override
  public boolean isWriteEmptyYAMLArrays() {
    return writeEmptyYAMLArrays;
  }

  public static class Builder {

    protected boolean serializeNulls = false;

    protected boolean writeDatesAsTimestamps = true;

    protected boolean writeEmptyYAMLArrays = true;

    protected boolean mapKeyAndValueCanonical = false;

    /**
     * Sets whether object members are serialized when their value is null. This has no impact on
     * array elements. The default is true.
     *
     * @param serializeNulls true if should serializeNulls
     * @return the builder
     */
    public Builder serializeNulls(boolean serializeNulls) {
      this.serializeNulls = serializeNulls;
      return this;
    }

    /**
     * Determines whether {@link Date} and {@link java.sql.Timestamp} values are to be serialized as
     * numeric timestamps (true; the default), or as textual representation.
     *
     * <p>If textual representation is used, the actual format is Option is enabled by default.
     *
     * @param writeDatesAsTimestamps true if should writeDatesAsTimestamps
     * @return the builder
     */
    public Builder writeDatesAsTimestamps(boolean writeDatesAsTimestamps) {
      this.writeDatesAsTimestamps = writeDatesAsTimestamps;
      return this;
    }

    /**
     * Feature that determines whether Container properties (POJO properties with declared value of
     * Collection or array; i.e. things that produce YAML arrays) that are empty (have no elements)
     * will be serialized as empty YAML arrays (true), or suppressed from output (false).
     *
     * <p>Note that this does not change behavior of {@link Map}s, or "Collection-like" types.
     *
     * <p>Feature is enabled by default.
     *
     * @param writeEmptyYAMLArrays true if should writeEmptyYAMLArrays
     * @return the builder
     */
    public Builder writeEmptyYAMLArrays(boolean writeEmptyYAMLArrays) {
      this.writeEmptyYAMLArrays = writeEmptyYAMLArrays;
      return this;
    }

    public final YAMLSerializationContext build() {
      return new DefaultYAMLSerializationContext(
          serializeNulls, writeDatesAsTimestamps, writeEmptyYAMLArrays);
    }
  }

  public static class DefaultBuilder extends Builder {

    private DefaultBuilder() {}
  }
}
