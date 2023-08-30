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


package org.jboss.errai.ioc.tests.wiring.client.res;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Typed;

import org.jboss.errai.common.client.api.annotations.IOCProducer;

import static org.jboss.errai.ioc.tests.wiring.client.res.QualForProducedTypeBean.ProducerType.FIELD;
import static org.jboss.errai.ioc.tests.wiring.client.res.QualForProducedTypeBean.ProducerType.METHOD;

/**
 *
 * @author Max Barkley <mbarkley@redhat.com>
 */
@Dependent
public class TypedProducer {

  @QualForProducedTypeBean(isStatic = true, type = METHOD)
  @Typed({TypedType.class, TypedTargetInterface.class})
  @IOCProducer
  public static TypedType staticProducerMethod() {
    return new TypedType();
  }

  @QualForProducedTypeBean(isStatic = true, type = FIELD)
  @Typed({TypedType.class, TypedTargetInterface.class})
  @IOCProducer
  public static TypedType staticProducerField = new TypedType();

  @QualForProducedTypeBean(isStatic = false, type = METHOD)
  @Typed({TypedType.class, TypedTargetInterface.class})
  @IOCProducer
  public TypedType instanceProducerMethod() {
    return new TypedType();
  }

  @QualForProducedTypeBean(isStatic = false, type = FIELD)
  @Typed({TypedType.class, TypedTargetInterface.class})
  @IOCProducer
  public TypedType instanceProducerField = new TypedType();

}
