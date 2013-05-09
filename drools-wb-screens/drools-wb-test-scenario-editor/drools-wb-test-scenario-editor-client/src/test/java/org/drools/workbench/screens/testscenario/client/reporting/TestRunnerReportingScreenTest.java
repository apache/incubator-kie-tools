package org.drools.workbench.screens.testscenario.client.reporting;

import org.drools.workbench.screens.testscenario.client.service.TestRuntimeReportingService;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class TestRunnerReportingScreenTest {

    private TestRunnerReportingView view;
    private TestRunnerReportingScreen screen;

    @Before
    public void setUp() throws Exception {
        view = mock(TestRunnerReportingView.class);
        TestRuntimeReportingService testRuntimeReportingService = mock(TestRuntimeReportingService.class);
        screen = new TestRunnerReportingScreen(view,
                testRuntimeReportingService);
    }

    @Test
    public void testSetPresenter() throws Exception {
        verify(view).setPresenter(screen);
    }
}
