package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.function.Consumer;
import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.dmn.api.qualifiers.DMNEditor;
import org.kie.workbench.common.dmn.client.commands.factory.DefaultCanvasCommandFactory;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramsSession;
import org.kie.workbench.common.stunner.client.lienzo.canvas.command.LienzoCanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommand;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.shape.view.BoundingBox;
import org.kie.workbench.common.stunner.core.definition.morph.MorphDefinition;
import org.kie.workbench.common.stunner.core.diagram.GraphsProvider;
import org.kie.workbench.common.stunner.core.domainobject.DomainObject;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.Connection;
import org.kie.workbench.common.stunner.core.graph.content.view.ControlPoint;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.ChildrenTraverseProcessor;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.ViewTraverseProcessor;

public class Type_factory__o_k_w_c_d_c_c_f_DefaultCanvasCommandFactory__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor extends Factory<DefaultCanvasCommandFactory> { private class Type_factory__o_k_w_c_d_c_c_f_DefaultCanvasCommandFactory__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditorProxyImpl extends DefaultCanvasCommandFactory implements Proxy<DefaultCanvasCommandFactory> {
    private final ProxyHelper<DefaultCanvasCommandFactory> proxyHelper = new ProxyHelperImpl<DefaultCanvasCommandFactory>("Type_factory__o_k_w_c_d_c_c_f_DefaultCanvasCommandFactory__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor");
    public void initProxyProperties(final DefaultCanvasCommandFactory instance) {

    }

    public DefaultCanvasCommandFactory asBeanType() {
      return this;
    }

    public void setInstance(final DefaultCanvasCommandFactory instance) {
      proxyHelper.setInstance(instance);
    }

    public void clearInstance() {
      proxyHelper.clearInstance();
    }

    public void setProxyContext(final Context context) {
      proxyHelper.setProxyContext(context);
    }

    public Context getProxyContext() {
      return proxyHelper.getProxyContext();
    }

    public Object unwrap() {
      return proxyHelper.getInstance(this);
    }

    public boolean equals(Object obj) {
      obj = Factory.maybeUnwrapProxy(obj);
      return proxyHelper.getInstance(this).equals(obj);
    }

    @Override public CanvasCommand addChildNode(Node parent, Node candidate, String shapeSetId) {
      if (proxyHelper != null) {
        final DefaultCanvasCommandFactory proxiedInstance = proxyHelper.getInstance(this);
        final CanvasCommand retVal = proxiedInstance.addChildNode(parent, candidate, shapeSetId);
        return retVal;
      } else {
        return super.addChildNode(parent, candidate, shapeSetId);
      }
    }

    @Override public CanvasCommand delete(Collection candidates) {
      if (proxyHelper != null) {
        final DefaultCanvasCommandFactory proxiedInstance = proxyHelper.getInstance(this);
        final CanvasCommand retVal = proxiedInstance.delete(candidates);
        return retVal;
      } else {
        return super.delete(candidates);
      }
    }

    @Override public CanvasCommand deleteNode(Node candidate) {
      if (proxyHelper != null) {
        final DefaultCanvasCommandFactory proxiedInstance = proxyHelper.getInstance(this);
        final CanvasCommand retVal = proxiedInstance.deleteNode(candidate);
        return retVal;
      } else {
        return super.deleteNode(candidate);
      }
    }

    @Override public CanvasCommand resize(Element element, BoundingBox boundingBox) {
      if (proxyHelper != null) {
        final DefaultCanvasCommandFactory proxiedInstance = proxyHelper.getInstance(this);
        final CanvasCommand retVal = proxiedInstance.resize(element, boundingBox);
        return retVal;
      } else {
        return super.resize(element, boundingBox);
      }
    }

    @Override public CanvasCommand addNode(Node candidate, String shapeSetId) {
      if (proxyHelper != null) {
        final DefaultCanvasCommandFactory proxiedInstance = proxyHelper.getInstance(this);
        final CanvasCommand retVal = proxiedInstance.addNode(candidate, shapeSetId);
        return retVal;
      } else {
        return super.addNode(candidate, shapeSetId);
      }
    }

    @Override public CanvasCommand addDockedNode(Node parent, Node candidate, String shapeSetId) {
      if (proxyHelper != null) {
        final DefaultCanvasCommandFactory proxiedInstance = proxyHelper.getInstance(this);
        final CanvasCommand retVal = proxiedInstance.addDockedNode(parent, candidate, shapeSetId);
        return retVal;
      } else {
        return super.addDockedNode(parent, candidate, shapeSetId);
      }
    }

