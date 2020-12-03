package org.jboss.errai.ioc.client;

import java.util.Collection;
import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.stunner.core.definition.morph.MorphDefinition;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.impl.AddChildNodeCommand;
import org.kie.workbench.common.stunner.core.graph.command.impl.AddConnectorCommand;
import org.kie.workbench.common.stunner.core.graph.command.impl.AddControlPointCommand;
import org.kie.workbench.common.stunner.core.graph.command.impl.AddDockedNodeCommand;
import org.kie.workbench.common.stunner.core.graph.command.impl.AddNodeCommand;
import org.kie.workbench.common.stunner.core.graph.command.impl.ClearGraphCommand;
import org.kie.workbench.common.stunner.core.graph.command.impl.DeleteConnectorCommand;
import org.kie.workbench.common.stunner.core.graph.command.impl.DeleteElementsCommand;
import org.kie.workbench.common.stunner.core.graph.command.impl.DeleteNodeCommand;
import org.kie.workbench.common.stunner.core.graph.command.impl.DockNodeCommand;
import org.kie.workbench.common.stunner.core.graph.command.impl.GraphCommandFactory;
import org.kie.workbench.common.stunner.core.graph.command.impl.MorphNodeCommand;
import org.kie.workbench.common.stunner.core.graph.command.impl.RemoveChildrenCommand;
import org.kie.workbench.common.stunner.core.graph.command.impl.SafeDeleteNodeCommand;
import org.kie.workbench.common.stunner.core.graph.command.impl.SetChildrenCommand;
import org.kie.workbench.common.stunner.core.graph.command.impl.SetConnectionSourceNodeCommand;
import org.kie.workbench.common.stunner.core.graph.command.impl.SetConnectionTargetNodeCommand;
import org.kie.workbench.common.stunner.core.graph.command.impl.UnDockNodeCommand;
import org.kie.workbench.common.stunner.core.graph.command.impl.UpdateElementPositionCommand;
import org.kie.workbench.common.stunner.core.graph.command.impl.UpdateElementPropertyValueCommand;
import org.kie.workbench.common.stunner.core.graph.content.view.Connection;
import org.kie.workbench.common.stunner.core.graph.content.view.ControlPoint;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;

public class Type_factory__o_k_w_c_s_c_g_c_i_GraphCommandFactory__quals__j_e_i_Any_j_e_i_Default extends Factory<GraphCommandFactory> { private class Type_factory__o_k_w_c_s_c_g_c_i_GraphCommandFactory__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends GraphCommandFactory implements Proxy<GraphCommandFactory> {
    private final ProxyHelper<GraphCommandFactory> proxyHelper = new ProxyHelperImpl<GraphCommandFactory>("Type_factory__o_k_w_c_s_c_g_c_i_GraphCommandFactory__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final GraphCommandFactory instance) {

    }

    public GraphCommandFactory asBeanType() {
      return this;
    }

