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
package org.snakeyaml.engine.v2.comments;

import java.util.Objects;
import java.util.Optional;

import org.snakeyaml.engine.v2.events.CommentEvent;
import org.snakeyaml.engine.v2.exceptions.Mark;

/** A comment line. Maybe a block comment, blank line, or inline comment. */
public class CommentLine {

  private final Optional<Mark> startMark;
  private final Optional<Mark> endMark;
  private final String value;
  private final CommentType commentType;

  /**
   * Create
   *
   * @param event - the source
   */
  public CommentLine(CommentEvent event) {
    this(event.getStartMark(), event.getEndMark(), event.getValue(), event.getCommentType());
  }

  /**
   * Create
   *
   * @param startMark - start
   * @param endMark - end
   * @param value - the comment
   * @param commentType - the type
   */
  public CommentLine(
      Optional<Mark> startMark, Optional<Mark> endMark, String value, CommentType commentType) {
    Objects.requireNonNull(startMark);
    this.startMark = startMark;
    Objects.requireNonNull(endMark);
    this.endMark = endMark;
    Objects.requireNonNull(value);
    this.value = value;
    Objects.requireNonNull(commentType);
    this.commentType = commentType;
  }

  /**
   * getter
   *
   * @return end position
   */
  public Optional<Mark> getEndMark() {
    return endMark;
  }

  /**
   * getter
   *
   * @return start position
   */
  public Optional<Mark> getStartMark() {
    return startMark;
  }

  /**
   * getter
   *
   * @return type of it
   */
  public CommentType getCommentType() {
    return commentType;
  }

  /**
   * Value of this comment.
   *
   * @return comment's value.
   */
  public String getValue() {
    return value;
  }

  public String toString() {
    return "<"
        + this.getClass().getName()
        + " (type="
        + getCommentType()
        + ", value="
        + getValue()
        + ")>";
  }
}
