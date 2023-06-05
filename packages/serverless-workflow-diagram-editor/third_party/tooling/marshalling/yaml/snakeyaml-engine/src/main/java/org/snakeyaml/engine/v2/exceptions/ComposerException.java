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

import java.util.Objects;
import java.util.Optional;

/** General exception during composition step */
public class ComposerException extends MarkedYamlEngineException { // NOSONAR

  /**
   * Create
   *
   * @param context - part of the document
   * @param contextMark - context position
   * @param problem - the issue
   * @param problemMark - the problem location
   */
  public ComposerException(
      String context, Optional<Mark> contextMark, String problem, Optional<Mark> problemMark) {
    super(context, contextMark, problem, problemMark);
    Objects.requireNonNull(context);
  }

  /**
   * Create
   *
   * @param problem - the issue
   * @param problemMark - the problem location
   */
  public ComposerException(String problem, Optional<Mark> problemMark) {
    super("", Optional.empty(), problem, problemMark);
  }
}
