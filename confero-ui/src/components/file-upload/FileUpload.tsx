"use client";

import * as React from "react";
import {Check, ChevronDown, ChevronUp, UploadCloud, X} from "lucide-react";

import {Button} from "@/components/ui/button";
import {
    Dialog,
    DialogContent,
    DialogDescription,
    DialogHeader,
    DialogTitle,
    DialogTrigger,
} from "@/components/ui/dialog";
import {useRef, useState, DragEvent, ChangeEvent} from "react";
import {Alert, AlertDescription, AlertTitle} from "@/components/ui/alert";
import {useApi} from "@/api/useApi";
import {useQueryClient} from "@tanstack/react-query";
import {
    ApiError,
    ApplicationPreviewResponse,
    CreateApplicationRequest,
} from "@/generated";
import {Collapsible, CollapsibleContent, CollapsibleTrigger} from "../ui/collapsible";
import {useToast} from "@/hooks/use-toast";
import Ajv from "ajv";

interface UploadedFile {
    name: string;
    status: "success" | "error";
    jsonData?: Record<string, any> | null;
}

const schema = {
    type: "object",
    properties: {
        title: {type: "string"},
        type: {type: "string", enum: ["SESSION", "WORKSHOP", "TUTORIAL"]},
        tags: {
            type: "array",
            items: {type: "string"},
        },
        description: {type: "string"},
        presentations: {
            type: "array",
            items: {
                type: "object",
                properties: {
                    title: {type: "string"},
                    description: {type: "string"},
                    presenters: {
                        type: "array",
                        items: {
                            type: "object",
                            properties: {
                                orcid: {type: "string"},
                                email: {type: "string"},
                                isSpeaker: {type: "boolean"},
                            },
                            required: ["orcid", "email"],
                            additionalProperties: false,
                        },
                    },
                },
                required: ["title", "description", "presenters"],
                additionalProperties: false,
            },
        },
    },
    required: ["title", "type", "tags", "description", "presentations"],
    additionalProperties: false,
};

const ajv = new Ajv();
const validate = ajv.compile(schema);

const verifyApplicationJsonStructure = (data: Record<string, any>): boolean => {
    return validate(data) as boolean;
};

