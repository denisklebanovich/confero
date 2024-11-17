import ApplicationComment from "@/components/admin-application/ApplicationComment";
import ProposalForm from "@/components/proposal/ProposalForm.tsx";
import {useApi} from "@/api/useApi.ts";
import { ApplicationResponse} from "@/generated";
import {useParams} from "react-router-dom";
import {Spinner} from "@/components/ui/spiner.tsx";

const comments = [
    {
        user: "Admin",
        comment: "Change the objective. The type of the event is wrong.",
    },
    {
        user: "Admin",
        comment:
            "Update the timeline. Ensure all dates align with the new objectives.",
    },
];

const ProposalEditView = () => {
    const {apiClient, useApiQuery} = useApi();
    const {id} = useParams();

    const {data: proposal, isLoading} = useApiQuery<ApplicationResponse>(
        ["proposal", id],
        () => apiClient.application.getApplication(Number(id))
    );

    if (isLoading) {
        return (
            <div className="relative flex items-start">
                <div className="flex flex-col w-full items-center">
                    <div className="text-3xl font-bold pb-5">Edit application</div>
                    <Spinner />
                </div>
            </div>
        )
    }

    return (
        <div className="relative flex items-start">
            <div className="flex flex-col w-full items-center">
                <div className="text-3xl font-bold pb-5">Edit application</div>
                <ProposalForm proposal={proposal} proposalId={id}/>
            </div>

            <div className="absolute top-0 right-0 h-full flex flex-col items-end p-4 space-y-4 z-40">
                {proposal?.comments?.map((comment, index) => (
                    <ApplicationComment key={index} {...comment} />
                )) ?? null}
            </div>
        </div>
    );
};

export default ProposalEditView;
