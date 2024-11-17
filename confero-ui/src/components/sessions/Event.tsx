import { Button } from "@/components/ui/button"
import { Card } from "@/components/ui/card"
import {useNavigate} from "react-router-dom";

export default function Event({userFirstName, userLastName, sessionId}) {
    const navigate = useNavigate();
    return (
        <Card className="w-full max-w-md p-4 flex items-start justify-between gap-4 mb-2">
            <div className="space-y-1">
                <h3 className="font-semibold text-lg">{userFirstName} {userLastName}</h3>
                <p className="text-muted-foreground text-sm">Added new file.</p>
            </div>
            <Button variant="secondary_grey" size="sm" onClick={() => navigate(`/session/${sessionId}`)}>
                Go to session
            </Button>
        </Card>
    )
}