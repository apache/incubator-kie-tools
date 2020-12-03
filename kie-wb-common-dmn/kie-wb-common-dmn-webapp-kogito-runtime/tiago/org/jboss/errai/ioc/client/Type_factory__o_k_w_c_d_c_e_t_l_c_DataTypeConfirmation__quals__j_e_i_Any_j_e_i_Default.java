package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.dmn.client.editors.common.messages.FlashMessage;
import org.kie.workbench.common.dmn.client.editors.types.listview.confirmation.DataTypeConfirmation;
import org.kie.workbench.common.dmn.client.editors.types.listview.confirmation.DataTypeHasFieldsWarningMessage;
import org.kie.workbench.common.dmn.client.editors.types.listview.confirmation.ReferencedDataTypeWarningMessage;
import org.kie.workbench.common.dmn.client.editors.types.persistence.DataTypeStore;
import org.kie.workbench.common.dmn.client.editors.types.persistence.ItemDefinitionStore;

public class Type_factory__o_k_w_c_d_c_e_t_l_c_DataTypeConfirmation__quals__j_e_i_Any_j_e_i_Default extends Factory<DataTypeConfirmation> { public Type_factory__o_k_w_c_d_c_e_t_l_c_DataTypeConfirmation__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DataTypeConfirmation.class, "Type_factory__o_k_w_c_d_c_e_t_l_c_DataTypeConfirmation__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DataTypeConfirmation.class, Object.class });
  }

  public DataTypeConfirmation createInstance(final ContextManager contextManager) {
    final Event<FlashMessage> _flashMessageEvent_2 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { FlashMessage.class }, new Annotation[] { });
    final ItemDefinitionStore _itemDefinitionStore_1 = (ItemDefinitionStore) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_p_ItemDefinitionStore__quals__j_e_i_Any_j_e_i_Default");
    final DataTypeHasFieldsWarningMessage _dataTypeHasFieldsWarningMessage_3 = (DataTypeHasFieldsWarningMessage) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_l_c_DataTypeHasFieldsWarningMessage__quals__j_e_i_Any_j_e_i_Default");
    final DataTypeStore _dataTypeStore_0 = (DataTypeStore) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_p_DataTypeStore__quals__j_e_i_Any_j_e_i_Default");
    final ReferencedDataTypeWarningMessage _referencedDataTypeWarningMessage_4 = (ReferencedDataTypeWarningMessage) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_l_c_ReferencedDataTypeWarningMessage__quals__j_e_i_Any_j_e_i_Default");
    final TranslationService _translationService_5 = (TranslationService) contextManager.getInstance("Provider_factory__o_j_e_u_c_l_s_TranslationService__quals__j_e_i_Any_j_e_i_Default");
    final DataTypeConfirmation instance = new DataTypeConfirmation(_dataTypeStore_0, _itemDefinitionStore_1, _flashMessageEvent_2, _dataTypeHasFieldsWarningMessage_3, _referencedDataTypeWarningMessage_4, _translationService_5);
    registerDependentScopedReference(instance, _flashMessageEvent_2);
    registerDependentScopedReference(instance, _dataTypeHasFieldsWarningMessage_3);
    registerDependentScopedReference(instance, _referencedDataTypeWarningMessage_4);
    registerDependentScopedReference(instance, _translationService_5);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}