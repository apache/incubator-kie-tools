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

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.function.Predicate;

import com.google.common.collect.Multimap;
import org.jboss.errai.reflections.scanners.AbstractScanner;
import org.jboss.errai.reflections.vfs.Vfs;

public class ErraiPropertyScanner extends AbstractScanner {

  private final Predicate<String> predicate;

  ErraiPropertyScanner(final Predicate<String> predicate) {
    this.predicate = predicate;
  }

  public boolean acceptsInput(final String file) {
    return predicate.test(file);
  }

  public void scan(final Vfs.File file) {
    try (final InputStream fileInputStream = file.openInputStream()) {
      final Properties properties = new Properties();
      properties.load(fileInputStream);

      final Multimap<String, String> store = getStore();
      for (final Map.Entry<Object, Object> entry : properties.entrySet()) {
        store.put((String) entry.getKey(), (String) entry.getValue());
      }
    }
    catch (IOException e) {
      throw new RuntimeException("Failed to load properties: " + file.getFullPath(), e);
    }
  }

  public void scan(final Object cls) {
    throw new UnsupportedOperationException(); //shouldn't get here
  }

  public Multimap<String, String> getProperties() {
    return getStore();
  }
}