    public void setInstance(final GraphCommandFactory instance) {
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

    @Override public AddNodeCommand addNode(Node candidate) {
      if (proxyHelper != null) {
        final GraphCommandFactory proxiedInstance = proxyHelper.getInstance(this);
        final AddNodeCommand retVal = proxiedInstance.addNode(candidate);
        return retVal;
      } else {
        return super.addNode(candidate);
      }
    }

    @Override public AddChildNodeCommand addChildNode(Node parent, Node candidate) {
      if (proxyHelper != null) {
        final GraphCommandFactory proxiedInstance = proxyHelper.getInstance(this);
        final AddChildNodeCommand retVal = proxiedInstance.addChildNode(parent, candidate);
        return retVal;
      } else {
        return super.addChildNode(parent, candidate);
      }
    }

    @Override public AddChildNodeCommand addChildNode(Node parent, Node candidate, Point2D location) {
      if (proxyHelper != null) {
        final GraphCommandFactory proxiedInstance = proxyHelper.getInstance(this);
        final AddChildNodeCommand retVal = proxiedInstance.addChildNode(parent, candidate, location);
        return retVal;
      } else {
        return super.addChildNode(parent, candidate, location);
      }
    }

    @Override public AddDockedNodeCommand addDockedNode(Node parent, Node candidate) {
      if (proxyHelper != null) {
        final GraphCommandFactory proxiedInstance = proxyHelper.getInstance(this);
        final AddDockedNodeCommand retVal = proxiedInstance.addDockedNode(parent, candidate);
        return retVal;
      } else {
        return super.addDockedNode(parent, candidate);
      }
    }

    @Override public AddConnectorCommand addConnector(Node target, Edge edge, Connection connection) {
      if (proxyHelper != null) {
        final GraphCommandFactory proxiedInstance = proxyHelper.getInstance(this);
        final AddConnectorCommand retVal = proxiedInstance.addConnector(target, edge, connection);
        return retVal;
      } else {
        return super.addConnector(target, edge, connection);
      }
    }

    @Override public SetChildrenCommand setChild(Node parent, Node candidate) {
      if (proxyHelper != null) {
        final GraphCommandFactory proxiedInstance = proxyHelper.getInstance(this);
        final SetChildrenCommand retVal = proxiedInstance.setChild(parent, candidate);
        return retVal;
      } else {
        return super.setChild(parent, candidate);
      }
    }

    @Override public SetChildrenCommand setChildren(Node parent, Collection candidates) {
      if (proxyHelper != null) {
        final GraphCommandFactory proxiedInstance = proxyHelper.getInstance(this);
        final SetChildrenCommand retVal = proxiedInstance.setChildren(parent, candidates);
        return retVal;
      } else {
        return super.setChildren(parent, candidates);
      }
    }

    @Override public DockNodeCommand dockNode(Node parent, Node candidate) {
      if (proxyHelper != null) {
        final GraphCommandFactory proxiedInstance = proxyHelper.getInstance(this);
        final DockNodeCommand retVal = proxiedInstance.dockNode(parent, candidate);
        return retVal;
      } else {
        return super.dockNode(parent, candidate);
      }
    }

    @Override public MorphNodeCommand morphNode(Node candidate, MorphDefinition morphDefinition, String morphTarget) {
      if (proxyHelper != null) {
        final GraphCommandFactory proxiedInstance = proxyHelper.getInstance(this);
        final MorphNodeCommand retVal = proxiedInstance.morphNode(candidate, morphDefinition, morphTarget);
        return retVal;
      } else {
        return super.morphNode(candidate, morphDefinition, morphTarget);
      }
    }

    @Override public SetConnectionSourceNodeCommand setSourceNode(Node sourceNode, Edge edge, Connection connection) {
      if (proxyHelper != null) {
        final GraphCommandFactory proxiedInstance = proxyHelper.getInstance(this);
        final SetConnectionSourceNodeCommand retVal = proxiedInstance.setSourceNode(sourceNode, edge, connection);
        return retVal;
      } else {
        return super.setSourceNode(sourceNode, edge, connection);
      }
    }

    @Override public SetConnectionTargetNodeCommand setTargetNode(Node targetNode, Edge edge, Connection connection) {
      if (proxyHelper != null) {
        final GraphCommandFactory proxiedInstance = proxyHelper.getInstance(this);
        final SetConnectionTargetNodeCommand retVal = proxiedInstance.setTargetNode(targetNode, edge, connection);
        return retVal;
      } else {
        return super.setTargetNode(targetNode, edge, connection);
      }
    }

    @Override public UpdateElementPositionCommand updatePosition(Node element, Point2D location) {
      if (proxyHelper != null) {
        final GraphCommandFactory proxiedInstance = proxyHelper.getInstance(this);
        final UpdateElementPositionCommand retVal = proxiedInstance.updatePosition(element, location);
        return retVal;
      } else {
        return super.updatePosition(element, location);
      }
    }

    @Override public UpdateElementPropertyValueCommand updatePropertyValue(Element element, String field, Object value) {
      if (proxyHelper != null) {
        final GraphCommandFactory proxiedInstance = proxyHelper.getInstance(this);
        final UpdateElementPropertyValueCommand retVal = proxiedInstance.updatePropertyValue(element, field, value);
        return retVal;
      } else {
        return super.updatePropertyValue(element, field, value);
      }
    }

    @Override public SafeDeleteNodeCommand safeDeleteNode(Node candidate) {
      if (proxyHelper != null) {
        final GraphCommandFactory proxiedInstance = proxyHelper.getInstance(this);
        final SafeDeleteNodeCommand retVal = proxiedInstance.safeDeleteNode(candidate);
        return retVal;
      } else {
        return super.safeDeleteNode(candidate);
      }
    }

    @Override public DeleteNodeCommand deleteNode(Node candidate) {
      if (proxyHelper != null) {
        final GraphCommandFactory proxiedInstance = proxyHelper.getInstance(this);
        final DeleteNodeCommand retVal = proxiedInstance.deleteNode(candidate);
        return retVal;
      } else {
        return super.deleteNode(candidate);
      }
    }

    @Override public DeleteElementsCommand delete(Collection elements) {
      if (proxyHelper != null) {
        final GraphCommandFactory proxiedInstance = proxyHelper.getInstance(this);
        final DeleteElementsCommand retVal = proxiedInstance.delete(elements);
        return retVal;
      } else {
        return super.delete(elements);
      }
    }

    @Override public RemoveChildrenCommand removeChild(Node parent, Node candidate) {
      if (proxyHelper != null) {
        final GraphCommandFactory proxiedInstance = proxyHelper.getInstance(this);
        final RemoveChildrenCommand retVal = proxiedInstance.removeChild(parent, candidate);
        return retVal;
      } else {
        return super.removeChild(parent, candidate);
      }
    }

    @Override public RemoveChildrenCommand removeChildren(Node parent, Collection candidates) {
      if (proxyHelper != null) {
        final GraphCommandFactory proxiedInstance = proxyHelper.getInstance(this);
        final RemoveChildrenCommand retVal = proxiedInstance.removeChildren(parent, candidates);
        return retVal;
      } else {
        return super.removeChildren(parent, candidates);
      }
    }

    @Override public UnDockNodeCommand unDockNode(Node parent, Node candidate) {
      if (proxyHelper != null) {
        final GraphCommandFactory proxiedInstance = proxyHelper.getInstance(this);
        final UnDockNodeCommand retVal = proxiedInstance.unDockNode(parent, candidate);
        return retVal;
      } else {
        return super.unDockNode(parent, candidate);
      }
    }

    @Override public DeleteConnectorCommand deleteConnector(Edge edge) {
      if (proxyHelper != null) {
        final GraphCommandFactory proxiedInstance = proxyHelper.getInstance(this);
        final DeleteConnectorCommand retVal = proxiedInstance.deleteConnector(edge);
        return retVal;
      } else {
        return super.deleteConnector(edge);
      }
    }

    @Override public ClearGraphCommand clearGraph() {
      if (proxyHelper != null) {
        final GraphCommandFactory proxiedInstance = proxyHelper.getInstance(this);
        final ClearGraphCommand retVal = proxiedInstance.clearGraph();
        return retVal;
      } else {
        return super.clearGraph();
      }
    }

    @Override public ClearGraphCommand clearGraph(String rootUUID) {
      if (proxyHelper != null) {
        final GraphCommandFactory proxiedInstance = proxyHelper.getInstance(this);
        final ClearGraphCommand retVal = proxiedInstance.clearGraph(rootUUID);
        return retVal;
      } else {
        return super.clearGraph(rootUUID);
      }
    }

    @Override public AddControlPointCommand addControlPoint(Edge edge, ControlPoint controlPoint, int index) {
      if (proxyHelper != null) {
        final GraphCommandFactory proxiedInstance = proxyHelper.getInstance(this);
        final AddControlPointCommand retVal = proxiedInstance.addControlPoint(edge, controlPoint, index);
        return retVal;
      } else {
        return super.addControlPoint(edge, controlPoint, index);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final GraphCommandFactory proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_s_c_g_c_i_GraphCommandFactory__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(GraphCommandFactory.class, "Type_factory__o_k_w_c_s_c_g_c_i_GraphCommandFactory__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { GraphCommandFactory.class, Object.class });
  }

  public GraphCommandFactory createInstance(final ContextManager contextManager) {
    final GraphCommandFactory instance = new GraphCommandFactory();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<GraphCommandFactory> proxyImpl = new Type_factory__o_k_w_c_s_c_g_c_i_GraphCommandFactory__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}