    @Override public CanvasCommand addConnector(Node sourceNode, Edge candidate, Connection connection, String shapeSetId) {
      if (proxyHelper != null) {
        final DefaultCanvasCommandFactory proxiedInstance = proxyHelper.getInstance(this);
        final CanvasCommand retVal = proxiedInstance.addConnector(sourceNode, candidate, connection, shapeSetId);
        return retVal;
      } else {
        return super.addConnector(sourceNode, candidate, connection, shapeSetId);
      }
    }

    @Override public CanvasCommand deleteConnector(Edge candidate) {
      if (proxyHelper != null) {
        final DefaultCanvasCommandFactory proxiedInstance = proxyHelper.getInstance(this);
        final CanvasCommand retVal = proxiedInstance.deleteConnector(candidate);
        return retVal;
      } else {
        return super.deleteConnector(candidate);
      }
    }

    @Override public CanvasCommand setChildNode(Node parent, Node candidate) {
      if (proxyHelper != null) {
        final DefaultCanvasCommandFactory proxiedInstance = proxyHelper.getInstance(this);
        final CanvasCommand retVal = proxiedInstance.setChildNode(parent, candidate);
        return retVal;
      } else {
        return super.setChildNode(parent, candidate);
      }
    }

    @Override public CanvasCommand removeChild(Node parent, Node candidate) {
      if (proxyHelper != null) {
        final DefaultCanvasCommandFactory proxiedInstance = proxyHelper.getInstance(this);
        final CanvasCommand retVal = proxiedInstance.removeChild(parent, candidate);
        return retVal;
      } else {
        return super.removeChild(parent, candidate);
      }
    }

    @Override public CanvasCommand updateChildNode(Node parent, Node candidate) {
      if (proxyHelper != null) {
        final DefaultCanvasCommandFactory proxiedInstance = proxyHelper.getInstance(this);
        final CanvasCommand retVal = proxiedInstance.updateChildNode(parent, candidate);
        return retVal;
      } else {
        return super.updateChildNode(parent, candidate);
      }
    }

    @Override public CanvasCommand updateChildren(Node parent, Collection candidates) {
      if (proxyHelper != null) {
        final DefaultCanvasCommandFactory proxiedInstance = proxyHelper.getInstance(this);
        final CanvasCommand retVal = proxiedInstance.updateChildren(parent, candidates);
        return retVal;
      } else {
        return super.updateChildren(parent, candidates);
      }
    }

    @Override public CanvasCommand dockNode(Node parent, Node candidate) {
      if (proxyHelper != null) {
        final DefaultCanvasCommandFactory proxiedInstance = proxyHelper.getInstance(this);
        final CanvasCommand retVal = proxiedInstance.dockNode(parent, candidate);
        return retVal;
      } else {
        return super.dockNode(parent, candidate);
      }
    }

    @Override public CanvasCommand unDockNode(Node parent, Node candidate) {
      if (proxyHelper != null) {
        final DefaultCanvasCommandFactory proxiedInstance = proxyHelper.getInstance(this);
        final CanvasCommand retVal = proxiedInstance.unDockNode(parent, candidate);
        return retVal;
      } else {
        return super.unDockNode(parent, candidate);
      }
    }

    @Override public CanvasCommand updateDockNode(Node parent, Node candidate) {
      if (proxyHelper != null) {
        final DefaultCanvasCommandFactory proxiedInstance = proxyHelper.getInstance(this);
        final CanvasCommand retVal = proxiedInstance.updateDockNode(parent, candidate);
        return retVal;
      } else {
        return super.updateDockNode(parent, candidate);
      }
    }

    @Override public CanvasCommand updateDockNode(Node parent, Node candidate, boolean adjustPosition) {
      if (proxyHelper != null) {
        final DefaultCanvasCommandFactory proxiedInstance = proxyHelper.getInstance(this);
        final CanvasCommand retVal = proxiedInstance.updateDockNode(parent, candidate, adjustPosition);
        return retVal;
      } else {
        return super.updateDockNode(parent, candidate, adjustPosition);
      }
    }

    @Override public CanvasCommand draw() {
      if (proxyHelper != null) {
        final DefaultCanvasCommandFactory proxiedInstance = proxyHelper.getInstance(this);
        final CanvasCommand retVal = proxiedInstance.draw();
        return retVal;
      } else {
        return super.draw();
      }
    }

    @Override public CanvasCommand morphNode(Node candidate, MorphDefinition morphDefinition, String morphTarget, String shapeSetId) {
      if (proxyHelper != null) {
        final DefaultCanvasCommandFactory proxiedInstance = proxyHelper.getInstance(this);
        final CanvasCommand retVal = proxiedInstance.morphNode(candidate, morphDefinition, morphTarget, shapeSetId);
        return retVal;
      } else {
        return super.morphNode(candidate, morphDefinition, morphTarget, shapeSetId);
      }
    }

