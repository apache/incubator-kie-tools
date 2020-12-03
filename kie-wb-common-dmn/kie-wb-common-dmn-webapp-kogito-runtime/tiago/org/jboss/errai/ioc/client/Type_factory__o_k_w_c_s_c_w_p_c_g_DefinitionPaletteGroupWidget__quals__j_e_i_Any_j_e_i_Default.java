package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.client.widgets.palette.BS3PaletteWidgetPresenter;
import org.kie.workbench.common.stunner.client.widgets.palette.categories.group.DefinitionPaletteGroupWidget;
import org.kie.workbench.common.stunner.client.widgets.palette.categories.group.DefinitionPaletteGroupWidgetView;
import org.kie.workbench.common.stunner.client.widgets.palette.categories.group.DefinitionPaletteGroupWidgetView.Presenter;
import org.kie.workbench.common.stunner.client.widgets.palette.categories.group.DefinitionPaletteGroupWidgetViewImpl;
import org.kie.workbench.common.stunner.client.widgets.palette.categories.items.DefinitionPaletteItemWidget;

public class Type_factory__o_k_w_c_s_c_w_p_c_g_DefinitionPaletteGroupWidget__quals__j_e_i_Any_j_e_i_Default extends Factory<DefinitionPaletteGroupWidget> { public Type_factory__o_k_w_c_s_c_w_p_c_g_DefinitionPaletteGroupWidget__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DefinitionPaletteGroupWidget.class, "Type_factory__o_k_w_c_s_c_w_p_c_g_DefinitionPaletteGroupWidget__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DefinitionPaletteGroupWidget.class, Object.class, Presenter.class, BS3PaletteWidgetPresenter.class, IsElement.class });
  }

  public DefinitionPaletteGroupWidget createInstance(final ContextManager contextManager) {
    final ManagedInstance<DefinitionPaletteItemWidget> _definitionPaletteItemWidgets_1 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { DefinitionPaletteItemWidget.class }, new Annotation[] { });
    final DefinitionPaletteGroupWidgetView _view_0 = (DefinitionPaletteGroupWidgetViewImpl) contextManager.getInstance("Type_factory__o_k_w_c_s_c_w_p_c_g_DefinitionPaletteGroupWidgetViewImpl__quals__j_e_i_Any_j_e_i_Default");
    final DefinitionPaletteGroupWidget instance = new DefinitionPaletteGroupWidget(_view_0, _definitionPaletteItemWidgets_1);
    registerDependentScopedReference(instance, _definitionPaletteItemWidgets_1);
    registerDependentScopedReference(instance, _view_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((DefinitionPaletteGroupWidget) instance, contextManager);
  }

  public void destroyInstanceHelper(final DefinitionPaletteGroupWidget instance, final ContextManager contextManager) {
    instance.destroy();
  }

  public void invokePostConstructs(final DefinitionPaletteGroupWidget instance) {
    instance.setUp();
  }
}