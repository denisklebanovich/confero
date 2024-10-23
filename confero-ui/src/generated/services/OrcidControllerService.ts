/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { OrcidInfo } from '../models/OrcidInfo';
import type { CancelablePromise } from '../core/CancelablePromise';
import type { BaseHttpRequest } from '../core/BaseHttpRequest';
export class OrcidControllerService {
    constructor(public readonly httpRequest: BaseHttpRequest) {}
    /**
     * @param id
     * @returns OrcidInfo OK
     * @throws ApiError
     */
    public getRecord(
        id: string,
    ): CancelablePromise<OrcidInfo> {
        return this.httpRequest.request({
            method: 'GET',
            url: '/orcid/{id}',
            path: {
                'id': id,
            },
        });
    }
}
