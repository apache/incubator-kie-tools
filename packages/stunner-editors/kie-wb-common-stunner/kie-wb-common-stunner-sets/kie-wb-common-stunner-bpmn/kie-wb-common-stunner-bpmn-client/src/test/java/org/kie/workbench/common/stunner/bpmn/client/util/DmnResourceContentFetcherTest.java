package org.kie.workbench.common.stunner.bpmn.client.util;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.enterprise.event.Event;

import elemental2.promise.Promise;
import org.appformer.kogito.bridge.client.dmneditor.marshaller.DmnLanguageServiceServiceProducer;
import org.appformer.kogito.bridge.client.dmneditor.marshaller.model.DmnDocumentData;
import org.appformer.kogito.bridge.client.resource.ResourceContentService;
import org.appformer.kogito.bridge.client.resource.interop.ResourceContentOptions;
import org.appformer.kogito.bridge.client.resource.interop.ResourceListOptions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.forms.client.event.RefreshFormPropertiesEvent;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.client.promise.Promises;
import org.uberfire.promise.SyncPromises;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DmnResourceContentFetcherTest {
    private DmnResourceContentFetcher tested;
    private ResourceContentService resourceContentService;
    private Promises promises;
    @Mock
    private Event<RefreshFormPropertiesEvent> refreshFormPropertiesEvent;
    @Mock
    private SessionManager sessionManager;
    @Mock
    private DmnLanguageServiceServiceProducer dmnLanguageServiceServiceProducer;
    private static String dmnFile = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> <dmn:definitions xmlns:dmn=\"http://www.omg.org/spec/DMN/20180521/MODEL/\" xmlns=\"https://kie.apache.org/dmn/_57B8BED3-0077-4154-8435-30E57EA6F02E\" xmlns:feel=\"http://www.omg.org/spec/DMN/20180521/FEEL/\" xmlns:kie=\"http://www.drools.org/kie/dmn/1.2\" xmlns:dmndi=\"http://www.omg.org/spec/DMN/20180521/DMNDI/\" xmlns:di=\"http://www.omg.org/spec/DMN/20180521/DI/\" xmlns:dc=\"http://www.omg.org/spec/DMN/20180521/DC/\" id=\"_69FF465F-72D8-4541-9916-99174CC60EDC\" name=\"My Model Name\" typeLanguage=\"http://www.omg.org/spec/DMN/20180521/FEEL/\" namespace=\"https://kie.apache.org/dmn/_57B8BED3-0077-4154-8435-30E57EA6F02E\"> <dmn:extensionElements/> <dmn:decision id=\"_DBFC1810-89DF-4FD8-9D42-2C87C29354AC\" name=\"Decision-1\"> <dmn:extensionElements/> <dmn:variable id=\"_17C7D2CE-047B-46A8-A2DC-3C3256EDA5E7\" name=\"Decision-1\"/> </dmn:decision> <dmn:decision id=\"_49CDEC14-8D60-408A-9EAB-523E59E2FFAF\" name=\"Decision-2\"> <dmn:extensionElements/> <dmn:variable id=\"_A5ECF5B2-278B-487D-A9F4-0C3DF2C25042\" name=\"Decision-2\"/> <dmn:informationRequirement id=\"_E69C6AD0-0BBA-4126-8C0C-7C7381646EEA\"> <dmn:requiredDecision href=\"#_DBFC1810-89DF-4FD8-9D42-2C87C29354AC\"/> </dmn:informationRequirement> </dmn:decision> <dmn:decision id=\"_52F81D8D-5FA7-4300-A855-C8CE88D4B825\" name=\"Decision-3\"> <dmn:extensionElements/> <dmn:variable id=\"_BD33A344-7F8F-4B18-9535-7ED4041664CB\" name=\"Decision-3\"/> <dmn:informationRequirement id=\"_97E802F2-AFE9-4461-A575-6D8A3B05FD55\"> <dmn:requiredDecision href=\"#_49CDEC14-8D60-408A-9EAB-523E59E2FFAF\"/> </dmn:informationRequirement> </dmn:decision> <dmndi:DMNDI> <dmndi:DMNDiagram id=\"_B708E43A-EB44-4DAB-8098-0883E470865F\" name=\"DRG\"> <di:extension> <kie:ComponentsWidthsExtension/> </di:extension> <dmndi:DMNShape id=\"dmnshape-drg-_DBFC1810-89DF-4FD8-9D42-2C87C29354AC\" dmnElementRef=\"_DBFC1810-89DF-4FD8-9D42-2C87C29354AC\" isCollapsed=\"false\"> <dmndi:DMNStyle><dmndi:FillColor red=\"255\" green=\"255\" blue=\"255\"/><dmndi:StrokeColor red=\"0\" green=\"0\" blue=\"0\"/><dmndi:FontColor red=\"0\" green=\"0\" blue=\"0\"/></dmndi:DMNStyle><dc:Bounds x=\"130\" y=\"126\" width=\"100\" height=\"50\"/><dmndi:DMNLabel/></dmndi:DMNShape><dmndi:DMNShape id=\"dmnshape-drg-_49CDEC14-8D60-408A-9EAB-523E59E2FFAF\" dmnElementRef=\"_49CDEC14-8D60-408A-9EAB-523E59E2FFAF\" isCollapsed=\"false\"><dmndi:DMNStyle><dmndi:FillColor red=\"255\" green=\"255\" blue=\"255\"/><dmndi:StrokeColor red=\"0\" green=\"0\" blue=\"0\"/><dmndi:FontColor red=\"0\" green=\"0\" blue=\"0\"/></dmndi:DMNStyle><dc:Bounds x=\"130\" y=\"-4\" width=\"100\" height=\"50\"/><dmndi:DMNLabel/></dmndi:DMNShape><dmndi:DMNShape id=\"dmnshape-drg-_52F81D8D-5FA7-4300-A855-C8CE88D4B825\" dmnElementRef=\"_52F81D8D-5FA7-4300-A855-C8CE88D4B825\" isCollapsed=\"false\"><dmndi:DMNStyle><dmndi:FillColor red=\"255\" green=\"255\" blue=\"255\"/><dmndi:StrokeColor red=\"0\" green=\"0\" blue=\"0\"/><dmndi:FontColor red=\"0\" green=\"0\" blue=\"0\"/></dmndi:DMNStyle><dc:Bounds x=\"130\" y=\"-134\" width=\"100\" height=\"50\"/><dmndi:DMNLabel/></dmndi:DMNShape><dmndi:DMNEdge id=\"dmnedge-drg-_E69C6AD0-0BBA-4126-8C0C-7C7381646EEA-AUTO-SOURCE-AUTO-TARGET\" dmnElementRef=\"_E69C6AD0-0BBA-4126-8C0C-7C7381646EEA\"><di:waypoint x=\"180\" y=\"126\"/><di:waypoint x=\"180\" y=\"46\"/></dmndi:DMNEdge><dmndi:DMNEdge id=\"dmnedge-drg-_97E802F2-AFE9-4461-A575-6D8A3B05FD55-AUTO-SOURCE-AUTO-TARGET\" dmnElementRef=\"_97E802F2-AFE9-4461-A575-6D8A3B05FD55\"><di:waypoint x=\"180\" y=\"-4\"/><di:waypoint x=\"180\" y=\"-84\"/></dmndi:DMNEdge></dmndi:DMNDiagram></dmndi:DMNDI></dmn:definitions>";

    @Before
    public void setUp() {

        promises = new SyncPromises();
        resourceContentService = new ResourceContentService() {
            @Override
            public Promise<String> get(String uri) {
                String returnContent = uri.equals("File1.dmn") ? dmnFile : "";
                return promises.resolve(returnContent);
            }

            @Override
            public Promise<String> get(String uri, ResourceContentOptions options) {
                return get(uri);
            }

            @Override
            public Promise<String[]> list(String pattern) {
                return promises.resolve(new String[] { "File1.dmn", "File2.dmn", "File3.dmn", "File4.dmn", "File5.dmn" });
            }

            @Override
            public Promise<String[]> list(String pattern, ResourceListOptions options) {
                return list(pattern);
            }
        };

        tested = new DmnResourceContentFetcher(resourceContentService, promises, refreshFormPropertiesEvent, sessionManager, dmnLanguageServiceServiceProducer);
    }

    @Test
    public void testFetchFilenames() {
        tested.fetchFileNames();
        assert(tested.getFileNames().size() == 5);
        assert(tested.getFileNames().containsKey("File1.dmn"));
        assert(tested.getFileNames().containsKey("File2.dmn"));
        assert(tested.getFileNames().containsKey("File3.dmn"));
        assert(tested.getFileNames().containsKey("File4.dmn"));
        assert(tested.getFileNames().containsKey("File5.dmn"));
        verify(refreshFormPropertiesEvent, times(1)).fire(any(RefreshFormPropertiesEvent.class));
    }
    @Test
    public void testFetchFile() {
        when(dmnLanguageServiceServiceProducer.produce()).thenReturn(xmlContent -> new DmnDocumentData());
        AtomicBoolean consumerCalled = new AtomicBoolean(false);
        tested.fetchFile("File1.dmn", dmnDocumentData -> {
            consumerCalled.set(true);
        });
        assertTrue(consumerCalled.get());
        verify(refreshFormPropertiesEvent, times(1)).fire(any(RefreshFormPropertiesEvent.class));
    }
}