package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.client.editors.types.imported.ImportDataObjectModal;
import org.kie.workbench.common.dmn.client.editors.types.imported.ImportDataObjectModal.View;
import org.kie.workbench.common.dmn.client.editors.types.imported.ImportDataObjectModalView;
import org.kie.workbench.common.dmn.client.service.DMNClientServicesProxy;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.services.DMNClientServicesProxyImpl;
import org.uberfire.ext.editor.commons.client.file.popups.elemental2.Elemental2Modal;

public class Type_factory__o_k_w_c_d_c_e_t_i_ImportDataObjectModal__quals__j_e_i_Any_j_e_i_Default extends Factory<ImportDataObjectModal> { public Type_factory__o_k_w_c_d_c_e_t_i_ImportDataObjectModal__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ImportDataObjectModal.class, "Type_factory__o_k_w_c_d_c_e_t_i_ImportDataObjectModal__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ImportDataObjectModal.class, Elemental2Modal.class, Object.class });
  }

  public ImportDataObjectModal createInstance(final ContextManager contextManager) {
    final View _view_0 = (ImportDataObjectModalView) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_i_ImportDataObjectModalView__quals__j_e_i_Any_j_e_i_Default");
    final DMNClientServicesProxy _client_1 = (DMNClientServicesProxyImpl) contextManager.getInstance("Type_factory__o_k_w_c_d_w_k_c_c_s_DMNClientServicesProxyImpl__quals__j_e_i_Any_j_e_i_Default");
    final ImportDataObjectModal instance = new ImportDataObjectModal(_view_0, _client_1);
    registerDependentScopedReference(instance, _view_0);
    registerDependentScopedReference(instance, _client_1);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}