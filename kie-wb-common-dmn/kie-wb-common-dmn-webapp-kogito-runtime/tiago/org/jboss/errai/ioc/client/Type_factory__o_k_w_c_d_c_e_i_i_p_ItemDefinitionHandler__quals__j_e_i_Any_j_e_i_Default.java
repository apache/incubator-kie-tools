package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.client.editors.included.imports.persistence.DRGElementHandler;
import org.kie.workbench.common.dmn.client.editors.included.imports.persistence.ItemDefinitionHandler;
import org.kie.workbench.common.dmn.client.editors.types.common.ItemDefinitionUtils;
import org.kie.workbench.common.dmn.client.editors.types.common.events.RefreshDataTypesListEvent;
import org.kie.workbench.common.dmn.client.editors.types.persistence.handlers.common.PropertiesPanelNotifier;

public class Type_factory__o_k_w_c_d_c_e_i_i_p_ItemDefinitionHandler__quals__j_e_i_Any_j_e_i_Default extends Factory<ItemDefinitionHandler> { public Type_factory__o_k_w_c_d_c_e_i_i_p_ItemDefinitionHandler__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ItemDefinitionHandler.class, "Type_factory__o_k_w_c_d_c_e_i_i_p_ItemDefinitionHandler__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ItemDefinitionHandler.class, Object.class, DRGElementHandler.class });
  }

  public ItemDefinitionHandler createInstance(final ContextManager contextManager) {
    final ItemDefinitionUtils _itemDefinitionUtils_0 = (ItemDefinitionUtils) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_c_ItemDefinitionUtils__quals__j_e_i_Any_j_e_i_Default");
    final PropertiesPanelNotifier _panelNotifier_2 = (PropertiesPanelNotifier) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_p_h_c_PropertiesPanelNotifier__quals__j_e_i_Any_j_e_i_Default");
    final Event<RefreshDataTypesListEvent> _refreshDataTypesListEvent_1 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { RefreshDataTypesListEvent.class }, new Annotation[] { });
    final ItemDefinitionHandler instance = new ItemDefinitionHandler(_itemDefinitionUtils_0, _refreshDataTypesListEvent_1, _panelNotifier_2);
    registerDependentScopedReference(instance, _itemDefinitionUtils_0);
    registerDependentScopedReference(instance, _refreshDataTypesListEvent_1);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}