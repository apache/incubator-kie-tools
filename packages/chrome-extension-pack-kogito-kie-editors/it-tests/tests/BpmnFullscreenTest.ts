import BpmnEditor from "../framework/editor/bpmn/BpmnEditor";
import Explorer from "../framework/editor/Explorer";
import FullscreenPage from "../framework/fullscreen-editor/FullscreenPage";
import GitHubEditorPage from "../framework/github-editor/GitHubEditorPage";
import SideBar from "../framework/editor/SideBar";
import Tools from "../utils/Tools";

const TEST_NAME = "BpmnFullScreenTest";

let tools: Tools;

beforeEach(async () => {
    tools = await Tools.init(TEST_NAME);
});

test(TEST_NAME, async () => {
    const processUrl: string = "https://github.com/kiegroup/" +
        "kogito-examples/blob/stable/process-business-rules-quarkus/src/main/resources/org/acme/travels/persons.bpmn";
    let bpmnPage: GitHubEditorPage = await tools.openPage(GitHubEditorPage, processUrl);
    const fullScreenPage: FullscreenPage = await bpmnPage.fullScreen();
    const fullScreenEditor: BpmnEditor = await fullScreenPage.getBpmnEditor();
    await fullScreenEditor.enter();
    const fullScreenSideBar: SideBar = await fullScreenEditor.getSideBar();
    const fullScreenExplorer: Explorer = await fullScreenSideBar.openExplorer();
    expect((await fullScreenExplorer.getNodeNames()).sort())
        .toEqual([
            "StartProcess",
            "End Event 1",
            "End Event 2",
            "Evaluate Person",
            "Exclusive Gateway 1",
            "Special handling for children"
        ].sort());
    await fullScreenEditor.leave();

    expect(await fullScreenPage.getExitFullscreenUrl()).toBe(processUrl + "#");

    await fullScreenPage.scrollToTop();
    bpmnPage = await fullScreenPage.exitFullscreen();
    expect(await bpmnPage.isEditorVisible()).toBe(true);
    expect(await bpmnPage.isSourceVisible()).toBe(false);
});

afterEach(async () => {
    await tools.finishTest();
});
