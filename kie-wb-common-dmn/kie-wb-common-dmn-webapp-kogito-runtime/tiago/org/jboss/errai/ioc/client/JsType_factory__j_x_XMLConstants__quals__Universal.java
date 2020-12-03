package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.xml.XMLConstants;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;

public class JsType_factory__j_x_XMLConstants__quals__Universal extends Factory<XMLConstants> { public JsType_factory__j_x_XMLConstants__quals__Universal() {
    super(new FactoryHandleImpl(XMLConstants.class, "JsType_factory__j_x_XMLConstants__quals__Universal", Dependent.class, false, null, false));
    handle.setAssignableTypes(new Class[] { XMLConstants.class, Object.class });
    handle.setQualifiers(new Annotation[] { });
  }

  public XMLConstants createInstance(final ContextManager contextManager) {
    return (XMLConstants) WindowInjectionContextStorage.createOrGet().getBean("javax.xml.XMLConstants");
  }
}