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
package org.snakeyaml.engine.v2.parser;

import java.util.Iterator;

import org.snakeyaml.engine.v2.events.Event;
import org.snakeyaml.engine.v2.exceptions.ParserException;

/**
 * This interface represents an input stream of {@link Event Events}.
 *
 * <p>The parser and the scanner form together the 'Parse' step in the loading process.
 *
 * @see <a href="https://yaml.org/spec/1.2.2/#31-processes">Figure 3.1. Processing Overview</a>
 */
public interface Parser extends Iterator<Event> {

  /**
   * Check if the next event is one of the given type.
   *
   * @param choice Event ID to match
   * @return <code>true</code> if the next event has the given ID. Returns <code>false</code> if no
   *     more events are available.
   * @throws ParserException Thrown in case of malformed input.
   */
  boolean checkEvent(Event.ID choice);

  /**
   * Return the next event, but do not delete it from the stream.
   *
   * @return The event that will be returned on the next call to {@link #next}
   * @throws ParserException Thrown in case of malformed input or NoSuchElementException in case no
   *     event is available.
   */
  Event peekEvent();

  /**
   * Returns the next event.
   *
   * <p>The event will be removed from the stream.
   *
   * @return the next parsed event
   * @throws ParserException Thrown in case of malformed input.
   */
  Event next();
}
