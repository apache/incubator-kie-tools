package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Any;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.client.widgets.presenters.Viewer;
import org.kie.workbench.common.stunner.client.widgets.presenters.canvas.AbstractCanvasViewer;
import org.kie.workbench.common.stunner.client.widgets.presenters.canvas.CanvasViewer;
import org.kie.workbench.common.stunner.client.widgets.presenters.diagram.DiagramViewer;
import org.kie.workbench.common.stunner.client.widgets.presenters.diagram.impl.AbstractDiagramViewer;
import org.kie.workbench.common.stunner.client.widgets.presenters.diagram.impl.DefaultDiagramViewer;
import org.kie.workbench.common.stunner.client.widgets.views.WidgetWrapperView;
import org.kie.workbench.common.stunner.client.widgets.views.WidgetWrapperViewImpl;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasPanel;
import org.kie.workbench.common.stunner.core.client.canvas.controls.MediatorsControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.SelectionControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.select.SingleSelection;
import org.kie.workbench.common.stunner.core.client.preferences.StunnerPreferencesRegistries;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

public class Type_factory__o_k_w_c_s_c_w_p_d_i_DefaultDiagramViewer__quals__j_e_i_Any_j_e_i_Default extends Factory<DefaultDiagramViewer> { public Type_factory__o_k_w_c_s_c_w_p_d_i_DefaultDiagramViewer__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DefaultDiagramViewer.class, "Type_factory__o_k_w_c_s_c_w_p_d_i_DefaultDiagramViewer__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DefaultDiagramViewer.class, AbstractDiagramViewer.class, AbstractCanvasViewer.class, Object.class, CanvasViewer.class, Viewer.class, DiagramViewer.class });
  }

  public DefaultDiagramViewer createInstance(final ContextManager contextManager) {
    final WidgetWrapperView _view_6 = (WidgetWrapperViewImpl) contextManager.getInstance("Type_factory__o_k_w_c_s_c_w_v_WidgetWrapperViewImpl__quals__j_e_i_Any_j_e_i_Default");
    final ManagedInstance<AbstractCanvasHandler> _canvasHandlerInstances_3 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { AbstractCanvasHandler.class }, new Annotation[] { new Any() {
        public Class annotationType() {
          return Any.class;
        }
        public String toString() {
          return "@javax.enterprise.inject.Any()";
        }
    } });
    final ManagedInstance<SelectionControl<AbstractCanvasHandler, Element>> _selectionControlInstances_5 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { SelectionControl.class }, new Annotation[] { new Any() {
        public Class annotationType() {
          return Any.class;
        }
        public String toString() {
          return "@javax.enterprise.inject.Any()";
        }
      }, new SingleSelection() {
        public Class annotationType() {
          return SingleSelection.class;
        }
        public String toString() {
          return "@org.kie.workbench.common.stunner.core.client.canvas.controls.select.SingleSelection()";
        }
    } });
    final ManagedInstance<MediatorsControl<AbstractCanvas>> _mediatorsControlInstances_4 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { MediatorsControl.class }, new Annotation[] { new Any() {
        public Class annotationType() {
          return Any.class;
        }
        public String toString() {
          return "@javax.enterprise.inject.Any()";
        }
    } });
    final ManagedInstance<CanvasPanel> _canvasPanelInstances_2 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { CanvasPanel.class }, new Annotation[] { new Any() {
        public Class annotationType() {
          return Any.class;
        }
        public String toString() {
          return "@javax.enterprise.inject.Any()";
        }
    } });
    final StunnerPreferencesRegistries _preferencesRegistries_7 = (StunnerPreferencesRegistries) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_p_StunnerPreferencesRegistries__quals__j_e_i_Any_j_e_i_Default");
    final DefinitionUtils _definitionUtils_0 = (DefinitionUtils) contextManager.getInstance("Type_factory__o_k_w_c_s_c_u_DefinitionUtils__quals__j_e_i_Any_j_e_i_Default");
    final ManagedInstance<AbstractCanvas> _canvasInstances_1 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { AbstractCanvas.class }, new Annotation[] { new Any() {
        public Class annotationType() {
          return Any.class;
        }
        public String toString() {
          return "@javax.enterprise.inject.Any()";
        }
    } });
    final DefaultDiagramViewer instance = new DefaultDiagramViewer(_definitionUtils_0, _canvasInstances_1, _canvasPanelInstances_2, _canvasHandlerInstances_3, _mediatorsControlInstances_4, _selectionControlInstances_5, _view_6, _preferencesRegistries_7);
    registerDependentScopedReference(instance, _view_6);
    registerDependentScopedReference(instance, _canvasHandlerInstances_3);
    registerDependentScopedReference(instance, _selectionControlInstances_5);
    registerDependentScopedReference(instance, _mediatorsControlInstances_4);
    registerDependentScopedReference(instance, _canvasPanelInstances_2);
    registerDependentScopedReference(instance, _canvasInstances_1);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}