import DmnEditor from "../framework/editor/dmn/DmnEditor";
import DmnSideBar from "../framework/editor/dmn/DmnSideBar";
import GitHubEditorPage from "../framework/github-editor/GitHubEditorPage";
import OnlineEditorPage from "../framework/online-editor/OnlineEditorPage";
import Properties from "../framework/editor/Properties";
import Tools from "../utils/Tools";

const TEST_NAME = "DmnOpenOnlineEditorTest";

let tools: Tools;

beforeEach(async () => {
    tools = await Tools.init(TEST_NAME);
});

test(TEST_NAME, async () => {
    const dmnPage: GitHubEditorPage = await tools.openPage(GitHubEditorPage, "https://github.com/kiegroup/" +
        "kogito-examples/blob/stable/dmn-quarkus-example/src/main/resources/Traffic%20Violation.dmn");
    const onlineEditorPage: OnlineEditorPage = await dmnPage.openOnlineEditor();
    expect(await onlineEditorPage.getFileName()).toEqual("Traffic Violation.dmn");
    const onlineEditor: DmnEditor = await onlineEditorPage.getDmnEditor();
    await onlineEditor.enter();
    const onlineEditorSideBar: DmnSideBar = await onlineEditor.getSideBar();
    const onlineProperties: Properties = await onlineEditorSideBar.openProperties();
    expect((await onlineProperties.getDmnNameFromInput())).toEqual("Traffic Violation");
});

afterEach(async () => {
    await tools.finishTest();
});
