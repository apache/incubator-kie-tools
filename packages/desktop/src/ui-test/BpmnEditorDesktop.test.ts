import * as path from 'path';
import { Application } from  'spectron';
import { HomePageSelector } from './utils/HomePageSelector';
import { BpmnEditorPageSelector } from'./utils/BpmnEditorPageSelector';
import DesktopTestHelper from './helpers/DesktopTestHelper';
import BpmnEditorTestHelper from './helpers/BpmnEditorTestHelper';
import HomePageTestHelper from './helpers/HomePageTestHelper';

describe("BPMN Editor Tests", () => {
    jest.setTimeout(50000);
    const appTester = new DesktopTestHelper();
    const homePage = new HomePageSelector();
    const editorPage = new BpmnEditorPageSelector();
    
    const GO_TO_HOMEPAGE_BUTTON_LOCATOR = '//button[@aria-label=\'Go to homepage\']';

    let app: Application;
    let editorTester: BpmnEditorTestHelper;
    let homeTester: HomePageTestHelper;

    beforeEach(async () => {
        app = await appTester.startApplication();
        expect(app).toBeDefined();
        editorTester = new BpmnEditorTestHelper(app);
        homeTester = new HomePageTestHelper(app);
    });

    afterEach(async() => {
      if (app && app.isRunning()){
        await appTester.stopApplication();
      } 
    });

    it('opens BPMN editor - new file', async () => {
      await homeTester.openNewBpmn();

      await appTester.switchToFrame(editorPage.diagramIframeId());
      await editorTester.waitUntilEditorLoaded();

      await editorTester.openDiagramProperties();
      await editorTester.openDiagramExplorer();

      await appTester.switchToParentFrame();

      await app.client.click(GO_TO_HOMEPAGE_BUTTON_LOCATOR);
      await appTester.checkBasicProperties();
    });

    it('opens BPMN editor - sample file', async () => {
        await homeTester.openSampleBpmn();
  
        await appTester.switchToFrame(editorPage.diagramIframeId());
        await editorTester.waitUntilEditorLoaded();
  
        await editorTester.openDiagramProperties();
        await editorTester.openDiagramExplorer();
  
        await appTester.switchToParentFrame();
  
        await app.client.click(GO_TO_HOMEPAGE_BUTTON_LOCATOR);
        await appTester.checkBasicProperties();
      });
});