package org.jboss.errai.ioc.client;

import com.google.gwt.event.dom.client.HasFocusHandlers;
import com.google.gwt.event.logical.shared.HasAttachHandlers;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.IsRenderable;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.client.workbench.widgets.panel.StaticFocusedResizePanel;

public class ExtensionProvided_factory__o_u_c_w_w_p_StaticFocusedResizePanel__quals__j_e_i_Any_j_e_i_Default extends Factory<StaticFocusedResizePanel> { public ExtensionProvided_factory__o_u_c_w_w_p_StaticFocusedResizePanel__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(StaticFocusedResizePanel.class, "ExtensionProvided_factory__o_u_c_w_w_p_StaticFocusedResizePanel__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { StaticFocusedResizePanel.class, ResizeComposite.class, Composite.class, Widget.class, UIObject.class, Object.class, HasVisibility.class, EventListener.class, HasAttachHandlers.class, HasHandlers.class, IsWidget.class, IsRenderable.class, RequiresResize.class, HasSelectionHandlers.class, HasFocusHandlers.class });
  }

  public StaticFocusedResizePanel createInstance(final ContextManager contextManager) {
    return new StaticFocusedResizePanel();
  }
}