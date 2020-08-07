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

export const getElectronPath = (): string => {
  let electronPath: string = path.join(__dirname, '../../', 'node_modules', '.bin', 'electron')
  if (process.platform === 'win32') {
    electronPath += '.cmd'
  }
  return electronPath;
}

export const setupTimeout = (test: Suite): void => {
  test.timeout(30000);
}

export const startApplication = async (options: any): Promise<Application> => {
  options.path = exports.getElectronPath()
  if (process.env.CI) {
    options.startTimeout = 30000
  }

  const app = new Application(options)
  await app.start();
  assert.strictEqual(app.isRunning(), true);
  return app;
}

export const stopApplication = async (app: Application): Promise<void> => {
  if (!app || !app.isRunning()) {
    return;
  }

  await app.stop();
  assert.strictEqual(app.isRunning(), false);
}