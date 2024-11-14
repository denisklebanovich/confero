'use client'

import {Upload} from 'lucide-react'
import {useEffect, useState} from "react"
import {Button} from "@/components/ui/button"
import {Dialog, DialogContent, DialogHeader, DialogTitle,} from "@/components/ui/dialog"
import {Input} from "@/components/ui/input"
import {Label} from "@/components/ui/label"
import {cn} from "@/lib/utils"
import {Card} from "@/components/ui/card"
import {useQueryClient} from "@tanstack/react-query"
import {useToast} from "@/hooks/use-toast.ts";
import {useApi} from "@/api/useApi.ts";
import type {
    ConferenceEditionResponse,
    CreateConferenceEditionRequest,
    UpdateConferenceEditionRequest
} from "@/generated";
import {getInputDate} from "@/utils/dateUtils.ts";

interface EditionModalProps {
    open: boolean
    setOpen: (open: boolean) => void
    edition?: ConferenceEditionResponse
}

export default function EditionModal({open, setOpen, edition}: EditionModalProps) {
    const [dragActive, setDragActive] = useState(false)
    const [file, setFile] = useState<File | null>(null)
    const [date, setDate] = useState<string | undefined>(edition ? getInputDate(edition.applicationDeadlineTime) : undefined)

    const {toast} = useToast()
    const {apiClient, useApiMutation} = useApi()
    const queryClient = useQueryClient()

    const handleDragOver = (e: React.DragEvent<HTMLDivElement>) => {
        e.preventDefault()
        setDragActive(true)
    }

    const handleDragLeave = (e: React.DragEvent<HTMLDivElement>) => {
        e.preventDefault()
        setDragActive(false)
    }

    const handleDrop = (e: React.DragEvent<HTMLDivElement>) => {
        e.preventDefault()
        setDragActive(false)
        const droppedFile = e.dataTransfer.files[0]
        handleFileSelection(droppedFile)
    }

    const handleFileSelection = (selectedFile: File) => {
        setFile(selectedFile)
    }

    const handleFileInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const selectedFile = e.target.files?.[0]
        if (selectedFile) {
            handleFileSelection(selectedFile)
        }
    }

    useEffect(() => {
        if (edition?.applicationDeadlineTime) {
            setDate(getInputDate(edition.applicationDeadlineTime))
        }
    }, [edition])


    const createMutation = useApiMutation<ConferenceEditionResponse, CreateConferenceEditionRequest>(
        (request) => apiClient.conferenceEdition.createConferenceEdition(request),
        {
            onSuccess: (newEdition) => {
                queryClient.setQueryData<ConferenceEditionResponse[]>(
                    ["editions"],
                    (oldEditions) => {
                        if (!oldEditions) return [newEdition]
                        return [...oldEditions, newEdition]
                    }
                )
                toast({
                    title: "Success!",
                    description: "New conference edition created",
                })
            },
            onError: () => {
                toast({
                    title: "Error occurred",
                    description: "Failed to create new conference edition",
                    variant: "destructive",
                })
            },
        }
    )

    const updateMutation = useApiMutation<
        ConferenceEditionResponse,
        { id: number; request: UpdateConferenceEditionRequest }
    >(
        ({id, request}) => apiClient.conferenceEdition.updateConferenceEdition(id, request),
        {
            onSuccess: (newEdition) => {
                queryClient.setQueryData<ConferenceEditionResponse[]>(
                    ["editions"],
                    (oldEditions) => {
                        if (!oldEditions) return [newEdition]
                        const updatedEditions = [...oldEditions]
                        const index = updatedEditions.findIndex((edition) => edition.id === newEdition.id)
                        if (index !== -1) {
                            updatedEditions[index] = newEdition
                        } else {
                            updatedEditions.push(newEdition)
                        }
                        return updatedEditions
                    }
                )
                toast({
                    title: "Success!",
                    description: "Conference edition updated",
                })
            },
            onError: () => {
                toast({
                    title: "Error occurred",
                    description: "Failed to update conference edition",
                    variant: "destructive",
                })
            },
        }
    )

    const handleSave = () => {
        if (edition) {
            updateMutation.mutate({
                id: edition.id,
                request: {
                    applicationDeadlineTime: date,
                    invitationList: file,
                },
            })
        } else {
            createMutation.mutate({
                applicationDeadlineTime: date,
                invitationList: file,
            })
        }
        handleClose()
    }

    const handleClose = () => {
        setFile(null)
        if (!edition) {
            setDate("")
        }
        setOpen(false)
    }

    return (
        <Dialog open={open} onOpenChange={handleClose}>
            <DialogContent className="sm:max-w-[425px]">
                <DialogHeader>
                    <DialogTitle className="text-xl font-bold">
                        {!edition ? "New Edition" : `Edition #${edition?.id}`}
                    </DialogTitle>
                </DialogHeader>
                <div className="grid gap-6 py-4 w-full">
                    <div className="grid gap-2 w-full">
                        <Label htmlFor="deadline">Applications&apos; deadline</Label>
                        <div className="relative w-full flex items-center justify-center">
                            <Input
                                id="deadline"
                                type="date"
                                value={date}
                                onChange={(e) => setDate(e.target.value)}
                            />
                        </div>
                    </div>
                    <div className="grid gap-2">
                        <Label>Participants</Label>
                        <div
                            className={cn(
                                "border-2 border-dashed rounded-lg p-8 text-center",
                                dragActive ? "border-primary" : "border-gray-300"
                            )}
                            onDragOver={handleDragOver}
                            onDragLeave={handleDragLeave}
                            onDrop={handleDrop}
                        >
                            <div className="flex flex-col items-center gap-2">
                                <Upload className="h-8 w-8 text-muted-foreground"/>
                                <p className="text-sm text-muted-foreground">Drag and drop file</p>
                                <p className="text-xs text-muted-foreground">or</p>
                                <Button
                                    variant="secondary"
                                    size="sm"
                                    onClick={() => document.getElementById("file-upload")?.click()}
                                >
                                    Browse for .csv file
                                    Browse for .csv file
                                </Button>
                                <input
                                    id="file-upload"
                                    type="file"
                                    className="hidden"
                                    accept=".csv"
                                    onChange={handleFileInputChange}
                                />
                            </div>
                        </div>
                        {file && (
                            <Card className="bg-white/50 mt-2 p-3 shadow-sm">
                                <h1 className="text-l font-medium">{file.name}</h1>
                            </Card>
                        )}
                    </div>
                    <Button className="w-full" size="lg" onClick={handleSave}>
                        Save
                    </Button>
                </div>
            </DialogContent>
        </Dialog>
    )
}