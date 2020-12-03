package org.jboss.errai.ioc.client;

import com.google.gwt.event.logical.shared.HasAttachHandlers;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.HasWidgets.ForIsWidget;
import com.google.gwt.user.client.ui.IndexedPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import javax.enterprise.context.Dependent;
import org.gwtbootstrap3.client.ui.IsClosable;
import org.gwtbootstrap3.client.ui.Modal;
import org.gwtbootstrap3.client.ui.base.ComplexWidget;
import org.gwtbootstrap3.client.ui.base.HasId;
import org.gwtbootstrap3.client.ui.base.HasInlineStyle;
import org.gwtbootstrap3.client.ui.base.HasPull;
import org.gwtbootstrap3.client.ui.base.HasResponsiveness;
import org.gwtbootstrap3.client.ui.html.Div;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.client.mvp.HasPresenter;
import org.uberfire.client.mvp.UberView;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.core.client.wizards.WizardView;
import org.uberfire.ext.widgets.core.client.wizards.WizardViewImpl;

public class Type_factory__o_u_e_w_c_c_w_WizardViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<WizardViewImpl> { public Type_factory__o_u_e_w_c_c_w_WizardViewImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(WizardViewImpl.class, "Type_factory__o_u_e_w_c_c_w_WizardViewImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { WizardViewImpl.class, BaseModal.class, Modal.class, Div.class, ComplexWidget.class, ComplexPanel.class, Panel.class, Widget.class, UIObject.class, Object.class, HasVisibility.class, EventListener.class, HasAttachHandlers.class, HasHandlers.class, IsWidget.class, ForIsWidget.class, HasWidgets.class, Iterable.class, com.google.gwt.user.client.ui.IndexedPanel.ForIsWidget.class, IndexedPanel.class, HasId.class, HasResponsiveness.class, HasInlineStyle.class, HasPull.class, IsClosable.class, WizardView.class, UberView.class, HasPresenter.class });
  }

  public WizardViewImpl createInstance(final ContextManager contextManager) {
    final WizardViewImpl instance = new WizardViewImpl();
    setIncompleteInstance(instance);
    final SyncBeanManager WizardViewImpl_iocBeanManager = (SyncBeanManager) contextManager.getInstance("Producer_factory__o_j_e_i_c_c_SyncBeanManager__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, WizardViewImpl_iocBeanManager);
    WizardViewImpl_SyncBeanManager_iocBeanManager(instance, WizardViewImpl_iocBeanManager);
    setIncompleteInstance(null);
    return instance;
  }

  native static SyncBeanManager WizardViewImpl_SyncBeanManager_iocBeanManager(WizardViewImpl instance) /*-{
    return instance.@org.uberfire.ext.widgets.core.client.wizards.WizardViewImpl::iocBeanManager;
  }-*/;

  native static void WizardViewImpl_SyncBeanManager_iocBeanManager(WizardViewImpl instance, SyncBeanManager value) /*-{
    instance.@org.uberfire.ext.widgets.core.client.wizards.WizardViewImpl::iocBeanManager = value;
  }-*/;
}