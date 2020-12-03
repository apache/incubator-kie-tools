package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.client.widgets.palette.BS3PaletteWidgetPresenter;
import org.kie.workbench.common.stunner.client.widgets.palette.categories.items.DefinitionPaletteItemWidget;
import org.kie.workbench.common.stunner.client.widgets.palette.categories.items.DefinitionPaletteItemWidgetView;
import org.kie.workbench.common.stunner.client.widgets.palette.categories.items.DefinitionPaletteItemWidgetView.Presenter;
import org.kie.workbench.common.stunner.client.widgets.palette.categories.items.DefinitionPaletteItemWidgetViewImpl;

public class Type_factory__o_k_w_c_s_c_w_p_c_i_DefinitionPaletteItemWidget__quals__j_e_i_Any_j_e_i_Default extends Factory<DefinitionPaletteItemWidget> { public Type_factory__o_k_w_c_s_c_w_p_c_i_DefinitionPaletteItemWidget__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DefinitionPaletteItemWidget.class, "Type_factory__o_k_w_c_s_c_w_p_c_i_DefinitionPaletteItemWidget__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DefinitionPaletteItemWidget.class, Object.class, Presenter.class, BS3PaletteWidgetPresenter.class, IsElement.class });
  }

  public DefinitionPaletteItemWidget createInstance(final ContextManager contextManager) {
    final DefinitionPaletteItemWidgetView _view_0 = (DefinitionPaletteItemWidgetViewImpl) contextManager.getInstance("Type_factory__o_k_w_c_s_c_w_p_c_i_DefinitionPaletteItemWidgetViewImpl__quals__j_e_i_Any_j_e_i_Default");
    final DefinitionPaletteItemWidget instance = new DefinitionPaletteItemWidget(_view_0);
    registerDependentScopedReference(instance, _view_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((DefinitionPaletteItemWidget) instance, contextManager);
  }

  public void destroyInstanceHelper(final DefinitionPaletteItemWidget instance, final ContextManager contextManager) {
    instance.destroy();
  }

  public void invokePostConstructs(final DefinitionPaletteItemWidget instance) {
    instance.setUp();
  }
}