    @Override public CanvasCommand setSourceNode(Node node, Edge edge, Connection connection) {
      if (proxyHelper != null) {
        final DefaultCanvasCommandFactory proxiedInstance = proxyHelper.getInstance(this);
        final CanvasCommand retVal = proxiedInstance.setSourceNode(node, edge, connection);
        return retVal;
      } else {
        return super.setSourceNode(node, edge, connection);
      }
    }

    @Override public CanvasCommand setTargetNode(Node node, Edge edge, Connection connection) {
      if (proxyHelper != null) {
        final DefaultCanvasCommandFactory proxiedInstance = proxyHelper.getInstance(this);
        final CanvasCommand retVal = proxiedInstance.setTargetNode(node, edge, connection);
        return retVal;
      } else {
        return super.setTargetNode(node, edge, connection);
      }
    }

    @Override public CanvasCommand updatePosition(Node element, Point2D location) {
      if (proxyHelper != null) {
        final DefaultCanvasCommandFactory proxiedInstance = proxyHelper.getInstance(this);
        final CanvasCommand retVal = proxiedInstance.updatePosition(element, location);
        return retVal;
      } else {
        return super.updatePosition(element, location);
      }
    }

    @Override public CanvasCommand updatePropertyValue(Element element, String field, Object value) {
      if (proxyHelper != null) {
        final DefaultCanvasCommandFactory proxiedInstance = proxyHelper.getInstance(this);
        final CanvasCommand retVal = proxiedInstance.updatePropertyValue(element, field, value);
        return retVal;
      } else {
        return super.updatePropertyValue(element, field, value);
      }
    }

    @Override public CanvasCommand updateDomainObjectPropertyValue(DomainObject domainObject, String propertyId, Object value) {
      if (proxyHelper != null) {
        final DefaultCanvasCommandFactory proxiedInstance = proxyHelper.getInstance(this);
        final CanvasCommand retVal = proxiedInstance.updateDomainObjectPropertyValue(domainObject, propertyId, value);
        return retVal;
      } else {
        return super.updateDomainObjectPropertyValue(domainObject, propertyId, value);
      }
    }

    @Override public CanvasCommand clearCanvas() {
      if (proxyHelper != null) {
        final DefaultCanvasCommandFactory proxiedInstance = proxyHelper.getInstance(this);
        final CanvasCommand retVal = proxiedInstance.clearCanvas();
        return retVal;
      } else {
        return super.clearCanvas();
      }
    }

    @Override public CanvasCommand cloneNode(Node candidate, String parentUuid, Point2D cloneLocation, Consumer cloneNodeCallback) {
      if (proxyHelper != null) {
        final DefaultCanvasCommandFactory proxiedInstance = proxyHelper.getInstance(this);
        final CanvasCommand retVal = proxiedInstance.cloneNode(candidate, parentUuid, cloneLocation, cloneNodeCallback);
        return retVal;
      } else {
        return super.cloneNode(candidate, parentUuid, cloneLocation, cloneNodeCallback);
      }
    }

    @Override public CanvasCommand cloneConnector(Edge candidate, String sourceUUID, String targetUUID, String shapeSetId, Consumer callback) {
      if (proxyHelper != null) {
        final DefaultCanvasCommandFactory proxiedInstance = proxyHelper.getInstance(this);
        final CanvasCommand retVal = proxiedInstance.cloneConnector(candidate, sourceUUID, targetUUID, shapeSetId, callback);
        return retVal;
      } else {
        return super.cloneConnector(candidate, sourceUUID, targetUUID, shapeSetId, callback);
      }
    }

    @Override public CanvasCommand addControlPoint(Edge candidate, ControlPoint controlPoint, int index) {
      if (proxyHelper != null) {
        final DefaultCanvasCommandFactory proxiedInstance = proxyHelper.getInstance(this);
        final CanvasCommand retVal = proxiedInstance.addControlPoint(candidate, controlPoint, index);
        return retVal;
      } else {
        return super.addControlPoint(candidate, controlPoint, index);
      }
    }

    @Override public CanvasCommand deleteControlPoint(Edge candidate, int index) {
      if (proxyHelper != null) {
        final DefaultCanvasCommandFactory proxiedInstance = proxyHelper.getInstance(this);
        final CanvasCommand retVal = proxiedInstance.deleteControlPoint(candidate, index);
        return retVal;
      } else {
        return super.deleteControlPoint(candidate, index);
      }
    }

    @Override public CanvasCommand updateControlPointPosition(Edge candidate, ControlPoint[] controlPoints) {
      if (proxyHelper != null) {
        final DefaultCanvasCommandFactory proxiedInstance = proxyHelper.getInstance(this);
        final CanvasCommand retVal = proxiedInstance.updateControlPointPosition(candidate, controlPoints);
        return retVal;
      } else {
        return super.updateControlPointPosition(candidate, controlPoints);
      }
    }

