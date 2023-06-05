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

import java.util.AbstractQueue;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;

import org.snakeyaml.engine.v2.events.CommentEvent;
import org.snakeyaml.engine.v2.events.Event;
import org.snakeyaml.engine.v2.parser.Parser;

/**
 * Used by the Composer and Emitter to collect comment events so that they can be used at a later
 * point in the process.
 */
public class CommentEventsCollector {

  private final Queue<Event> eventSource;
  private final CommentType[] expectedCommentTypes;
  private List<CommentLine> commentLineList;

  /**
   * Constructor used to collect comment events emitted by a Parser.
   *
   * @param parser the event source.
   * @param expectedCommentTypes the comment types expected. Any comment types not included are not
   *     collected.
   */
  public CommentEventsCollector(final Parser parser, CommentType... expectedCommentTypes) {
    this.eventSource =
        new AbstractQueue<Event>() {

          @Override
          public boolean offer(Event e) {
            throw new UnsupportedOperationException();
          }

          @Override
          public Event poll() {
            return parser.next();
          }

          @Override
          public Event peek() {
            return parser.peekEvent();
          }

          @Override
          public Iterator<Event> iterator() {
            throw new UnsupportedOperationException();
          }

          @Override
          public int size() {
            throw new UnsupportedOperationException();
          }
        };
    this.expectedCommentTypes = expectedCommentTypes;
    commentLineList = new ArrayList<>();
  }

  /**
   * Constructor used to collect events emitted by the Serializer.
   *
   * @param eventSource the event source.
   * @param expectedCommentTypes the comment types expected. Any comment types not included are not
   *     collected.
   */
  public CommentEventsCollector(Queue<Event> eventSource, CommentType... expectedCommentTypes) {
    this.eventSource = eventSource;
    this.expectedCommentTypes = expectedCommentTypes;
    commentLineList = new ArrayList<>();
  }

  /**
   * Determine if the event is a comment of one of the expected types set during construction.
   *
   * @param event the event to test.
   * @return <code>true</code> if the events is a comment of the expected type; Otherwise, false.
   */
  private boolean isEventExpected(Event event) {
    if (event == null || event.getEventId() != Event.ID.Comment) {
      return false;
    }
    CommentEvent commentEvent = (CommentEvent) event;
    for (CommentType type : expectedCommentTypes) {
      if (commentEvent.getCommentType() == type) {
        return true;
      }
    }
    return false;
  }

  /**
   * Collect all events of the expected type (set during construction) starting with the top event
   * on the event source. Collection stops as soon as a non comment or comment of the unexpected
   * type is encountered.
   *
   * @return this object.
   */
  public CommentEventsCollector collectEvents() {
    collectEvents(null);
    return this;
  }

  /**
   * Collect all events of the expected type (set during construction) starting with event provided
   * as an argument and continuing with the top event on the event source. Collection stops as soon
   * as a non comment or comment of the unexpected type is encountered.
   *
   * @param event the first event to attempt to collect.
   * @return the event provided as an argument, if it is not collected; Otherwise, <code>null</code>
   */
  public Event collectEvents(Event event) {
    if (event != null) {
      if (isEventExpected(event)) {
        commentLineList.add(new CommentLine((CommentEvent) event));
      } else {
        return event;
      }
    }
    while (isEventExpected(eventSource.peek())) {
      commentLineList.add(new CommentLine((CommentEvent) eventSource.poll()));
    }
    return null;
  }

  /**
   * Collect all events of the expected type (set during construction) starting with event provided
   * as an argument and continuing with the top event on the event source. Collection stops as soon
   * as a non comment or comment of the unexpected type is encountered.
   *
   * @param event the first event to attempt to collect.
   * @return the event provided as an argument, if it is not collected; Otherwise, the first event
   *     that is not collected.
   */
  public Event collectEventsAndPoll(Event event) {
    Event nextEvent = collectEvents(event);
    return nextEvent != null ? nextEvent : eventSource.poll();
  }

  /**
   * Return the events collected and reset the collector.
   *
   * @return the events collected.
   */
  public List<CommentLine> consume() {
    try {
      return commentLineList;
    } finally {
      commentLineList = new ArrayList<>();
    }
  }

  /**
   * Test if the collector contains any collected events.
   *
   * @return <code>true</code> if it does; Otherwise, <code>false</code>
   */
  public boolean isEmpty() {
    return commentLineList.isEmpty();
  }
}
