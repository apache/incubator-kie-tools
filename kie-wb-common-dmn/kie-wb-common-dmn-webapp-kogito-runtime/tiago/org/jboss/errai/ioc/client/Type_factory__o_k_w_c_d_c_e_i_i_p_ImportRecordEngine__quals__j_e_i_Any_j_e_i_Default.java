package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Any;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.client.editors.common.messages.FlashMessage;
import org.kie.workbench.common.dmn.client.editors.common.persistence.RecordEngine;
import org.kie.workbench.common.dmn.client.editors.included.imports.ImportFactory;
import org.kie.workbench.common.dmn.client.editors.included.imports.IncludedModelsIndex;
import org.kie.workbench.common.dmn.client.editors.included.imports.IncludedModelsPageStateProviderImpl;
import org.kie.workbench.common.dmn.client.editors.included.imports.messages.IncludedModelErrorMessageFactory;
import org.kie.workbench.common.dmn.client.editors.included.imports.persistence.DRGElementHandler;
import org.kie.workbench.common.dmn.client.editors.included.imports.persistence.DefinitionsHandler;
import org.kie.workbench.common.dmn.client.editors.included.imports.persistence.ImportRecordEngine;

public class Type_factory__o_k_w_c_d_c_e_i_i_p_ImportRecordEngine__quals__j_e_i_Any_j_e_i_Default extends Factory<ImportRecordEngine> { public Type_factory__o_k_w_c_d_c_e_i_i_p_ImportRecordEngine__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ImportRecordEngine.class, "Type_factory__o_k_w_c_d_c_e_i_i_p_ImportRecordEngine__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ImportRecordEngine.class, Object.class, RecordEngine.class });
  }

  public ImportRecordEngine createInstance(final ContextManager contextManager) {
    final IncludedModelErrorMessageFactory _messageFactory_2 = (IncludedModelErrorMessageFactory) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_i_i_m_IncludedModelErrorMessageFactory__quals__j_e_i_Any_j_e_i_Default");
    final DefinitionsHandler _definitionsHandler_5 = (DefinitionsHandler) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_i_i_p_DefinitionsHandler__quals__j_e_i_Any_j_e_i_Default");
    final IncludedModelsIndex _includedModelsIndex_1 = (IncludedModelsIndex) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_i_i_IncludedModelsIndex__quals__j_e_i_Any_j_e_i_Default");
    final ManagedInstance<DRGElementHandler> _drgElementHandlers_6 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { DRGElementHandler.class }, new Annotation[] { new Any() {
        public Class annotationType() {
          return Any.class;
        }
        public String toString() {
          return "@javax.enterprise.inject.Any()";
        }
    } });
    final ImportFactory _importFactory_3 = (ImportFactory) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_i_i_ImportFactory__quals__j_e_i_Any_j_e_i_Default");
    final Event<FlashMessage> _flashMessageEvent_4 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { FlashMessage.class }, new Annotation[] { });
    final IncludedModelsPageStateProviderImpl _stateProvider_0 = (IncludedModelsPageStateProviderImpl) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_i_i_IncludedModelsPageStateProviderImpl__quals__j_e_i_Any_j_e_i_Default");
    final ImportRecordEngine instance = new ImportRecordEngine(_stateProvider_0, _includedModelsIndex_1, _messageFactory_2, _importFactory_3, _flashMessageEvent_4, _definitionsHandler_5, _drgElementHandlers_6);
    registerDependentScopedReference(instance, _messageFactory_2);
    registerDependentScopedReference(instance, _definitionsHandler_5);
    registerDependentScopedReference(instance, _drgElementHandlers_6);
    registerDependentScopedReference(instance, _importFactory_3);
    registerDependentScopedReference(instance, _flashMessageEvent_4);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}