import { Calendar } from "lucide-react"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"

interface ApplicationCardProps {
    onClick?: () => void;
    title: string;
    organisers: string[];
    date: Date;
    status: string;
}

export default function ApplicationCard({ title, organisers, onClick,date, status }: ApplicationCardProps) {
    const formattedDate = date.toLocaleString('en-US', { year: 'numeric', month: 'long' });

    const statusColors = {
        Accepted: "bg-green-100 text-green-800 hover:bg-green-200",
        Rejected: "bg-red-100 text-red-800 hover:bg-red-200",
        Draft: "bg-blue-100 text-blue-800 hover:bg-blue-200",
        Pending: "bg-yellow-100 text-yellow-800 hover:bg-yellow-200",
    };

    return (
        <Card onClick={onClick} className="max-w-md cursor-pointer">
            <CardHeader>
                <CardTitle className="text-xl font-semibold">{title}</CardTitle>
            </CardHeader>
            <CardContent className="grid gap-4">
                <div className="text-sm">
                    <span className="font-semibold">Organizers:</span> {organisers.join(", ")}
                </div>
                <div className="flex items-center justify-between">
                    <div className="flex items-center text-sm text-muted-foreground">
                        <Calendar className="mr-1 h-4 w-4" />
                        {formattedDate}
                    </div>
                    <Badge variant="outline"  className={statusColors[status]}>
                        {status}
                    </Badge>
                </div>
            </CardContent>
        </Card>
    )
}