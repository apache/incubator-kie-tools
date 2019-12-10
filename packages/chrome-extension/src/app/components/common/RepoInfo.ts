import { GitHubPageType } from "../../github/GitHubPageType";
import { PrInfo } from "../pr/IsolatedPrEditor";
import * as dependencies__ from "../../dependencies";

export class RepoInfo {
    public owner: string;
    public repo: string;
    public gitref: string;
}