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


package org.jboss.errai.codegen.builder.callstack;

import java.util.Arrays;

import org.jboss.errai.codegen.Context;
import org.jboss.errai.codegen.Statement;
import org.jboss.errai.codegen.VariableReference;
import org.jboss.errai.codegen.exception.GenerationException;
import org.jboss.errai.codegen.exception.InvalidTypeException;
import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.meta.MetaClassFactory;
import org.jboss.errai.codegen.util.GenUtil;

/**
 * {@link CallElement} to load {@link VariableReference}s. Indexes can be provided in case of an array.
 *
 * @author Mike Brock <cbrock@redhat.com>
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public class LoadVariable extends AbstractCallElement {
  private final String variableName;
  private final Object[] indexes;
  private boolean classMember;

  public LoadVariable(final String variableName, final Object... indexes) {
    this.variableName = variableName;
    this.indexes = indexes == null ? new Object[0] : indexes;
  }

  public LoadVariable(final String variableName, final boolean classMember, final Object... indexes) {
    this(variableName, indexes);
    this.classMember = classMember;
  }

  @Override
  public void handleCall(final CallWriter writer, final Context context, final Statement statement) {
    writer.reset();

    try {
      final Statement[] idx = new Statement[this.indexes.length];
      for (int i = 0; i < idx.length; i++) {
        idx[i] = GenUtil.convert(context, GenUtil.generate(context, this.indexes[i]), MetaClassFactory.get(Integer.class));
      }
  
      final VariableReference ref = context.getVariable(variableName);
  
      if (idx.length > 0) {
        if (!ref.getType().isArray()) {
          throw new InvalidTypeException("attempt to use indexed accessor on non-array type: " + ref);
        }
      }
  
      final Statement stmt = new VariableReference() {
        @Override
        public String getName() {
          return ref.getName();
        }
  
        @Override
        public Statement getValue() {
          return ref.getValue();
        }
  
        String generatedCache;
  
        @Override
        public String generate(final Context context) {
          if (generatedCache != null) return generatedCache;

          if (variableName.equals("this") && next != null && !(next instanceof ReturnValue)) {
            return generatedCache = "";
          }

          final StringBuilder buf = new StringBuilder((classMember
                  && context.isAmbiguous(ref.getName()) ? "this." : "").concat(getName()));
  
          Arrays.stream(idx).forEach(s -> buf.append('[').append(s.generate(context)).append(']'));
          return generatedCache = buf.toString();
        }
  
        @Override
        public MetaClass getType() {
          final MetaClass ret;
  
          final int dims = GenUtil.getArrayDimensions(ref.getType());
  
          if (ref.getType().isArray() && idx.length > 0) {
            final int newDims = dims - idx.length;
            if (newDims > 0) {
              ret = ref.getType().getOuterComponentType().asArrayOf(dims - idx.length);
            }
            else {
              ret = ref.getType().getOuterComponentType();
            }
          }
          else {
            ret = ref.getType();
          }
  
          return ret;
        }
      };
  
      ref.setIndexes(idx);
      nextOrReturn(writer, context, stmt);
    } 
    catch (final GenerationException e) {
      blameAndRethrow(e);
    }
  }

  public String getVariableName() {
    return variableName;
  }

  @Override
  public String toString() {
    return "[[LoadVariable<" + variableName + ">]" + next + "]";
  }
}
