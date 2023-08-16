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


package org.jboss.errai.ui.test.error.client;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.jboss.errai.enterprise.client.cdi.AbstractErraiCDITest;
import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.test.error.client.res.MissingPackagePrivateFieldInTemplate;
import org.jboss.errai.ui.test.error.client.res.MissingPackagePrivateSubclass;
import org.jboss.errai.ui.test.error.client.res.MissingPrivateFieldInTemplate;
import org.jboss.errai.ui.test.error.client.res.MissingPrivateSubclass;
import org.jboss.errai.ui.test.error.client.res.MissingProtectedFieldInTemplate;
import org.jboss.errai.ui.test.error.client.res.MissingProtectedSubclass;
import org.jboss.errai.ui.test.error.client.res.MissingPublicFieldInTemplate;
import org.jboss.errai.ui.test.error.client.res.MissingPublicSubclass;
import org.jboss.errai.ui.test.error.client.res.Subclass;
import org.jboss.errai.ui.test.error.client.res.SubclassMissingField;
import org.jboss.errai.ui.test.error.client.res.SubclassOfAbstractWithTemplateMissingPackagePrivateField;
import org.jboss.errai.ui.test.error.client.res.SubclassOfAbstractWithTemplateMissingPrivateField;
import org.jboss.errai.ui.test.error.client.res.SubclassOfAbstractWithTemplateMissingProtectedField;
import org.jboss.errai.ui.test.error.client.res.SubclassOfAbstractWithTemplateMissingPublicField;
import org.jboss.errai.ui.test.error.client.res.SubclassWithTemplateMissingPackagePrivateField;
import org.jboss.errai.ui.test.error.client.res.SubclassWithTemplateMissingPrivateField;
import org.jboss.errai.ui.test.error.client.res.SubclassWithTemplateMissingProtectedField;
import org.jboss.errai.ui.test.error.client.res.SubclassWithTemplateMissingPublicField;
import org.jboss.errai.ui.test.error.client.res.TemplateWithDifferentDataFieldElementKind;
import org.jboss.errai.ui.test.error.client.res.TemplateWithNullDataField;

public class TemplateErrorTest extends AbstractErraiCDITest {

  private Subclass anno = new Subclass() {
    @Override
    public Class<? extends Annotation> annotationType() {
      return Subclass.class;
    }
  };

  @Override
  public String getModuleName() {
    return getClass().getName().replaceAll("client.*$", "Test");
  }

  public void testMissingPrivateDataField() throws Exception {
    try {
      IOC.getBeanManager().lookupBean(MissingPrivateFieldInTemplate.class).getInstance();
      fail("No error was thrown for missing data-field.");
    } catch (AssertionError ae) {
      throw ae;
    } catch (Throwable t) {
      assertTrue("Unexpected error message: " + t.getMessage(), t.getMessage().contains("did not contain data-field"));
      assertTrue("Message did not reference the unsatisfied data-field: " + t.getMessage(),
              t.getMessage().contains(MissingPrivateFieldInTemplate.class.getSimpleName() + ".field"));
    }
  }

  public void testMissingPackagePrivateDataField() throws Exception {
    try {
      IOC.getBeanManager().lookupBean(MissingPackagePrivateFieldInTemplate.class).getInstance();
      fail("No error was thrown for missing data-field.");
    } catch (AssertionError ae) {
      throw ae;
    } catch (Throwable t) {
      assertTrue("Unexpected error message: " + t.getMessage(), t.getMessage().contains("did not contain data-field"));
      assertTrue("Message did not reference the unsatisfied data-field: " + t.getMessage(),
              t.getMessage().contains(MissingPackagePrivateFieldInTemplate.class.getSimpleName() + ".field"));
    }
  }

