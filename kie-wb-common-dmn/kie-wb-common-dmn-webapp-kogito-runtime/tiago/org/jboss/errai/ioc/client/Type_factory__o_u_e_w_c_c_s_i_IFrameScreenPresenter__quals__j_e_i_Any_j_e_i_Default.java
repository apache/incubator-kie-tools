package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.ext.widgets.core.client.screens.iframe.IFrameScreenPresenter;
import org.uberfire.ext.widgets.core.client.screens.iframe.IFrameScreenView;

public class Type_factory__o_u_e_w_c_c_s_i_IFrameScreenPresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<IFrameScreenPresenter> { public Type_factory__o_u_e_w_c_c_s_i_IFrameScreenPresenter__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(IFrameScreenPresenter.class, "Type_factory__o_u_e_w_c_c_s_i_IFrameScreenPresenter__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { IFrameScreenPresenter.class, Object.class });
  }

  public IFrameScreenPresenter createInstance(final ContextManager contextManager) {
    final IFrameScreenPresenter instance = new IFrameScreenPresenter();
    setIncompleteInstance(instance);
    final IFrameScreenView IFrameScreenPresenter_view = (IFrameScreenView) contextManager.getInstance("Type_factory__o_u_e_w_c_c_s_i_IFrameScreenView__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, IFrameScreenPresenter_view);
    instance.view = IFrameScreenPresenter_view;
    setIncompleteInstance(null);
    return instance;
  }
}