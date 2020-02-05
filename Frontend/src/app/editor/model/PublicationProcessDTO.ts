import { ReviewTaskDTO } from './ReviewTaskDTO';

export interface PublicationProcessDTO {
    publicationTitle: string;
    publicationVersion: number;
    processState: string;
    reviewerAssignments: Array<ReviewTaskDTO>;
}
