"use client";

import * as React from "react";
import { Check, UploadCloud, X } from "lucide-react";

import { Button } from "@/components/ui/button";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import { useRef, useState, DragEvent, ChangeEvent } from "react";
import { Alert, AlertDescription, AlertTitle } from "@/components/ui/alert";
import { useApi } from "@/api/useApi";
import { useQueryClient } from "@tanstack/react-query";
import {
  ApplicationPreviewResponse,
  CreateApplicationRequest,
} from "@/generated";

interface UploadedFile {
  name: string;
  status: "success" | "error";
  jsonData?: Record<string, any> | null;
}

const verifyApplicationJsonStructure = (data: Record<string, any>): boolean => {
  return (
    typeof data.title === "string" &&
    ["SESSION", "WORKSHOP", "TUTORIAL"].includes(data.type) &&
    Array.isArray(data.tags) &&
    data.tags.every((tag) => typeof tag === "string") &&
    typeof data.description === "string" &&
    Array.isArray(data.presentations) &&
    data.presentations.every(
      (presentation) =>
        typeof presentation.title === "string" &&
        typeof presentation.description === "string" &&
        Array.isArray(presentation.presenters) &&
        presentation.presenters.every(
          (presenter) =>
            typeof presenter.orcid === "string" &&
            typeof presenter.email === "string" &&
            typeof presenter.isSpeaker === "boolean"
        )
    )
  );
};

const FileUpload = () => {
  const [open, setOpen] = useState(false);
  const [isDragging, setIsDragging] = useState(false);
  const [uploadedFile, setUploadedFile] = useState<UploadedFile | null>(null);
  const [error, setError] = useState<string | null>(null);
  const inputRef = useRef<HTMLInputElement>(null);

  const { apiClient, useApiMutation } = useApi();
  const queryClient = useQueryClient();

  const createApplicationMutation = useApiMutation<
    ApplicationPreviewResponse,
    CreateApplicationRequest
  >((request) => apiClient.application.createApplication(request), {
    onSuccess: (newApplication) => {
      queryClient.setQueryData<ApplicationPreviewResponse[]>(
        ["applications"],
        (oldApplications) => [...(oldApplications || []), newApplication]
      );
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

    if (file.type !== "application/json") {
      setError("Please upload a JSON file.");
      return;
    }

    try {
      const fileContent = await file.text();
      const jsonData = JSON.parse(fileContent);

      if (verifyApplicationJsonStructure(jsonData)) {
        setUploadedFile({ name: file.name, status: "success", jsonData });
      } else {
        setUploadedFile({ name: file.name, status: "error", jsonData: null });
        setError(
          "Invalid JSON structure. Please check your file and try again."
        );
      }
    } catch (err) {
      setUploadedFile({ name: file.name, status: "error", jsonData: null });
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
    setOpen(false);
    setUploadedFile(null);

    const applicationData: CreateApplicationRequest = {
      title: uploadedFile.jsonData.title,
      type: uploadedFile.jsonData.type,
      tags: uploadedFile.jsonData.tags,
      description: uploadedFile.jsonData.description,
      presentations: uploadedFile.jsonData.presentations,
      saveAsDraft: true
    };

    createApplicationMutation.mutate(applicationData);
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
      <DialogContent className="sm:max-w-md">
        <DialogHeader>
          <DialogTitle>Upload File</DialogTitle>
          <DialogDescription>
            Drag and drop file with your application information.
          </DialogDescription>
        </DialogHeader>
        <div
          onDragOver={handleDragOver}
          onDragLeave={handleDragLeave}
          onDrop={handleDrop}
          onClick={() => inputRef.current?.click()}
          className={`
            mt-4 cursor-pointer rounded-lg border-2 border-dashed p-10
            text-center transition-colors
            hover:bg-muted/50
            ${
              isDragging
                ? "border-primary bg-muted/50"
                : "border-muted-foreground/25"
            }
          `}
        >
          <div className="flex flex-col items-center gap-4">
            <UploadCloud className="h-10 w-10 text-muted-foreground" />
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
        {uploadedFile && (
          <div className="mt-4 flex items-center justify-between rounded-md border p-2">
            <span className="truncate">{uploadedFile.name}</span>
            <div className="flex items-center gap-2">
              {uploadedFile.status === "success" && (
                <Check className="text-green-500" />
              )}
              {uploadedFile.status === "error" && (
                <X className="text-red-500" />
              )}
              <Button variant="ghost" size="sm" onClick={handleDelete}>
                <X className="h-4 w-4" />
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
