import React, {useState} from 'react';
import {Card, CardContent} from "@/components/ui/card"
import {Calendar} from "lucide-react"
import {Button} from "@/components/ui/button"
import {useApi} from "@/api/useApi.ts";
import {useQueryClient} from "@tanstack/react-query";
import {useToast} from "@/hooks/use-toast.ts";
import {ConferenceEditionResponse} from "@/generated";
import EditionModal from "@/components/conference-editions/EditionModal.tsx";

function convertDate(applicationDeadlineTime: string) {
    const date = new Date(applicationDeadlineTime);

    const day = String(date.getDate()).padStart(2, '0');
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const year = date.getFullYear();

    return `${day}.${month}.${year}`;

}

const ConferenceEdition = (edition: ConferenceEditionResponse) => {


    const {toast} = useToast()
    const {apiClient, useApiMutation} = useApi();
    const queryClient = useQueryClient();
    const [open, setOpen] = useState(false);

    const deleteMutation = useApiMutation(
        () => apiClient.conferenceEdition.deleteConferenceEdition(edition.id),
        {
            onSuccess: (oldEdition) => {
                queryClient.setQueryData<ConferenceEditionResponse[]>(
                    ["editions"],
                    (oldEditions) =>
                        oldEditions.filter((edition) => edition.id !== edition.id)
                );
            },
            onError: () => {
                toast({
                    title: "Error occurred",
                    description: "Failed to delete conference edition",
                });
            }
        }
    );

    function handleDelete() {
        deleteMutation.mutate(edition.id);
    }


    return (
        <>
            <Card className="w-full">
                <CardContent className="flex items-center justify-between p-4">
                    <div className="space-y-1">
                        <h3 className="text-lg font-medium">Edition #{edition.id}</h3>
                        <div className="flex items-center gap-2 text-sm text-muted-foreground">
                            <Calendar className="h-4 w-4"/>
                            <span>Accepting applications until: {convertDate(edition.applicationDeadlineTime)}</span>
                        </div>
                    </div>
                    <div className="flex gap-2">
                        <Button onClick={() => setOpen(true)}>
                            Edit
                        </Button>
                        <Button variant="destructive" onClick={handleDelete}>
                            Delete
                        </Button>
                    </div>
                </CardContent>
            </Card>
            <EditionModal open={open} setOpen={setOpen} edition={edition}/>
        </>
    );
};

export default ConferenceEdition;