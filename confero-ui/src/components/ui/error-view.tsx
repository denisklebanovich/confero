'use client'

import { AlertCircle, Home, RefreshCcw } from 'lucide-react'
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardFooter, CardHeader, CardTitle } from "@/components/ui/card"

interface ErrorViewProps {
    title?: string
    message?: string
    onRetry?: () => void
}

export default function ErrorView({
                                              title = "Oops! Something went wrong",
                                              message = "We're sorry, but we encountered an unexpected error. Please try again or return to the home page.",
                                              onRetry
                                          }: ErrorViewProps = {}) {
    return (
        <div className="flex items-center justify-center min-h-screen bg-gradient-to-br from-gray-100 to-gray-200 p-4">
            <Card className="w-full max-w-lg overflow-hidden">
                <CardHeader className="bg-gradient-to-r from-red-500 to-pink-500 text-white">
                    <CardTitle className="flex items-center gap-2 text-2xl font-bold">
                        <AlertCircle className="h-6 w-6" />
                        {title}
                    </CardTitle>
                </CardHeader>
                <CardContent className="p-6">
                    <div className="flex flex-col items-center space-y-4">
                        <div className="flex items-center justify-center">
                            <div className="w-48 h-48 bg-red-100 rounded-full flex items-center justify-center">
                                <svg className="w-32 h-32 text-red-500" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                                </svg>
                            </div>
                        </div>
                        <p className="text-center text-gray-600 mt-4">{message}</p>
                    </div>
                </CardContent>
                <CardFooter className="flex justify-center space-x-4 bg-gray-50 p-6">
                    {onRetry && (
                        <Button variant="outline" onClick={onRetry} className="w-full sm:w-auto">
                            <RefreshCcw className="mr-2 h-4 w-4" />
                            Try Again
                        </Button>
                    )}
                    <Button asChild className="w-full sm:w-auto">
                        <a href="/">
                            <Home className="mr-2 h-4 w-4" />
                            Back to Home
                        </a>
                    </Button>
                </CardFooter>
            </Card>
        </div>
    )
}