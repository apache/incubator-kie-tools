package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.xml.namespace.QName;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;

public class JsType_factory__j_x_n_QName__quals__Universal extends Factory<QName> { public JsType_factory__j_x_n_QName__quals__Universal() {
    super(new FactoryHandleImpl(QName.class, "JsType_factory__j_x_n_QName__quals__Universal", Dependent.class, false, null, false));
    handle.setAssignableTypes(new Class[] { QName.class, Object.class });
    handle.setQualifiers(new Annotation[] { });
  }

  public QName createInstance(final ContextManager contextManager) {
    return (QName) WindowInjectionContextStorage.createOrGet().getBean("javax.xml.namespace.QName");
  }
}