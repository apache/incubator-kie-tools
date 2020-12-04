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
import org.uberfire.client.views.pfly.splash.SplashModalFooter;
import org.uberfire.client.views.pfly.splash.SplashViewImpl;
import org.uberfire.client.workbench.widgets.splash.SplashView;

public class Type_factory__o_u_c_v_p_s_SplashViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<SplashViewImpl> { public Type_factory__o_u_c_v_p_s_SplashViewImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(SplashViewImpl.class, "Type_factory__o_u_c_v_p_s_SplashViewImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { SplashViewImpl.class, Composite.class, Widget.class, UIObject.class, Object.class, HasVisibility.class, EventListener.class, HasAttachHandlers.class, HasHandlers.class, IsWidget.class, IsRenderable.class, SplashView.class, HasCloseHandlers.class });
  }

  public SplashViewImpl createInstance(final ContextManager contextManager) {
    final SplashViewImpl instance = new SplashViewImpl();
    setIncompleteInstance(instance);
    final Bs3Modal SplashViewImpl_modal = (Bs3Modal) contextManager.getInstance("Type_factory__o_u_c_v_p_m_Bs3Modal__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, SplashViewImpl_modal);
    SplashViewImpl_Bs3Modal_modal(instance, SplashViewImpl_modal);
    final SplashModalFooter SplashViewImpl_footer = (SplashModalFooter) contextManager.getInstance("Type_factory__o_u_c_v_p_s_SplashModalFooter__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, SplashViewImpl_footer);
    SplashViewImpl_SplashModalFooter_footer(instance, SplashViewImpl_footer);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final SplashViewImpl instance) {
    instance.setup();
  }

  native static Bs3Modal SplashViewImpl_Bs3Modal_modal(SplashViewImpl instance) /*-{
    return instance.@org.uberfire.client.views.pfly.splash.SplashViewImpl::modal;
  }-*/;

  native static void SplashViewImpl_Bs3Modal_modal(SplashViewImpl instance, Bs3Modal value) /*-{
    instance.@org.uberfire.client.views.pfly.splash.SplashViewImpl::modal = value;
  }-*/;

  native static SplashModalFooter SplashViewImpl_SplashModalFooter_footer(SplashViewImpl instance) /*-{
    return instance.@org.uberfire.client.views.pfly.splash.SplashViewImpl::footer;
  }-*/;

  native static void SplashViewImpl_SplashModalFooter_footer(SplashViewImpl instance, SplashModalFooter value) /*-{
    instance.@org.uberfire.client.views.pfly.splash.SplashViewImpl::footer = value;
  }-*/;
}