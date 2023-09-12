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


package org.jboss.errai.codegen;

import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.meta.MetaClassFactory;

/**
 * @author Mike Brock
 */
public class Comment implements Statement {
  private final String comment;

  public Comment(final String comment) {
    this.comment = comment.trim();
  }

  @Override
  public String generate(final Context context) {
    final StringBuilder sb = new StringBuilder("// ");
    for (int i = 0; i < comment.length(); i++) {
      switch (comment.charAt(i)) {
        case '\r':
          continue;
        case '\n':
          sb.append('\n').append("// ");
          continue;
        default:
          sb.append(comment.charAt(i));
      }
    }
    return sb.toString();
  }

  @Override
  public MetaClass getType() {
    return MetaClassFactory.get(void.class);
  }
}
