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


package org.jboss.errai.config.rebind;

import java.util.concurrent.Future;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import org.jboss.errai.common.client.api.Assert;

/**
 * @author Mike Brock
 */
public class AsyncGenerationJob {
  private final Runnable runIfStarting;
  private final Runnable runIfStarted;
  private final TreeLogger treeLogger;
  private final GeneratorContext generatorContext;
  private final Class<?> interfaceType;

  private AsyncGenerationJob(Builder builder) {
    this.runIfStarted = builder.runIfStarted;
    this.runIfStarting = builder.runIfStarting;
    this.treeLogger = Assert.notNull("treeLogger cannot be null", builder.treeLogger);
    this.generatorContext = Assert.notNull("generatorContext cannot be null", builder.generatorContext);
    this.interfaceType = Assert.notNull("interfaceType cannot be null", builder.interfaceType);
  }

  public static Builder createBuilder() {
   return new Builder();
  }

  public static class Builder {
    private Runnable runIfStarting;
    private Runnable runIfStarted;
    private GeneratorContext generatorContext;
    private TreeLogger treeLogger;
    private Class<?> interfaceType;

    public Builder runIfStarting(Runnable runnable) {
      this.runIfStarting = runnable;
      return this;
    }

    public Builder runIfStarted(Runnable runnable) {
      this.runIfStarted = runnable;
      return this;
    }

    public Builder generatorContext(GeneratorContext context) {
      this.generatorContext = context;
      return this;
    }

    public Builder treeLogger(TreeLogger treeLogger) {
      this.treeLogger = treeLogger;
      return this;
    }

    public Builder interfaceType(Class<?> interfaceType) {
      this.interfaceType = interfaceType;
      return this;
    }

    public AsyncGenerationJob build() {
      return new AsyncGenerationJob(this);
    }
  }

  void notifyStarting() {
    if (runIfStarting != null) {
      runIfStarting.run();
    }
  }

  void notifyStarted() {
    if (runIfStarted != null) {
      runIfStarted.run();
    }
  }

  public TreeLogger getTreeLogger() {
    return treeLogger;
  }

  public GeneratorContext getGeneratorContext() {
    return generatorContext;
  }

  public Class<?> getInterfaceType() {
    return interfaceType;
  }

  public Future<String> submit() {
    return AsyncGenerators.getFutureFor(this);
  }
}
