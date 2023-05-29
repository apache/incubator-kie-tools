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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.snakeyaml.engine.v2.api.DumpSettings;
import org.snakeyaml.engine.v2.emitter.Emitable;
import org.snakeyaml.engine.v2.events.Event;
import org.snakeyaml.engine.v2.nodes.Node;
import org.snakeyaml.engine.v2.serializer.Serializer;

/** Implementation of the step which translates Nodes to Events */
public class Serialize {

  private final DumpSettings settings;

  /**
   * Create instance with provided {@link DumpSettings}
   *
   * @param settings - configuration
   */
  public Serialize(DumpSettings settings) {
    Objects.requireNonNull(settings, "DumpSettings cannot be null");
    this.settings = settings;
  }

  /**
   * Serialize a {@link Node} and produce events.
   *
   * @param node - {@link Node} to serialize
   * @return serialized events
   * @see <a href="http://www.yaml.org/spec/1.2/spec.html#id2762107">Processing Overview</a>
   */
  public List<Event> serializeOne(Node node) {
    Objects.requireNonNull(node, "Node cannot be null");
    return serializeAll(Collections.singletonList(node));
  }

  /**
   * Serialize {@link Node}s and produce events.
   *
   * @param nodes - {@link Node}s to serialize
   * @return serialized events
   * @see <a href="http://www.yaml.org/spec/1.2/spec.html#id2762107">Processing Overview</a>
   */
  public List<Event> serializeAll(List<Node> nodes) {
    Objects.requireNonNull(nodes, "Nodes cannot be null");
    EmitableEvents emitableEvents = new EmitableEvents();
    Serializer serializer = new Serializer(settings, emitableEvents);
    serializer.emitStreamStart();
    for (Node node : nodes) {
      serializer.serializeDocument(node);
    }
    serializer.emitStreamEnd();
    return emitableEvents.getEvents();
  }
}

class EmitableEvents implements Emitable {

  private final List<Event> events = new ArrayList<>();

  @Override
  public void emit(Event event) {
    events.add(event);
  }

  public List<Event> getEvents() {
    return events;
  }
}
