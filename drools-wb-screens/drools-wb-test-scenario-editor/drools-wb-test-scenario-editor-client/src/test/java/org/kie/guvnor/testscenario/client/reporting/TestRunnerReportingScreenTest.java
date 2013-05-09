package org.kie.guvnor.testscenario.client.reporting;

import org.junit.Before;
import org.junit.Test;
import org.kie.guvnor.testscenario.client.service.TestRuntimeReportingService;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

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
