import React, {useState} from 'react';
import {Button} from "@/components/ui/button.tsx";
import ConferenceEdition from "@/components/conference-editions/ConferenceEdition.tsx";
import EditionModal from "@/components/conference-editions/EditionModal.tsx";
import {useApi} from "@/api/useApi.ts";
import {ApiError, ConferenceEditionResponse, CreateConferenceEditionRequest, OrganizerResponse} from "@/generated";
import {useToast} from "@/hooks/use-toast.ts";
import {useQueryClient} from "@tanstack/react-query";

const ConferenceEditionsView = () => {
    const {apiClient, useApiQuery, useApiMutation} = useApi();
    const {toast} = useToast()

    const {data: editions, isLoading} = useApiQuery<ConferenceEditionResponse[]>(
        ["editions"],
        () => apiClient.conferenceEdition.getAllConferenceEditions()
    );

    const massUpdateOrganisersMutation = useApiMutation<>(
        () => apiClient.organizer.massUpdateOrganizers(),
        {
            onSuccess: () => {
                toast({
                    title: "Success!",
                    description: "Organizers updated",
                })
            },
            onError: (error: ApiError) => {
                toast({
                    title: "Error occurred",
                    description: error.body["reason"],
                    variant: "destructive"
                })
            },
        }
    )



    const [open, setOpen] = useState(false);

    function addConferenceEdition() {
        setOpen(true);
    }


    return (
        <div className={"min-w-full min-h-full"}>
            {isLoading ? (
                <div>Loading...</div>
            ) : (
                <>
                    <div className="flex w-full justify-center">
                        <div className={"w-2/3 items-center gap-5 flex flex-col"}>
                            <div className="flex w-full">
                                <div className="text-3xl font-bold w-full">Editions:</div>
                                <Button className={"mr-3"} variant={"secondary_grey" as any} onClick={() => massUpdateOrganisersMutation.mutate({},{})}>
                                    Update organizers
                                </Button>
                                <Button variant={"secondary_grey" as any} onClick={() => addConferenceEdition()}>
                                    Add conference edition
                                </Button>
                            </div>
                            {editions.map((edition, index) =>
                                <ConferenceEdition key={index}
                                                   {...edition}/>
                            )
                            }
                        </div>
                    </div>
                    <EditionModal open={open} setOpen={setOpen}/>
                </>
            )}
        </div>
    );
};

export default ConferenceEditionsView;