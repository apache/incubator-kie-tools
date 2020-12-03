package org.jboss.errai.ioc.client;

import elemental2.dom.DomGlobal;
import elemental2.dom.Element;
import elemental2.dom.EventTarget;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLTextAreaElement;
import elemental2.dom.Node;
import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import jsinterop.base.Js;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;

public class ExtensionProvided_factory__e_d_HTMLTextAreaElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named extends Factory<HTMLTextAreaElement> { public ExtensionProvided_factory__e_d_HTMLTextAreaElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named() {
    super(new FactoryHandleImpl(HTMLTextAreaElement.class, "ExtensionProvided_factory__e_d_HTMLTextAreaElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named", Dependent.class, false, "textarea", true));
    handle.setAssignableTypes(new Class[] { HTMLTextAreaElement.class, HTMLElement.class, Element.class, Node.class, Object.class, EventTarget.class });
    handle.setQualifiers(new Annotation[] { QualifierUtil.ANY_ANNOTATION, QualifierUtil.DEFAULT_ANNOTATION, QualifierUtil.createNamed("textarea") });
  }

  public HTMLTextAreaElement createInstance(final ContextManager contextManager) {
    final Element element = DomGlobal.document.createElement("textarea");
    final HTMLTextAreaElement retVal = Js.cast(element);
    return retVal;
  }
}