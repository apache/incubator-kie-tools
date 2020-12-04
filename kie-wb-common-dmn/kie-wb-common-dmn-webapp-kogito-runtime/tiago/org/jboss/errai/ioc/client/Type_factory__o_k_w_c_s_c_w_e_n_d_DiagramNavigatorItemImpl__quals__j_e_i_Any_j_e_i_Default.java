package org.jboss.errai.ioc.client;

import com.google.gwt.user.client.ui.IsWidget;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.client.widgets.explorer.navigator.NavigatorItem;
import org.kie.workbench.common.stunner.client.widgets.explorer.navigator.NavigatorItemView;
import org.kie.workbench.common.stunner.client.widgets.explorer.navigator.diagrams.DiagramNavigatorItem;
import org.kie.workbench.common.stunner.client.widgets.explorer.navigator.diagrams.DiagramNavigatorItemImpl;
import org.kie.workbench.common.stunner.client.widgets.explorer.navigator.item.NavigatorThumbnailItemView;
import org.kie.workbench.common.stunner.core.client.api.ShapeManager;
import org.kie.workbench.common.stunner.core.client.api.ShapeManagerImpl;

public class Type_factory__o_k_w_c_s_c_w_e_n_d_DiagramNavigatorItemImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<DiagramNavigatorItemImpl> { public Type_factory__o_k_w_c_s_c_w_e_n_d_DiagramNavigatorItemImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DiagramNavigatorItemImpl.class, "Type_factory__o_k_w_c_s_c_w_e_n_d_DiagramNavigatorItemImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DiagramNavigatorItemImpl.class, Object.class, IsWidget.class, DiagramNavigatorItem.class, NavigatorItem.class });
  }

  public DiagramNavigatorItemImpl createInstance(final ContextManager contextManager) {
    final NavigatorItemView<NavigatorItem> _view_1 = (NavigatorThumbnailItemView) contextManager.getInstance("Type_factory__o_k_w_c_s_c_w_e_n_i_NavigatorThumbnailItemView__quals__j_e_i_Any_j_e_i_Default");
    final ShapeManager _shapeManager_0 = (ShapeManagerImpl) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_a_ShapeManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    final DiagramNavigatorItemImpl instance = new DiagramNavigatorItemImpl(_shapeManager_0, _view_1);
    registerDependentScopedReference(instance, _view_1);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((DiagramNavigatorItemImpl) instance, contextManager);
  }

  public void destroyInstanceHelper(final DiagramNavigatorItemImpl instance, final ContextManager contextManager) {
    instance.destroy();
  }

  public void invokePostConstructs(final DiagramNavigatorItemImpl instance) {
    instance.init();
  }
}