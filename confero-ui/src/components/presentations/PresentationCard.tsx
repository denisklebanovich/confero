import {Button} from "@/components/ui/button"
import {Card, CardContent, CardHeader} from "@/components/ui/card"
import {Clock, Upload, X} from 'lucide-react'
import {useState} from "react"
import {AttachmentResponse, PresentationSessionResponse} from "@/generated";
import {getFormattedTime} from "@/utils/dateUtils.ts";

interface File {
    name: string
    size?: number
}


export default function PresentationCard({
                                             presenters,
                                             title,
                                             description,
                                             attachments,
                                             startTime,
                                             endTime
                                         }: PresentationSessionResponse) {
    const [uploadedFiles, setUploadedFiles] = useState<AttachmentResponse[]>(attachments)

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
                                    key={file.name}
                                    className="flex items-center justify-between rounded-lg border p-2"
                                >
                                    <span className="text-sm">{file.name}</span>
                                    <Button
                                        variant="ghost"
                                        size="sm"
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