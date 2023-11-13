/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.jboss.errai.common.rebind;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

/**
 * To be used in conjunction with {@link NameUtil} for generating unique
 * identifier names for fully qualified class names.
 *
 * @author Max Barkley <mbarkley@redhat.com>
 */
public class UniqueNameGenerator {

  private final Multiset<String> usedNames = HashMultiset.create();

  /**
   * @param name
   *          An identifier that does not end with an underscore followed by an
   *          integer. If the name has such an ending, the return value is not
   *          guaranteed to be unique.
   * @return A unique name (will have a number appended to the end if the given
   *         name has been used before).
   */
  public String uniqueName(final String name) {
    final String uniqueName;

    final int collisions = usedNames.count(name);
    if (collisions > 0) {
      uniqueName = name + "_" + String.valueOf(collisions);
    } else {
      uniqueName = name;
    }
    usedNames.add(name);

    return uniqueName;
  }

}
