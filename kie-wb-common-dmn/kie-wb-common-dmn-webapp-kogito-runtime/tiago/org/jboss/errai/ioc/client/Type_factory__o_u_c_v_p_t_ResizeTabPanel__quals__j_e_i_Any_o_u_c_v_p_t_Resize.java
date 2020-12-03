package org.jboss.errai.ioc.client;

import com.google.gwt.event.logical.shared.HasAttachHandlers;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.IsRenderable;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.client.views.pfly.tab.Resize;
import org.uberfire.client.views.pfly.tab.ResizeTabPanel;
import org.uberfire.client.views.pfly.tab.TabPanelWithDropdowns;

public class Type_factory__o_u_c_v_p_t_ResizeTabPanel__quals__j_e_i_Any_o_u_c_v_p_t_Resize extends Factory<ResizeTabPanel> { public Type_factory__o_u_c_v_p_t_ResizeTabPanel__quals__j_e_i_Any_o_u_c_v_p_t_Resize() {
    super(new FactoryHandleImpl(ResizeTabPanel.class, "Type_factory__o_u_c_v_p_t_ResizeTabPanel__quals__j_e_i_Any_o_u_c_v_p_t_Resize", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ResizeTabPanel.class, TabPanelWithDropdowns.class, Composite.class, Widget.class, UIObject.class, Object.class, HasVisibility.class, EventListener.class, HasAttachHandlers.class, HasHandlers.class, IsWidget.class, IsRenderable.class, RequiresResize.class, ProvidesResize.class });
    handle.setQualifiers(new Annotation[] { QualifierUtil.ANY_ANNOTATION, new Resize() {
        public Class annotationType() {
          return Resize.class;
        }
        public String toString() {
          return "@org.uberfire.client.views.pfly.tab.Resize()";
        }
    } });
  }

  public ResizeTabPanel createInstance(final ContextManager contextManager) {
    final ResizeTabPanel instance = new ResizeTabPanel();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final ResizeTabPanel instance) {
    instance.init();
  }
}