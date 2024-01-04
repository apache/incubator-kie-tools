import { readFileSync } from "fs";
import * as __path from "path";

export const singleImport = () => {
  const fsFileAbsolutePath = __path.resolve(__dirname, "singleImport.dmn");
  return readFileSync(fsFileAbsolutePath, "utf8");
};

export const doubleImport = () => {
  const fsFileAbsolutePath = __path.resolve(__dirname, "doubleImport.dmn");
  return readFileSync(fsFileAbsolutePath, "utf8");
};

export const deepNested = () => {
  const fsFileAbsolutePath = __path.resolve(__dirname, "deep/nested.dmn".split("/").join(__path.sep));
  return readFileSync(fsFileAbsolutePath, "utf8");
};

export const deepRecursive = () => {
  const fsFileAbsolutePath = __path.resolve(__dirname, "deep/recursive.dmn".split("/").join(__path.sep));
  return readFileSync(fsFileAbsolutePath, "utf8");
};

export const example1 = () => {
  const fsFileAbsolutePath = __path.resolve(__dirname, "example1.dmn");
  return readFileSync(fsFileAbsolutePath, "utf8");
};

export const example2 = () => {
  const fsFileAbsolutePath = __path.resolve(__dirname, "example2.dmn");
  return readFileSync(fsFileAbsolutePath, "utf8");
};

export const example3 = () => {
  const fsFileAbsolutePath = __path.resolve(__dirname, "example3.dmn");
  return readFileSync(fsFileAbsolutePath, "utf8");
};

export const example4 = () => {
  const fsFileAbsolutePath = __path.resolve(__dirname, "example4.dmn");
  return readFileSync(fsFileAbsolutePath, "utf8");
};

export const example5 = () => {
  const fsFileAbsolutePath = __path.resolve(__dirname, "example5.dmn");
  return readFileSync(fsFileAbsolutePath, "utf8");
};

export const decisions = () => {
  const fsFileAbsolutePath = __path.resolve(__dirname, "decisions.dmn");
  return readFileSync(fsFileAbsolutePath, "utf8");
};

export const simple15 = () => {
  const fsFileAbsolutePath = __path.resolve(__dirname, "simple-1.5.dmn");
  return readFileSync(fsFileAbsolutePath, "utf8");
};

export const simple152 = () => {
  const fsFileAbsolutePath = __path.resolve(__dirname, "simple-1.5-2.dmn");
  return readFileSync(fsFileAbsolutePath, "utf8");
};
