import {useNavigate, useParams} from "react-router-dom";
import {Button} from "@/components/ui/button";
import {Badge} from "@/components/ui/badge";
import {Separator} from "@/components/ui/separator";
import {Card, CardContent, CardHeader, CardTitle} from "@/components/ui/card";
import {
    ApplicationPreviewResponse,
    ApplicationResponse,
    ApplicationStatus,
    type ReviewRequest,
    ReviewType
} from "@/generated";
import {useApi} from "@/api/useApi.ts";
import {useState} from "react";
import {Spinner} from "@/components/ui/spiner.tsx";
import CommentModal from "@/components/proposal/CommentModal.tsx";
import {useToast} from "@/hooks/use-toast.ts";
import {useQueryClient} from "@tanstack/react-query";
import {STATUS_COLORS} from "@/components/application/ApplicationCard.tsx";
import {ApplicationComments} from "@/components/proposal/ApplicationComments.tsx";
import {Organisers} from "@/utils/Organisers.tsx";

const ProposalAdminView = () => {
    const navigate = useNavigate();
    const {id} = useParams();
    const {apiClient, useApiQuery, useApiMutation} = useApi();
    const queryClient = useQueryClient();
    const [modalOpen, setModalOpen] = useState(false);
    const {toast} = useToast();

    const {data: application, isLoading} = useApiQuery<ApplicationResponse>(
        ["application", id],
        () => apiClient.application.getApplication(Number(id))
    );

    const reviewApplication = useApiMutation<ApplicationPreviewResponse, { id: string, request: ReviewRequest }>(
        (args) =>
            apiClient.application.reviewApplication(Number(args.id), args.request),
        {
            onSuccess: (data) => {
                toast({
                    title: `${data.title} has been reviewed.`,
                    description: `The application has been ${data.status.toLowerCase()}.`,
                    variant: "success",
                });
                queryClient.setQueryData<ApplicationResponse>(["application", id], (oldData) => {
                    return {...oldData, status: data.status};
                });
                queryClient.invalidateQueries({queryKey: ["applications"]});
                navigate("/applications");
            },
        }
    );

    const onApprove = () => {
        reviewApplication.mutate({
            id: application.id,
            request: {
                type: ReviewType.ACCEPT,
            },
        });
    };

    const onReject = () => {
        reviewApplication.mutate({
            id: application.id,
            request: {
                type: ReviewType.REJECT,
            },
        });
    };

    const onAddComment = (comment: string) => {
        reviewApplication.mutate({
            id: application.id,
            request: {
                type: ReviewType.ASK_FOR_ADJUSTMENTS,
                comment: comment,
            },
        });
    }


    return (
        isLoading ?
            <div className="flex items-center justify-center h-full">
                <Spinner/>
            </div> :
            <div className="w-full max-w-3xl mx-auto p-6">
                <Card>
                    <CardHeader className='flex flex-row justify-between'>
                        <CardTitle>{application.title}</CardTitle>
                        <Badge
                            className={STATUS_COLORS[application.status]}>
                            {application.status}
                        </Badge>
                    </CardHeader>
                    <CardContent className="space-y-6">
                        <div>
                            <h3 className="text-lg font-semibold mb-2">Description</h3>
                            <p className="text-muted-foreground">{application.description}</p>
                        </div>

                        <div>
                            <h3 className="text-lg font-semibold mb-2">Tags</h3>
                            <div className="flex flex-wrap gap-2">
                                {application.tags.map((tag, index) => (
                                    <Badge key={index} variant="secondary">
                                        {tag}
                                    </Badge>
                                ))}
                            </div>
                        </div>

                        <Separator className="my-6"/>

                        <div>
                            <h3 className="text-lg font-semibold mb-4">Presentations</h3>
                            {application.presentations.map((presentation, index) => (
                                <Card key={index} className="mb-4">
                                    <CardHeader>
                                        <CardTitle className="text-base">{presentation.title}</CardTitle>
                                    </CardHeader>
                                    <CardContent>
                                        <p className="text-sm text-muted-foreground mb-2">{presentation.description}</p>
                                        <div className="text-sm">
                                            <strong>Presenters:</strong>
                                            <Organisers organisers={presentation.presenters} chunkSize={5}/>
                                        </div>
                                    </CardContent>
                                </Card>
                            ))}
                        </div>

                        <div className="flex justify-between items-center mt-6">
                            <Button variant="secondary" onClick={() => navigate("/applications")}>
                                Back
                            </Button>
                            {application.status == ApplicationStatus.PENDING &&
                                <div className="space-x-4">
                                    <Button variant="secondary" onClick={() => setModalOpen(true)}>
                                        Add a comment
                                    </Button>
                                    <Button variant="destructive" onClick={onReject}>
                                        Reject
                                    </Button>
                                    <Button onClick={onApprove}>Approve</Button>
                                </div>
                            }
                        </div>
                    </CardContent>
                </Card>
                <ApplicationComments comments={application.comments}/>
                <CommentModal isOpen={modalOpen} setIsOpen={setModalOpen}
                              onSubmit={onAddComment}
                />
            </div>
    );
};

export default ProposalAdminView;