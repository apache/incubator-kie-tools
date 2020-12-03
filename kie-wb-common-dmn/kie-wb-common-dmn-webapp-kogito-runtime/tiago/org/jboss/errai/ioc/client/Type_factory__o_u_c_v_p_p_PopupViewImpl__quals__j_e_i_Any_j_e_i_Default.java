package org.jboss.errai.ioc.client;

import com.google.gwt.event.logical.shared.HasAttachHandlers;
import com.google.gwt.event.logical.shared.HasCloseHandlers;
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
import org.uberfire.client.views.pfly.modal.Bs3Modal;
import org.uberfire.client.views.pfly.popup.PopupViewImpl;
import org.uberfire.client.workbench.widgets.popup.PopupView;

public class Type_factory__o_u_c_v_p_p_PopupViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<PopupViewImpl> { public Type_factory__o_u_c_v_p_p_PopupViewImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(PopupViewImpl.class, "Type_factory__o_u_c_v_p_p_PopupViewImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { PopupViewImpl.class, Composite.class, Widget.class, UIObject.class, Object.class, HasVisibility.class, EventListener.class, HasAttachHandlers.class, HasHandlers.class, IsWidget.class, IsRenderable.class, PopupView.class, HasCloseHandlers.class });
  }

  public PopupViewImpl createInstance(final ContextManager contextManager) {
    final PopupViewImpl instance = new PopupViewImpl();
    setIncompleteInstance(instance);
    final Bs3Modal PopupViewImpl_modal = (Bs3Modal) contextManager.getInstance("Type_factory__o_u_c_v_p_m_Bs3Modal__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, PopupViewImpl_modal);
    PopupViewImpl_Bs3Modal_modal(instance, PopupViewImpl_modal);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final PopupViewImpl instance) {
    instance.init();
  }

  native static Bs3Modal PopupViewImpl_Bs3Modal_modal(PopupViewImpl instance) /*-{
    return instance.@org.uberfire.client.views.pfly.popup.PopupViewImpl::modal;
  }-*/;

  native static void PopupViewImpl_Bs3Modal_modal(PopupViewImpl instance, Bs3Modal value) /*-{
    instance.@org.uberfire.client.views.pfly.popup.PopupViewImpl::modal = value;
  }-*/;
}