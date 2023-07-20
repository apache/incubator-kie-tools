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

import java.util.Optional;

import org.snakeyaml.engine.v2.exceptions.Mark;

/**
 * Basic unit of output from a {@link org.snakeyaml.engine.v2.parser.Parser} or input of a {@link
 * org.snakeyaml.engine.v2.emitter.Emitter}.
 */
public abstract class Event {

  private final Optional<Mark> startMark;
  private final Optional<Mark> endMark;

  public Event(Optional<Mark> startMark, Optional<Mark> endMark) {
    if ((startMark.isPresent() && !endMark.isPresent())
        || (!startMark.isPresent() && endMark.isPresent())) {
      throw new NullPointerException("Both marks must be either present or absent.");
    }
    this.startMark = startMark;
    this.endMark = endMark;
  }

  /*
   * Create Node for emitter
   */
  public Event() {
    this(Optional.empty(), Optional.empty());
  }

  public Optional<Mark> getStartMark() {
    return startMark;
  }

  public Optional<Mark> getEndMark() {
    return endMark;
  }

  /**
   * Get the type (kind) if this Event
   *
   * @return the ID of this Event
   */
  public abstract ID getEventId();

  /** ID of a non-abstract Event */
  public enum ID {
    Alias,
    Comment,
    DocumentEnd,
    DocumentStart,
    MappingEnd,
    MappingStart,
    Scalar,
    SequenceEnd,
    SequenceStart,
    StreamEnd,
    StreamStart // NOSONAR
  }
}
