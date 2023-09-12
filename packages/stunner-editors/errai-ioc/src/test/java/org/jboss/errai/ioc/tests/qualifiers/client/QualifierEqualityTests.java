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


package org.jboss.errai.ioc.tests.qualifiers.client;

import java.lang.annotation.Annotation;

import com.google.gwt.core.client.GWT;
import com.google.gwt.junit.client.GWTTestCase;
import org.jboss.errai.ioc.client.QualifierEqualityFactory;
import org.jboss.errai.ioc.client.QualifierEqualityFactoryProvider;
import org.jboss.errai.ioc.client.QualifierUtil;
import org.jboss.errai.ioc.tests.qualifiers.client.res.LesAnno;

/**
 * @author Mike Brock
 */

public class QualifierEqualityTests extends GWTTestCase {
  @Override
  protected void gwtSetUp() throws Exception {
    super.gwtSetUp();
    QualifierUtil.initFromFactoryProvider(new QualifierEqualityFactoryProvider() {
      @Override
      public QualifierEqualityFactory provide() {
        return GWT.create(QualifierEqualityFactory.class);
      }
    });
  }

  @Override
  public String getModuleName() {
    return "org.jboss.errai.ioc.tests.qualifiers.QualifierEqualityTests";
  }

  public void testQualifierEquality1() {
    LesAnno anno1 = new LesAnno() {
      @Override
      public Class[] value() {
        return new Class[] { String.class, Integer.class, Float.class };
      }

      @Override
      public Class<? extends Annotation> annotationType() {
        return LesAnno.class;
      }
    };

    LesAnno anno2 = new LesAnno() {
      @Override
      public Class[] value() {
        return new Class[] { String.class, Integer.class, Float.class };
      }

      @Override
      public Class<? extends Annotation> annotationType() {
        return LesAnno.class;
      }
    };

    LesAnno anno3 = new LesAnno() {
       @Override
       public Class[] value() {
         return new Class[] { String.class, Integer.class, Short.class };
       }

       @Override
       public Class<? extends Annotation> annotationType() {
         return LesAnno.class;
       }
     };


    assertTrue("anno1 should be same as anno2",  QualifierUtil.isEqual(anno1, anno2));
    assertFalse("annot should be different from anno3", QualifierUtil.isEqual(anno1, anno3));
  }
}