  public void testMissingPublicDataField() throws Exception {
    try {
      IOC.getBeanManager().lookupBean(MissingPublicFieldInTemplate.class).getInstance();
      fail("No error was thrown for missing data-field.");
    } catch (AssertionError ae) {
      throw ae;
    } catch (Throwable t) {
      assertTrue("Unexpected error message: " + t.getMessage(), t.getMessage().contains("did not contain data-field"));
      assertTrue("Message did not reference the unsatisfied data-field: " + t.getMessage(),
              t.getMessage().contains(MissingPublicFieldInTemplate.class.getSimpleName() + ".field"));
    }
  }

  public void testMissingProtectedDataField() throws Exception {
    try {
      IOC.getBeanManager().lookupBean(MissingProtectedFieldInTemplate.class).getInstance();
      fail("No error was thrown for missing data-field.");
    } catch (AssertionError ae) {
      throw ae;
    } catch (Throwable t) {
      assertTrue("Unexpected error message: " + t.getMessage(), t.getMessage().contains("did not contain data-field"));
      assertTrue("Message did not reference the unsatisfied data-field: " + t.getMessage(),
              t.getMessage().contains(MissingProtectedFieldInTemplate.class.getSimpleName() + ".field"));
    }
  }

  public void testSubclassWithSameTemplateMissingProtectedDataField() throws Exception {
    try {
      IOC.getBeanManager().lookupBean(MissingProtectedSubclass.class, anno).getInstance();
      fail("No error was thrown for missing data-field.");
    } catch (AssertionError ae) {
      throw ae;
    } catch (Throwable t) {
      assertTrue("Unexpected error message: " + t.getMessage(), t.getMessage().contains("did not contain data-field"));
      assertTrue("Message did not reference the unsatisfied data-field: " + t.getMessage(),
              t.getMessage().contains(MissingProtectedSubclass.class.getSimpleName() + ".field"));
    }
  }

  public void testSubclassWithSameTemplateMissingPrivateDataField() throws Exception {
    try {
      IOC.getBeanManager().lookupBean(MissingPrivateSubclass.class, anno).getInstance();
      fail("No error was thrown for missing data-field.");
    } catch (AssertionError ae) {
      throw ae;
    } catch (Throwable t) {
      assertTrue("Unexpected error message: " + t.getMessage(), t.getMessage().contains("did not contain data-field"));
      assertTrue("Message did not reference the unsatisfied data-field: " + t.getMessage(),
              t.getMessage().contains(MissingPrivateSubclass.class.getSimpleName() + ".field"));
    }
  }

  public void testSubclassWithSameTemplateMissingPackagePrivateDataField() throws Exception {
    try {
      IOC.getBeanManager().lookupBean(MissingPackagePrivateSubclass.class, anno).getInstance();
      fail("No error was thrown for missing data-field.");
    } catch (AssertionError ae) {
      throw ae;
    } catch (Throwable t) {
      assertTrue("Unexpected error message: " + t.getMessage(), t.getMessage().contains("did not contain data-field"));
      assertTrue("Message did not reference the unsatisfied data-field: " + t.getMessage(),
              t.getMessage().contains(MissingPackagePrivateSubclass.class.getSimpleName() + ".field"));
    }
  }

  public void testSubclassWithSameTemplateMissingPublicDataField() throws Exception {
    try {
      IOC.getBeanManager().lookupBean(MissingPublicSubclass.class, anno).getInstance();
      fail("No error was thrown for missing data-field.");
    } catch (AssertionError ae) {
      throw ae;
    } catch (Throwable t) {
      assertTrue("Unexpected error message: " + t.getMessage(), t.getMessage().contains("did not contain data-field"));
      assertTrue("Message did not reference the unsatisfied data-field: " + t.getMessage(),
              t.getMessage().contains(MissingPublicSubclass.class.getSimpleName() + ".field"));
    }
  }

  public void testSubclassWithDifferentTemplateMissingPublicField() throws Exception {
    try {
      IOC.getBeanManager().lookupBean(SubclassWithTemplateMissingPublicField.class, anno).getInstance();
      fail("No error was thrown for missing data-field.");
    } catch (AssertionError ae) {
      throw ae;
    } catch (Throwable t) {
      assertTrue("Unexpected error message: " + t.getMessage(), t.getMessage().contains("did not contain data-field"));
      assertTrue("Message did not reference the unsatisfied data-field: " + t.getMessage(),
              t.getMessage().contains(SubclassWithTemplateMissingPublicField.class.getSimpleName() + ".field"));
    }
  }

