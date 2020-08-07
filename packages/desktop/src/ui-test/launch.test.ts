import * as path from 'path';
import * as helpers from './global-setup'
import {Application, SpectronClient} from  'spectron';
import { HomePage } from './utils/HomePage';
import { EditorPage } from'./utils/EditorPage';

const describe = global.describe
const it = global.it
const beforeEach = global.beforeEach
const afterEach = global.afterEach

const UNSAVED_FILE = 'unsaved file';
const GO_TO_HOMEPAGE_BUTTON_LOCATOR = '//button[@aria-label=\'Go to homepage\']';
const BMP_TITLE = "Business Modeler Preview";

const waitForLoadingSpinner = async (appClient: SpectronClient) => {
  await appClient.waitUntil(
    async () => await appClient.isVisible(new EditorPage().diagramLoadingScreenLocator()) === false)
}

const checkBasicDimensions = async (app: Application): Promise<void> => {
    await app.client.waitUntilWindowLoaded()
    app.browserWindow.focus();
    await app.client.getWindowCount().should.eventually.equal(1);
    await app.browserWindow.isMinimized().should.eventually.be.false;
    await app.browserWindow.isVisible().should.eventually.be.true;
    await app.browserWindow.isFocused().should.eventually.be.true;
    await app.browserWindow.getBounds().should.eventually.have.property('width').and.be.above(0)
    await app.browserWindow.getBounds().should.eventually.have.property('height').and.be.above(0)
    await app.client.getTitle().should.eventually.equal(BMP_TITLE)
}

describe('Application Startup', function () {
  helpers.setupTimeout(this)

  let app: Application

  beforeEach(async () => {
      const startedApp = await helpers.startApplication({
      args: [path.join(__dirname, '../../')],
      port: 3000,
      startTimeout: 25000,
      waitTimeout: 10000
    });
    app = startedApp;
  });

  afterEach(() => {
    return helpers.stopApplication(app)
  });

  it('Opens application main window',  async () => {
    await checkBasicDimensions(app);
  })

  describe('Editors can be opened', () => {

    const homePage = new HomePage();
    const editorPage = new EditorPage();

    it('opens BPMN editor - new file', async () => {
      await app.client.waitUntilWindowLoaded()
      await app.client.click(homePage.openNewBpmnDiagramButtonSelector());
      await app.client.waitUntilWindowLoaded();
      await app.client.getWindowCount().should.eventually.equal(1);
      await waitForLoadingSpinner(app.client);

      await app.client.getText(editorPage.diagramNameHeaderLocator()).should.eventually.be.equal(UNSAVED_FILE)
      await app.client.isVisible(editorPage.diagramIframeLocator()).should.eventually.be.true
      await app.client.waitUntilWindowLoaded();
      await app.client.click(GO_TO_HOMEPAGE_BUTTON_LOCATOR)

      await checkBasicDimensions(app);
    })

    it('opens DMN editor - new file', async () => {
      await app.client.waitUntilWindowLoaded();
      await app.client.click(homePage.openNewDmnDiagramButtonSelector())
      await app.client.waitUntilWindowLoaded();
      await app.client.getWindowCount().should.eventually.equal(1);
      await waitForLoadingSpinner(app.client);

      await app.client.getText(editorPage.diagramNameHeaderLocator()).should.eventually.be.equal(UNSAVED_FILE);
      await app.client.isVisible(editorPage.diagramIframeLocator()).should.eventually.be.true;
      await app.client.waitUntilWindowLoaded();
      await app.client.click(GO_TO_HOMEPAGE_BUTTON_LOCATOR);

      checkBasicDimensions(app);
    })

    it('opens BPMN editor - sample file', async () => {
      await app.client.waitUntilWindowLoaded();
      await app.client.click(homePage.openSampleBpmnDiagramButtonSelector())
      await app.client.waitUntilWindowLoaded();
      await app.client.getWindowCount().should.eventually.equal(1);
      await waitForLoadingSpinner(app.client);
      
      await app.client.getText(editorPage.diagramNameHeaderLocator()).should.eventually.be.equal(UNSAVED_FILE)
      await app.client.isVisible(editorPage.diagramIframeLocator()).should.eventually.be.true
      await app.client.waitUntilWindowLoaded();
      await app.client.click(GO_TO_HOMEPAGE_BUTTON_LOCATOR);

      await checkBasicDimensions(app);
    })

    it('opens DMN editor - sample file', async () => {
      await app.client.waitUntilWindowLoaded();
      await app.client.click(homePage.openSampleDmnDiagramButtonSelector())
      await app.client.waitUntilWindowLoaded();
      await app.client.getWindowCount().should.eventually.equal(1);
      await waitForLoadingSpinner(app.client);
      
      await app.client.getText(editorPage.diagramNameHeaderLocator()).should.eventually.be.equal(UNSAVED_FILE);
      await app.client.isVisible(editorPage.diagramIframeLocator()).should.eventually.be.true;
      await app.client.waitUntilWindowLoaded();
      await app.client.click(GO_TO_HOMEPAGE_BUTTON_LOCATOR);

      await checkBasicDimensions(app);
    })
  })
})