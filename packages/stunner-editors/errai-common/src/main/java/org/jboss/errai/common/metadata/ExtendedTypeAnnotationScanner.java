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


package org.jboss.errai.common.metadata;

import java.lang.annotation.Inherited;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javassist.bytecode.ClassFile;
import org.jboss.errai.common.metadata.MetaDataScanner.CacheHolder;
import org.jboss.errai.common.rebind.CacheUtil;
import org.jboss.errai.reflections.adapters.MetadataAdapter;
import org.jboss.errai.reflections.scanners.TypeAnnotationsScanner;

/**
 * @author Mike Brock
 */
public class ExtendedTypeAnnotationScanner extends TypeAnnotationsScanner {
  private final CacheHolder cache = CacheUtil.getCache(MetaDataScanner.CacheHolder.class);
  
  @Override
  public void scan(final Object cls) {
    final MetadataAdapter adapter = getMetadataAdapter();
    final String className = adapter.getClassName(cls);

    for (final String annotationType : (List<String>) adapter.getClassAnnotationNames(cls)) {
      if (acceptResult(annotationType) ||
          annotationType.equals(Inherited.class.getName())) { // as an exception, accept
        // Inherited as well
        getStore().put(annotationType, className);

        if (cls instanceof ClassFile) {
          Set<SortableClassFileWrapper> classes = cache.ANNOTATIONS_TO_CLASS.get(annotationType);
          if (classes == null) {
            cache.ANNOTATIONS_TO_CLASS.put(annotationType, classes = 
                    Collections.synchronizedSet(new TreeSet<SortableClassFileWrapper>()));
          }
          classes.add(new SortableClassFileWrapper(className, (ClassFile) cls));
        }
      }
    }
  }
}
