package org.kie.workbench.common.stunner.sw.autolayout;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import elemental2.core.JsArray;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.TestingGraphMockHandler;
import org.kie.workbench.common.stunner.core.command.impl.CompositeCommand;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.sw.autolayout.elkjs.ELKNode;
import org.kie.workbench.common.stunner.sw.definition.State;
import org.kie.workbench.common.stunner.sw.definition.Workflow;
import org.kie.workbench.common.stunner.sw.marshall.BaseMarshallingTest;
import org.kie.workbench.common.stunner.sw.marshall.BuilderContext;
import org.kie.workbench.common.stunner.sw.marshall.Context;
import org.kie.workbench.common.stunner.sw.marshall.Marshaller;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

@RunWith(LienzoMockitoTestRunner.class)
public class AutoLayoutBrokenTransitionTest extends BaseMarshallingTest {

    @Override
    protected Workflow createWorkflow() {
        return new Workflow()
                .setId("workflow1")
                .setName("Workflow1")
                .setStart("State1")
                .setStates(new State[]{
                        new State()
                                .setName("State1")
                                .setTransition("State 2"), // Broken Transition
                        new State()
                                .setName("State2")
                                .setTransition("State3"),
                        new State()
                                .setName("State3")
                                .setEnd(true)
                });
    }

    @Before
    @Override
    public void setUp() {
        graphHandler = new TestingGraphMockHandler();
        context = new Context(graphHandler.graphIndex);
        builderContext = new BuilderContext(context,
                                            graphHandler.getDefinitionManager(),
                                            graphHandler.getFactoryManager());
        workflow = createWorkflow();
        Marshaller.LOAD_DETAILS = true;

        unmarshallWorkflow();
    }

    @Test
    @SuppressWarnings("all")
    public void testUpdateNodesPosition() {
        final Graph<?, ?> graph = getGraph();
        final Object parentLayout = mock(Object.class);
        final Object nestedLayout = mock(Object.class);
        final CompositeCommand.Builder layoutCommands = new CompositeCommand.Builder();
        final ELKNode elkRootNode = AutoLayout.buildElkInputNode(graph,
                                                                 context.getWorkflowRootNode(),
                                                                 parentLayout,
                                                                 nestedLayout,
                                                                 false);

        simulateProcessedELKEResult(elkRootNode);

        AutoLayout.updateGraphNodeSizes(elkRootNode, graph);
        AutoLayout.updateNodesPosition(elkRootNode, graph, layoutCommands);

        final CompositeCommand<GraphCommandExecutionContext, RuleViolation> all =
                new CompositeCommand.Builder<>()
                        .addCommand(layoutCommands.build())
                        .build();

        all.execute(builderContext.buildExecutionContext());

        for (int i = 0; i < elkRootNode.getChildren().length; i++) {
            final ELKNode childElkNode = elkRootNode.getChildren().getAt(i);
            final Node<View, Edge> node = graph.getNode(childElkNode.getId());
            assertEquals(childElkNode.getX(), node.getContent().getBounds().getX(), 0);
            assertEquals(childElkNode.getY(), node.getContent().getBounds().getY(), 0);
        }
    }

    private static void simulateProcessedELKEResult(ELKNode elkRootNode) {
        // Node size and location
        for (int i = 0; i < elkRootNode.getChildren().length; i++) {
            ELKNode childElkNode = elkRootNode.getChildren().getAt(i);

            childElkNode.setWidth(childElkNode.getWidth() + 5d);
            childElkNode.setHeight(childElkNode.getHeight() + 5d);
            childElkNode.setX(300);
            childElkNode.setY(i * 100);
        }

        // BendPoints
        for (int i = 1; i <= elkRootNode.getEdges().length; i++) {
            final Point2D p0 = new Point2D(310, i * 50);
            final Point2D p1 = new Point2D(310, i * 55);
            final Point2D p2 = new Point2D(310, i * 50);
            final JsArray<Point2D> point2DJsArray = new JsArray<>();
            point2DJsArray.push(p0, p1, p2);
            elkRootNode.getEdges().getAt(i - 1).setBendPoints(point2DJsArray);
        }
    }

}
