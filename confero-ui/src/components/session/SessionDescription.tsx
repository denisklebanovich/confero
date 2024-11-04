import {Card, CardContent, CardDescription, CardHeader, CardTitle} from "@/components/ui/card.tsx";

const SessionDescription = ({title, description, organisers}) => {

    function chunkOrganisers(){
        const chunkSize = 2;
        const createPairs = (organisers) => {
            const pairs = [];
            for (let i = 0; i < organisers.length; i += chunkSize) {
                pairs.push(organisers.slice(i, i + chunkSize));
            }
            return pairs;
        };

        const pairs = createPairs(organisers);

        return (
            <>
                {pairs.map((pair, index) => {
                    // If it's the last pair, we handle the "and" logic
                    if (index === pairs.length - 1 && pair.length === 2) {
                        return (
                            <li key={index}>
                                {pair[0]} and {pair[1]}
                            </li>
                        );
                    } else if (index === pairs.length - 1 && pair.length === 1) {
                        return (
                            <li key={index}>
                                {pair[0]}
                            </li>
                        );
                    } else {
                        return (
                            <li key={index}>
                                {pair.join(', ')}
                            </li>
                        );
                    }
                })}
            </>
        );
    }

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
                <ul className="space-y-1 text-sm text-muted-foreground">
                    {chunkOrganisers()}
                </ul>
            </CardContent>
        </Card>
    );
};

export default SessionDescription;