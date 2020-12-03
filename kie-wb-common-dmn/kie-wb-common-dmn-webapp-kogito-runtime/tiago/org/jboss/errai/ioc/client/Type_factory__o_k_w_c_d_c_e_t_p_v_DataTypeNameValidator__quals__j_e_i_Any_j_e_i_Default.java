package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.client.editors.common.messages.FlashMessage;
import org.kie.workbench.common.dmn.client.editors.types.common.errors.DataTypeNameIsBlankErrorMessage;
import org.kie.workbench.common.dmn.client.editors.types.common.errors.DataTypeNameIsDefaultTypeMessage;
import org.kie.workbench.common.dmn.client.editors.types.common.errors.DataTypeNameIsNotUniqueErrorMessage;
import org.kie.workbench.common.dmn.client.editors.types.persistence.DataTypeStore;
import org.kie.workbench.common.dmn.client.editors.types.persistence.validation.DataTypeNameValidator;

public class Type_factory__o_k_w_c_d_c_e_t_p_v_DataTypeNameValidator__quals__j_e_i_Any_j_e_i_Default extends Factory<DataTypeNameValidator> { public Type_factory__o_k_w_c_d_c_e_t_p_v_DataTypeNameValidator__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DataTypeNameValidator.class, "Type_factory__o_k_w_c_d_c_e_t_p_v_DataTypeNameValidator__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DataTypeNameValidator.class, Object.class });
  }

  public DataTypeNameValidator createInstance(final ContextManager contextManager) {
    final DataTypeNameIsDefaultTypeMessage _nameIsDefaultTypeMessage_3 = (DataTypeNameIsDefaultTypeMessage) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_c_e_DataTypeNameIsDefaultTypeMessage__quals__j_e_i_Any_j_e_i_Default");
    final Event<FlashMessage> _flashMessageEvent_0 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { FlashMessage.class }, new Annotation[] { });
    final DataTypeNameIsNotUniqueErrorMessage _notUniqueErrorMessage_2 = (DataTypeNameIsNotUniqueErrorMessage) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_c_e_DataTypeNameIsNotUniqueErrorMessage__quals__j_e_i_Any_j_e_i_Default");
    final DataTypeStore _dataTypeStore_4 = (DataTypeStore) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_p_DataTypeStore__quals__j_e_i_Any_j_e_i_Default");
    final DataTypeNameIsBlankErrorMessage _blankErrorMessage_1 = (DataTypeNameIsBlankErrorMessage) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_c_e_DataTypeNameIsBlankErrorMessage__quals__j_e_i_Any_j_e_i_Default");
    final DataTypeNameValidator instance = new DataTypeNameValidator(_flashMessageEvent_0, _blankErrorMessage_1, _notUniqueErrorMessage_2, _nameIsDefaultTypeMessage_3, _dataTypeStore_4);
    registerDependentScopedReference(instance, _nameIsDefaultTypeMessage_3);
    registerDependentScopedReference(instance, _flashMessageEvent_0);
    registerDependentScopedReference(instance, _notUniqueErrorMessage_2);
    registerDependentScopedReference(instance, _blankErrorMessage_1);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}