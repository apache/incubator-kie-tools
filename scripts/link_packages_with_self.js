const { getPackagesSync } = require("@lerna/project");
const fs = require("fs");
const path = require("path");

getPackagesSync().forEach((pkg) => {
  const isScopedPackage = pkg.name.includes("@");

  if (isScopedPackage) {
    const [pkgScope, pkgSimpleName] = pkg.name.split("/");
    fs.mkdirSync(path.join(pkg.location, "node_modules", pkgScope), { recursive: true });
    const selfLinkPath = path.join(pkg.location, "node_modules", pkgScope, pkgSimpleName);
    selfLink(pkg, selfLinkPath);
    return;
  }

  const selfLinkPath = path.join(pkg.location, "node_modules", pkg.name);
  selfLink(pkg, selfLinkPath);
});

function selfLink(pkg, selfLinkPath) {
  const relTargetPath = path.relative(path.dirname(selfLinkPath), pkg.location);
  fs.unlinkSync(selfLinkPath);
  fs.symlinkSync(relTargetPath, selfLinkPath);
  console.info(`Self-linking '${pkg.name}'. ${path.relative(pkg.location, selfLinkPath)} -> ${relTargetPath}`);
}
