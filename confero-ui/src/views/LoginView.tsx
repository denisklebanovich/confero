'use client'

import {useState} from 'react'
import {Button} from "@/components/ui/button"
import {Card, CardContent, CardDescription, CardHeader, CardTitle} from "@/components/ui/card"
import {Linkedin} from 'lucide-react'
import {supabase} from "@/service/supabaseClient.ts";

export default function LoginView() {
    const [loading, setLoading] = useState(false)

    const handleGoogleLogin = async () => {
        try {
            setLoading(true)
            const {error} = await supabase.auth.signInWithOAuth({
                provider: 'google',
                options: {
                    redirectTo: `${window.location.origin}/auth/callback`,
                },
            })
            if (error) throw error
        } catch (error) {
            console.error('Error logging in with Google:', error)
            alert('Error logging in with Google. Please try again.')
        } finally {
            setLoading(false)
        }
    }

    const handleLinkedInLogin = async () => {
        try {
            setLoading(true)
            const {error} = await supabase.auth.signInWithOAuth({
                provider: 'linkedin_oidc',
                options: {
                    redirectTo: `${window.location.origin}/auth/callback`,
                },
            })
            if (error) throw error
        } catch (error) {
            console.error('Error logging in with LinkedIn:', error)
            alert('Error logging in with LinkedIn. Please try again.')
        } finally {
            setLoading(false)
        }
    }

    return (
        <div>
            <div className="text-7xl font-bold text-center text-primary">confero</div>
            <div className="h-0.5 bg-gray-300 w-1/2 mx-auto mt-2 mb-20"/>
            <Card className="w-[350px] mx-auto mt-10">
                <CardHeader>
                    <CardTitle>Login</CardTitle>
                    <CardDescription>Choose a method to log in</CardDescription>
                </CardHeader>
                <CardContent className="space-y-4">
                    <Button
                        className="w-full"
                        onClick={handleGoogleLogin}
                        disabled={loading}
                    >
                        {loading ? 'Loading...' : 'Login with Google'}
                    </Button>
                    <Button
                        className="w-full"
                        onClick={handleLinkedInLogin}
                        disabled={loading}
                    >
                        <Linkedin className="mr-2 h-4 w-4"/>
                        Login with LinkedIn
                    </Button>
                </CardContent>
            </Card>
        </div>
    )
}