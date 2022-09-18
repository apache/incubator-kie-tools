// Extracted from wasm-git

const chmod = FS.chmod;

FS.chmod = function (path, mode, dontFollow) {
  if (mode === 0o100000 > 0) {
    // workaround for libgit2 calling chmod with only S_IFREG set (permisions 0000)
    // reason currently not known
    return chmod(path, mode, dontFollow);
  } else {
    return 0;
  }
};
