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
package org.snakeyaml.engine.v2.api;

import java.io.StringWriter;
import java.util.Collections;
import java.util.Iterator;
import java.util.Objects;

import org.snakeyaml.engine.v2.emitter.Emitter;
import org.snakeyaml.engine.v2.nodes.Node;
import org.snakeyaml.engine.v2.representer.BaseRepresenter;
import org.snakeyaml.engine.v2.representer.StandardRepresenter;
import org.snakeyaml.engine.v2.serializer.Serializer;

/**
 * Common way to serialize any Java instance(s). The instance is stateful. Only one of the 'dump'
 * methods may be called, and it may be called only once.
 */
public class Dump {

  /** Configuration options */
  protected DumpSettings settings;

  /** The component to translate Java instances to Nodes */
  protected BaseRepresenter representer;

  /**
   * Create instance
   *
   * @param settings - configuration
   */
  public Dump(DumpSettings settings) {
    this(settings, new StandardRepresenter(settings));
  }

  /**
   * Create instance
   *
   * @param settings - configuration
   * @param representer - custom representer
   */
  public Dump(DumpSettings settings, BaseRepresenter representer) {
    Objects.requireNonNull(settings, "DumpSettings cannot be null");
    Objects.requireNonNull(representer, "Representer cannot be null");
    this.settings = settings;
    this.representer = representer;
  }

  /**
   * Dump all the instances from the iterator into a stream with every instance in a separate YAML
   * document
   *
   * @param instancesIterator - instances to serialize
   * @param streamDataWriter - destination I/O writer
   */
  public void dumpAll(
      Iterator<? extends Object> instancesIterator, StreamDataWriter streamDataWriter) {
    Objects.requireNonNull(instancesIterator, "Iterator cannot be null");
    Objects.requireNonNull(streamDataWriter, "StreamDataWriter cannot be null");
    Serializer serializer = new Serializer(settings, new Emitter(settings, streamDataWriter));
    serializer.emitStreamStart();
    while (instancesIterator.hasNext()) {
      Object instance = instancesIterator.next();
      Node node = representer.represent(instance);
      serializer.serializeDocument(node);
    }
    serializer.emitStreamEnd();
  }

  /**
   * Dump a single instance into a YAML document
   *
   * @param yaml - instance to serialize
   * @param streamDataWriter - destination I/O writer
   */
  public void dump(Object yaml, StreamDataWriter streamDataWriter) {
    Iterator<? extends Object> iter = Collections.singleton(yaml).iterator();
    dumpAll(iter, streamDataWriter);
  }

  /**
   * Dump all the instances from the iterator into a stream with every instance in a separate YAML
   * document
   *
   * @param instancesIterator - instances to serialize
   * @return String representation of the YAML stream
   */
  public String dumpAllToString(Iterator<? extends Object> instancesIterator) {
    StreamToStringWriter writer = new StreamToStringWriter();
    dumpAll(instancesIterator, writer);
    return writer.toString();
  }

  /**
   * Dump all the instances from the iterator into a stream with every instance in a separate YAML
   * document
   *
   * @param yaml - instance to serialize
   * @return String representation of the YAML stream
   */
  public String dumpToString(Object yaml) {
    StreamToStringWriter writer = new StreamToStringWriter();
    dump(yaml, writer);
    return writer.toString();
  }

  /**
   * Dump the provided Node into a YAML stream.
   *
   * @param node - YAML node to be serialized to YAML document
   * @param streamDataWriter - stream to write to
   */
  public void dumpNode(Node node, StreamDataWriter streamDataWriter) {
    Objects.requireNonNull(node, "Node cannot be null");
    Objects.requireNonNull(streamDataWriter, "StreamDataWriter cannot be null");
    Serializer serializer = new Serializer(settings, new Emitter(settings, streamDataWriter));
    serializer.emitStreamStart();
    serializer.serializeDocument(node);
    serializer.emitStreamEnd();
  }
}

/** Internal helper class to support dumping to String */
class StreamToStringWriter implements StreamDataWriter {

  private final StringWriter buffer = new StringWriter();

  @Override
  public void write(String str) {

    try {
      buffer.write(str);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void write(String str, int off, int len) {
    try {
      buffer.write(str, off, len);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public String toString() {
    return buffer.toString();
  }
}
