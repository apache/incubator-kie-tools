package org.jboss.errai.ioc.client;

import com.google.gwt.event.logical.shared.HasAttachHandlers;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.IsRenderable;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.client.widgets.toolbar.ToolbarView;
import org.kie.workbench.common.stunner.client.widgets.toolbar.impl.DefaultToolbarView;
import org.uberfire.client.mvp.HasPresenter;
import org.uberfire.client.mvp.UberView;

public class Type_factory__o_k_w_c_s_c_w_t_i_DefaultToolbarView__quals__j_e_i_Any_j_e_i_Default extends Factory<DefaultToolbarView> { public Type_factory__o_k_w_c_s_c_w_t_i_DefaultToolbarView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DefaultToolbarView.class, "Type_factory__o_k_w_c_s_c_w_t_i_DefaultToolbarView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DefaultToolbarView.class, Composite.class, Widget.class, UIObject.class, Object.class, HasVisibility.class, EventListener.class, HasAttachHandlers.class, HasHandlers.class, IsWidget.class, IsRenderable.class, ToolbarView.class, UberView.class, HasPresenter.class });
  }

  public DefaultToolbarView createInstance(final ContextManager contextManager) {
    final DefaultToolbarView instance = new DefaultToolbarView();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}