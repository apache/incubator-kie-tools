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
package org.snakeyaml.engine.v2.events;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.snakeyaml.engine.v2.common.Anchor;
import org.snakeyaml.engine.v2.common.CharConstants;
import org.snakeyaml.engine.v2.common.ScalarStyle;
import org.snakeyaml.engine.v2.exceptions.Mark;

/** Marks a scalar value. */
public final class ScalarEvent extends NodeEvent {

  private final Optional<String> tag;
  // style flag of a scalar event indicates the style of the scalar. Possible
  // values are None, '', '\'', '"', '|', '>'
  private final ScalarStyle style;
  private final String value;
  // The implicit flag of a scalar event is a pair of boolean values that
  // indicate if the tag may be omitted when the scalar is emitted in a plain
  // and non-plain style correspondingly.
  private final ImplicitTuple implicit;

  public ScalarEvent(
      Optional<Anchor> anchor,
      Optional<String> tag,
      ImplicitTuple implicit,
      String value,
      ScalarStyle style,
      Optional<Mark> startMark,
      Optional<Mark> endMark) {
    super(anchor, startMark, endMark);
    Objects.requireNonNull(tag);
    this.tag = tag;
    this.implicit = implicit;
    Objects.requireNonNull(value);
    this.value = value;
    Objects.requireNonNull(style);
    this.style = style;
  }

  public ScalarEvent(
      Optional<Anchor> anchor,
      Optional<String> tag,
      ImplicitTuple implicit,
      String value,
      ScalarStyle style) {
    this(anchor, tag, implicit, value, style, Optional.empty(), Optional.empty());
  }

  /**
   * Tag of this scalar.
   *
   * @return The tag of this scalar, or <code>null</code> if no explicit tag is available.
   */
  public Optional<String> getTag() {
    return this.tag;
  }

  /**
   * Style of the scalar.
   *
   * <dl>
   *   <dt>null
   *   <dd>Flow Style - Plain
   *   <dt>'\''
   *   <dd>Flow Style - Single-Quoted
   *   <dt>'"'
   *   <dd>Flow Style - Double-Quoted
   *   <dt>'|'
   *   <dd>Block Style - Literal
   *   <dt>'&gt;'
   *   <dd>Block Style - Folded
   * </dl>
   *
   * @return Style of the scalar.
   */
  public ScalarStyle getScalarStyle() {
    return this.style;
  }

  /**
   * String representation of the value.
   *
   * <p>Without quotes and escaping.
   *
   * @return Value as Unicode string.
   */
  public String getValue() {
    return this.value;
  }

  public ImplicitTuple getImplicit() {
    return this.implicit;
  }

  @Override
  public ID getEventId() {
    return ID.Scalar;
  }

  public boolean isPlain() {
    return style == ScalarStyle.PLAIN;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder("=VAL");
    getAnchor().ifPresent(a -> builder.append(" &" + a));
    if (implicit.bothFalse()) {
      getTag().ifPresent(theTag -> builder.append(" <" + theTag + ">"));
    }
    builder.append(" ");
    builder.append(getScalarStyle().toString());
    builder.append(escapedValue());
    return builder.toString();
  }

  // escape
  public String escapedValue() {
    return codePoints(value)
        .filter(i -> i < Character.MAX_VALUE)
        .mapToObj(ch -> CharConstants.escapeChar(String.valueOf(Character.toChars(ch))))
        .collect(Collectors.joining(""));
  }

  private IntStream codePoints(String s) {
    throw new UnsupportedOperationException("Not implemented yet");
    // return s.codePoints();
  }
}
