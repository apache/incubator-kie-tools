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


package org.jboss.errai.ioc.rebind.ioc.bootstrapper;

import java.util.Collection;
import java.util.Set;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.meta.MetaClassFactory;
import org.jboss.errai.common.metadata.RebindUtils;
import org.jboss.errai.config.rebind.AbstractAsyncGenerator;
import org.jboss.errai.config.rebind.EnvUtil;
import org.jboss.errai.config.rebind.GenerateAsync;
import org.jboss.errai.ioc.client.Bootstrapper;
import org.jboss.errai.ioc.client.container.IOCEnvironment;

/**
 * The main generator class for the Errai IOC framework.
 * <p/>
 * <pre>
 *
 * </pre>
 *
 * @author Mike Brock
 * @author Christian Sadilek <csadilek@redhat.com>
 */
@GenerateAsync(Bootstrapper.class)
public class IOCGenerator extends AbstractAsyncGenerator {
  private final String className = Bootstrapper.class.getSimpleName() + "Impl";
  private final String packageName = Bootstrapper.class.getPackage().getName();

  public static final boolean isTestMode = EnvUtil.isJUnitTest();

  public IOCGenerator() {
  }

  @Override
  public String generate(final TreeLogger logger,
                         final GeneratorContext context,
                         final String typeName)
      throws UnableToCompleteException {

    logger.log(TreeLogger.INFO, "generating ioc bootstrapping code...");
    return startAsyncGeneratorsAndWaitFor(Bootstrapper.class, context, logger, packageName, className);
  }

  @Override
  protected String generate(TreeLogger logger, GeneratorContext context) {
    final Set<String> translatablePackages = RebindUtils.findTranslatablePackages(context);

    final IOCBootstrapGenerator iocBootstrapGenerator = new IOCBootstrapGenerator(context, logger,
        translatablePackages, false);

    return iocBootstrapGenerator.generate(packageName, className);
  }

  @Override
  protected boolean isCacheValid() {
    // This ensures the logged total build time of factories is reset even if
    // the BootstrapperImpl is not regenerated.
    FactoryGenerator.resetTotalTime();
    Collection<MetaClass> newOrUpdated = MetaClassFactory.getAllNewOrUpdatedClasses();
    // filter out generated IOC environment config
    if (newOrUpdated.size() == 1) {
      MetaClass clazz = newOrUpdated.iterator().next();
      if (clazz.isAssignableTo(IOCEnvironment.class)) {
        newOrUpdated.clear();
      }
    }

    boolean hasAnyChanges =  !newOrUpdated.isEmpty() || !MetaClassFactory.getAllDeletedClasses().isEmpty();
    return hasGenerationCache() && (EnvUtil.isProdMode() || !hasAnyChanges);
  }

}