  public void testSubclassWithDifferentTemplateMissingPrivateField() throws Exception {
    try {
      IOC.getBeanManager().lookupBean(SubclassWithTemplateMissingPrivateField.class, anno).getInstance();
      fail("No error was thrown for missing data-field.");
    } catch (AssertionError ae) {
      throw ae;
    } catch (Throwable t) {
      assertTrue("Unexpected error message: " + t.getMessage(), t.getMessage().contains("did not contain data-field"));
      assertTrue("Message did not reference the unsatisfied data-field: " + t.getMessage(),
              t.getMessage().contains(SubclassWithTemplateMissingPrivateField.class.getSimpleName() + ".field"));
    }
  }

  public void testSubclassWithDifferentTemplateMissingPackagePrivateField() throws Exception {
    try {
      IOC.getBeanManager().lookupBean(SubclassWithTemplateMissingPackagePrivateField.class, anno).getInstance();
      fail("No error was thrown for missing data-field.");
    } catch (AssertionError ae) {
      throw ae;
    } catch (Throwable t) {
      assertTrue("Unexpected error message: " + t.getMessage(), t.getMessage().contains("did not contain data-field"));
      assertTrue("Message did not reference the unsatisfied data-field: " + t.getMessage(),
              t.getMessage().contains(SubclassWithTemplateMissingPackagePrivateField.class.getSimpleName() + ".field"));
    }
  }

  public void testSubclassWithDifferentTemplateMissingProtectedField() throws Exception {
    try {
      IOC.getBeanManager().lookupBean(SubclassWithTemplateMissingProtectedField.class, anno).getInstance();
      fail("No error was thrown for missing data-field.");
    } catch (AssertionError ae) {
      throw ae;
    } catch (Throwable t) {
      assertTrue("Unexpected error message: " + t.getMessage(), t.getMessage().contains("did not contain data-field"));
      assertTrue("Message did not reference the unsatisfied data-field: " + t.getMessage(),
              t.getMessage().contains(SubclassWithTemplateMissingProtectedField.class.getSimpleName() + ".field"));
    }
  }

  public void testSubclassOfAbstractWithDifferentTemplateMissingPublicField() throws Exception {
    try {
      IOC.getBeanManager().lookupBean(SubclassOfAbstractWithTemplateMissingPublicField.class, anno).getInstance();
      fail("No error was thrown for missing data-field.");
    } catch (AssertionError ae) {
      throw ae;
    } catch (Throwable t) {
      assertTrue("Unexpected error message: " + t.getMessage(), t.getMessage().contains("did not contain data-field"));
      assertTrue("Message did not reference the unsatisfied data-field: " + t.getMessage(),
              t.getMessage().contains(SubclassOfAbstractWithTemplateMissingPublicField.class.getSimpleName() + ".field"));
    }
  }

  public void testSubclassOfAbstractWithDifferentTemplateMissingPrivateField() throws Exception {
    try {
      IOC.getBeanManager().lookupBean(SubclassOfAbstractWithTemplateMissingPrivateField.class, anno).getInstance();
      fail("No error was thrown for missing data-field.");
    } catch (AssertionError ae) {
      throw ae;
    } catch (Throwable t) {
      assertTrue("Unexpected error message: " + t.getMessage(), t.getMessage().contains("did not contain data-field"));
      assertTrue("Message did not reference the unsatisfied data-field: " + t.getMessage(),
              t.getMessage().contains(SubclassOfAbstractWithTemplateMissingPrivateField.class.getSimpleName() + ".field"));
    }
  }

