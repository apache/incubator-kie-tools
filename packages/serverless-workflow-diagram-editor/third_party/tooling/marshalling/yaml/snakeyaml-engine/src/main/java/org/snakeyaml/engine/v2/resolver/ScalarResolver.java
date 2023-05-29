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
package org.snakeyaml.engine.v2.resolver;

import org.snakeyaml.engine.v2.nodes.Tag;

/**
 * ScalarResolver tries to detect a type of scalar value by its content (when the tag is implicit)
 */
public interface ScalarResolver {

  /**
   * Resolve (detect) the tag of the scalar node of the given type.
   *
   * @param value - the value of the scalar node
   * @param implicit - true if there was no tag specified (the tag will be resolved)
   * @return the Tag that matches the contents
   */
  Tag resolve(String value, Boolean implicit);
}
