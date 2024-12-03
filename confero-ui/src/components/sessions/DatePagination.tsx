'use client'

import { useState } from 'react'
import { Button } from "@/components/ui/button"
import { ChevronLeft, ChevronRight } from 'lucide-react'
import { format, addDays, subDays } from 'date-fns'

interface DatePaginationProps {
    onDateChange: (date: Date) => void
}

export function DatePagination({ onDateChange }: DatePaginationProps) {
    const [currentDate, setCurrentDate] = useState(new Date())

    const handlePreviousDay = () => {
        const newDate = subDays(currentDate, 1)
        setCurrentDate(newDate)
        onDateChange(newDate)
    }

    const handleNextDay = () => {
        const newDate = addDays(currentDate, 1)
        setCurrentDate(newDate)
        onDateChange(newDate)
    }

    return (
        <div className="flex items-center justify-center space-x-4">
            <Button variant="outline" size="icon" onClick={handlePreviousDay}>
                <ChevronLeft className="h-4 w-4" />
            </Button>
            <div className="text-lg font-semibold">
                {format(currentDate, 'dd MMM yyyy')}
            </div>
            <Button variant="outline" size="icon" onClick={handleNextDay}>
                <ChevronRight className="h-4 w-4" />
            </Button>
        </div>
    )
}

