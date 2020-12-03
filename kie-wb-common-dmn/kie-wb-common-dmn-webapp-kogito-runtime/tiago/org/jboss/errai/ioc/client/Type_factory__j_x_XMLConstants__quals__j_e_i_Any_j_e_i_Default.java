package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import javax.xml.XMLConstants;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;

public class Type_factory__j_x_XMLConstants__quals__j_e_i_Any_j_e_i_Default extends Factory<XMLConstants> { public Type_factory__j_x_XMLConstants__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(XMLConstants.class, "Type_factory__j_x_XMLConstants__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { XMLConstants.class, Object.class });
  }

  public XMLConstants createInstance(final ContextManager contextManager) {
    final XMLConstants instance = new XMLConstants();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}