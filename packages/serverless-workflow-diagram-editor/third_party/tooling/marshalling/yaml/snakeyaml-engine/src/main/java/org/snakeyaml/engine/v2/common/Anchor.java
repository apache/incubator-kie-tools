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

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.snakeyaml.engine.v2.exceptions.EmitterException;

/** Value inside Anchor and Alias */
public class Anchor {

  private static final Set<Character> INVALID_ANCHOR = new HashSet<>();
  private static final Pattern SPACES_PATTERN = Pattern.compile("\\s");

  static {
    INVALID_ANCHOR.add('[');
    INVALID_ANCHOR.add(']');
    INVALID_ANCHOR.add('{');
    INVALID_ANCHOR.add('}');
    INVALID_ANCHOR.add(',');
    INVALID_ANCHOR.add('*');
    INVALID_ANCHOR.add('&');
  }

  private final String value;

  /**
   * Create
   *
   * @param value - the anchor value
   */
  public Anchor(String value) {
    Objects.requireNonNull(value);
    if (value.isEmpty()) {
      throw new IllegalArgumentException("Empty anchor.");
    }
    for (int i = 0; i < value.length(); i++) {
      char ch = value.charAt(i);
      if (INVALID_ANCHOR.contains(ch)) {
        throw new EmitterException("Invalid character '" + ch + "' in the anchor: " + value);
      }
    }
    Matcher matcher = SPACES_PATTERN.matcher(value);
    if (matcher.find()) {
      throw new EmitterException("Anchor may not contain spaces: " + value);
    }
    this.value = value;
  }

  /**
   * getter
   *
   * @return anchor value
   */
  public String getValue() {
    return value;
  }

  @Override
  public String toString() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Anchor anchor1 = (Anchor) o;
    return Objects.equals(value, anchor1.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }
}
