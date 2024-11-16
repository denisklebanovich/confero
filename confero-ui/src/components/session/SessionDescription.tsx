import {Card, CardContent, CardDescription, CardHeader, CardTitle} from "@/components/ui/card.tsx";
import {PresenterResponse} from "@/generated";
import Organiser from "@/utils/Organiser.tsx";

type SessionDescriptionProps = {
    title: string;
    description: string;
    organisers: PresenterResponse[];
}
const SessionDescription = ({title, description, organisers}: SessionDescriptionProps) => {

    return (
        <Card className="w-1/3">
            <CardHeader>
                <CardTitle>{title}</CardTitle>
                <CardDescription>
                    {description}
                </CardDescription>
            </CardHeader>
            <CardContent>
                <h3 className="font-semibold mb-2">Organizers</h3>
                {organisers.map((organiser) => (
                    <Organiser key={organiser.id} organiser={organiser}/>
                ))}
            </CardContent>
        </Card>
    );
};

export default SessionDescription;