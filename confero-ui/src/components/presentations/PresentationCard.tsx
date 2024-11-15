import {Button} from "@/components/ui/button"
import {Card, CardContent, CardHeader} from "@/components/ui/card"
import {Clock, Download, Upload, X} from 'lucide-react'
import {useState} from "react"
import {AttachmentRequest, AttachmentResponse, PresentationSessionResponse} from "@/generated";
import {getFormattedTime} from "@/utils/dateUtils.ts";
import {useApi} from "@/api/useApi.ts";
import {useToast} from "@/hooks/use-toast.ts";


interface PresentationCardProps {
    presentation: PresentationSessionResponse
    sessionId: number
}

export default function PresentationCard({
                                             presentation: {
                                                 id: presentationId,
                                                 title,
                                                 description,
                                                 presenters,
                                                 attachments,
                                                 startTime,
                                                 endTime,
                                             },
                                             sessionId,
                                         }: PresentationCardProps) {
    const [uploadedFiles, setUploadedFiles] = useState<AttachmentResponse[]>(attachments)
    const {apiClient, useApiMutation} = useApi()
    const {toast} = useToast()

    const {mutate: uploadFile} = useApiMutation<AttachmentResponse, {
        sessionId: number,
        presentationId: number,
        formData: AttachmentRequest
    }>(
        ({sessionId, presentationId, formData}) =>
            apiClient.presentation.addPresentationAttachment(sessionId, presentationId, formData),
        {
            onSuccess: (data) => {
                setUploadedFiles((prev) => [...prev, data])
                toast({
                    title: "Files uploaded successfully",
                    description: "The files have been added to the presentation.",
                })
            },
            onError: (error) => {
                console.error(error)
                toast({
                    title: "Error uploading files",
                    description: "There was a problem uploading your files. Please try again.",
                    variant: "destructive",
                })
            }
        }
    )

    const {mutate: deleteFile} = useApiMutation<void, {
        sessionId: number,
        presentationId: number,
        attachmentId: number
    }>(
        ({sessionId, presentationId, attachmentId}) =>
            apiClient.presentation.deletePresentationAttachment(sessionId, attachmentId, presentationId),
        {
            onSuccess: (_, variables) => {
                setUploadedFiles((prev) => prev.filter((file) => file.id !== variables.attachmentId))
                toast({
                    title: "File deleted successfully",
                    description: "The file has been removed from the presentation.",
                })
            },
            onError: (error) => {
                console.error(error)
                toast({
                    title: "Error deleting file",
                    description: "There was a problem deleting the file. Please try again.",
                    variant: "destructive",
                })
            }
        }
    )

    const handleFileUpload = (event: React.ChangeEvent<HTMLInputElement>) => {
        const files = event.target.files
        if (files) {
            // uploadFile(sessionId, presentationId, {file: files[0]})
        }
    }

    const handleFileDownload = (file: AttachmentResponse) => {
        window.open(file.url, '_blank')
    }

    return (
        <Card className="w-full max-w-7xl">
            <CardHeader>
                <div className="space-y-2">
                    <div className="text-sm text-muted-foreground">
                        {presenters.map((presenter, index) => (
                            <span key={presenter.id} className={presenter.isSpeaker ? "font-semibold" : ""}>
                                {presenter.name}
                                {index < presenters.length - 1 ? ", " : null}
                            </span>
                        ))}
                    </div>
                    <h2 className="text-2xl font-bold tracking-tight">{title}</h2>
                    <p className="text-muted-foreground">{description}</p>
                </div>
            </CardHeader>
            <CardContent>
                <div className="flex flex-col gap-6">
                    <div className="flex items-center text-sm text-muted-foreground">
                        <Clock className="mr-2 h-4 w-4"/>
                        <span>{getFormattedTime(startTime)} - {getFormattedTime(endTime)}</span>
                    </div>
                    <div className="space-y-4">
                        <div className="flex items-center justify-between">
                            <h3 className="text-lg font-semibold">Files</h3>
                            <div className="relative">
                                <input
                                    type="file"
                                    id="file-upload"
                                    className="hidden"
                                    multiple
                                    aria-label="Upload files"
                                />
                                <Button
                                    variant="secondary"
                                    size="sm"
                                    onClick={() => document.getElementById("file-upload")?.click()}
                                >
                                    <Upload className="mr-2 h-4 w-4"/>
                                    Upload files
                                </Button>
                            </div>
                        </div>
                        <div className="space-y-2">
                            {uploadedFiles.map((file) => (
                                <div
                                    key={file.id}
                                    className="flex items-center justify-between rounded-lg border p-2"
                                >
                                    <Button
                                        variant="ghost"
                                        size="sm"
                                        className="text-sm text-left"
                                        onClick={() => handleFileDownload(file)}
                                    >
                                        <Download className="mr-2 h-4 w-4"/>
                                        {file.name}
                                    </Button>
                                    <Button
                                        variant="ghost"
                                        size="sm"
                                        // onClick={() => deleteFile({sessionId, presentationId, attachmentId: file.id})}
                                        aria-label={`Remove ${file.name}`}
                                    >
                                        <X className="h-4 w-4"/>
                                    </Button>
                                </div>
                            ))}
                        </div>
                    </div>
                </div>
            </CardContent>
        </Card>
    )
}