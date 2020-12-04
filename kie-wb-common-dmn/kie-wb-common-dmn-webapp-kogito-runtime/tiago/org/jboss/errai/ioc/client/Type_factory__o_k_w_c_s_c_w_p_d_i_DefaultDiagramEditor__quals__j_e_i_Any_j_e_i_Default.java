package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Any;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.client.widgets.presenters.diagram.DiagramEditor;
import org.kie.workbench.common.stunner.client.widgets.presenters.diagram.DiagramViewer;
import org.kie.workbench.common.stunner.client.widgets.presenters.diagram.impl.DefaultDiagramEditor;
import org.kie.workbench.common.stunner.client.widgets.presenters.diagram.impl.DefaultDiagramViewer;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.ContainmentAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.DockingAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.LocationControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.ResizeControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.EdgeBuilderControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.ElementBuilderControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.NodeBuilderControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.impl.Observer;
import org.kie.workbench.common.stunner.core.client.canvas.controls.connection.ConnectionAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.connection.ControlPointControl;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

public class Type_factory__o_k_w_c_s_c_w_p_d_i_DefaultDiagramEditor__quals__j_e_i_Any_j_e_i_Default extends Factory<DefaultDiagramEditor> { public Type_factory__o_k_w_c_s_c_w_p_d_i_DefaultDiagramEditor__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DefaultDiagramEditor.class, "Type_factory__o_k_w_c_s_c_w_p_d_i_DefaultDiagramEditor__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DiagramEditor.class, Object.class });
  }

  public DefaultDiagramEditor createInstance(final ContextManager contextManager) {
    final DefinitionUtils _definitionUtils_0 = (DefinitionUtils) contextManager.getInstance("Type_factory__o_k_w_c_s_c_u_DefinitionUtils__quals__j_e_i_Any_j_e_i_Default");
    final ManagedInstance<ContainmentAcceptorControl<AbstractCanvasHandler>> _containmentAcceptorControls_10 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { ContainmentAcceptorControl.class }, new Annotation[] { new Any() {
        public Class annotationType() {
          return Any.class;
        }
        public String toString() {
          return "@javax.enterprise.inject.Any()";
        }
    } });
    final ManagedInstance<DockingAcceptorControl<AbstractCanvasHandler>> _dockingAcceptorControls_11 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { DockingAcceptorControl.class }, new Annotation[] { new Any() {
        public Class annotationType() {
          return Any.class;
        }
        public String toString() {
          return "@javax.enterprise.inject.Any()";
        }
    } });
    final DiagramViewer<Diagram, AbstractCanvasHandler> _viewer_1 = (DefaultDiagramViewer) contextManager.getInstance("Type_factory__o_k_w_c_s_c_w_p_d_i_DefaultDiagramViewer__quals__j_e_i_Any_j_e_i_Default");
    final ManagedInstance<CanvasCommandManager<AbstractCanvasHandler>> _commandManagers_2 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { CanvasCommandManager.class }, new Annotation[] { new Any() {
        public Class annotationType() {
          return Any.class;
        }
        public String toString() {
          return "@javax.enterprise.inject.Any()";
        }
    } });
    final ManagedInstance<ResizeControl<AbstractCanvasHandler, Element>> _resizeControls_4 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { ResizeControl.class }, new Annotation[] { new Any() {
        public Class annotationType() {
          return Any.class;
        }
        public String toString() {
          return "@javax.enterprise.inject.Any()";
        }
    } });
    final ManagedInstance<ControlPointControl<AbstractCanvasHandler>> _controlPointControls_8 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { ControlPointControl.class }, new Annotation[] { new Any() {
        public Class annotationType() {
          return Any.class;
        }
        public String toString() {
          return "@javax.enterprise.inject.Any()";
        }
    } });
    final ManagedInstance<LocationControl<AbstractCanvasHandler, Element>> _locationControls_3 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { LocationControl.class }, new Annotation[] { new Any() {
        public Class annotationType() {
          return Any.class;
        }
        public String toString() {
          return "@javax.enterprise.inject.Any()";
        }
    } });
    final ManagedInstance<ConnectionAcceptorControl<AbstractCanvasHandler>> _connectionAcceptorControls_9 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { ConnectionAcceptorControl.class }, new Annotation[] { new Any() {
        public Class annotationType() {
          return Any.class;
        }
        public String toString() {
          return "@javax.enterprise.inject.Any()";
        }
    } });
    final ManagedInstance<NodeBuilderControl<AbstractCanvasHandler>> _nodeBuilderControls_6 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { NodeBuilderControl.class }, new Annotation[] { new Any() {
        public Class annotationType() {
          return Any.class;
        }
        public String toString() {
          return "@javax.enterprise.inject.Any()";
        }
    } });
    final ManagedInstance<EdgeBuilderControl<AbstractCanvasHandler>> _edgeBuilderControls_7 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { EdgeBuilderControl.class }, new Annotation[] { new Any() {
        public Class annotationType() {
          return Any.class;
        }
        public String toString() {
          return "@javax.enterprise.inject.Any()";
        }
    } });
    final ManagedInstance<ElementBuilderControl<AbstractCanvasHandler>> _elementBuilderControls_5 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { ElementBuilderControl.class }, new Annotation[] { new Any() {
        public Class annotationType() {
          return Any.class;
        }
        public String toString() {
          return "@javax.enterprise.inject.Any()";
        }
      }, new Observer() {
        public Class annotationType() {
          return Observer.class;
        }
        public String toString() {
          return "@org.kie.workbench.common.stunner.core.client.canvas.controls.builder.impl.Observer()";
        }
    } });
    final DefaultDiagramEditor instance = new DefaultDiagramEditor(_definitionUtils_0, _viewer_1, _commandManagers_2, _locationControls_3, _resizeControls_4, _elementBuilderControls_5, _nodeBuilderControls_6, _edgeBuilderControls_7, _controlPointControls_8, _connectionAcceptorControls_9, _containmentAcceptorControls_10, _dockingAcceptorControls_11);
    registerDependentScopedReference(instance, _containmentAcceptorControls_10);
    registerDependentScopedReference(instance, _dockingAcceptorControls_11);
    registerDependentScopedReference(instance, _viewer_1);
    registerDependentScopedReference(instance, _commandManagers_2);
    registerDependentScopedReference(instance, _resizeControls_4);
    registerDependentScopedReference(instance, _controlPointControls_8);
    registerDependentScopedReference(instance, _locationControls_3);
    registerDependentScopedReference(instance, _connectionAcceptorControls_9);
    registerDependentScopedReference(instance, _nodeBuilderControls_6);
    registerDependentScopedReference(instance, _edgeBuilderControls_7);
    registerDependentScopedReference(instance, _elementBuilderControls_5);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}