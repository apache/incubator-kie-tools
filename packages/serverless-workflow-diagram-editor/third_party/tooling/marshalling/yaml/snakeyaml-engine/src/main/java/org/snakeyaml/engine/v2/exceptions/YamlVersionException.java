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
package org.snakeyaml.engine.v2.exceptions;

import org.snakeyaml.engine.v2.common.SpecVersion;

/** Indication of invalid YAML version */
public class YamlVersionException extends YamlEngineException {

  /** specified version */
  private final SpecVersion specVersion;

  /**
   * Create
   *
   * @param specVersion - the version
   */
  public YamlVersionException(SpecVersion specVersion) {
    super(specVersion.toString());
    this.specVersion = specVersion;
  }

  /**
   * getter
   *
   * @return its version
   */
  public SpecVersion getSpecVersion() {
    return specVersion;
  }
}
