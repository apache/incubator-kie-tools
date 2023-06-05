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
package org.snakeyaml.engine.v2.emitter;

/** Accumulate information to choose the scalar style */
public final class ScalarAnalysis {

  private final String scalar;
  private final boolean empty;
  private final boolean multiline;
  private final boolean allowFlowPlain;
  private final boolean allowBlockPlain;
  private final boolean allowSingleQuoted;
  private final boolean allowBlock;

  /**
   * Create
   *
   * @param scalar - the data to analyse
   * @param empty - true for empty scalar
   * @param multiline - true if it may take many lines
   * @param allowFlowPlain - true if can be plain in flow context
   * @param allowBlockPlain - true if can be plain in block context
   * @param allowSingleQuoted - true if single quotes are allowed
   * @param allowBlock - true if block style is alowed
   */
  public ScalarAnalysis(
      String scalar,
      boolean empty,
      boolean multiline,
      boolean allowFlowPlain,
      boolean allowBlockPlain,
      boolean allowSingleQuoted,
      boolean allowBlock) {
    this.scalar = scalar;
    this.empty = empty;
    this.multiline = multiline;
    this.allowFlowPlain = allowFlowPlain;
    this.allowBlockPlain = allowBlockPlain;
    this.allowSingleQuoted = allowSingleQuoted;
    this.allowBlock = allowBlock;
  }

  /**
   * getter
   *
   * @return the scalar to be analysed
   */
  public String getScalar() {
    return scalar;
  }

  /**
   * getter
   *
   * @return true when empty
   */
  public boolean isEmpty() {
    return empty;
  }

  /**
   * getter
   *
   * @return true if it may take many lines
   */
  public boolean isMultiline() {
    return multiline;
  }

  /**
   * getter
   *
   * @return true if can be plain in flow context
   */
  public boolean isAllowFlowPlain() {
    return allowFlowPlain;
  }

  /**
   * getter
   *
   * @return true if can be plain in block context
   */
  public boolean isAllowBlockPlain() {
    return allowBlockPlain;
  }

  /**
   * getter
   *
   * @return true if single quotes are allowed
   */
  public boolean isAllowSingleQuoted() {
    return allowSingleQuoted;
  }

  /**
   * getter
   *
   * @return true when block style is allowed for this scalar
   */
  public boolean isAllowBlock() {
    return allowBlock;
  }
}
