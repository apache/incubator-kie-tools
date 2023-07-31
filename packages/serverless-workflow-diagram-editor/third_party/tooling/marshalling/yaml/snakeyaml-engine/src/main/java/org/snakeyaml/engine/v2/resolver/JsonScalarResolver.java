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
package org.snakeyaml.engine.v2.resolver;

import java.util.regex.Pattern;

import org.snakeyaml.engine.v2.nodes.Tag;

/**
 * ScalarResolver for JSON Schema
 *
 * @see <a href="http://www.yaml.org/spec/1.2/spec.html#id2803231">Chapter 10.2. JSON Schema</a>
 */
public class JsonScalarResolver extends BaseScalarResolver {

  /** Boolean as defined in JSON */

  public static final Pattern BOOL = Pattern.compile("^(?:true|false)$");

  /** Float as defined in JSON (Number which is Float) */
  public static final Pattern FLOAT =
      Pattern.compile("^((-?(0|[1-9][0-9]*)(\\.[0-9]*)?([eE][-+]?[0-9]+)?)|(-?\\.inf)|(\\.nan))$");

  /** Integer as defined in JSON (Number which is Integer) */
  public static final Pattern INT = Pattern.compile("^-?(0|[1-9][0-9]*)$");

  /** Null as defined in JSON */
  public static final Pattern NULL = Pattern.compile("^(?:null)$");

  /** Register all the resolvers to be applied */
  @Override
  protected void addImplicitResolvers() {
    addImplicitResolver(Tag.NULL, EMPTY, null);
    addImplicitResolver(Tag.BOOL, BOOL, "tf");
    /*
     * INT must be before FLOAT because the regular expression for FLOAT matches INT (see issue 130)
     * http://code.google.com/p/snakeyaml/issues/detail?id=130
     */
    addImplicitResolver(Tag.INT, INT, "-0123456789");
    addImplicitResolver(Tag.FLOAT, FLOAT, "-0123456789.");
    addImplicitResolver(Tag.NULL, NULL, "n\u0000");
    addImplicitResolver(Tag.ENV_TAG, ENV_FORMAT, "$");
  }
}
