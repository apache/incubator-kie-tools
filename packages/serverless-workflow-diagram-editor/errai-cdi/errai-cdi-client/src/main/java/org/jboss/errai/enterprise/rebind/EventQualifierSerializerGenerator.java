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


package org.jboss.errai.enterprise.rebind;

import javax.inject.Qualifier;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.config.rebind.AbstractAsyncGenerator;
import org.jboss.errai.config.rebind.GenerateAsync;
import org.jboss.errai.enterprise.client.cdi.EventQualifierSerializer;
import org.jboss.errai.ioc.util.TranslatableAnnotationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.jboss.errai.enterprise.client.cdi.EventQualifierSerializer.SERIALIZER_CLASS_NAME;
import static org.jboss.errai.enterprise.client.cdi.EventQualifierSerializer.SERIALIZER_PACKAGE_NAME;

/**
 *
 * @author Max Barkley <mbarkley@redhat.com>
 */
@GenerateAsync(EventQualifierSerializer.class)
public class EventQualifierSerializerGenerator extends AbstractAsyncGenerator {

  private static final Logger logger = LoggerFactory.getLogger(EventQualifierSerializerGenerator.class);

  @Override
  public String generate(final TreeLogger logger, final GeneratorContext context, final String typeName) {
    return startAsyncGeneratorsAndWaitFor(EventQualifierSerializer.class, context, logger, SERIALIZER_PACKAGE_NAME, SERIALIZER_CLASS_NAME);
  }

  @Override
  protected String generate(final TreeLogger treeLogger, final GeneratorContext context) {
    logger.info("Generating {}.{}...", SERIALIZER_PACKAGE_NAME, SERIALIZER_CLASS_NAME);
    return NonGwtEventQualifierSerializerGenerator.generateSource(TranslatableAnnotationUtils.getTranslatableQualifiers(context.getTypeOracle()));
  }

  @Override
  protected boolean isRelevantClass(final MetaClass clazz) {
    return clazz.isAnnotation() && clazz.isAnnotationPresent(Qualifier.class);
  }

}