    @Override protected ChildrenTraverseProcessor newChildrenTraverseProcessor() {
      if (proxyHelper != null) {
        final DefaultCanvasCommandFactory proxiedInstance = proxyHelper.getInstance(this);
        final ChildrenTraverseProcessor retVal = DefaultCanvasCommandFactory_newChildrenTraverseProcessor(proxiedInstance);
        return retVal;
      } else {
        return super.newChildrenTraverseProcessor();
      }
    }

    @Override protected ViewTraverseProcessor newViewTraverseProcessor() {
      if (proxyHelper != null) {
        final DefaultCanvasCommandFactory proxiedInstance = proxyHelper.getInstance(this);
        final ViewTraverseProcessor retVal = DefaultCanvasCommandFactory_newViewTraverseProcessor(proxiedInstance);
        return retVal;
      } else {
        return super.newViewTraverseProcessor();
      }
    }

    @Override protected ManagedInstance getChildrenTraverseProcessors() {
      if (proxyHelper != null) {
        final DefaultCanvasCommandFactory proxiedInstance = proxyHelper.getInstance(this);
        final ManagedInstance retVal = DefaultCanvasCommandFactory_getChildrenTraverseProcessors(proxiedInstance);
        return retVal;
      } else {
        return super.getChildrenTraverseProcessors();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final DefaultCanvasCommandFactory proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_d_c_c_f_DefaultCanvasCommandFactory__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor() {
    super(new FactoryHandleImpl(DefaultCanvasCommandFactory.class, "Type_factory__o_k_w_c_d_c_c_f_DefaultCanvasCommandFactory__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DefaultCanvasCommandFactory.class, LienzoCanvasCommandFactory.class, org.kie.workbench.common.stunner.core.client.canvas.command.DefaultCanvasCommandFactory.class, Object.class, CanvasCommandFactory.class });
    handle.setQualifiers(new Annotation[] { QualifierUtil.ANY_ANNOTATION, new DMNEditor() {
        public Class annotationType() {
          return DMNEditor.class;
        }
        public String toString() {
          return "@org.kie.workbench.common.dmn.api.qualifiers.DMNEditor()";
        }
    } });
  }

  public DefaultCanvasCommandFactory createInstance(final ContextManager contextManager) {
    final ManagedInstance<ChildrenTraverseProcessor> _childrenTraverseProcessors_0 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { ChildrenTraverseProcessor.class }, new Annotation[] { });
    final GraphsProvider _graphsProvider_2 = (DMNDiagramsSession) contextManager.getInstance("Type_factory__o_k_w_c_d_c_d_n_d_DMNDiagramsSession__quals__j_e_i_Any_j_e_i_Default");
    final ManagedInstance<ViewTraverseProcessor> _viewTraverseProcessors_1 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { ViewTraverseProcessor.class }, new Annotation[] { });
    final DefaultCanvasCommandFactory instance = new DefaultCanvasCommandFactory(_childrenTraverseProcessors_0, _viewTraverseProcessors_1, _graphsProvider_2);
    registerDependentScopedReference(instance, _childrenTraverseProcessors_0);
    registerDependentScopedReference(instance, _viewTraverseProcessors_1);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<DefaultCanvasCommandFactory> proxyImpl = new Type_factory__o_k_w_c_d_c_c_f_DefaultCanvasCommandFactory__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditorProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  public native static ChildrenTraverseProcessor DefaultCanvasCommandFactory_newChildrenTraverseProcessor(org.kie.workbench.common.stunner.core.client.canvas.command.DefaultCanvasCommandFactory instance) /*-{
    return instance.@org.kie.workbench.common.stunner.core.client.canvas.command.DefaultCanvasCommandFactory::newChildrenTraverseProcessor()();
  }-*/;

  public native static ViewTraverseProcessor DefaultCanvasCommandFactory_newViewTraverseProcessor(org.kie.workbench.common.stunner.core.client.canvas.command.DefaultCanvasCommandFactory instance) /*-{
    return instance.@org.kie.workbench.common.stunner.core.client.canvas.command.DefaultCanvasCommandFactory::newViewTraverseProcessor()();
  }-*/;

  public native static ManagedInstance DefaultCanvasCommandFactory_getChildrenTraverseProcessors(org.kie.workbench.common.stunner.core.client.canvas.command.DefaultCanvasCommandFactory instance) /*-{
    return instance.@org.kie.workbench.common.stunner.core.client.canvas.command.DefaultCanvasCommandFactory::getChildrenTraverseProcessors()();
  }-*/;
}