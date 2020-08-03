import { Rect } from "../api";

export interface GuidedTourEnvelopeApi {
  receive_guidedTourElementPositionRequest(selector: string): Promise<Rect>;
}
