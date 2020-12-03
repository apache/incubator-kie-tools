package org.jboss.errai.ioc.client;

import com.google.gwt.event.logical.shared.HasAttachHandlers;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.HasWidgets.ForIsWidget;
import com.google.gwt.user.client.ui.IndexedPanel;
import com.google.gwt.user.client.ui.InsertPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionContainer;
import org.uberfire.client.workbench.widgets.listbar.ResizeFlowPanel;

public class ExtensionProvided_factory__o_k_w_c_s_c_w_p_s_i_SessionContainer__quals__j_e_i_Any_j_e_i_Default extends Factory<SessionContainer> { public ExtensionProvided_factory__o_k_w_c_s_c_w_p_s_i_SessionContainer__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(SessionContainer.class, "ExtensionProvided_factory__o_k_w_c_s_c_w_p_s_i_SessionContainer__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { SessionContainer.class, ResizeFlowPanel.class, FlowPanel.class, ComplexPanel.class, Panel.class, Widget.class, UIObject.class, Object.class, HasVisibility.class, EventListener.class, HasAttachHandlers.class, HasHandlers.class, IsWidget.class, ForIsWidget.class, HasWidgets.class, Iterable.class, com.google.gwt.user.client.ui.IndexedPanel.ForIsWidget.class, IndexedPanel.class, com.google.gwt.user.client.ui.InsertPanel.ForIsWidget.class, InsertPanel.class, RequiresResize.class, ProvidesResize.class, HasHTML.class, HasText.class });
  }

  public SessionContainer createInstance(final ContextManager contextManager) {
    return new SessionContainer();
  }
}