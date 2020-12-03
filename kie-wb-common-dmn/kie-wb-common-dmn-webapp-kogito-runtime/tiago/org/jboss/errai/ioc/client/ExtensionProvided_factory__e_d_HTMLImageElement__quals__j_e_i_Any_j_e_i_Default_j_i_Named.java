package org.jboss.errai.ioc.client;

import elemental2.dom.DomGlobal;
import elemental2.dom.Element;
import elemental2.dom.EventTarget;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLImageElement;
import elemental2.dom.Node;
import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import jsinterop.base.Js;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;

public class ExtensionProvided_factory__e_d_HTMLImageElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named extends Factory<HTMLImageElement> { public ExtensionProvided_factory__e_d_HTMLImageElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named() {
    super(new FactoryHandleImpl(HTMLImageElement.class, "ExtensionProvided_factory__e_d_HTMLImageElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named", Dependent.class, false, "img", true));
    handle.setAssignableTypes(new Class[] { HTMLImageElement.class, HTMLElement.class, Element.class, Node.class, Object.class, EventTarget.class });
    handle.setQualifiers(new Annotation[] { QualifierUtil.ANY_ANNOTATION, QualifierUtil.DEFAULT_ANNOTATION, QualifierUtil.createNamed("img") });
  }

  public HTMLImageElement createInstance(final ContextManager contextManager) {
    final Element element = DomGlobal.document.createElement("img");
    final HTMLImageElement retVal = Js.cast(element);
    return retVal;
  }
}