package org.jboss.errai.ioc.client;

import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.client.views.pfly.HTML5DndSeleniumSupport;
import org.uberfire.client.views.pfly.PatternFlyEntryPoint;

public class Type_factory__o_u_c_v_p_HTML5DndSeleniumSupport__quals__j_e_i_Any_j_e_i_Default extends Factory<HTML5DndSeleniumSupport> { public Type_factory__o_u_c_v_p_HTML5DndSeleniumSupport__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(HTML5DndSeleniumSupport.class, "Type_factory__o_u_c_v_p_HTML5DndSeleniumSupport__quals__j_e_i_Any_j_e_i_Default", EntryPoint.class, true, null, true));
    handle.setAssignableTypes(new Class[] { HTML5DndSeleniumSupport.class, Object.class });
  }

  public HTML5DndSeleniumSupport createInstance(final ContextManager contextManager) {
    final HTML5DndSeleniumSupport instance = new HTML5DndSeleniumSupport();
    setIncompleteInstance(instance);
    final PatternFlyEntryPoint HTML5DndSeleniumSupport_entryPoint = (PatternFlyEntryPoint) contextManager.getInstance("Type_factory__o_u_c_v_p_PatternFlyEntryPoint__quals__j_e_i_Any_j_e_i_Default");
    HTML5DndSeleniumSupport_PatternFlyEntryPoint_entryPoint(instance, HTML5DndSeleniumSupport_entryPoint);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final HTML5DndSeleniumSupport instance) {
    instance.init();
  }

  native static PatternFlyEntryPoint HTML5DndSeleniumSupport_PatternFlyEntryPoint_entryPoint(HTML5DndSeleniumSupport instance) /*-{
    return instance.@org.uberfire.client.views.pfly.HTML5DndSeleniumSupport::entryPoint;
  }-*/;

  native static void HTML5DndSeleniumSupport_PatternFlyEntryPoint_entryPoint(HTML5DndSeleniumSupport instance, PatternFlyEntryPoint value) /*-{
    instance.@org.uberfire.client.views.pfly.HTML5DndSeleniumSupport::entryPoint = value;
  }-*/;
}