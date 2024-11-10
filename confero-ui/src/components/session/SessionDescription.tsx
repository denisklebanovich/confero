import {Card, CardContent, CardDescription, CardHeader, CardTitle} from "@/components/ui/card.tsx";
import {Organisers} from "@/utils/Organisers.tsx";

const SessionDescription = ({title, description, organisers}) => {

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
                <Organisers organisers={organisers}/>
            </CardContent>
        </Card>
    );
};

export default SessionDescription;