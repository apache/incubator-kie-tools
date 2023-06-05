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
package org.snakeyaml.engine.v2.api.lowlevel;

import java.io.StringWriter;
import java.util.Iterator;
import java.util.Objects;

import org.snakeyaml.engine.v2.GwtIncompatible;
import org.snakeyaml.engine.v2.api.DumpSettings;
import org.snakeyaml.engine.v2.api.StreamDataWriter;
import org.snakeyaml.engine.v2.emitter.Emitter;
import org.snakeyaml.engine.v2.events.Event;

/** Emit the events into a data stream (opposite for Parse) */
@GwtIncompatible
public class Present {

  private final DumpSettings settings;

  /**
   * Create Present (emitter)
   *
   * @param settings - configuration
   */
  public Present(DumpSettings settings) {
    Objects.requireNonNull(settings, "DumpSettings cannot be null");
    this.settings = settings;
  }

  /**
   * Serialise the provided Events
   *
   * @param events - the data to serialise
   * @return - the YAML document
   */
  public String emitToString(Iterator<Event> events) {
    Objects.requireNonNull(events, "events cannot be null");
    StreamToStringWriter writer = new StreamToStringWriter();
    final Emitter emitter = new Emitter(settings, writer);
    events.forEachRemaining(emitter::emit);
    return writer.toString();
  }
}

/** Internal helper class to support emitting to String */
@GwtIncompatible
class StreamToStringWriter extends StringWriter implements StreamDataWriter {}
