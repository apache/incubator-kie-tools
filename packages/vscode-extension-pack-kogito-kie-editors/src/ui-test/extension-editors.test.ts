/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { WebView, By, SideBarView } from 'vscode-extension-tester';
import * as path from 'path';
import { aComponentWithText } from './helpers/CommonLocators';
import { EditorTab } from './helpers/EditorTab';
import { assertWebElementIsDisplayedEnabled } from './helpers/CommonAsserts'
import VSCodeTestHelper from './helpers/VSCodeTestHelper';
import BpmnEditorTestHelper from './helpers/BpmnEditorTestHelper';
import ScesimEditorTestHelper from './helpers/ScesimEditorTestHelper';
import DmnEditorTestHelper from './helpers/DmnEditorTestHelper';

describe("Editors are loading properly", () => {

    const RESOURCES: string = path.resolve('src', 'ui-test', 'resources');
	const DEMO_BPMN: string = 'demo.bpmn';
    const DEMO_DMN: string = 'demo.dmn';
    const DEMO_SCESIM: string = 'demo.scesim';

    const REUSABLE_DMN: string = 'reusable-model.dmn';

    let testHelper: VSCodeTestHelper;
    let webview : WebView;
    let folderView : SideBarView;

    before(async function() {
        this.timeout(60000);
        testHelper = new VSCodeTestHelper();
        await testHelper.closeAllEditors();
        folderView = await testHelper.openFolder(RESOURCES);
    });

    afterEach(async function () {
        this.timeout(15000);
        await testHelper.closeAllEditors();
    })

    it('Opens demo.bpmn file in BPMN Editor and loads correct diagram', async function () {
        this.timeout(20000);
        webview = await testHelper.openFileFromSidebar(DEMO_BPMN);     
        await webview.switchToFrame();
        const bpmnEditorTester = new BpmnEditorTestHelper(webview);

        const envelopApp = await webview.findWebElement(By.id('envelope-app'));
        await assertWebElementIsDisplayedEnabled(envelopApp);
        
        const palette = await bpmnEditorTester.getPalette();
        await assertWebElementIsDisplayedEnabled(palette);

        await bpmnEditorTester.openDiagramProperties();

        const explorer = await bpmnEditorTester.openDiagramExplorer();
        await assertWebElementIsDisplayedEnabled(await explorer.findElement(By.xpath(aComponentWithText('demo'))));
        await assertWebElementIsDisplayedEnabled(await explorer.findElement(By.xpath(aComponentWithText('Start'))));
        await assertWebElementIsDisplayedEnabled(await explorer.findElement(By.xpath(aComponentWithText('End'))));
        
        await webview.switchBack();
    });

    it('Opens demo.dmn file in DMN Editor', async function () {
        this.timeout(20000);
        webview = await testHelper.openFileFromSidebar(DEMO_DMN); 
        await webview.switchToFrame();
        const dmnEditorTester = new DmnEditorTestHelper(webview);

        const envelopApp = await webview.findWebElement(By.id('envelope-app'));
        await assertWebElementIsDisplayedEnabled(envelopApp);

        await dmnEditorTester.openDiagramProperties();
        await dmnEditorTester.openDiagramExplorer();
        await dmnEditorTester.openDecisionNavigator();
     
        await webview.switchBack();
    });

    // kiegroup/appformer#1090
    // kiegroup/kie-wb-common#3537
    it.skip('Include reusable-model in DMN Editor', async function () {
        this.timeout(20000);
        webview = await testHelper.openFileFromSidebar(DEMO_DMN); 
        await webview.switchToFrame();
        const dmnEditorTester = new DmnEditorTestHelper(webview);

        const envelopApp = await webview.findWebElement(By.id('envelope-app'));
        await assertWebElementIsDisplayedEnabled(envelopApp);

        await dmnEditorTester.switchEditorTab(EditorTab.IncludedModels);
        await dmnEditorTester.includeModelWithNodes(REUSABLE_DMN, 2);
     
        await webview.switchBack();
    });

    it('Opens demo.scesim file in SCESIM Editor', async function () {
        this.timeout(20000);

        webview = await testHelper.openFileFromSidebar(DEMO_SCESIM); 
        await webview.switchToFrame();
        const scesimEditorTester = new ScesimEditorTestHelper(webview);

        const envelopApp = await webview.findWebElement(By.id('envelope-app'));
        await assertWebElementIsDisplayedEnabled(envelopApp);
       
        await scesimEditorTester.openScenarioCheatsheet();
        await scesimEditorTester.openSettings();
        await scesimEditorTester.openTestTools();
        
        await webview.switchBack();
    });
})