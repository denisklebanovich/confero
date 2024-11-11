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

interface UploadedFile {
  name: string;
  status: "uploading" | "success" | "error";
}

const verifyJsonStructure = (file: File): Promise<boolean> => {
  return new Promise((resolve) => {
    const reader = new FileReader();
    reader.onload = (e) => {
      try {
        const jsonData = JSON.parse(e.target?.result as string);

        const isValid =
          typeof jsonData.title === "string" &&
          (jsonData.type === "SESSION" ||
            jsonData.type === "WORKSHOP" ||
            jsonData.type === "TUTORIAL") &&
          Array.isArray(jsonData.tags) &&
          jsonData.tags.every((tag) => typeof tag === "string") &&
          typeof jsonData.description === "string" &&
          Array.isArray(jsonData.presentations) &&
          jsonData.presentations.every(
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
          );
        resolve(isValid);
      } catch (error) {
        resolve(false);
      }
    };
    reader.onerror = () => resolve(false);
    reader.readAsText(file);
  });
};

const FileUpload = () => {
  const [open, setOpen] = useState(false);
  const [isDragging, setIsDragging] = useState(false);
  const [uploadedFile, setUploadedFile] = useState<UploadedFile | null>(null);
  const [error, setError] = useState<string | null>(null);
  const inputRef = useRef<HTMLInputElement>(null);

  const handleDragOver = (e: DragEvent) => {
    e.preventDefault();
    setIsDragging(true);
  };

  const handleDragLeave = (e: DragEvent) => {
    e.preventDefault();
    setIsDragging(false);
  };

  const handleDrop = (e: DragEvent) => {
    e.preventDefault();
    setIsDragging(false);
    const files = Array.from(e.dataTransfer.files);
    handleFiles(files);
  };

  const handleFileInput = (e: ChangeEvent<HTMLInputElement>) => {
    const files = e.target.files ? Array.from(e.target.files) : [];
    handleFiles(files);
  };

  const handleFiles = async (files: File[]) => {
    setError(null);
    if (files.length === 0) return;

    const file = files[0];

    if (file.type !== "application/json") {
      setError("Please upload a JSON file.");
      return;
    }

    setUploadedFile({ name: file.name, status: "uploading" });

    try {
      const isValid = await verifyJsonStructure(file);
      if (isValid) {
        setUploadedFile({ name: file.name, status: "success" });
      } else {
        setUploadedFile({ name: file.name, status: "error" });
        setError(
          "Invalid JSON structure. Please check your file and try again."
        );
      }
    } catch (err) {
      setUploadedFile({ name: file.name, status: "error" });
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
  };

  return (
    <Dialog open={open} onOpenChange={setOpen}>
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
              {uploadedFile.status === "uploading" && <span>Uploading...</span>}
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
