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
package org.snakeyaml.engine.v2.constructor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;

import org.snakeyaml.engine.v2.GwtIncompatible;
import org.snakeyaml.engine.v2.api.ConstructNode;
import org.snakeyaml.engine.v2.api.LoadSettings;
import org.snakeyaml.engine.v2.env.EnvConfig;
import org.snakeyaml.engine.v2.exceptions.ConstructorException;
import org.snakeyaml.engine.v2.exceptions.DuplicateKeyException;
import org.snakeyaml.engine.v2.exceptions.Mark;
import org.snakeyaml.engine.v2.exceptions.MissingEnvironmentVariableException;
import org.snakeyaml.engine.v2.exceptions.YamlEngineException;
import org.snakeyaml.engine.v2.nodes.MappingNode;
import org.snakeyaml.engine.v2.nodes.Node;
import org.snakeyaml.engine.v2.nodes.NodeTuple;
import org.snakeyaml.engine.v2.nodes.SequenceNode;
import org.snakeyaml.engine.v2.nodes.Tag;
import org.snakeyaml.engine.v2.resolver.JsonScalarResolver;

/** Construct standard Java classes */
public class StandardConstructor extends BaseConstructor {

  /**
   * Create
   *
   * @param settings - configuration options
   */
  public StandardConstructor(LoadSettings settings) {
    super(settings);
    this.tagConstructors.put(Tag.SET, new ConstructYamlSet());
    this.tagConstructors.put(Tag.STR, new ConstructYamlStr());
    this.tagConstructors.put(Tag.SEQ, new ConstructYamlSeq());
    this.tagConstructors.put(Tag.MAP, new ConstructYamlMap());
    this.tagConstructors.put(Tag.ENV_TAG, new ConstructEnv());

    // apply the tag constructors from the provided schema
    this.tagConstructors.putAll(settings.getSchema().getSchemaTagConstructors());

    // the explicit config overrides all
    this.tagConstructors.putAll(settings.getTagConstructors());
  }

  /**
   * Flattening is not required because merge was removed from YAML 1.2 Only check duplications
   *
   * @param node - mapping to check the duplications
   */
  protected void flattenMapping(MappingNode node) {
    processDuplicateKeys(node);
  }

  /**
   * detect and process the duplicate key in mapping according to the configured setting
   *
   * @param node - the source
   */
  protected void processDuplicateKeys(MappingNode node) {
    List<NodeTuple> nodeValue = node.getValue();
    Map<Object, Integer> keys = new HashMap<>(nodeValue.size());
    TreeSet<Integer> toRemove = new TreeSet<>();
    int i = 0;
    for (NodeTuple tuple : nodeValue) {
      Node keyNode = tuple.getKeyNode();
      Object key = constructKey(keyNode, node.getStartMark(), tuple.getKeyNode().getStartMark());
      Integer prevIndex = keys.put(key, i);
      if (prevIndex != null) {
        if (!settings.getAllowDuplicateKeys()) {
          throw new DuplicateKeyException(
              node.getStartMark(), key, tuple.getKeyNode().getStartMark());
        }
        toRemove.add(prevIndex);
      }
      i = i + 1;
    }

    Iterator<Integer> indices2remove = toRemove.descendingIterator();
    while (indices2remove.hasNext()) {
      nodeValue.remove(indices2remove.next().intValue());
    }
  }

  private Object constructKey(
      Node keyNode, Optional<Mark> contextMark, Optional<Mark> problemMark) {
    Object key = constructObject(keyNode);
    if (key != null) {
      try {
        key.hashCode(); // check circular dependencies
      } catch (Exception e) {
        throw new ConstructorException(
            "while constructing a mapping",
            contextMark,
            "found unacceptable key " + key,
            problemMark,
            e);
      }
    }
    return key;
  }

  @Override
  protected void constructMapping2ndStep(MappingNode node, Map<Object, Object> mapping) {
    flattenMapping(node);
    super.constructMapping2ndStep(node, mapping);
  }

  @Override
  protected void constructSet2ndStep(MappingNode node, Set<Object> set) {
    flattenMapping(node);
    super.constructSet2ndStep(node, set);
  }

  /** Create Set instances */
  public class ConstructYamlSet implements ConstructNode {

    @Override
    public Object construct(Node node) {
      if (node.isRecursive()) {
        return constructedObjects.containsKey(node)
            ? constructedObjects.get(node)
            : createEmptySetForNode((MappingNode) node);
      } else {
        return constructSet((MappingNode) node);
      }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void constructRecursive(Node node, Object object) {
      if (node.isRecursive()) {
        constructSet2ndStep((MappingNode) node, (Set<Object>) object);
      } else {
        throw new YamlEngineException("Unexpected recursive set structure. Node: " + node);
      }
    }
  }

