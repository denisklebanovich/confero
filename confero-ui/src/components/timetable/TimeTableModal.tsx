import { Button } from "@/components/ui/button"
import {
    Dialog,
    DialogContent,
    DialogHeader,
    DialogTitle
} from "@/components/ui/dialog"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Textarea } from "@/components/ui/textarea"

export default function TimeTableModal({open, setOpen, selectedEvent, setSelectedEvent, setPresentations}) {

    return (
        <Dialog open={open} onOpenChange={setOpen}>
            <DialogContent className="max-w-[625px] h-3/5">
                <DialogHeader>
                    <DialogTitle className="text-left">Title</DialogTitle>
                </DialogHeader>
                <div className="grid gap-4 py-4">
                    <div className="grid gap-2">
                        <Input
                            id="title"
                            value={selectedEvent.title}
                            onChange={(e) => setSelectedEvent({...selectedEvent, title: e.target.value})}
                            className="w-full"
                        />
                    </div>
                    <div className="grid gap-2">
                        <Label htmlFor="description" className="text-left">
                            Description
                        </Label>
                        <Textarea
                            id="description"
                            value={selectedEvent.description}
                            onChange={(e) => setSelectedEvent({...selectedEvent, description: e.target.value})}
                            className="min-h-[150px]"
                        />
                    </div>
                </div>
                <Button
                    className="w-20 mx-auto mb-4"
                    onClick={() => {
                        setOpen(false)
                        setPresentations(prev => {
                            const presentationsCopy = [...prev];
                            const presentationCopy = presentationsCopy.find(presentation => presentation.internal_id === selectedEvent.internal_id);
                            presentationCopy.title  = selectedEvent.title;
                            presentationCopy.description = selectedEvent.description;
                            return presentationsCopy});
                    }}
                >
                    Save
                </Button>
            </DialogContent>
        </Dialog>
    )
}