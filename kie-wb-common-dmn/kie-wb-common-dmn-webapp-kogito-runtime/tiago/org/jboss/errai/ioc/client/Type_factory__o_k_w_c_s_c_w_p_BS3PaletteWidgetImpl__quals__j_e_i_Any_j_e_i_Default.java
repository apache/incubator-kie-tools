package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.client.lienzo.components.glyph.ShapeGlyphDragHandler;
import org.kie.workbench.common.stunner.client.widgets.palette.BS3PaletteWidget;
import org.kie.workbench.common.stunner.client.widgets.palette.BS3PaletteWidgetImpl;
import org.kie.workbench.common.stunner.client.widgets.palette.BS3PaletteWidgetView;
import org.kie.workbench.common.stunner.client.widgets.palette.BS3PaletteWidgetViewImpl;
import org.kie.workbench.common.stunner.client.widgets.palette.DefaultPaletteWidget;
import org.kie.workbench.common.stunner.client.widgets.palette.PaletteWidget;
import org.kie.workbench.common.stunner.client.widgets.palette.categories.DefinitionPaletteCategoryWidget;
import org.kie.workbench.common.stunner.client.widgets.palette.categories.items.DefinitionPaletteItemWidget;
import org.kie.workbench.common.stunner.client.widgets.palette.collapsed.CollapsedDefinitionPaletteItemWidget;
import org.kie.workbench.common.stunner.core.client.api.ShapeManager;
import org.kie.workbench.common.stunner.core.client.api.ShapeManagerImpl;
import org.kie.workbench.common.stunner.core.client.components.palette.AbstractPalette;
import org.kie.workbench.common.stunner.core.client.components.palette.Palette;
import org.kie.workbench.common.stunner.core.client.preferences.StunnerPreferencesRegistries;
import org.kie.workbench.common.stunner.core.client.service.ClientFactoryService;

public class Type_factory__o_k_w_c_s_c_w_p_BS3PaletteWidgetImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<BS3PaletteWidgetImpl> { public Type_factory__o_k_w_c_s_c_w_p_BS3PaletteWidgetImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(BS3PaletteWidgetImpl.class, "Type_factory__o_k_w_c_s_c_w_p_BS3PaletteWidgetImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { BS3PaletteWidgetImpl.class, AbstractPalette.class, Object.class, Palette.class, BS3PaletteWidget.class, DefaultPaletteWidget.class, PaletteWidget.class, IsElement.class });
  }

  public BS3PaletteWidgetImpl createInstance(final ContextManager contextManager) {
    final ShapeManager _shapeManager_0 = (ShapeManagerImpl) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_a_ShapeManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    final ShapeGlyphDragHandler _shapeGlyphDragHandler_3 = (ShapeGlyphDragHandler) contextManager.getInstance("Type_factory__o_k_w_c_s_c_l_c_g_ShapeGlyphDragHandler__quals__j_e_i_Any_j_e_i_Default");
    final ManagedInstance<DefinitionPaletteItemWidget> _definitionPaletteItemWidgets_6 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { DefinitionPaletteItemWidget.class }, new Annotation[] { });
    final StunnerPreferencesRegistries _preferencesRegistries_4 = (StunnerPreferencesRegistries) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_p_StunnerPreferencesRegistries__quals__j_e_i_Any_j_e_i_Default");
    final ManagedInstance<CollapsedDefinitionPaletteItemWidget> _collapsedDefinitionPaletteItemWidgets_7 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { CollapsedDefinitionPaletteItemWidget.class }, new Annotation[] { });
    final BS3PaletteWidgetView _view_2 = (BS3PaletteWidgetViewImpl) contextManager.getInstance("Type_factory__o_k_w_c_s_c_w_p_BS3PaletteWidgetViewImpl__quals__j_e_i_Any_j_e_i_Default");
    final ClientFactoryService _clientFactoryServices_1 = (ClientFactoryService) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_s_ClientFactoryService__quals__j_e_i_Any_j_e_i_Default");
    final ManagedInstance<DefinitionPaletteCategoryWidget> _categoryWidgetInstance_5 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { DefinitionPaletteCategoryWidget.class }, new Annotation[] { });
    final BS3PaletteWidgetImpl instance = new BS3PaletteWidgetImpl(_shapeManager_0, _clientFactoryServices_1, _view_2, _shapeGlyphDragHandler_3, _preferencesRegistries_4, _categoryWidgetInstance_5, _definitionPaletteItemWidgets_6, _collapsedDefinitionPaletteItemWidgets_7);
    registerDependentScopedReference(instance, _shapeGlyphDragHandler_3);
    registerDependentScopedReference(instance, _definitionPaletteItemWidgets_6);
    registerDependentScopedReference(instance, _collapsedDefinitionPaletteItemWidgets_7);
    registerDependentScopedReference(instance, _view_2);
    registerDependentScopedReference(instance, _categoryWidgetInstance_5);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final BS3PaletteWidgetImpl instance) {
    instance.init();
  }
}