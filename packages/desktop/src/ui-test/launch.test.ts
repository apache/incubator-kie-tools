const helpers = require('./global-setup');
const path = require( 'path');

import { HomePage } from './utils/HomePage';
import { EditorPage } from'./utils/EditorPage';

const describe = global.describe
const it = global.it
const beforeEach = global.beforeEach
const afterEach = global.afterEach

const UNSAVED_FILE = 'unsaved file';
const GO_TO_HOMEPAGE_BUTTON_LOCATOR = '//button[@aria-label=\'Go to homepage\']';
const BMP_TITLE = "Business Modeler Preview";

const waitForLoadingSpinner = async (appClient) => {
  await appClient.waitUntil(
    async () => await appClient.browserWindow.isVisible(new EditorPage().diagramLoadingScreenLocator()) === false)
}

describe('Application Startup', function () {
  helpers.setupTimeout(this)

  let app;

  beforeEach(() => {
      return helpers.startApplication({
        args: [path.join(__dirname, '../../..')],
        port: 3000,
        startTimeout: 25000,
        waitTimeout: 25000
      }).then((startedApp) => { 
        app = startedApp
      })
  });

  afterEach(() => {
    return helpers.stopApplication(app)
  });

  it('opens application window',  () => {
    return app.client.waitUntilWindowLoaded()
      .browserWindow.focus()
      .getWindowCount().should.eventually.equal(1)
      .browserWindow.isMinimized().should.eventually.be.false
      .browserWindow.isDevToolsOpened().should.eventually.be.false
      .browserWindow.isVisible().should.eventually.be.true
      .browserWindow.isFocused().should.eventually.be.true
      .browserWindow.getBounds().should.eventually.have.property('width').and.be.above(0)
      .browserWindow.getBounds().should.eventually.have.property('height').and.be.above(0)
      .getTitle().should.eventually.equal(BMP_TITLE)
  })

  describe('Editors can be opened', () => {

    const homePage = new HomePage();
    const editorPage = new EditorPage();

    it('opens BPMN editor - new file', async () => {
      await app.client.waitUntilWindowLoaded().click(homePage.openNewBpmnDiagramButtonSelector());
      await app.client.waitUntilWindowLoaded().getWindowCount().should.eventually.equal(1);
      waitForLoadingSpinner(app.client);
      await app.client.browserWindow.isVisible(editorPage.diagramIframeLocator()).should.eventually.be.true
      await app.client.getText(editorPage.diagramNameHeaderLocator()).should.eventually.be.equal(UNSAVED_FILE)
      await app.client.browserWindow.focus().waitUntilWindowLoaded();
      await app.client.browserWindow.focus().click(GO_TO_HOMEPAGE_BUTTON_LOCATOR)

      return app.client.waitUntilWindowLoaded()
      .browserWindow.focus()
      .getWindowCount().should.eventually.equal(1)
      .browserWindow.isMinimized().should.eventually.be.false
      .browserWindow.isDevToolsOpened().should.eventually.be.false
      .browserWindow.isVisible().should.eventually.be.true
      .browserWindow.isFocused().should.eventually.be.true
      .browserWindow.getBounds().should.eventually.have.property('width').and.be.above(0)
      .browserWindow.getBounds().should.eventually.have.property('height').and.be.above(0)
      .getTitle().should.eventually.equal(BMP_TITLE)
    })

    it('opens DMN editor - new file', async () => {
      await app.client.waitUntilWindowLoaded();
      await app.client.click(homePage.openNewDmnDiagramButtonSelector())
      await app.client.waitUntilWindowLoaded().getWindowCount().should.eventually.equal(1)
      waitForLoadingSpinner(app.client)
           
      await app.client.getText(editorPage.diagramNameHeaderLocator()).should.eventually.be.equal(UNSAVED_FILE)
      await app.client.isVisible(editorPage.diagramIframeLocator()).should.eventually.be.true
      await app.client.browserWindow.focus().waitUntilWindowLoaded();
      await app.client.browserWindow.focus().click(GO_TO_HOMEPAGE_BUTTON_LOCATOR).waitUntilWindowLoaded()

      await app.client.waitUntilWindowLoaded()
      .browserWindow.focus()
      .getWindowCount().should.eventually.equal(1)
      .browserWindow.isMinimized().should.eventually.be.false
      .browserWindow.isDevToolsOpened().should.eventually.be.false
      .browserWindow.isVisible().should.eventually.be.true
      .browserWindow.isFocused().should.eventually.be.true
      .browserWindow.getBounds().should.eventually.have.property('width').and.be.above(0)
      .browserWindow.getBounds().should.eventually.have.property('height').and.be.above(0)
      .getTitle().should.eventually.equal(BMP_TITLE)
    })

    it('opens BPMN editor - sample file', async () => {
      await app.client.waitUntilWindowLoaded();
      await app.client.click(homePage.openSampleBpmnDiagramButtonSelector())
      await app.client.waitUntilWindowLoaded().getWindowCount().should.eventually.equal(1)
      waitForLoadingSpinner(app.client)
      
      await app.client.getText(editorPage.diagramNameHeaderLocator()).should.eventually.be.equal(UNSAVED_FILE)
      await app.client.isVisible(editorPage.diagramIframeLocator()).should.eventually.be.true
      await app.client.browserWindow.focus().waitUntilWindowLoaded();
      await app.client.browserWindow.focus().click(GO_TO_HOMEPAGE_BUTTON_LOCATOR)

      await app.client.waitUntilWindowLoaded()
      .browserWindow.focus()
      .getWindowCount().should.eventually.equal(1)
      .browserWindow.isMinimized().should.eventually.be.false
      .browserWindow.isDevToolsOpened().should.eventually.be.false
      .browserWindow.isVisible().should.eventually.be.true
      .browserWindow.isFocused().should.eventually.be.true
      .browserWindow.getBounds().should.eventually.have.property('width').and.be.above(0)
      .browserWindow.getBounds().should.eventually.have.property('height').and.be.above(0)
      .getTitle().should.eventually.equal(BMP_TITLE)
    })

    it('opens DMN editor - sample file', async () => {
      await app.client.waitUntilWindowLoaded();
      await app.client.click(homePage.openSampleDmnDiagramButtonSelector())
      await app.client.waitUntilWindowLoaded().getWindowCount().should.eventually.equal(1)
      waitForLoadingSpinner(app.client)
      
      await app.client.getText(editorPage.diagramNameHeaderLocator()).should.eventually.be.equal(UNSAVED_FILE)
      await app.client.isVisible(editorPage.diagramIframeLocator()).should.eventually.be.true
      await app.client.browserWindow.focus().waitUntilWindowLoaded();
      await app.client.browserWindow.focus().click(GO_TO_HOMEPAGE_BUTTON_LOCATOR).waitUntilWindowLoaded()

      await app.client.waitUntilWindowLoaded()
      .browserWindow.focus()
      .getWindowCount().should.eventually.equal(1)
      .browserWindow.isMinimized().should.eventually.be.false
      .browserWindow.isDevToolsOpened().should.eventually.be.false
      .browserWindow.isVisible().should.eventually.be.true
      .browserWindow.isFocused().should.eventually.be.true
      .browserWindow.getBounds().should.eventually.have.property('width').and.be.above(0)
      .browserWindow.getBounds().should.eventually.have.property('height').and.be.above(0)
      .getTitle().should.eventually.equal(BMP_TITLE)
    })
  })
})