import {Button} from "@/components/ui/button"
import {Card, CardContent, CardHeader, CardTitle} from "@/components/ui/card"
import {Clock, Download, Upload, X} from 'lucide-react'
import {useRef, useState} from "react"
import {AttachmentRequest, AttachmentResponse, PresentationSessionResponse} from "@/generated";
import {getFormattedTime} from "@/utils/dateUtils.ts";
import {useApi} from "@/api/useApi.ts";
import {useToast} from "@/hooks/use-toast.ts";
import {Organisers} from "@/utils/Organisers.tsx";
import {useAuth} from "@/auth/AuthProvider.tsx";


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
    const {authorized} = useAuth()
    const fileInputRef = useRef(null);

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
        console.log(event.target.files[0])
        if (files) {
            uploadFile({sessionId: Number(sessionId), presentationId: Number(presentationId), formData: {file: files[0]}})
        }
    }

    const handleFileDownload = (file: AttachmentResponse) => {
        window.open(file.url, '_blank')
    }

    const handleFileDelete = (file: AttachmentResponse) => {
        deleteFile({sessionId: Number(sessionId), presentationId: Number(presentationId), attachmentId: file.id})
    }




    return (
        <Card className={`${!authorized ? "min-w-[500px] max-w-[600px]" : "w-[95%] mb-3"} snap-start`}>
            <CardHeader>
                <div className={"w-full flex flex-row justify-around"}>
                    <div className={`space-y-2 ${authorized ? "w-3/4" : "w-full" } `}>
                        <div className="text-sm text-muted-foreground">
                            <Organisers organisers={presenters} chunkSize={authorized ? 10 : 4}/>
                        </div>
                        <h2 className="text-2xl font-bold tracking-tight">{title}</h2>
                        <p className="text-muted-foreground">{description}</p>
                        <div className="flex items-center text-sm text-muted-foreground">
                            <Clock className="mr-2 h-4 w-4"/>
                            <span>{getFormattedTime(startTime)} - {getFormattedTime(endTime)}</span>
                        </div>
                    </div>
                    {authorized && (
                    <div className={"w-1/4"}>
                        <Card className="w-full max-w-2x">
                            <div className="flex flex-row items-center justify-between space-y-0 px-5 pt-4">
                                <div className="text-xl font-semibold">Files</div>
                                <Button
                                    variant="secondary"
                                    className="text-sm font-medium"
                                    onClick={() => {
                                        fileInputRef.current.click();
                                    }}
                                >
                                    Upload files
                                </Button>
                                <input
                                    type="file"
                                    ref={fileInputRef}
                                    style={{display: 'none'}}
                                    onChange={handleFileUpload}
                                />
                            </div>
                            <CardContent>
                                <div>
                                    {uploadedFiles.map((file) => (
                                        <div
                                            key={file.name}
                                            className="flex items-center justify-between py-2"
                                        >
                                            <span className="text-sm cursor-pointer" onClick={()=>handleFileDownload(file)}>{file.name}</span>
                                            <Button
                                                variant="ghost"
                                                size="icon"
                                                className="h-8 w-8"
                                                onClick={() => handleFileDelete(file)}
                                            >
                                                <X className="h-4 w-4 cursor-pointer" />
                                                <span className="sr-only cursor-pointer">Remove file</span>
                                            </Button>
                                        </div>
                                    ))}
                                </div>
                            </CardContent>
                        </Card>
                    </div>
                        )}
                </div>

            </CardHeader>
        </Card>
    )
}