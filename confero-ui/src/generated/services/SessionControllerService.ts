/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { SessionDto } from '../models/SessionDto';
import type { CancelablePromise } from '../core/CancelablePromise';
import type { BaseHttpRequest } from '../core/BaseHttpRequest';
export class SessionControllerService {
    constructor(public readonly httpRequest: BaseHttpRequest) {}
    /**
     * @returns SessionDto OK
     * @throws ApiError
     */
    public getAllSessions(): CancelablePromise<Array<SessionDto>> {
        return this.httpRequest.request({
            method: 'GET',
            url: '/sessions',
        });
    }
}
