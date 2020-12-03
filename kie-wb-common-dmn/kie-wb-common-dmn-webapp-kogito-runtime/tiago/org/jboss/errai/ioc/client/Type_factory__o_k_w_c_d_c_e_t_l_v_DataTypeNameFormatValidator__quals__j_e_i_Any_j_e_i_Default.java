package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.client.editors.common.messages.FlashMessage;
import org.kie.workbench.common.dmn.client.editors.types.common.errors.DataTypeNameIsInvalidErrorMessage;
import org.kie.workbench.common.dmn.client.editors.types.listview.validation.DataTypeNameFormatValidator;
import org.kie.workbench.common.dmn.client.service.DMNClientServicesProxy;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.services.DMNClientServicesProxyImpl;

public class Type_factory__o_k_w_c_d_c_e_t_l_v_DataTypeNameFormatValidator__quals__j_e_i_Any_j_e_i_Default extends Factory<DataTypeNameFormatValidator> { public Type_factory__o_k_w_c_d_c_e_t_l_v_DataTypeNameFormatValidator__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DataTypeNameFormatValidator.class, "Type_factory__o_k_w_c_d_c_e_t_l_v_DataTypeNameFormatValidator__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DataTypeNameFormatValidator.class, Object.class });
  }

  public DataTypeNameFormatValidator createInstance(final ContextManager contextManager) {
    final DMNClientServicesProxy _clientServicesProxy_0 = (DMNClientServicesProxyImpl) contextManager.getInstance("Type_factory__o_k_w_c_d_w_k_c_c_s_DMNClientServicesProxyImpl__quals__j_e_i_Any_j_e_i_Default");
    final Event<FlashMessage> _flashMessageEvent_1 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { FlashMessage.class }, new Annotation[] { });
    final DataTypeNameIsInvalidErrorMessage _nameIsInvalidErrorMessage_2 = (DataTypeNameIsInvalidErrorMessage) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_c_e_DataTypeNameIsInvalidErrorMessage__quals__j_e_i_Any_j_e_i_Default");
    final DataTypeNameFormatValidator instance = new DataTypeNameFormatValidator(_clientServicesProxy_0, _flashMessageEvent_1, _nameIsInvalidErrorMessage_2);
    registerDependentScopedReference(instance, _clientServicesProxy_0);
    registerDependentScopedReference(instance, _flashMessageEvent_1);
    registerDependentScopedReference(instance, _nameIsInvalidErrorMessage_2);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}