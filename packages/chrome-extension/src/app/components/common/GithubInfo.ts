import { GitHubPageType } from "../../github/GitHubPageType";
import { PrInfo } from "../pr/IsolatedPrEditor";
import * as dependencies__ from "../../dependencies";

class RepoInfo {
    public owner: string;
    public repo: string;
    public gitref: string;
}

function uriMatches(regex: string) {
    return !!window.location.pathname.match(new RegExp(regex));
}

function discoverCurrentGitHubPageType() {
    if (uriMatches(`.*/.*/edit/.*`)) {
        return GitHubPageType.EDIT;
    }

    if (uriMatches(`.*/.*/blob/.*`)) {
        return GitHubPageType.VIEW;
    }

    if (uriMatches(`.*/.*/pull/[0-9]+/files.*`)) {
        return GitHubPageType.PR;
    }

    if (uriMatches(`.*/.*/pull/[0-9]+/commits.*`)) {
        return GitHubPageType.PR;
    }

    return GitHubPageType.ANY;
}

export { RepoInfo, discoverCurrentGitHubPageType };