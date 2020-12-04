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
import org.kie.workbench.common.widgets.client.source.ViewDRLSourceWidget;
import org.kie.workbench.common.widgets.client.source.ViewSourceView;
import org.kie.workbench.common.widgets.client.source.ViewSourceViewImpl;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.ext.widgets.common.client.common.HasBusyIndicator;

public class Type_factory__o_k_w_c_w_c_s_ViewSourceViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<ViewSourceViewImpl> { public Type_factory__o_k_w_c_w_c_s_ViewSourceViewImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ViewSourceViewImpl.class, "Type_factory__o_k_w_c_w_c_s_ViewSourceViewImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ViewSourceViewImpl.class, Composite.class, Widget.class, UIObject.class, Object.class, HasVisibility.class, EventListener.class, HasAttachHandlers.class, HasHandlers.class, IsWidget.class, IsRenderable.class, ViewSourceView.class, HasBusyIndicator.class });
  }

  public ViewSourceViewImpl createInstance(final ContextManager contextManager) {
    final ViewSourceViewImpl instance = new ViewSourceViewImpl();
    setIncompleteInstance(instance);
    final ViewDRLSourceWidget ViewSourceViewImpl_drlSourceViewer = (ViewDRLSourceWidget) contextManager.getInstance("ExtensionProvided_factory__o_k_w_c_w_c_s_ViewDRLSourceWidget__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, ViewSourceViewImpl_drlSourceViewer);
    ViewSourceViewImpl_ViewDRLSourceWidget_drlSourceViewer(instance, ViewSourceViewImpl_drlSourceViewer);
    final BusyIndicatorView ViewSourceViewImpl_busyIndicatorView = (BusyIndicatorView) contextManager.getInstance("Type_factory__o_u_e_w_c_c_c_BusyIndicatorView__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, ViewSourceViewImpl_busyIndicatorView);
    ViewSourceViewImpl_BusyIndicatorView_busyIndicatorView(instance, ViewSourceViewImpl_busyIndicatorView);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final ViewSourceViewImpl instance) {
    instance.initialize();
  }

  native static BusyIndicatorView ViewSourceViewImpl_BusyIndicatorView_busyIndicatorView(ViewSourceViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.widgets.client.source.ViewSourceViewImpl::busyIndicatorView;
  }-*/;

  native static void ViewSourceViewImpl_BusyIndicatorView_busyIndicatorView(ViewSourceViewImpl instance, BusyIndicatorView value) /*-{
    instance.@org.kie.workbench.common.widgets.client.source.ViewSourceViewImpl::busyIndicatorView = value;
  }-*/;

  native static ViewDRLSourceWidget ViewSourceViewImpl_ViewDRLSourceWidget_drlSourceViewer(ViewSourceViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.widgets.client.source.ViewSourceViewImpl::drlSourceViewer;
  }-*/;

  native static void ViewSourceViewImpl_ViewDRLSourceWidget_drlSourceViewer(ViewSourceViewImpl instance, ViewDRLSourceWidget value) /*-{
    instance.@org.kie.workbench.common.widgets.client.source.ViewSourceViewImpl::drlSourceViewer = value;
  }-*/;
}