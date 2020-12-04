package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.client.widgets.canvas.ScrollableLienzoPanel;
import org.kie.workbench.common.stunner.client.widgets.presenters.Editor;
import org.kie.workbench.common.stunner.client.widgets.presenters.Viewer;
import org.kie.workbench.common.stunner.client.widgets.presenters.canvas.CanvasViewer;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionDiagramEditor;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionEditor;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionViewer;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.AbstractSessionViewer;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionEditorImpl;
import org.kie.workbench.common.stunner.client.widgets.views.WidgetWrapperView;
import org.kie.workbench.common.stunner.client.widgets.views.WidgetWrapperViewImpl;
import org.kie.workbench.common.stunner.core.client.preferences.StunnerPreferencesRegistries;

public class Type_factory__o_k_w_c_s_c_w_p_s_i_SessionEditorImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<SessionEditorImpl> { public Type_factory__o_k_w_c_s_c_w_p_s_i_SessionEditorImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(SessionEditorImpl.class, "Type_factory__o_k_w_c_s_c_w_p_s_i_SessionEditorImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { SessionEditorImpl.class, AbstractSessionViewer.class, Object.class, SessionViewer.class, CanvasViewer.class, Viewer.class, SessionDiagramEditor.class, SessionEditor.class, SessionViewer.class, CanvasViewer.class, Viewer.class, Editor.class, Viewer.class });
  }

  public SessionEditorImpl createInstance(final ContextManager contextManager) {
    final StunnerPreferencesRegistries _preferencesRegistries_2 = (StunnerPreferencesRegistries) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_p_StunnerPreferencesRegistries__quals__j_e_i_Any_j_e_i_Default");
    final ScrollableLienzoPanel _canvasPanel_1 = (ScrollableLienzoPanel) contextManager.getInstance("Type_factory__o_k_w_c_s_c_w_c_ScrollableLienzoPanel__quals__j_e_i_Any_j_e_i_Default");
    final WidgetWrapperView _view_0 = (WidgetWrapperViewImpl) contextManager.getInstance("Type_factory__o_k_w_c_s_c_w_v_WidgetWrapperViewImpl__quals__j_e_i_Any_j_e_i_Default");
    final SessionEditorImpl instance = new SessionEditorImpl(_view_0, _canvasPanel_1, _preferencesRegistries_2);
    registerDependentScopedReference(instance, _canvasPanel_1);
    registerDependentScopedReference(instance, _view_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}