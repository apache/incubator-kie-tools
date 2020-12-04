package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.client.api.included.legacy.DMNIncludeModelsClient;
import org.kie.workbench.common.dmn.client.docks.navigator.events.RefreshDecisionComponents;
import org.kie.workbench.common.dmn.client.editors.included.imports.persistence.ImportRecordEngine;
import org.kie.workbench.common.dmn.client.editors.included.modal.IncludedModelModal;
import org.kie.workbench.common.dmn.client.editors.included.modal.IncludedModelModal.View;
import org.kie.workbench.common.dmn.client.editors.included.modal.IncludedModelModalView;
import org.kie.workbench.common.dmn.client.editors.included.modal.dropdown.DMNAssetsDropdown;
import org.kie.workbench.common.dmn.client.editors.types.common.events.RefreshDataTypesListEvent;
import org.uberfire.ext.editor.commons.client.file.popups.elemental2.Elemental2Modal;

public class Type_factory__o_k_w_c_d_c_e_i_m_IncludedModelModal__quals__j_e_i_Any_j_e_i_Default extends Factory<IncludedModelModal> { public Type_factory__o_k_w_c_d_c_e_i_m_IncludedModelModal__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(IncludedModelModal.class, "Type_factory__o_k_w_c_d_c_e_i_m_IncludedModelModal__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { IncludedModelModal.class, Elemental2Modal.class, Object.class });
  }

  public IncludedModelModal createInstance(final ContextManager contextManager) {
    final ImportRecordEngine _recordEngine_2 = (ImportRecordEngine) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_i_i_p_ImportRecordEngine__quals__j_e_i_Any_j_e_i_Default");
    final Event<RefreshDecisionComponents> _refreshDecisionComponentsEvent_5 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { RefreshDecisionComponents.class }, new Annotation[] { });
    final View _view_0 = (IncludedModelModalView) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_i_m_IncludedModelModalView__quals__j_e_i_Any_j_e_i_Default");
    final DMNAssetsDropdown _dropdown_1 = (DMNAssetsDropdown) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_i_m_d_DMNAssetsDropdown__quals__j_e_i_Any_j_e_i_Default");
    final DMNIncludeModelsClient _client_3 = (DMNIncludeModelsClient) contextManager.getInstance("Type_factory__o_k_w_c_d_c_a_i_l_DMNIncludeModelsClient__quals__j_e_i_Any_j_e_i_Default");
    final Event<RefreshDataTypesListEvent> _refreshDataTypesListEvent_4 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { RefreshDataTypesListEvent.class }, new Annotation[] { });
    final IncludedModelModal instance = new IncludedModelModal(_view_0, _dropdown_1, _recordEngine_2, _client_3, _refreshDataTypesListEvent_4, _refreshDecisionComponentsEvent_5);
    registerDependentScopedReference(instance, _recordEngine_2);
    registerDependentScopedReference(instance, _refreshDecisionComponentsEvent_5);
    registerDependentScopedReference(instance, _view_0);
    registerDependentScopedReference(instance, _dropdown_1);
    registerDependentScopedReference(instance, _client_3);
    registerDependentScopedReference(instance, _refreshDataTypesListEvent_4);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final IncludedModelModal instance) {
    instance.setup();
  }
}