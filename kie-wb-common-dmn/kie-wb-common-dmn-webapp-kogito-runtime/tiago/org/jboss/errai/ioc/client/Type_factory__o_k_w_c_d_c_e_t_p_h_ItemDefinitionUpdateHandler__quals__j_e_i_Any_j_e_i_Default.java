package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.client.editors.types.DataTypeChangedEvent;
import org.kie.workbench.common.dmn.client.editors.types.common.DataTypeManager;
import org.kie.workbench.common.dmn.client.editors.types.common.ItemDefinitionUtils;
import org.kie.workbench.common.dmn.client.editors.types.persistence.handlers.ItemDefinitionUpdateHandler;
import org.kie.workbench.common.dmn.client.editors.types.persistence.handlers.common.PropertiesPanelNotifier;

public class Type_factory__o_k_w_c_d_c_e_t_p_h_ItemDefinitionUpdateHandler__quals__j_e_i_Any_j_e_i_Default extends Factory<ItemDefinitionUpdateHandler> { public Type_factory__o_k_w_c_d_c_e_t_p_h_ItemDefinitionUpdateHandler__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ItemDefinitionUpdateHandler.class, "Type_factory__o_k_w_c_d_c_e_t_p_h_ItemDefinitionUpdateHandler__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ItemDefinitionUpdateHandler.class, Object.class });
  }

  public ItemDefinitionUpdateHandler createInstance(final ContextManager contextManager) {
    final ItemDefinitionUtils _itemDefinitionUtils_1 = (ItemDefinitionUtils) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_c_ItemDefinitionUtils__quals__j_e_i_Any_j_e_i_Default");
    final Event<DataTypeChangedEvent> _dataTypeChangedEvent_3 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { DataTypeChangedEvent.class }, new Annotation[] { });
    final DataTypeManager _dataTypeManager_0 = (DataTypeManager) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_c_DataTypeManager__quals__j_e_i_Any_j_e_i_Default");
    final PropertiesPanelNotifier _panelNotifier_2 = (PropertiesPanelNotifier) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_p_h_c_PropertiesPanelNotifier__quals__j_e_i_Any_j_e_i_Default");
    final ItemDefinitionUpdateHandler instance = new ItemDefinitionUpdateHandler(_dataTypeManager_0, _itemDefinitionUtils_1, _panelNotifier_2, _dataTypeChangedEvent_3);
    registerDependentScopedReference(instance, _itemDefinitionUtils_1);
    registerDependentScopedReference(instance, _dataTypeChangedEvent_3);
    registerDependentScopedReference(instance, _dataTypeManager_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}