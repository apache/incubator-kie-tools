package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.dmn.client.editors.types.common.DataTypeManager;
import org.kie.workbench.common.dmn.client.editors.types.common.DataTypeManagerStackStore;
import org.kie.workbench.common.dmn.client.editors.types.common.ItemDefinitionUtils;
import org.kie.workbench.common.dmn.client.editors.types.persistence.DataTypeStore;
import org.kie.workbench.common.dmn.client.editors.types.persistence.ItemDefinitionRecordEngine;
import org.kie.workbench.common.dmn.client.editors.types.persistence.ItemDefinitionStore;
import org.kie.workbench.common.dmn.client.editors.types.persistence.validation.DataTypeNameValidator;

public class Type_factory__o_k_w_c_d_c_e_t_c_DataTypeManager__quals__j_e_i_Any_j_e_i_Default extends Factory<DataTypeManager> { public Type_factory__o_k_w_c_d_c_e_t_c_DataTypeManager__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DataTypeManager.class, "Type_factory__o_k_w_c_d_c_e_t_c_DataTypeManager__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DataTypeManager.class, Object.class });
  }

  public DataTypeManager createInstance(final ContextManager contextManager) {
    final DataTypeNameValidator _dataTypeNameValidator_6 = (DataTypeNameValidator) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_p_v_DataTypeNameValidator__quals__j_e_i_Any_j_e_i_Default");
    final ItemDefinitionStore _itemDefinitionStore_2 = (ItemDefinitionStore) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_p_ItemDefinitionStore__quals__j_e_i_Any_j_e_i_Default");
    final DataTypeStore _dataTypeStore_3 = (DataTypeStore) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_p_DataTypeStore__quals__j_e_i_Any_j_e_i_Default");
    final ManagedInstance<DataTypeManager> _dataTypeManagers_5 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { DataTypeManager.class }, new Annotation[] { });
    final ItemDefinitionRecordEngine _recordEngine_1 = (ItemDefinitionRecordEngine) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_p_ItemDefinitionRecordEngine__quals__j_e_i_Any_j_e_i_Default");
    final TranslationService _translationService_0 = (TranslationService) contextManager.getInstance("Provider_factory__o_j_e_u_c_l_s_TranslationService__quals__j_e_i_Any_j_e_i_Default");
    final ItemDefinitionUtils _itemDefinitionUtils_4 = (ItemDefinitionUtils) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_c_ItemDefinitionUtils__quals__j_e_i_Any_j_e_i_Default");
    final DataTypeManagerStackStore _typeStack_7 = (DataTypeManagerStackStore) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_c_DataTypeManagerStackStore__quals__j_e_i_Any_j_e_i_Default");
    final DataTypeManager instance = new DataTypeManager(_translationService_0, _recordEngine_1, _itemDefinitionStore_2, _dataTypeStore_3, _itemDefinitionUtils_4, _dataTypeManagers_5, _dataTypeNameValidator_6, _typeStack_7);
    registerDependentScopedReference(instance, _dataTypeNameValidator_6);
    registerDependentScopedReference(instance, _dataTypeManagers_5);
    registerDependentScopedReference(instance, _translationService_0);
    registerDependentScopedReference(instance, _itemDefinitionUtils_4);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}