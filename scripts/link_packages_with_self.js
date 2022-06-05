const { findWorkspacePackagesNoCheck } = require("@pnpm/find-workspace-packages");
const fs = require("fs");
const path = require("path");

findWorkspacePackagesNoCheck(path.join(__dirname, "../")).then((pkgs) =>
  pkgs.forEach((pkg) => {
    const isScopedPackage = pkg.manifest.name.includes("@");

    if (isScopedPackage) {
      const [pkgScope, pkgSimpleName] = pkg.manifest.name.split("/");
      fs.mkdirSync(path.join(pkg.dir, "node_modules", pkgScope), { recursive: true });
      const selfLinkPath = path.join(pkg.dir, "node_modules", pkgScope, pkgSimpleName);
      selfLink(pkg, selfLinkPath);
      return;
    }

    const selfLinkPath = path.join(pkg.dir, "node_modules", pkg.manifest.name);
    selfLink(pkg, selfLinkPath);
  })
);

function selfLink(pkg, selfLinkPath) {
  const relTargetPath = path.relative(path.dirname(selfLinkPath), pkg.dir);
  if (fs.existsSync(selfLinkPath)) {
    fs.unlinkSync(selfLinkPath);
  }
  fs.symlinkSync(relTargetPath, selfLinkPath);
  console.info(`Self-linking '${pkg.manifest.name}'. ${path.relative(pkg.dir, selfLinkPath)} -> ${relTargetPath}`);
}
