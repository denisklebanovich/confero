import { CalendarIcon, Upload, X } from "lucide-react";
import {useEffect, useState} from "react";
import { Button } from "@/components/ui/button";
import {
    Dialog,
    DialogContent,
    DialogHeader,
    DialogTitle,
} from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { cn } from "@/lib/utils";
import {Card} from "@/components/ui/card.tsx";
import {useToast} from "@/hooks/use-toast.ts";


export default function EditionModal({ open, setOpen, date, setDate, index = -1, setIndex }) {
    const [dragActive, setDragActive] = useState(false);
    const [file, setFile] = useState(null);

    const handleDragOver = (e) => {
        e.preventDefault();
        setDragActive(true);
    };

    const handleDragLeave = (e) => {
        e.preventDefault();
        setDragActive(false);
    };

    const handleDrop = (e) => {
        e.preventDefault();
        setDragActive(false);
        const droppedFile = e.dataTransfer.files[0];
        handleFileSelection(droppedFile);
    };

    const handleFileSelection = (selectedFile) => {
        setFile(selectedFile);
    };

    const handleFileInputChange = (e) => {
        const selectedFile = e.target.files[0];
        handleFileSelection(selectedFile);
    };

    const handleSave = () => {
        setOpen(false);
        showNotification(false);
        // showNotification(true);
    };

    const { toast } = useToast();

    const showNotification = (isError) => {
        const variant = isError ? "error" : "success";

        const title = isError ? "Error occurred" : "Success!";

        toast({
            variant: variant as any,
            title: title,
        });
    };

    useEffect(() => {
        if(!open){
            setFile(null);
            setDate("");
            setIndex(-1);
        }
    }, [open]);

    return (
        <Dialog open={open} onOpenChange={setOpen}>
            <DialogContent className="sm:max-w-[425px]">
                <DialogHeader>
                    <DialogTitle className="text-xl font-bold">{index === -1 ? "New Edition" : `Edition #${index}`}</DialogTitle>
                </DialogHeader>
                <div className="grid gap-6 py-4 w-full">
                    <div className="grid gap-2 w-full">
                        <Label htmlFor="deadline">Applications&apos; deadline</Label>
                        <div className="relative w-full flex items-center justify-center">
                            <Input
                                id="deadline"
                                type="date"
                                className="pl-10 bg-gray-500 text-white w-full"
                                value={date}
                                onChange={(e) => setDate(e.target.value)}
                            />
                            <CalendarIcon className="absolute left-2.5 top-2 h-6 w-6 text-muted-foreground text-white " />
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
                                <Upload className="h-8 w-8 text-muted-foreground" />
                                <p className="text-sm text-muted-foreground">
                                    Drag and drop file
                                </p>
                                <p className="text-xs text-muted-foreground">or</p>
                                <Button
                                    variant="secondary"
                                    size="sm"
                                    onClick={() => document.getElementById("file-upload")?.click()}
                                >
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
                            <Card className="bg-white/50 mt-2  p-3 shadow-sm">
                                <h1 className="text-l font-medium ">{file.name}</h1>
                            </Card>
                        )}
                    </div>
                    <Button className="w-full" size="lg" onClick={handleSave}>
                        Save
                    </Button>

                </div>
            </DialogContent>
        </Dialog>
    );
}
