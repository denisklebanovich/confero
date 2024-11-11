import Organiser from "@/utils/Organiser.tsx";

export function Organisers({organisers, chunkSize = 2}){

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
            <ul className="space-y-1 text-sm text-muted-foreground">
            {pairs.map((pair, index) => {
                    if (index === pairs.length - 1 && pair.length === 2) {
                        return (
                            <li key={index}>
                                <Organiser key={pair[0].id} organiser={pair[0]}/> <span>and</span> <Organiser key={pair[1].id} organiser={pair[1]}/>
                        </li>
                    );
                    } else if (index === pairs.length - 1 && pair.length === 1) {
                        return (
                            <li key={index}>
                                <Organiser key={pair[0].id} organiser={pair[0]}/>
                                </li>
                        );
                    } else {
                        return (
                            <li key={index}>
                                {pair.map((organiser,index1) => {
                                    return (<>
                                    <Organiser key={index1} organiser={organiser}/>
                                    {index1 < pair.length - 1 ? <span>, </span> : null}
                                </>);})}
                            </li>
                        );
                    }
                })}
            </ul>
        </>
    );
}