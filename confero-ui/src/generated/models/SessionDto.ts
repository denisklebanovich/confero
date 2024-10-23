/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { AttachmentDto } from './AttachmentDto';
import type { PresenterDto } from './PresenterDto';
export type SessionDto = {
    id?: number;
    duration?: number;
    title?: string;
    description?: string;
    presenter?: PresenterDto;
    startTime?: string;
    endTime?: string;
    streamUrl?: string;
    attachments?: Array<AttachmentDto>;
};

