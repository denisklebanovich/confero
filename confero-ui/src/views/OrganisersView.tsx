'use client'

import React, {useState} from 'react'
import {Search} from 'lucide-react'
import {Input} from "@/components/ui/input"
import OrganiserItem from "@/components/organisers/OrganiserItem"
import {useApi} from "@/api/useApi"
import {useDebounce} from "@uidotdev/usehooks";
import {Spinner} from "@/components/ui/spiner.tsx";
import {OrganizerResponse} from "@/generated";

const OrganisersView = () => {
    const {apiClient, useApiQuery} = useApi()
    const [prompt, setPrompt] = useState("")
    const debouncedPrompt = useDebounce(prompt, 300)

    const {data: organizers, isLoading, error} = useApiQuery<OrganizerResponse[]>(
        ['organizers', debouncedPrompt],
        () => apiClient.organizer.findOrganizers(debouncedPrompt)
    )

    return (
        <div className="w-full max-w-3xl mx-auto p-6">
            <div className="space-y-2">
                <h1 className="text-3xl font-bold tracking-tight">Organizers</h1>
                <p className="text-muted-foreground">
                    Search organizers and add their sessions to your calendar.
                </p>
            </div>
            <div className="relative mt-6 mb-4">
                <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground"/>
                <Input
                    className="pl-10"
                    placeholder="Type a name or surname..."
                    type="search"
                    value={prompt}
                    onChange={(e) => setPrompt(e.target.value)}
                />
            </div>
            {isLoading && (
                <div className="flex justify-center items-center h-32">
                    <Spinner className="h-8 w-8"/>
                </div>
            )}
            {error && (
                <div className="text-red-500 text-center">
                    An error occurred while fetching organizers. Please try again.
                </div>
            )}
            {organizers && organizers.length === 0 && (
                <div className="text-center text-muted-foreground">
                    No organizers found. Try a different search term.
                </div>
            )}
            {organizers && organizers.map((organizer) => (
                <OrganiserItem key={organizer.id} {...organizer} />
            ))}
        </div>
    )
}

export default OrganisersView