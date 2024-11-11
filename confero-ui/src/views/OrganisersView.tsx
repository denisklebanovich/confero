import React, {useState} from 'react';
import { Search } from "lucide-react"
import { Input } from "@/components/ui/input"
import OrganiserItem from "@/components/organisers/OrganiserItem.tsx";

const OrganisersView = () => {
    const mock = [
        {
            "id": "1",
            "name": "Alice",
            "surname": "Johnson",
            "orcid": "0000-0002-0759-9656"
        },
        {
            "id": "2",
            "name": "Bob",
            "surname": "Smith",
            "orcid": "0000-0002-0759-9656"
        },
        {
            "id": "3",
            "name": "Charlie",
            "surname": "Brown",
            "orcid": "0000-0002-0759-9656"
        },
        {
            "id": "4",
            "name": "Dana",
            "surname": "White",
            "orcid": "0000-0002-0759-9656"
        }
    ]


    const [organizers, setOrganizers] = useState(mock)
    const [prompt, setPrompt] = useState("")


    return (
        <div className="w-full max-w-3xl mx-auto p-6">
            <div className="space-y-2">
                <h1 className="text-3xl font-bold tracking-tight">Organizers</h1>
                <p className="text-muted-foreground">
                    Search organizers and add their sessions to your calendar.
                </p>
            </div>
            <div className="relative mt-6 mb-4">
                <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
                <Input
                    className="pl-10"
                    placeholder="Type a name or surname..."
                    type="search"
                    value={prompt}
                    onChange={(e) => setPrompt(e.target.value)}
                />
            </div>
            {
                organizers.map((organizer) => {
                    return (
                        <OrganiserItem key={organizer.id} {...organizer}/>
                    )
                })
            }
        </div>
    )
};

export default OrganisersView;