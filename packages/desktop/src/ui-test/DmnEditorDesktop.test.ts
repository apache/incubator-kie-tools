import * as path from 'path';
import { Application } from  'spectron';
import { HomePageSelector } from './utils/HomePageSelector';
import DesktopTestHelper from './helpers/DesktopTestHelper';
import { DmnEditorPageSelector } from './utils/DmnEditorPageSelector';
import DmnEditorTestHelper from './helpers/DmnEditorTestHelper';
import HomePageTestHelper from './helpers/HomePageTestHelper';

describe("DMN Editor Tests", () => {
    jest.setTimeout(50000); // increase to 50000 on low spec laptop
    const appTester = new DesktopTestHelper();
    const homePage = new HomePageSelector();
    const editorPage = new DmnEditorPageSelector();
    
    const GO_TO_HOMEPAGE_BUTTON_LOCATOR = '//button[@aria-label=\'Go to homepage\']';

    let app: Application;
    let editorTester: DmnEditorTestHelper;
    let homeTester: HomePageTestHelper;

    beforeEach(async () => {
        app = await appTester.startApplication();
        expect(app).toBeDefined();
        editorTester = new DmnEditorTestHelper(app);
        homeTester = new HomePageTestHelper(app);
    });

    afterEach(async() => {
      if (app && app.isRunning()){
        await appTester.stopApplication();
      } 
    });

    it('opens DMN editor - new file', async () => {
      await homeTester.openNewDmn();

      await appTester.switchToFrame(editorPage.diagramIframeId());
      await editorTester.waitUntilEditorLoaded();

      await editorTester.openDiagramProperties();
      await editorTester.openDiagramExplorer();

      await appTester.switchToParentFrame();

      await app.client.click(GO_TO_HOMEPAGE_BUTTON_LOCATOR);
      await appTester.checkBasicProperties();
    });

    it('opens DMN editor - sample file', async () => {
        await homeTester.openSampleDmn();
  
        await appTester.switchToFrame(editorPage.diagramIframeId());
        await editorTester.waitUntilEditorLoaded();
  
        await editorTester.openDiagramProperties();
        await editorTester.openDiagramExplorer();
  
        await appTester.switchToParentFrame();
  
        await app.client.click(GO_TO_HOMEPAGE_BUTTON_LOCATOR);
        await appTester.checkBasicProperties();
      });
});