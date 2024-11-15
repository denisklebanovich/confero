import {Button} from "@/components/ui/button"
import {Card} from "@/components/ui/card"
import {OrganizerResponse} from "@/generated";
import {useApi} from "@/api/useApi.ts";
import {useToast} from "@/hooks/use-toast.ts";

export default function OrganiserItem({id, name, surname, orcid}: OrganizerResponse) {

    const {apiClient, useApiMutation} = useApi()
    const {toast} = useToast()

    const addAllSessionsByOrganizerToAgendaMutation = useApiMutation(
        () => apiClient.session.addAllSessionsByOrganizerToAgenda(id),
        {
            onSuccess: () => {
                toast({
                    title: "Success",
                    description: "All sessions by this organizer have been added to your agenda",
                    variant: "success",
                });
            },
            onError: () => {
                toast({
                    title: "Error occurred",
                    description: "Failed to add all sessions by this organizer to your agenda",
                    variant: "error",
                });
            },
        });

    function addOrganiserSessionsToCalendar() {
        addAllSessionsByOrganizerToAgendaMutation.mutate(id);
    }


    return (
        <Card className="flex items-center justify-between p-4 w-full my-2">
            <div className="flex flex-col sm:flex-row sm:items-center gap-1 sm:gap-2 w-full">
                <span className="font-medium">{name} {surname},</span>
                <span className="text-muted-foreground cursor-pointer"
                      onClick={() => window.open(`https://orcid.org/${orcid}`)}>{orcid}</span>
            </div>
            <Button size="sm" className="whitespace-nowrap" onClick={addOrganiserSessionsToCalendar}>
                Add to my agenda
            </Button>
        </Card>
    )
}