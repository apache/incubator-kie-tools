import { ApplicationSettings, Application, BasicAppSettings } from "spectron";
import * as path from 'path';
import * as assert from 'assert';
import * as chai from 'chai';
import * as chaiAsPromised from 'chai-as-promised';
import * as chaiRoughly from 'chai-roughly';
import { Suite } from "mocha";


before(() => {
  chai.should()
  chai.use(chaiAsPromised)
  chai.use(chaiRoughly)
})

/**
 * Retrieve path to electron binary to run the tests.
 * Looks for 'desktop' package node_modules directory where the binary will
 * always be located when the project is built.
 * 
 * Handles windows platform.
 */
export const getElectronPath = (): string => {
  let electronPath: string = path.join(__dirname, '../../../', 'node_modules', '.bin', 'electron')
  if (process.platform === 'win32') {
    electronPath += '.cmd'
  }
  return electronPath;
}

/**
 * Sets timout for test suite. 
 * 
 * @param test Suite of which to set timout
 */
export const setupTimeout = (test: Suite): void => {
  test.timeout(30000);
}

/**
 * Starts the application with given options.
 * 
 * @param options Options to start the application with - {@see BasicAppSettings}
 * @returns resolved promise of application that is running
 */
export const startApplication = async (options: BasicAppSettings): Promise<Application> => {
  options.path = exports.getElectronPath();

  const app = new Application(options);
  await app.start();
  assert.strictEqual(app.isRunning(), true);
  return app;
}

/**
 * Stops the electron application.
 * 
 * @param app application to stop
 */
export const stopApplication = async (app: Application): Promise<void> => {
  if (!app || !app.isRunning()) {
    return;
  }

  await app.stop();
  assert.strictEqual(app.isRunning(), false);
}