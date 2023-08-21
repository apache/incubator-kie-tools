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
package org.snakeyaml.engine.v2.parser;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.snakeyaml.engine.v2.common.SpecVersion;

/** Store the internal state for directives */
class VersionTagsTuple {

  private final Optional<SpecVersion> specVersion;
  private final Map<String, String> tags;

  public VersionTagsTuple(Optional<SpecVersion> specVersion, Map<String, String> tags) {
    Objects.requireNonNull(specVersion);
    this.specVersion = specVersion;
    this.tags = tags;
  }

  public Optional<SpecVersion> getSpecVersion() {
    return specVersion;
  }

  public Map<String, String> getTags() {
    return tags;
  }

  @Override
  public String toString() {
    return "VersionTagsTuple<" + specVersion + "," + tags + ">";
  }
}
