package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.client.editors.search.DMNDataTypesSubIndex;
import org.kie.workbench.common.dmn.client.editors.search.DMNEditorSearchIndex;
import org.kie.workbench.common.dmn.client.editors.search.DMNGraphSubIndex;
import org.kie.workbench.common.dmn.client.editors.search.DMNGridSubIndex;
import org.kie.workbench.common.stunner.core.client.api.GlobalSessionManager;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.widgets.client.search.common.BaseEditorSearchIndex;
import org.kie.workbench.common.widgets.client.search.common.EditorSearchIndex;
import org.kie.workbench.common.widgets.client.search.common.SearchPerformedEvent;

public class Type_factory__o_k_w_c_d_c_e_s_DMNEditorSearchIndex__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNEditorSearchIndex> { public Type_factory__o_k_w_c_d_c_e_s_DMNEditorSearchIndex__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DMNEditorSearchIndex.class, "Type_factory__o_k_w_c_d_c_e_s_DMNEditorSearchIndex__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DMNEditorSearchIndex.class, BaseEditorSearchIndex.class, Object.class, EditorSearchIndex.class });
  }

  public DMNEditorSearchIndex createInstance(final ContextManager contextManager) {
    final DMNGraphSubIndex _graphSubIndex_0 = (DMNGraphSubIndex) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_s_DMNGraphSubIndex__quals__j_e_i_Any_j_e_i_Default");
    final Event<SearchPerformedEvent> _searchPerformedEvent_4 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { SearchPerformedEvent.class }, new Annotation[] { });
    final DMNGridSubIndex _gridSubIndex_1 = (DMNGridSubIndex) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_s_DMNGridSubIndex__quals__j_e_i_Any_j_e_i_Default");
    final DMNDataTypesSubIndex _dataTypesSubIndex_2 = (DMNDataTypesSubIndex) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_s_DMNDataTypesSubIndex__quals__j_e_i_Any_j_e_i_Default");
    final SessionManager _sessionManager_3 = (GlobalSessionManager) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_a_GlobalSessionManager__quals__j_e_i_Any_j_e_i_Default");
    final DMNEditorSearchIndex instance = new DMNEditorSearchIndex(_graphSubIndex_0, _gridSubIndex_1, _dataTypesSubIndex_2, _sessionManager_3, _searchPerformedEvent_4);
    registerDependentScopedReference(instance, _searchPerformedEvent_4);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final DMNEditorSearchIndex instance) {
    instance.init();
  }
}