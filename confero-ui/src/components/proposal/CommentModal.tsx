import {useState} from "react"
import {Button} from "@/components/ui/button"
import {
    Dialog,
    DialogContent,
    DialogDescription,
    DialogFooter,
    DialogHeader,
    DialogTitle,
} from "@/components/ui/dialog"
import {Textarea} from "@/components/ui/textarea"
import {Label} from "@/components/ui/label"

interface CommentModalProps {
    isOpen: boolean
    setIsOpen: (open: boolean) => void
    onSubmit: (comment: string) => void
}

export default function CommentModal({isOpen, setIsOpen, onSubmit}: CommentModalProps) {
    const [comment, setComment] = useState("")

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault()
        onSubmit(comment)
        setComment("")
        setIsOpen(false)
    }

    return (
        <Dialog open={isOpen} onOpenChange={setIsOpen}>
            <DialogContent className="sm:max-w-[425px]">
                <DialogHeader>
                    <DialogTitle>Add Comment</DialogTitle>
                    <DialogDescription>
                        Add your comment or feedback for the application. Click send when you're done.
                    </DialogDescription>
                </DialogHeader>
                <form onSubmit={handleSubmit}>
                    <div className="grid gap-4 py-4">
                        <div className="grid gap-2">
                            <Label htmlFor="comment">Comment</Label>
                            <Textarea
                                id="comment"
                                value={comment}
                                onChange={(e) => setComment(e.target.value)}
                                placeholder="Type your comment here."
                                className="h-40"
                            />
                        </div>
                    </div>
                    <DialogFooter>
                        <Button type="button" variant="secondary" onClick={() => setIsOpen(false)}>
                            Cancel
                        </Button>
                        <Button type="submit">Send for adjustments</Button>
                    </DialogFooter>
                </form>
            </DialogContent>
        </Dialog>
    )
}