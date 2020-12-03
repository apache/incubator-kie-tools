package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.client.widgets.palette.BS3PaletteWidgetPresenter;
import org.kie.workbench.common.stunner.client.widgets.palette.collapsed.CollapsedDefinitionPaletteItemWidget;
import org.kie.workbench.common.stunner.client.widgets.palette.collapsed.CollapsedDefinitionPaletteItemWidgetView;
import org.kie.workbench.common.stunner.client.widgets.palette.collapsed.CollapsedDefinitionPaletteItemWidgetView.Presenter;
import org.kie.workbench.common.stunner.client.widgets.palette.collapsed.CollapsedDefinitionPaletteItemWidgetViewImpl;

public class Type_factory__o_k_w_c_s_c_w_p_c_CollapsedDefinitionPaletteItemWidget__quals__j_e_i_Any_j_e_i_Default extends Factory<CollapsedDefinitionPaletteItemWidget> { public Type_factory__o_k_w_c_s_c_w_p_c_CollapsedDefinitionPaletteItemWidget__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(CollapsedDefinitionPaletteItemWidget.class, "Type_factory__o_k_w_c_s_c_w_p_c_CollapsedDefinitionPaletteItemWidget__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { CollapsedDefinitionPaletteItemWidget.class, Object.class, Presenter.class, BS3PaletteWidgetPresenter.class, IsElement.class });
  }

  public CollapsedDefinitionPaletteItemWidget createInstance(final ContextManager contextManager) {
    final CollapsedDefinitionPaletteItemWidgetView _view_0 = (CollapsedDefinitionPaletteItemWidgetViewImpl) contextManager.getInstance("Type_factory__o_k_w_c_s_c_w_p_c_CollapsedDefinitionPaletteItemWidgetViewImpl__quals__j_e_i_Any_j_e_i_Default");
    final CollapsedDefinitionPaletteItemWidget instance = new CollapsedDefinitionPaletteItemWidget(_view_0);
    registerDependentScopedReference(instance, _view_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((CollapsedDefinitionPaletteItemWidget) instance, contextManager);
  }

  public void destroyInstanceHelper(final CollapsedDefinitionPaletteItemWidget instance, final ContextManager contextManager) {
    instance.destroy();
  }

  public void invokePostConstructs(final CollapsedDefinitionPaletteItemWidget instance) {
    instance.setUp();
  }
}