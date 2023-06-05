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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.snakeyaml.engine.v2.nodes.Tag;

/** Base resolver */
public abstract class BaseScalarResolver implements ScalarResolver {

  /** No value indication */
  public static final Pattern EMPTY = Pattern.compile("^$");

  /** group 1: name, group 2: separator, group 3: value */
  @SuppressWarnings("squid:S4784")
  public static final Pattern ENV_FORMAT =
      Pattern.compile("^\\$\\{\\s*(?:(\\w+)(?:(:?[-?])(\\w+)?)?)\\s*\\}$");

  /** Map from the char to the resolver which may begin with this char */
  protected Map<Character, List<ResolverTuple>> yamlImplicitResolvers = new HashMap<>();

  /** Create */
  public BaseScalarResolver() {
    addImplicitResolvers();
  }

  /**
   * Add a resolver to resolve a value that matches the provided regular expression to the provided
   * tag
   *
   * @param tag - the Tag to assign when the value matches
   * @param regexp - the RE which is applied for every value
   * @param first - the possible first characters (this is merely for performance improvement) to
   *     skip RE evaluation to gain time
   */
  public void addImplicitResolver(Tag tag, Pattern regexp, String first) {
    if (first == null) {
      List<ResolverTuple> curr =
          yamlImplicitResolvers.computeIfAbsent(null, c -> new ArrayList<>());
      curr.add(new ResolverTuple(tag, regexp));
    } else {
      char[] chrs = first.toCharArray();
      for (int i = 0, j = chrs.length; i < j; i++) {
        Character theC = Character.valueOf(chrs[i]);
        if (theC == 0) {
          // special case: for null
          theC = null;
        }
        List<ResolverTuple> curr = yamlImplicitResolvers.get(theC);
        if (curr == null) {
          curr = new ArrayList<>();
          yamlImplicitResolvers.put(theC, curr);
        }
        curr.add(new ResolverTuple(tag, regexp));
      }
    }
  }

  /** Register all the resolvers to be applied */
  abstract void addImplicitResolvers();

  @Override
  public Tag resolve(String value, Boolean implicit) {
    if (!implicit) {
      return Tag.STR;
    }
    final List<ResolverTuple> resolvers;
    if (value.length() == 0) {
      resolvers = yamlImplicitResolvers.get('\0');
    } else {
      resolvers = yamlImplicitResolvers.get(value.charAt(0));
    }
    if (resolvers != null) {
      for (ResolverTuple v : resolvers) {
        Tag tag = v.getTag();
        Pattern regexp = v.getRegexp();
        if (regexp.matcher(value).matches()) {
          return tag;
        }
      }
    }
    if (yamlImplicitResolvers.containsKey(null)) {
      for (ResolverTuple v : yamlImplicitResolvers.get(null)) {
        Tag tag = v.getTag();
        Pattern regexp = v.getRegexp();
        if (regexp.matcher(value).matches()) {
          return tag;
        }
      }
    }
    return Tag.STR;
  }
}
