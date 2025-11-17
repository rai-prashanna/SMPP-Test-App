import type { OptionalParameter } from "./OptionalParameter";

export interface SubmitSmResult {
    messageId:          string;
    optionalParameters: OptionalParameter[];
}