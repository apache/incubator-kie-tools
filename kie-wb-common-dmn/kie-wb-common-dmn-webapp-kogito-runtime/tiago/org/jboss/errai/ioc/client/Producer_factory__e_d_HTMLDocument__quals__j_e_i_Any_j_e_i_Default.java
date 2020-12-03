package org.jboss.errai.ioc.client;

import elemental2.dom.Document;
import elemental2.dom.EventTarget;
import elemental2.dom.HTMLDocument;
import elemental2.dom.Node;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.client.views.pfly.widgets.Elemental2Producer;

public class Producer_factory__e_d_HTMLDocument__quals__j_e_i_Any_j_e_i_Default extends Factory<HTMLDocument> { public Producer_factory__e_d_HTMLDocument__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(HTMLDocument.class, "Producer_factory__e_d_HTMLDocument__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { HTMLDocument.class, Document.class, Node.class, Object.class, EventTarget.class });
  }

  public HTMLDocument createInstance(final ContextManager contextManager) {
    Elemental2Producer producerInstance = contextManager.getInstance("Type_factory__o_u_c_v_p_w_Elemental2Producer__quals__j_e_i_Any_j_e_i_Default");
    producerInstance = Factory.maybeUnwrapProxy(producerInstance);
    final HTMLDocument instance = producerInstance.produceDocument();
    thisInstance.setReference(instance, "producerInstance", producerInstance);
    return instance;
  }
}