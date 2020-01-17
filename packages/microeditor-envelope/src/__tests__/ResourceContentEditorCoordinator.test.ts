
import { ResourceContentEditorCoordinator } from "../ResourceContentEditorCoordinator";
import { EnvelopeBusInnerMessageHandler } from "../EnvelopeBusInnerMessageHandler";
import { LanguageData, ResourceContent, ResourcesList, EditorContent } from "@kogito-tooling/core-api";
import { ResourceContentEditorService } from "../ResourceContentEditorService";

let coordinator: ResourceContentEditorCoordinator;
let resourceContentEditorService: ResourceContentEditorService;

const handler = new EnvelopeBusInnerMessageHandler(
  {
    postMessage: (message, targetOrigin) => {
      // do nothing
    }
  },
  self => ({
    receive_contentResponse: (content: EditorContent) => {
      // do nothing
    },
    receive_languageResponse: (languageData: LanguageData) => {
      // do nothing
    },
    receive_contentRequest: () => {
      // do nothing
    },
    receive_resourceContentResponse: (content: ResourceContent) => {
      // do nothing
    },
    receive_resourceContentList: (resourcesList: ResourcesList) => {
      // do nothing
    }
  })
);

beforeEach(() => {
  coordinator = new ResourceContentEditorCoordinator();
  handler.targetOrigin = "test";
  handler.startListening();
  resourceContentEditorService = coordinator.exposed(handler);
});

afterEach(() => {
  handler.stopListening();
});

describe("ResourceContentEditorCoordinator", () => {
  test("resource content", done => {
    const resourceURI = "/foo/bar";
    const resourceContent = "resource value";

    const mockCallback1 = jest.fn(v => { console.log(v) });
    const mockCallback2 = jest.fn(v => { console.log(v) });
    resourceContentEditorService.get(resourceURI).then(mockCallback1);
    resourceContentEditorService.get(resourceURI).then(mockCallback2);

    expect(coordinator.resolvePending.length).toBe(1);

    coordinator.resolvePending(new ResourceContent(resourceURI, resourceContent));

    setTimeout(() => {
      expect(mockCallback1).toHaveBeenCalledTimes(1);
      expect(mockCallback2).toHaveBeenCalledTimes(1);

      expect(mockCallback1).toHaveBeenCalledWith(resourceContent);
      expect(mockCallback2).toHaveBeenCalledWith(resourceContent);

      done();

    }, 500);

  });
  test("resource list", done => {
    const pattern = "*";
    const resources = ["/foo", "/foo/bar"];

    const mockCallback1 = jest.fn();
    const mockCallback2 = jest.fn();
    resourceContentEditorService.list(pattern).then(mockCallback1);
    resourceContentEditorService.list(pattern).then(mockCallback2);

    expect(coordinator.resolvePending.length).toBe(1);

    coordinator.resolvePendingList(new ResourcesList(pattern, resources));

    setTimeout(() => {
      expect(mockCallback1).toHaveBeenCalledTimes(1);
      expect(mockCallback2).toHaveBeenCalledTimes(1);

      expect(mockCallback1).toHaveBeenCalledWith(resources);
      expect(mockCallback2).toHaveBeenCalledWith(resources);

      done();
    }, 500);


  });

});