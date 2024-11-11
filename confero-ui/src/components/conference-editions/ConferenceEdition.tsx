import React from 'react';
import { Card, CardContent } from "@/components/ui/card"
import { Calendar } from "lucide-react"
import { Button } from "@/components/ui/button"

const ConferenceEdition = ({index, id, applicationDeadlineTime, openModal}) => {

    function convertDate() {
        const date = new Date(applicationDeadlineTime);

        const day = String(date.getDate()).padStart(2, '0');
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const year = date.getFullYear();

        return `${day}.${month}.${year}`;

    }

    function onUpdate() {
        openModal();
        console.log("Update clicked");
    }

    function onDelete() {
        console.log("Delete clicked");
    }


    return (
        <Card className="w-full">
            <CardContent className="flex items-center justify-between p-4">
                <div className="space-y-1">
                    <h3 className="text-lg font-medium">Edition #{index}</h3>
                    <div className="flex items-center gap-2 text-sm text-muted-foreground">
                        <Calendar className="h-4 w-4" />
                        <span>Accepting applications until: {convertDate()}</span>
                    </div>
                </div>
                <div className="flex gap-2">
                    <Button onClick={onUpdate}>Update</Button>
                    <Button variant="destructive" onClick={onDelete}>
                        Delete
                    </Button>
                </div>
            </CardContent>
        </Card>
    );
};

export default ConferenceEdition;