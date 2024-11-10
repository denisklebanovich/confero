import { Button } from "@/components/ui/button"
import {
    Dialog,
    DialogContent,
    DialogHeader,
    DialogTitle,
    DialogTrigger,
} from "@/components/ui/dialog"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Textarea } from "@/components/ui/textarea"

export default function TimeTableModal({open, setOpen, title, setTitle, description, setDescription}) {

    return (
        <Dialog open={open} onOpenChange={setOpen}>
            <DialogContent className="max-w-[625px] h-1/2">
                <DialogHeader>
                    <DialogTitle className="text-left">Title</DialogTitle>
                </DialogHeader>
                <div className="grid gap-4 py-4">
                    <div className="grid gap-2">
                        <Input
                            id="title"
                            value={title}
                            onChange={(e) => setTitle(e.target.value)}
                            className="w-full"
                        />
                    </div>
                    <div className="grid gap-2">
                        <Label htmlFor="description" className="text-left">
                            Description
                        </Label>
                        <Textarea
                            id="description"
                            value={description}
                            onChange={(e) => setDescription(e.target.value)}
                            className="min-h-[150px]"
                        />
                    </div>
                </div>
                <Button
                    className="w-20 mx-auto mb-4"
                    onClick={() => {
                        setOpen(false)
                    }}
                >
                    Save
                </Button>
            </DialogContent>
        </Dialog>
    )
}