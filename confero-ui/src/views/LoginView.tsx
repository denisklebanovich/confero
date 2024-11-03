/// <reference types="vite-plugin-svgr/client" />
import {useEffect, useState} from 'react'
import {Button} from "@/components/ui/button"
import {Card, CardContent, CardDescription, CardHeader, CardTitle} from "@/components/ui/card"
import Footer from "@/components/layout/Footer.tsx"
import GoogleIcon from "@/assets/google.svg?react"
import LinkedInIcon from "@/assets/linkedin.svg?react"
import OrcidIcon from "@/assets/orcid.svg?react"
import {useNavigate} from "react-router-dom"
import {useAuth} from "@/auth/AuthProvider.tsx";
import {supabase} from "@/auth/supabaseClient.ts";


const ORCID_AUTH_URL = 'https://b252-77-254-238-252.ngrok-free.app/api/auth/orcid/login'

export default function LoginView() {
    const [loading, setLoading] = useState(false)
    const navigate = useNavigate()
    const {user} = useAuth()

    useEffect(() => {
        if (user) {
            navigate('/')
        }
    }, [user, navigate])

    const handleOrcidLogin = async () => {
        try {
            window.location.href = ORCID_AUTH_URL
        } catch (error) {
            console.error('Error logging in with ORCID:', error)
            alert('Error logging in with ORCID. Please try again.')
        } finally {
            setLoading(false)
        }
    }

    const handleGoogleLogin = async () => {
        try {
            setLoading(true)
            const {error} = await supabase.auth.signInWithOAuth({
                provider: 'google',
                options: {
                    redirectTo: '/'
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
                    redirectTo: '/'
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
        <div className={"w-screen h-screen"}>
            <div className="text-7xl font-bold text-center text-primary cursor-pointer"
                 onClick={() => navigate("/")}>Confero
            </div>
            <div className="h-0.5 bg-gray-200 w-1/2 mx-auto mt-2 mb-20"/>
            <Card className="w-[350px] mx-auto mt-32">
                <CardHeader>
                    <CardTitle>Login</CardTitle>
                    <CardDescription>Choose a method to log in</CardDescription>
                </CardHeader>
                <CardContent className="space-y-4">
                    <Button
                        variant='secondary'
                        className="w-full"
                        onClick={handleOrcidLogin}
                    >
                        <OrcidIcon/> Login with ORCID
                    </Button>
                    <Button
                        variant='secondary'
                        className="w-full"
                        onClick={handleGoogleLogin}
                        disabled={loading}
                    >
                        <GoogleIcon/> {loading ? 'Loading...' : 'Login with Google'}
                    </Button>
                    <Button
                        variant='secondary'
                        className="w-full"
                        onClick={handleLinkedInLogin}
                        disabled={loading}
                    >
                        <LinkedInIcon/> {loading ? 'Loading...' : 'Login with LinkedIn'}
                    </Button>
                </CardContent>
            </Card>
            <Footer/>
        </div>
    )
}