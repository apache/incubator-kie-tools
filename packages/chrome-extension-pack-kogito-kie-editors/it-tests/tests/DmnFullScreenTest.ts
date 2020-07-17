import DecisionNavigator from "../framework/editor/dmn/DecisionNavigator";
import DmnEditor from "../framework/editor/dmn/DmnEditor";
import DmnSideBar from "../framework/editor/dmn/DmnSideBar";
import FullscreenPage from "../framework/fullscreen-editor/FullscreenPage";
import GitHubEditorPage from "../framework/github-editor/GitHubEditorPage";
import Tools from "../utils/Tools";

const TEST_NAME = "DmnFullScreenTest";

let tools: Tools;

beforeEach(async () => {
    tools = await Tools.init(TEST_NAME);
});

test(TEST_NAME, async () => {
    const dmnUrl: string = "https://github.com/kiegroup/" + 
        "kogito-examples/blob/stable/dmn-quarkus-example/src/main/resources/Traffic%20Violation.dmn";
    let dmnPage: GitHubEditorPage = await tools.openPage(GitHubEditorPage, dmnUrl);
    // open and check full screen editor
    const fullScreenPage: FullscreenPage = await dmnPage.fullScreen();
    const fullScreenEditor: DmnEditor = await fullScreenPage.getDmnEditor();
    await fullScreenEditor.enter();
    const fullScreenSideBar: DmnSideBar = await fullScreenEditor.getSideBar();
    const fullScreenExplorer: DecisionNavigator = await fullScreenSideBar.openDecisionNavigator();
    expect((await fullScreenExplorer.getNodeNames()).sort())
        .toEqual([
            "Driver",
            "Fine",
            "Decision Table",
            "Should the driver be suspended?",
            "Context",
            "Violation"
        ].sort());
    await fullScreenEditor.leave();

    expect(await fullScreenPage.getExitFullscreenUrl()).toBe(dmnUrl + "#");

    await fullScreenPage.scrollToTop();
    dmnPage = await fullScreenPage.exitFullscreen();
    expect(await dmnPage.isEditorVisible()).toBe(true);
    expect(await dmnPage.isSourceVisible()).toBe(false);
});

afterEach(async () => {
    await tools.finishTest();
});
