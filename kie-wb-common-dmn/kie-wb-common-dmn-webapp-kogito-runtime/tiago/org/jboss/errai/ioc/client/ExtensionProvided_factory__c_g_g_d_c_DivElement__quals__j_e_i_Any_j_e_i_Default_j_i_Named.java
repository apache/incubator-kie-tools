package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import elemental2.dom.DomGlobal;
import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import jsinterop.base.Js;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;

public class ExtensionProvided_factory__c_g_g_d_c_DivElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named extends Factory<DivElement> { public ExtensionProvided_factory__c_g_g_d_c_DivElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named() {
    super(new FactoryHandleImpl(DivElement.class, "ExtensionProvided_factory__c_g_g_d_c_DivElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named", Dependent.class, false, "div", true));
    handle.setAssignableTypes(new Class[] { DivElement.class, Element.class, Node.class, JavaScriptObject.class, Object.class });
    handle.setQualifiers(new Annotation[] { QualifierUtil.ANY_ANNOTATION, QualifierUtil.DEFAULT_ANNOTATION, QualifierUtil.createNamed("div") });
  }

  public DivElement createInstance(final ContextManager contextManager) {
    final elemental2.dom.Element element = DomGlobal.document.createElement("div");
    final DivElement retVal = Js.cast(element);
    return retVal;
  }
}