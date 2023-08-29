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


package org.jboss.errai.codegen.literal;

import java.util.HashMap;
import java.util.Map;

import org.jboss.errai.codegen.Context;
import org.jboss.errai.codegen.builder.AnonymousClassStructureBuilder;
import org.jboss.errai.codegen.builder.BlockBuilder;
import org.jboss.errai.codegen.builder.impl.ObjectBuilder;
import org.jboss.errai.codegen.util.Stmt;

/**
 * @author Mike Brock
 */
public class MapValue extends LiteralValue<Map<Object, Object>> {
  public MapValue(final Map<Object, Object> value) {
    super(value);
  }

  @Override
  public String getCanonicalString(final Context context) {
    final BlockBuilder<AnonymousClassStructureBuilder> initBlock
            = ObjectBuilder.newInstanceOf(HashMap.class, context).extend().initialize();

    for (final Map.Entry<Object, Object> v : getValue().entrySet()) {
      initBlock.append(Stmt.loadVariable("this").invoke("put", LiteralFactory.getLiteral(v.getKey()),
              LiteralFactory.getLiteral(v.getValue())));
    }

    return initBlock.finish().finish().toJavaString();
  }
}
