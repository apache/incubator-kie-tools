package com.ait.lienzo.client.core.shape;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import com.ait.lienzo.client.core.event.NodeDragEndEvent;
import com.ait.lienzo.client.core.event.NodeDragMoveEvent;
import com.ait.lienzo.client.core.event.NodeDragStartEvent;
import com.ait.lienzo.client.core.shape.AbstractMultiPathPartShape.ResizeControlHandle;
import com.ait.lienzo.client.core.shape.AbstractMultiPathPartShape.ResizeHandleDragHandler;
import com.ait.lienzo.client.core.shape.wires.IControlHandleList;
import com.ait.lienzo.client.core.shape.wires.IControlHandle.ControlHandleStandardType;
import com.ait.lienzo.client.core.shape.wires.IControlHandle.ControlHandleType;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.PathPartEntryJSO;
import com.ait.lienzo.client.core.types.PathPartList;
import com.ait.lienzo.client.core.util.Geometry;
import com.ait.lienzo.client.widget.DragContext;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.ait.tooling.nativetools.client.collection.NFastArrayList;
import com.ait.tooling.nativetools.client.collection.NFastDoubleArrayJSO;

@RunWith(LienzoMockitoTestRunner.class)
public class ResizeHandleDragHandlerTest
{
	@Mock
	private Shape<?> shape;
	
	@Mock
	private BoundingBox boundingBox;
	
	@Mock    
	private NodeDragMoveEvent dragMoveEvent;
	
	@Mock
	private DragContext dragContext;
	
	@Mock
    private Shape<?> primitive;
	
	private MultiPath multiPath; 
	
	private NFastArrayList<PathPartList> listOfPaths;
	
	private ResizeHandleDragHandler tested;
	
	@Before
	public void setup()
	{
		multiPath = new MultiPath();
		multiPath.M(100, 200);
		multiPath.A(100, 100, 250, 100, 50);        
		multiPath.L(250, 100);
		multiPath.A(400, 100, 450, 200, 50);
		multiPath.L(450, 200);        
		multiPath.A(500, 300, 250, 300, 50);
		multiPath.L(250, 300);
		multiPath.A(100, 300, 100, 200, 50);
		multiPath.Z();
        
        Map<ControlHandleType, IControlHandleList> hmap = multiPath.getControlHandles(ControlHandleStandardType.RESIZE);

        listOfPaths = multiPath.getPathPartListArray();
        
        IControlHandleList chlist = hmap.get(ControlHandleStandardType.RESIZE); 
        
        ResizeControlHandle controlHandle = (ResizeControlHandle)chlist.getHandle(2);
        
        when(primitive.getLayer()).thenReturn(mock(Layer.class));        
        
        when(shape.getBoundingBox()).thenReturn(boundingBox);
        when(shape.getX()).thenReturn(0d);
        when(shape.getY()).thenReturn(0d);
        when(shape.getLayer()).thenReturn(mock(Layer.class));
        
        when(boundingBox.getX()).thenReturn(100d);
        when(boundingBox.getY()).thenReturn(100d);
        when(boundingBox.getWidth()).thenReturn(369.0983005625052);
        when(boundingBox.getHeight()).thenReturn(200d);
        
        when(dragMoveEvent.getDragContext()).thenReturn(dragContext);        
        when(dragContext.getDx()).thenReturn(300);
        when(dragContext.getDy()).thenReturn(20);
        
        tested = new ResizeHandleDragHandler(shape, listOfPaths, chlist, primitive, controlHandle);
	}
	
	@Test
	public void testOnNodeDragMove()
	{
		tested.onNodeDragStart(mock(NodeDragStartEvent.class));
		tested.onNodeDragMove(dragMoveEvent);
		tested.onNodeDragEnd(mock(NodeDragEndEvent.class));
		
		for (PathPartList list : listOfPaths)
        {
			PathPartEntryJSO entry;
	        NFastDoubleArrayJSO points;
	        
	        entry = list.get(1);
            points = entry.getPoints();
            
            assertTrue(Geometry.closeEnough(points.get(0), 100.0));
            assertTrue(Geometry.closeEnough(points.get(1), 100.0));
            assertTrue(Geometry.closeEnough(points.get(2), 371.9187407024635));
            assertTrue(Geometry.closeEnough(points.get(3), 100.0));
            assertTrue(Geometry.closeEnough(points.get(4), 90.63958023415448));
            assertTrue(Geometry.closeEnough(points.get(5), 90.63958023415448));
           
            entry = list.get(3);
            points = entry.getPoints();
            
            assertTrue(Geometry.closeEnough(points.get(0), 643.837481404927));
            assertTrue(Geometry.closeEnough(points.get(1), 100.0));
            assertTrue(Geometry.closeEnough(points.get(2), 734.4770616390815));
            assertTrue(Geometry.closeEnough(points.get(3), 210.0));
            assertTrue(Geometry.closeEnough(points.get(4), 83.5074968666364));
            assertTrue(Geometry.closeEnough(points.get(5), 83.5074968666364));
           
            entry = list.get(5);
            points = entry.getPoints();
            
            assertTrue(Geometry.closeEnough(points.get(0), 825.116641873236));
            assertTrue(Geometry.closeEnough(points.get(1), 320.0));
            assertTrue(Geometry.closeEnough(points.get(2), 371.9187407024635));
            assertTrue(Geometry.closeEnough(points.get(3), 320.0));
            assertTrue(Geometry.closeEnough(points.get(4), 67.2403744760607));
            assertTrue(Geometry.closeEnough(points.get(5), 69.18652744395138));
            
            entry = list.get(7);
            points = entry.getPoints();
            
            assertTrue(Geometry.closeEnough(points.get(0), 100.0));
            assertTrue(Geometry.closeEnough(points.get(1), 320.0));
            assertTrue(Geometry.closeEnough(points.get(2), 100.0));
            assertTrue(Geometry.closeEnough(points.get(3), 210.0));
            assertTrue(Geometry.closeEnough(points.get(4), 54.99999999999999));
            assertTrue(Geometry.closeEnough(points.get(5), 54.99999999999999));
        }
	}
}