package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.common.client.dom.Element;
import org.jboss.errai.common.client.dom.EventTarget;
import org.jboss.errai.common.client.dom.GlobalEventHandlers;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.Node;
import org.jboss.errai.ioc.client.api.ContextualTypeProvider;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;

public class ContextualProvider_factory__o_j_e_c_c_d_HTMLElement__quals__Universal extends Factory<HTMLElement> { public ContextualProvider_factory__o_j_e_c_c_d_HTMLElement__quals__Universal() {
    super(new FactoryHandleImpl(HTMLElement.class, "ContextualProvider_factory__o_j_e_c_c_d_HTMLElement__quals__Universal", Dependent.class, false, null, false));
    handle.setAssignableTypes(new Class[] { HTMLElement.class, Element.class, Node.class, EventTarget.class, GlobalEventHandlers.class });
    handle.setQualifiers(new Annotation[] { });
  }

  public HTMLElement createContextualInstance(final ContextManager contextManager, final Class[] typeArgs, final Annotation[] qualifiers) {
    final ContextualTypeProvider<HTMLElement> provider = (ContextualTypeProvider<HTMLElement>) contextManager.getInstance("Type_factory__o_j_e_u_c_l_p_HTMLElementProvider__quals__j_e_i_Any_j_e_i_Default");
    final HTMLElement instance = provider.provide(typeArgs, qualifiers);
    return instance;
  }
}