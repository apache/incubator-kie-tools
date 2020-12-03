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
import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Instance;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.client.views.pfly.modal.Bs3Modal;
import org.uberfire.client.views.pfly.modal.ErrorPopupView;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter.View;

public class Type_factory__o_u_c_v_p_m_ErrorPopupView__quals__j_e_i_Any_j_e_i_Default extends Factory<ErrorPopupView> { public Type_factory__o_u_c_v_p_m_ErrorPopupView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ErrorPopupView.class, "Type_factory__o_u_c_v_p_m_ErrorPopupView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ErrorPopupView.class, Composite.class, Widget.class, UIObject.class, Object.class, HasVisibility.class, EventListener.class, HasAttachHandlers.class, HasHandlers.class, IsWidget.class, IsRenderable.class, View.class });
  }

  public ErrorPopupView createInstance(final ContextManager contextManager) {
    final ErrorPopupView instance = new ErrorPopupView();
    setIncompleteInstance(instance);
    final Instance ErrorPopupView_modalFactory = (Instance) contextManager.getContextualInstance("ContextualProvider_factory__j_e_i_Instance__quals__Universal", new Class[] { Bs3Modal.class }, new Annotation[] { });
    registerDependentScopedReference(instance, ErrorPopupView_modalFactory);
    ErrorPopupView_Instance_modalFactory(instance, ErrorPopupView_modalFactory);
    setIncompleteInstance(null);
    return instance;
  }

  native static Instance ErrorPopupView_Instance_modalFactory(ErrorPopupView instance) /*-{
    return instance.@org.uberfire.client.views.pfly.modal.ErrorPopupView::modalFactory;
  }-*/;

  native static void ErrorPopupView_Instance_modalFactory(ErrorPopupView instance, Instance<Bs3Modal> value) /*-{
    instance.@org.uberfire.client.views.pfly.modal.ErrorPopupView::modalFactory = value;
  }-*/;
}