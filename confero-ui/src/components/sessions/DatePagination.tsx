'use client'

import {useState} from 'react'
import {Button} from "@/components/ui/button"
import {ChevronLeft, ChevronRight} from 'lucide-react'
import {format, addDays, subDays} from 'date-fns'

interface DatePaginationProps {
    onDateChange: (date: Date) => void
}

export function DatePagination({onDateChange}: DatePaginationProps) {
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

    const renderDateDisplay = (date: Date, isSelected: boolean) => (
        <div className={`flex-1 text-center ${isSelected ? 'font-bold' : ''}`}>
            <div className="flex flex-col items-center">
                <span className="text-sm">{format(date, 'EEE')}</span>
                <span className="text-lg">{format(date, 'd')}</span>
                <span className="text-xs">{format(date, 'MMM')}</span>
            </div>
        </div>
    )

    return (
        <div className="flex items-center justify-center space-x-2 w-full max-w-md">
            <Button variant="outline" size="icon" onClick={handlePreviousDay} aria-label="Previous day">
                <ChevronLeft className="h-4 w-4"/>
            </Button>
            <div className="flex-1 flex justify-between">
                {renderDateDisplay(subDays(currentDate, 1), false)}
                {renderDateDisplay(currentDate, true)}
                {renderDateDisplay(addDays(currentDate, 1), false)}
            </div>
            <Button variant="outline" size="icon" onClick={handleNextDay} aria-label="Next day">
                <ChevronRight className="h-4 w-4"/>
            </Button>
        </div>
    )
}
