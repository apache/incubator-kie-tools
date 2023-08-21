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

/** Parsing exception when the marks are available */
public class MarkedYamlEngineException extends YamlEngineException {

  private final String context;
  private final Optional<Mark> contextMark;
  private final String problem;
  private final Optional<Mark> problemMark;

  /**
   * Create
   *
   * @param context - the context of the problem
   * @param contextMark - position of the context
   * @param problem - the issue
   * @param problemMark - position of the issue
   * @param cause - exception which was thrown
   */
  protected MarkedYamlEngineException(
      String context,
      Optional<Mark> contextMark,
      String problem,
      Optional<Mark> problemMark,
      Throwable cause) {
    super(context + "; " + problem + "; " + problemMark, cause);
    Objects.requireNonNull(contextMark, "contextMark must be provided");
    Objects.requireNonNull(problemMark, "problemMark must be provided");
    this.context = context;
    this.contextMark = contextMark;
    this.problem = problem;
    this.problemMark = problemMark;
  }

  /**
   * Create
   *
   * @param context - the context of the problem
   * @param contextMark - position of the context
   * @param problem - the issue
   * @param problemMark - position of the issue
   */
  protected MarkedYamlEngineException(
      String context, Optional<Mark> contextMark, String problem, Optional<Mark> problemMark) {
    this(context, contextMark, problem, problemMark, null);
  }

  /**
   * Getter
   *
   * @return the problem
   */
  @Override
  public String getMessage() {
    return toString();
  }

  /**
   * get readable error
   *
   * @return readable problem
   */
  @Override
  public String toString() {
    StringBuilder lines = new StringBuilder();
    if (context != null) {
      lines.append(context);
      lines.append("\n");
    }
    if (contextMark.isPresent()
        && (problem == null
            || !problemMark.isPresent()
            || contextMark.get().getName().equals(problemMark.get().getName())
            || (contextMark.get().getLine() != problemMark.get().getLine())
            || (contextMark.get().getColumn() != problemMark.get().getColumn()))) {
      lines.append(contextMark.get());
      lines.append("\n");
    }
    if (problem != null) {
      lines.append(problem);
      lines.append("\n");
    }
    if (problemMark.isPresent()) {
      lines.append(problemMark.get());
      lines.append("\n");
    }
    return lines.toString();
  }

  /**
   * getter
   *
   * @return context of the error
   */
  public String getContext() {
    return context;
  }

  /**
   * getter
   *
   * @return position of the context of the error
   */
  public Optional<Mark> getContextMark() {
    return contextMark;
  }

  /**
   * getter
   *
   * @return the issue
   */
  public String getProblem() {
    return problem;
  }

  /**
   * getter
   *
   * @return position of the issue
   */
  public Optional<Mark> getProblemMark() {
    return problemMark;
  }
}
