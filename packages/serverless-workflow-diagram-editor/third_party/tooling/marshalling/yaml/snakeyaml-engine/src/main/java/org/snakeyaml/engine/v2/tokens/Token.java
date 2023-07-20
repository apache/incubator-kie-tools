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

import org.snakeyaml.engine.v2.exceptions.Mark;

/** A unit of YAML data */
public abstract class Token {

  private final Optional<Mark> startMark;
  private final Optional<Mark> endMark;

  public Token(Optional<Mark> startMark, Optional<Mark> endMark) {
    Objects.requireNonNull(startMark);
    Objects.requireNonNull(endMark);
    this.startMark = startMark;
    this.endMark = endMark;
  }

  public Optional<Mark> getStartMark() {
    return startMark;
  }

  public Optional<Mark> getEndMark() {
    return endMark;
  }

  /**
   * For error reporting.
   *
   * @return ID of this token
   */
  public abstract ID getTokenId();

  @Override
  public String toString() {
    return getTokenId().toString();
  }

  public enum ID {
    Alias("<alias>"), // NOSONAR
    Anchor("<anchor>"), // NOSONAR
    BlockEnd("<block end>"), // NOSONAR
    BlockEntry("-"), // NOSONAR
    BlockMappingStart("<block mapping start>"), // NOSONAR
    BlockSequenceStart("<block sequence start>"), // NOSONAR
    Directive("<directive>"), // NOSONAR
    DocumentEnd("<document end>"), // NOSONAR
    DocumentStart("<document start>"), // NOSONAR
    FlowEntry(","), // NOSONAR
    FlowMappingEnd("}"), // NOSONAR
    FlowMappingStart("{"), // NOSONAR
    FlowSequenceEnd("]"), // NOSONAR
    FlowSequenceStart("["), // NOSONAR
    Key("?"), // NOSONAR
    Scalar("<scalar>"), // NOSONAR
    StreamEnd("<stream end>"), // NOSONAR
    StreamStart("<stream start>"), // NOSONAR
    Tag("<tag>"), // NOSONAR
    Comment("#"),
    Value(":"); // NOSONAR

    private final String description;

    ID(String s) {
      description = s;
    }

    @Override
    public String toString() {
      return description;
    }
  }
}