  /** Create String instances */
  public class ConstructYamlStr extends ConstructScalar {

    @Override
    public Object construct(Node node) {
      return constructScalar(node);
    }
  }

  /** Create the List implementation (configured in setting) */
  public class ConstructYamlSeq implements ConstructNode {

    @Override
    public Object construct(Node node) {
      SequenceNode seqNode = (SequenceNode) node;
      if (node.isRecursive()) {
        return createEmptyListForNode(seqNode);
      } else {
        return constructSequence(seqNode);
      }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void constructRecursive(Node node, Object data) {
      if (node.isRecursive()) {
        constructSequenceStep2((SequenceNode) node, (List<Object>) data);
      } else {
        throw new YamlEngineException("Unexpected recursive sequence structure. Node: " + node);
      }
    }
  }

  /** Create Map instance */
  public class ConstructYamlMap implements ConstructNode {

    @Override
    public Object construct(Node node) {
      MappingNode mappingNode = (MappingNode) node;
      if (node.isRecursive()) {
        return createEmptyMapFor(mappingNode);
      } else {
        return constructMapping(mappingNode);
      }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void constructRecursive(Node node, Object object) {
      if (node.isRecursive()) {
        constructMapping2ndStep((MappingNode) node, (Map<Object, Object>) object);
      } else {
        throw new YamlEngineException("Unexpected recursive mapping structure. Node: " + node);
      }
    }
  }

  /**
   * Construct scalar for format ${VARIABLE} replacing the template with the value from environment.
   *
   * @see <a href="https://bitbucket.org/snakeyaml/snakeyaml/wiki/Variable%20substitution">Variable
   *     substitution</a>
   * @see <a href="https://docs.docker.com/compose/compose-file/#variable-substitution">Variable
   *     substitution</a>
   */
  public class ConstructEnv extends ConstructScalar {

    public Object construct(Node node) {
      String val = constructScalar(node);
      Optional<EnvConfig> opt = settings.getEnvConfig();
      if (opt.isPresent()) {
        EnvConfig config = opt.get();
        Matcher matcher = JsonScalarResolver.ENV_FORMAT.matcher(val);
        matcher.matches();
        String name = matcher.group(1);
        String value = matcher.group(3);
        String nonNullValue = value != null ? value : "";
        String separator = matcher.group(2);
        String env = getEnv(name);
        Optional<String> overruled = config.getValueFor(name, separator, nonNullValue, env);
        return overruled.orElseGet(() -> apply(name, separator, nonNullValue, env));
      } else {
        return val;
      }
    }

    /**
     * Implement the logic for missing and unset variables
     *
     * @param name - variable name in the template
     * @param separator - separator in the template, can be :-, -, :?, ?
     * @param value - default value or the error in the template
     * @param environment - the value from environment for the provided variable
     * @return the value to apply in the template
     */
    public String apply(String name, String separator, String value, String environment) {
      if (environment != null && !environment.isEmpty()) {
        return environment;
      }
      // variable is either unset or empty
      if (separator != null) {
        // there is a default value or error
        if (separator.equals("?")) {
          if (environment == null) {
            throw new MissingEnvironmentVariableException(
                "Missing mandatory variable " + name + ": " + value);
          }
        }
        if (separator.equals(":?")) {
          if (environment == null) {
            throw new MissingEnvironmentVariableException(
                "Missing mandatory variable " + name + ": " + value);
          }
          if (environment.isEmpty()) {
            throw new MissingEnvironmentVariableException(
                "Empty mandatory variable " + name + ": " + value);
          }
        }
        if (separator.startsWith(":")) {
          if (environment == null || environment.isEmpty()) {
            return value;
          }
        } else {
          if (environment == null) {
            return value;
          }
        }
      }
      return "";
    }

    /**
     * Get value of the environment variable
     *
     * @param key - the name of the variable
     * @return value or null if not set
     */
    @SuppressWarnings("squid:S5304")
    public String getEnv(String key) {
      return new Env().getEnv(key);
    }
  }

  private static class Env extends GWTEnv {

    @GwtIncompatible
    protected String getEnv(String key) {
      return System.getenv(key);
    }
  }

  private static class GWTEnv {

    protected String getEnv(String key) {
      throw new UnsupportedOperationException("Not implemented in GWT.");
    }
  }
}