  public void testSubclassOfAbstractWithDifferentTemplateMissingPackagePrivateField() throws Exception {
    try {
      IOC.getBeanManager().lookupBean(SubclassOfAbstractWithTemplateMissingPackagePrivateField.class, anno).getInstance();
      fail("No error was thrown for missing data-field.");
    } catch (AssertionError ae) {
      throw ae;
    } catch (Throwable t) {
      assertTrue("Unexpected error message: " + t.getMessage(), t.getMessage().contains("did not contain data-field"));
      assertTrue("Message did not reference the unsatisfied data-field: " + t.getMessage(),
              t.getMessage().contains(SubclassOfAbstractWithTemplateMissingPackagePrivateField.class.getSimpleName() + ".field"));
    }
  }

  public void testSubclassOfAbstractWithDifferentTemplateMissingProtectedField() throws Exception {
    try {
      IOC.getBeanManager().lookupBean(SubclassOfAbstractWithTemplateMissingProtectedField.class, anno).getInstance();
      fail("No error was thrown for missing data-field.");
    } catch (AssertionError ae) {
      throw ae;
    } catch (Throwable t) {
      assertTrue("Unexpected error message: " + t.getMessage(), t.getMessage().contains("did not contain data-field"));
      assertTrue("Message did not reference the unsatisfied data-field: " + t.getMessage(),
              t.getMessage().contains(SubclassOfAbstractWithTemplateMissingProtectedField.class.getSimpleName() + ".field"));
    }
  }

  public void testDefaultHTMLFileNamesForSubclassOfAbstractWithDifferentTemplateMissingField() throws Exception {
    try {
      IOC.getBeanManager().lookupBean(SubclassMissingField.class).getInstance();
      fail("No error was thrown for missing data-field.");
    } catch (AssertionError ae) {
      throw ae;
    } catch (Throwable t) {
      assertTrue("Unexpected error message: " + t.getMessage(), t.getMessage().contains("did not contain data-field"));
      assertTrue("Message did not reference the unsatisfied data-field: " + t.getMessage(),
              t.getMessage().contains(SubclassMissingField.class.getSimpleName() + ".field"));
    }
  }

  public void testNullDataFieldReferencesDataFieldAndTemplatedBeanNames() throws Exception {
    try {
      IOC.getBeanManager().lookupBean(TemplateWithNullDataField.class).getInstance();
      fail("No error was thrown for a template with a null @DataField.");
    } catch (AssertionError ae) {
      throw ae;
    } catch (Throwable t) {
      assertTrue("Error message does not contain name of the @Templated type: " + t.getMessage(),
              t.getMessage().contains(TemplateWithNullDataField.class.getName()));
      assertTrue("Error message does not the name of the @DataField: " + t.getMessage(),
              t.getMessage().contains("field"));
    }
  }

  public void testElementConversionWarningReferencesDataFieldAndTemplatedBeanNames() throws Exception {
    // Setup log handler to capture warning message.
    final Logger rootLogger = Logger.getLogger("");
    final List<LogRecord> logRecords = new ArrayList<>();
    rootLogger.addHandler(new Handler() {

      @Override
      public void publish(final LogRecord record) {
        if (record.getLoggerName().contains(TemplateUtil.class.getSimpleName()) && record.getLevel().equals(Level.WARNING)) {
          logRecords.add(record);
        }
      }

      @Override public void flush() {}
      @Override public void close() {}
    });

    IOC.getBeanManager().lookupBean(TemplateWithDifferentDataFieldElementKind.class).getInstance();
    assertFalse("No warnings captured from TemplateUtil. Please ensure logging is enabled for this test.", logRecords.isEmpty());
    assertEquals("There should only be a single warning logged from TemplateUtil.", 1, logRecords.size());
    final String logMessage = logRecords.get(0).getMessage();
    assertTrue("Log message did not reference the @Templated bean type: " + logMessage,
            logMessage.contains(TemplateWithDifferentDataFieldElementKind.class.getSimpleName()));
    assertTrue("Log message did not reference the field name for the @DataField: " + logMessage, logMessage.contains("field"));
  }
}
