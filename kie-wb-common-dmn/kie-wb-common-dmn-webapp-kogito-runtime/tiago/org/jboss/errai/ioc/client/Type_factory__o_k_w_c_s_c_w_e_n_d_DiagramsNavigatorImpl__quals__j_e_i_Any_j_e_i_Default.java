package org.jboss.errai.ioc.client;

import com.google.gwt.user.client.ui.IsWidget;
import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Any;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.client.widgets.event.LoadDiagramEvent;
import org.kie.workbench.common.stunner.client.widgets.explorer.navigator.Navigator;
import org.kie.workbench.common.stunner.client.widgets.explorer.navigator.NavigatorView;
import org.kie.workbench.common.stunner.client.widgets.explorer.navigator.diagrams.DiagramNavigatorItem;
import org.kie.workbench.common.stunner.client.widgets.explorer.navigator.diagrams.DiagramsNavigator;
import org.kie.workbench.common.stunner.client.widgets.explorer.navigator.diagrams.DiagramsNavigatorImpl;
import org.kie.workbench.common.stunner.client.widgets.explorer.navigator.view.BootstrapNavigatorView;
import org.kie.workbench.common.stunner.core.client.error.DiagramClientErrorHandler;
import org.kie.workbench.common.stunner.core.client.service.ClientDiagramService;
import org.kie.workbench.common.stunner.core.client.service.ClientDiagramServiceImpl;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;

public class Type_factory__o_k_w_c_s_c_w_e_n_d_DiagramsNavigatorImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<DiagramsNavigatorImpl> { public Type_factory__o_k_w_c_s_c_w_e_n_d_DiagramsNavigatorImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DiagramsNavigatorImpl.class, "Type_factory__o_k_w_c_s_c_w_e_n_d_DiagramsNavigatorImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DiagramsNavigatorImpl.class, Object.class, DiagramsNavigator.class, Navigator.class, IsWidget.class });
  }

  public DiagramsNavigatorImpl createInstance(final ContextManager contextManager) {
    final ErrorPopupPresenter _errorPopupPresenter_5 = (ErrorPopupPresenter) contextManager.getInstance("Type_factory__o_u_c_w_w_c_ErrorPopupPresenter__quals__j_e_i_Any_j_e_i_Default");
    final ClientDiagramService _clientDiagramServices_0 = (ClientDiagramServiceImpl) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_s_ClientDiagramServiceImpl__quals__j_e_i_Any_j_e_i_Default");
    final NavigatorView _view_3 = (BootstrapNavigatorView) contextManager.getInstance("Type_factory__o_k_w_c_s_c_w_e_n_v_BootstrapNavigatorView__quals__j_e_i_Any_j_e_i_Default");
    final Event<LoadDiagramEvent> _loadDiagramEventEvent_2 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { LoadDiagramEvent.class }, new Annotation[] { });
    final ManagedInstance<DiagramNavigatorItem> _navigatorItemInstances_1 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { DiagramNavigatorItem.class }, new Annotation[] { new Any() {
        public Class annotationType() {
          return Any.class;
        }
        public String toString() {
          return "@javax.enterprise.inject.Any()";
        }
    } });
    final DiagramClientErrorHandler _diagramClientErrorHandler_4 = (DiagramClientErrorHandler) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_e_DiagramClientErrorHandler__quals__j_e_i_Any_j_e_i_Default");
    final DiagramsNavigatorImpl instance = new DiagramsNavigatorImpl(_clientDiagramServices_0, _navigatorItemInstances_1, _loadDiagramEventEvent_2, _view_3, _diagramClientErrorHandler_4, _errorPopupPresenter_5);
    registerDependentScopedReference(instance, _clientDiagramServices_0);
    registerDependentScopedReference(instance, _view_3);
    registerDependentScopedReference(instance, _loadDiagramEventEvent_2);
    registerDependentScopedReference(instance, _navigatorItemInstances_1);
    registerDependentScopedReference(instance, _diagramClientErrorHandler_4);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((DiagramsNavigatorImpl) instance, contextManager);
  }

  public void destroyInstanceHelper(final DiagramsNavigatorImpl instance, final ContextManager contextManager) {
    instance.destroy();
  }
}