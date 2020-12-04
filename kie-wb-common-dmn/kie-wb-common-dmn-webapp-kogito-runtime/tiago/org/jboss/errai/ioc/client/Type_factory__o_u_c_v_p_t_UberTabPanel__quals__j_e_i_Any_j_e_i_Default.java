package org.jboss.errai.ioc.client;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasAttachHandlers;
import com.google.gwt.event.logical.shared.HasBeforeSelectionHandlers;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.shared.EventHandler;
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
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.PlaceManagerImpl;
import org.uberfire.client.views.pfly.tab.ResizeTabPanel;
import org.uberfire.client.views.pfly.tab.UberTabPanel;
import org.uberfire.client.workbench.panels.MultiPartWidget;

public class Type_factory__o_u_c_v_p_t_UberTabPanel__quals__j_e_i_Any_j_e_i_Default extends Factory<UberTabPanel> { public Type_factory__o_u_c_v_p_t_UberTabPanel__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(UberTabPanel.class, "Type_factory__o_u_c_v_p_t_UberTabPanel__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { UberTabPanel.class, ResizeComposite.class, Composite.class, Widget.class, UIObject.class, Object.class, HasVisibility.class, EventListener.class, HasAttachHandlers.class, HasHandlers.class, IsWidget.class, IsRenderable.class, RequiresResize.class, MultiPartWidget.class, HasBeforeSelectionHandlers.class, HasSelectionHandlers.class, ClickHandler.class, EventHandler.class });
  }

  public UberTabPanel createInstance(final ContextManager contextManager) {
    final ResizeTabPanel _tabPanel_1 = (ResizeTabPanel) contextManager.getInstance("Type_factory__o_u_c_v_p_t_ResizeTabPanel__quals__j_e_i_Any_o_u_c_v_p_t_Resize");
    final PlaceManager _panelManager_0 = (PlaceManagerImpl) contextManager.getInstance("Type_factory__o_u_c_m_PlaceManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    final UberTabPanel instance = new UberTabPanel(_panelManager_0, _tabPanel_1);
    registerDependentScopedReference(instance, _tabPanel_1);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final UberTabPanel instance) {
    instance.init();
  }
}