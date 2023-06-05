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
package org.snakeyaml.engine.v2.common;

import java.io.Serializable;

/** YAML Version indicator */
public class SpecVersion implements Serializable {

  /** keep major */
  private final int major;
  /** keep minor */
  private final int minor;

  /**
   * Create
   *
   * @param major - major part ov version, must be 1
   * @param minor - minor part of version, may be 0 or 1
   */
  public SpecVersion(int major, int minor) {
    this.major = major;
    this.minor = minor;
  }

  /**
   * getter
   *
   * @return 1
   */
  public int getMajor() {
    return major;
  }

  /**
   * getter
   *
   * @return 0 or 1
   */
  public int getMinor() {
    return minor;
  }

  /**
   * create readable text
   *
   * @return text
   */
  public String getRepresentation() {
    return major + "." + minor;
  }

  @Override
  public String toString() {
    return "Version{" + "major=" + major + ", minor=" + minor + '}';
  }
}
