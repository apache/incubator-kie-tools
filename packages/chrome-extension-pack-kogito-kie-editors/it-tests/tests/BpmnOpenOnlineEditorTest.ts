import BpmnEditor from "../framework/editor/bpmn/BpmnEditor";
import Explorer from "../framework/editor/Explorer";
import GitHubEditorPage from "../framework/github-editor/GitHubEditorPage";
import OnlineEditorPage from "../framework/online-editor/OnlineEditorPage";
import SideBar from "../framework/editor/SideBar";
import Tools from "../utils/Tools";

const TEST_NAME = "BpmnOpenOnlineEditorTest";

let tools: Tools;

beforeEach(async () => {
    tools = await Tools.init(TEST_NAME);
});

test(TEST_NAME, async () => {
    const bpmnPage: GitHubEditorPage = await tools.openPage(GitHubEditorPage, "https://github.com/kiegroup/" +
        "kogito-examples/blob/stable/process-business-rules-quarkus/src/main/resources/org/acme/travels/persons.bpmn");
    const onlineEditorPage: OnlineEditorPage = await bpmnPage.openOnlineEditor();
    expect(await onlineEditorPage.getFileName()).toEqual("persons.bpmn");
    const onlineEditor: BpmnEditor = await onlineEditorPage.getBpmnEditor();
    await onlineEditor.enter();
    const onlineEditorSideBar: SideBar = await onlineEditor.getSideBar();
    const onlineEditorExplorer: Explorer = await onlineEditorSideBar.openExplorer();
    expect((await onlineEditorExplorer.getNodeNames()).sort())
        .toEqual([
            "StartProcess",
            "End Event 1",
            "End Event 2",
            "Evaluate Person",
            "Exclusive Gateway 1",
            "Special handling for children"
        ].sort());
});

afterEach(async () => {
    await tools.finishTest();
});
