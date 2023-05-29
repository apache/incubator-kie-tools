/*
 * Copyright (c) 2018, SnakeYAML
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.snakeyaml.engine.v2.api;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.UnaryOperator;

import org.snakeyaml.engine.v2.common.SpecVersion;
import org.snakeyaml.engine.v2.env.EnvConfig;
import org.snakeyaml.engine.v2.nodes.Tag;
import org.snakeyaml.engine.v2.schema.Schema;

/**
 * Immutable configuration for loading. Description for all the fields can be found in the builder
 */
public final class LoadSettings {

  private final String label;
  private final Map<Tag, ConstructNode> tagConstructors;
  private final IntFunction<List<Object>> defaultList;
  private final IntFunction<Set<Object>> defaultSet;
  private final IntFunction<Map<Object, Object>> defaultMap;
  private final UnaryOperator<SpecVersion> versionFunction;
  private final Integer bufferSize;
  private final boolean allowDuplicateKeys;
  private final boolean allowRecursiveKeys;
  private final boolean parseComments;
  private final int maxAliasesForCollections;
  private final boolean useMarks;
  private final Optional<EnvConfig> envConfig;
  private final int codePointLimit;
  private final Schema schema;

  // general
  private final Map<SettingKey, Object> customProperties;

  LoadSettings(
      String label,
      Map<Tag, ConstructNode> tagConstructors,
      IntFunction<List<Object>> defaultList,
      IntFunction<Set<Object>> defaultSet,
      IntFunction<Map<Object, Object>> defaultMap,
      UnaryOperator<SpecVersion> versionFunction,
      Integer bufferSize,
      boolean allowDuplicateKeys,
      boolean allowRecursiveKeys,
      int maxAliasesForCollections,
      boolean useMarks,
      Map<SettingKey, Object> customProperties,
      Optional<EnvConfig> envConfig,
      boolean parseComments,
      int codePointLimit,
      Schema schema) {
    this.label = label;
    this.tagConstructors = tagConstructors;
    this.defaultList = defaultList;
    this.defaultSet = defaultSet;
    this.defaultMap = defaultMap;
    this.versionFunction = versionFunction;
    this.bufferSize = bufferSize;
    this.allowDuplicateKeys = allowDuplicateKeys;
    this.allowRecursiveKeys = allowRecursiveKeys;
    this.parseComments = parseComments;
    this.maxAliasesForCollections = maxAliasesForCollections;
    this.useMarks = useMarks;
    this.customProperties = customProperties;
    this.envConfig = envConfig;
    this.codePointLimit = codePointLimit;
    this.schema = schema;
  }

  /**
   * Create the builder
   *
   * @return the builder to fill the configuration options
   */
  public static LoadSettingsBuilder builder() {
    return new LoadSettingsBuilder();
  }

  public String getLabel() {
    return label;
  }

  public Map<Tag, ConstructNode> getTagConstructors() {
    return tagConstructors;
  }

  public IntFunction<List<Object>> getDefaultList() {
    return defaultList;
  }

  public IntFunction<Set<Object>> getDefaultSet() {
    return defaultSet;
  }

  public IntFunction<Map<Object, Object>> getDefaultMap() {
    return defaultMap;
  }

  public Integer getBufferSize() {
    return bufferSize;
  }

  public boolean getAllowDuplicateKeys() {
    return allowDuplicateKeys;
  }

  public boolean getAllowRecursiveKeys() {
    return allowRecursiveKeys;
  }

  public boolean getUseMarks() {
    return useMarks;
  }

  public Function<SpecVersion, SpecVersion> getVersionFunction() {
    return versionFunction;
  }

  public Object getCustomProperty(SettingKey key) {
    return customProperties.get(key);
  }

  public int getMaxAliasesForCollections() {
    return maxAliasesForCollections;
  }

  public Optional<EnvConfig> getEnvConfig() {
    return envConfig;
  }

  public boolean getParseComments() {
    return parseComments;
  }

  public int getCodePointLimit() {
    return codePointLimit;
  }

  public Schema getSchema() {
    return schema;
  }
}
