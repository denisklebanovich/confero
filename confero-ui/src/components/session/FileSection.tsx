import { X } from "lucide-react";
import { ScrollArea } from "@/components/ui/scrollarea";
import { Button } from "@/components/ui/button";
import { Card } from "../ui/card";

export type File = {
  name: string;
  type: string;
};

export type Section = {
  name: string;
  files: File[];
  canDelete?: boolean;
  canUpload?: boolean;
};

type FileSectionProps = {
  section: Section;
};

const FileSection = ({ section }: FileSectionProps) => {
  return (
    <Card className="p-2">
      <div className="flex justify-between items-center mb-1">
        <h2 className="font-semibold mb-2">{section.name}</h2>
        {section.canUpload && (
          <Button className="w-fit">Upload files</Button>
        )}
      </div>
      <ScrollArea
        className={`w-full p-4 ${
          section.files.length > 2 ? "max-h-[100px] overflow-y-auto" : "h-fit"
        }`}
      >
        {section.files.map((file) => (
          <div
            key={file.name}
            className="flex justify-between items-center py-2"
          >
            <span className="text-sm">{file.name}</span>
            {section.canDelete && (
              <Button variant="ghost" size="icon" className="h-6 w-6">
                <X className="h-4 w-4" />
                <span className="sr-only">Delete</span>
              </Button>
            )}
          </div>
        ))}
      </ScrollArea>
    </Card>
  );
};

export default FileSection;
