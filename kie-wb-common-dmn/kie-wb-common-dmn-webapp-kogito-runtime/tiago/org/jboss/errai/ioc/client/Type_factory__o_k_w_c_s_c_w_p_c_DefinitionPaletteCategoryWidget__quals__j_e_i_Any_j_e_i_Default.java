package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.client.widgets.palette.BS3PaletteWidgetPresenter;
import org.kie.workbench.common.stunner.client.widgets.palette.categories.DefinitionPaletteCategoryWidget;
import org.kie.workbench.common.stunner.client.widgets.palette.categories.DefinitionPaletteCategoryWidgetView;
import org.kie.workbench.common.stunner.client.widgets.palette.categories.DefinitionPaletteCategoryWidgetView.Presenter;
import org.kie.workbench.common.stunner.client.widgets.palette.categories.DefinitionPaletteCategoryWidgetViewImpl;
import org.kie.workbench.common.stunner.client.widgets.palette.categories.group.DefinitionPaletteGroupWidget;
import org.kie.workbench.common.stunner.client.widgets.palette.categories.items.DefinitionPaletteItemWidget;

public class Type_factory__o_k_w_c_s_c_w_p_c_DefinitionPaletteCategoryWidget__quals__j_e_i_Any_j_e_i_Default extends Factory<DefinitionPaletteCategoryWidget> { public Type_factory__o_k_w_c_s_c_w_p_c_DefinitionPaletteCategoryWidget__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DefinitionPaletteCategoryWidget.class, "Type_factory__o_k_w_c_s_c_w_p_c_DefinitionPaletteCategoryWidget__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DefinitionPaletteCategoryWidget.class, Object.class, Presenter.class, BS3PaletteWidgetPresenter.class, IsElement.class });
  }

  public DefinitionPaletteCategoryWidget createInstance(final ContextManager contextManager) {
    final DefinitionPaletteCategoryWidgetView _view_0 = (DefinitionPaletteCategoryWidgetViewImpl) contextManager.getInstance("Type_factory__o_k_w_c_s_c_w_p_c_DefinitionPaletteCategoryWidgetViewImpl__quals__j_e_i_Any_j_e_i_Default");
    final ManagedInstance<DefinitionPaletteItemWidget> _definitionPaletteItemWidgetInstance_1 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { DefinitionPaletteItemWidget.class }, new Annotation[] { });
    final ManagedInstance<DefinitionPaletteGroupWidget> _definitionPaletteGroupWidgetInstance_2 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { DefinitionPaletteGroupWidget.class }, new Annotation[] { });
    final DefinitionPaletteCategoryWidget instance = new DefinitionPaletteCategoryWidget(_view_0, _definitionPaletteItemWidgetInstance_1, _definitionPaletteGroupWidgetInstance_2);
    registerDependentScopedReference(instance, _view_0);
    registerDependentScopedReference(instance, _definitionPaletteItemWidgetInstance_1);
    registerDependentScopedReference(instance, _definitionPaletteGroupWidgetInstance_2);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((DefinitionPaletteCategoryWidget) instance, contextManager);
  }

  public void destroyInstanceHelper(final DefinitionPaletteCategoryWidget instance, final ContextManager contextManager) {
    instance.destroy();
  }

  public void invokePostConstructs(final DefinitionPaletteCategoryWidget instance) {
    instance.setUp();
  }
}