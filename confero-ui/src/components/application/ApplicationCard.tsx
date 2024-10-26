import { Calendar } from "lucide-react"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"

export default function ApplicationCard() {
    return (
        <Card className="max-w-md cursor-pointer">
            <CardHeader>
                <CardTitle className="text-xl font-semibold">Computer Vision and Intelligent systems</CardTitle>
            </CardHeader>
            <CardContent className="grid gap-4">
                <div className="text-sm">
                    <span className="font-semibold">Organizers:</span> Van-Dung Hoang, Dinh-Hien Nguyen, Huyen Trang Phan and
                    Kang-Hyun Jo
                </div>
                <div className="flex items-center justify-between">
                    <div className="flex items-center text-sm text-muted-foreground">
                        <Calendar className="mr-1 h-4 w-4" />
                        December 2024
                    </div>
                    <Badge variant="outline" className="bg-yellow-100 text-yellow-800 hover:bg-yellow-100">
                        Pending
                    </Badge>
                </div>
            </CardContent>
        </Card>
    )
}