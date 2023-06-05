package org.kie.workbench.common.stunner.core.client.api;

import java.util.Collection;
import java.util.stream.StreamSupport;

import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommand;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractSession;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.processing.index.Index;

@JsType
public class JsStunnerSession {

    @JsIgnore
    private AbstractSession session;

    public JsStunnerSession(AbstractSession session) {
        this.session = session;
    }

    public Diagram getDiagram() {
        return session.getCanvasHandler().getDiagram();
    }

    public Graph getGraph() {
        return getDiagram().getGraph();
    }

    public Node getNodeByUUID(String uuid) {
        return getGraphIndex().getNode(uuid);
    }

    public Node getNodeByName(String name) {
        Iterable<Node> nodes = getGraphIndex().getGraph().nodes();
        return StreamSupport.stream(nodes.spliterator(), false)
                .filter(node -> name.equals(getNodeName(node)))
                .findAny()
                .orElse(null);
    }

    public String getNodeName(Node node) {
        Object definition = ((Node<Definition, Edge>) node).getContent().getDefinition();
        return getDefinitionName(definition);
    }

    public String getDefinitionId(Object bean) {
        return JsWindow.editor.definitions.getId(bean).value();
    }

    public String getDefinitionName(Object bean) {
        return JsWindow.editor.definitions.getName(bean);
    }

    public Object getDefinitionByElementUUID(String uuid) {
        Element e = getGraphIndex().get(uuid);
        if (null == e) {
            return null;
        }
        View<?> content = (View<?>) e.getContent();
        Object definition = content.getDefinition();
        return definition;
    }

    public Edge getEdgeByUUID(String uuid) {
        return getGraphIndex().getEdge(uuid);
    }

    public String getSelectedElementUUID() {
        Collection<String> selectedItems = session.getSelectionControl().getSelectedItems();
        return selectedItems.isEmpty() ? null : selectedItems.iterator().next();
    }

    public Node getSelectedNode() {
        String selectedUUID = getSelectedElementUUID();
        return null == selectedUUID ? null : getNodeByUUID(selectedUUID);
    }

    public Edge getSelectedEdge() {
        String selectedUUID = getSelectedElementUUID();
        return null == selectedUUID ? null : getEdgeByUUID(selectedUUID);
    }

    public Object getSelectedDefinition() {
        Node node = getSelectedNode();
        if (null == node) {
            return null;
        }
        View<?> content = (View<?>) node.getContent();
        Object definition = content.getDefinition();
        return definition;
    }

    public void selectByUUID(String uuid) {
        session.getSelectionControl().clearSelection().addSelection(uuid);
    }

    public void selectByName(String name) {
        Node node = getNodeByName(name);
        if (null != node) {
            selectByUUID(node.getUUID());
        }
    }

    public void clearSelection() {
        session.getSelectionControl().clearSelection();
    }

    @SuppressWarnings("all")
    public CommandResult execute(CanvasCommand command) {
        CanvasCommandManager<AbstractCanvasHandler> commandManager = session.getCommandManager();
        CommandResult<CanvasViolation> result = commandManager.execute(getCanvasHandler(), command);
        return result;
    }

    private Index getGraphIndex() {
        return getCanvasHandler().getGraphIndex();
    }

    private AbstractCanvasHandler getCanvasHandler() {
        return Js.uncheckedCast(session.getCanvasHandler());
    }
}
