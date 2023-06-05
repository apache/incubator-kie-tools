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
package org.snakeyaml.engine.v2.tokens;

import java.util.Objects;
import java.util.Optional;

import org.snakeyaml.engine.v2.common.ScalarStyle;
import org.snakeyaml.engine.v2.exceptions.Mark;

public final class ScalarToken extends Token {

  private final String value;
  private final boolean plain;
  private final ScalarStyle style;

  public ScalarToken(
      String value, boolean plain, Optional<Mark> startMark, Optional<Mark> endMark) {
    this(value, plain, ScalarStyle.PLAIN, startMark, endMark);
  }

  public ScalarToken(
      String value,
      boolean plain,
      ScalarStyle style,
      Optional<Mark> startMark,
      Optional<Mark> endMark) {
    super(startMark, endMark);
    Objects.requireNonNull(value);
    this.value = value;
    this.plain = plain;
    Objects.requireNonNull(style);
    this.style = style;
  }

  public boolean isPlain() {
    return this.plain;
  }

  public String getValue() {
    return this.value;
  }

  public ScalarStyle getStyle() {
    return this.style;
  }

  @Override
  public ID getTokenId() {
    return ID.Scalar;
  }

  @Override
  public String toString() {
    return getTokenId().toString() + " plain=" + plain + " style=" + style + " value=" + value;
  }
}
