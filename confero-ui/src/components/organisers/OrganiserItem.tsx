import { Button } from "@/components/ui/button"
import { Card } from "@/components/ui/card"

export default function OrganiserItem({id, name, surname, orcid}) {

    function addOrganiserSessionsToCalendar() {

    }


    return (
        <Card className="flex items-center justify-between p-4 w-full my-2">
            <div className="flex flex-col sm:flex-row sm:items-center gap-1 sm:gap-2 w-full">
                <span className="font-medium">{name} {surname},</span>
                <span className="text-muted-foreground cursor-pointer" onClick={()=> window.open(`https://orcid.org/${orcid}`)}>{orcid}</span>
            </div>
            <Button size="sm" className="whitespace-nowrap" onClick={addOrganiserSessionsToCalendar}>
                Add to my calendar
            </Button>
        </Card>
    )
}