const jsonPreview = `
{
  "title": "string",
  "type": "SESSION" | "WORKSHOP" | "TUTORIAL",
  "tags": [
    "string"
  ],
  "description": "string",
  "presentations": [
    {
      "title": "string",
      "description": "string",
      "presenters": [
        {
          "orcid": "string",
          "email": "string",
          "isSpeaker": true
        }
      ]
    }
  ]
}
`
const FileUpload = () => {
    const [open, setOpen] = useState(false);
    const [isDragging, setIsDragging] = useState(false);
    const [uploadedFile, setUploadedFile] = useState<UploadedFile | null>(null);
    const [error, setError] = useState<string | null>(null);
    const inputRef = useRef<HTMLInputElement>(null);
    const [isPreviewOpen, setIsPreviewOpen] = useState(false)
    const {toast} = useToast();

    const {apiClient, useApiMutation} = useApi();
    const queryClient = useQueryClient();

    const {mutate: createApplication} = useApiMutation<
        ApplicationPreviewResponse,
        CreateApplicationRequest
    >((request) => apiClient.application.createApplication(request), {
        onSuccess: (newApplication) => {
            queryClient.setQueryData<ApplicationPreviewResponse[]>(
                ["applications"],
                (oldApplications) => [...(oldApplications || []), newApplication]
            );
            toast({
                title: "Application submitted",
                description: "Your application has been submitted. Please wait for approval.",
                variant: "success",
            });
        },
        onError: (error: ApiError) => {
            if (error.body['reason'] === "INVALID_ORCID") {
                toast({
                    title: "An error occurred",
                    description: "Invalid ORCID provided",
                    variant: "error",
                });
            } else {
                toast({
                    title: "An error occurred",
                    description: error.message,
                    variant: "error",
                });
            }
        },
    });

    const handleDragOver = (e: DragEvent) => {
        e.preventDefault();
        setIsDragging(true);
    };

    const handleDragLeave = (e: DragEvent) => {
        e.preventDefault();
        setIsDragging(false);
    };

    const handleFileInput = (e: ChangeEvent<HTMLInputElement>) => {
        const file = e.target.files?.[0];
        if (file) handleFile(file);
    };

    const handleDrop = (e: DragEvent) => {
        e.preventDefault();
        setIsDragging(false);
        const file = e.dataTransfer.files[0];
        if (file) handleFile(file);
    };

    const handleFile = async (file: File) => {
        setError(null);
        setUploadedFile(null);

        if (file.type !== "application/json") {
            setError("Please upload a JSON file.");
            return;
        }

        try {
            const fileContent = await file.text();
            const jsonData = JSON.parse(fileContent);

            if (verifyApplicationJsonStructure(jsonData)) {
                setUploadedFile({name: file.name, status: "success", jsonData});
            } else {
                setUploadedFile({name: file.name, status: "error", jsonData: null});
                setError(
                    `Invalid JSON structure. ${JSON.stringify(validate.errors?.[0].params)} ${validate.errors?.[0].message}`
                );
            }
        } catch (err) {
            setUploadedFile({name: file.name, status: "error", jsonData: null});
            setError(
                "An error occurred while processing the file. Please try again."
            );
        }
    };

    const handleDelete = () => {
        setUploadedFile(null);
        setError(null);
        if (inputRef.current) inputRef.current.value = "";
    };

    const handleUpload = () => {
        if (!uploadedFile) return;
        setOpen(false);
        setUploadedFile(null);
        setError(null);
        createApplication({
            title: uploadedFile.jsonData.title,
            type: uploadedFile.jsonData.type,
            tags: uploadedFile.jsonData.tags,
            description: uploadedFile.jsonData.description,
            presentations: uploadedFile.jsonData.presentations,
            saveAsDraft: true
        });
    };

    const handleOpenChange = (openState: boolean) => {
        if (!openState) {
            setUploadedFile(null);
            setError(null);
            setIsDragging(false);
            if (inputRef.current) inputRef.current.value = "";
        }
        setOpen(openState);
    };

    return (
        <Dialog
            open={open}
            onOpenChange={(openState) => handleOpenChange(openState)}
        >
            <DialogTrigger asChild>
                <Button variant="outline">Upload JSON File</Button>
            </DialogTrigger>
            <DialogContent className="sm:max-w-md max-h-[90vh] overflow-y-auto">
                <DialogHeader>
                    <DialogTitle>Upload File</DialogTitle>
                    <DialogDescription>
                        Drag and drop file with your application information.
                    </DialogDescription>
                </DialogHeader>
                <Collapsible open={isPreviewOpen} onOpenChange={setIsPreviewOpen} className="mt-4">
                    <CollapsibleTrigger asChild>
                        <Button variant="outline" size="sm" className="flex items-center justify-between w-full">
                            <span>View Expected JSON Format</span>
                            {isPreviewOpen ? <ChevronUp className="h-4 w-4"/> : <ChevronDown className="h-4 w-4"/>}
                        </Button>
                    </CollapsibleTrigger>
                    <CollapsibleContent className="mt-2">
            <pre className="p-4 bg-muted rounded-md overflow-x-auto text-xs">
              <code>{jsonPreview}</code>
            </pre>
                    </CollapsibleContent>
                </Collapsible>
                {!uploadedFile &&
                    <div
                        onDragOver={handleDragOver}
                        onDragLeave={handleDragLeave}
                        onDrop={handleDrop}
                        onClick={() => inputRef.current?.click()}
                        className={`
                                mt-4 cursor-pointer rounded-lg border-2 border-dashed p-10
                                text-center transition-colors
                                hover:bg-muted/50
                                ${isDragging ? "border-primary bg-muted/50" : "border-muted-foreground/25"}
                                `}
                    >
                        <div className="flex flex-col items-center gap-4">
                            <UploadCloud className="h-10 w-10 text-muted-foreground"/>
                            <div className="space-y-2">
                                <p className="text-lg font-medium">Drag and drop JSON file</p>
                                <p className="text-sm text-muted-foreground">or</p>
                                <Button variant="default" size="sm" type="button">
                                    Browse files
                                </Button>
                            </div>
                        </div>
                        <input
                            ref={inputRef}
                            type="file"
                            accept=".json,application/json"
                            className="hidden"
                            onChange={handleFileInput}
                        />
                    </div>
                }
                {uploadedFile && (
                    <div className="mt-4 flex items-center justify-between rounded-md border p-2">
                        <span className="truncate">{uploadedFile.name}</span>
                        <div className="flex items-center gap-2">
                            {uploadedFile.status === "success" && (
                                <Check className="text-green-500"/>
                            )}
                            {uploadedFile.status === "error" && (
                                <X className="text-red-500"/>
                            )}
                            <Button variant="ghost" size="sm" onClick={handleDelete}>
                                <X className="h-4 w-4"/>
                            </Button>
                        </div>
                    </div>
                )}
                {error && (
                    <Alert variant="destructive" className="mt-4">
                        <AlertTitle>Error</AlertTitle>
                        <AlertDescription>{error}</AlertDescription>
                    </Alert>
                )}
                {uploadedFile && uploadedFile.status === "success" && (
                    <div className="flex justify-end mt-4 ">
                        <Button
                            onClick={handleUpload}
                            variant="default"
                            size="sm"
                            type="button"
                        >
                            Upload
                        </Button>
                    </div>
                )}
            </DialogContent>
        </Dialog>
    );
};

export default FileUpload;
