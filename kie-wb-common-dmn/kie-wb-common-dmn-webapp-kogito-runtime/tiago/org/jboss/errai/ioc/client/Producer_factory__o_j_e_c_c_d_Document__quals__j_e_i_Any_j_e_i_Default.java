package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.common.client.dom.Document;
import org.jboss.errai.common.client.dom.EventTarget;
import org.jboss.errai.common.client.dom.GlobalEventHandlers;
import org.jboss.errai.common.client.dom.Node;
import org.jboss.errai.common.client.dom.Window;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;

public class Producer_factory__o_j_e_c_c_d_Document__quals__j_e_i_Any_j_e_i_Default extends Factory<Document> { public Producer_factory__o_j_e_c_c_d_Document__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(Document.class, "Producer_factory__o_j_e_c_c_d_Document__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { Document.class, Node.class, EventTarget.class, GlobalEventHandlers.class });
  }

  public Document createInstance(final ContextManager contextManager) {
    final Document instance = Window.getDocument();
    return instance;
  }
}