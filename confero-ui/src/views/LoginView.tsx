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
import {Form, FormControl, FormField, FormItem, FormLabel, FormMessage} from "@/components/ui/form.tsx";
import {Input} from "@/components/ui/input.tsx";
import {zodResolver} from "@hookform/resolvers/zod";
import {useForm} from "react-hook-form";
import {z} from "zod";
import {useToast} from "@/hooks/use-toast.ts";


export const handleOrcidLogin = async () => {
    try {
        window.location.href = '/api/auth/orcid/login';
    } catch (error) {
        console.error('Error logging in with ORCID:', error)
        alert('Error logging in with ORCID. Please try again.')
    }
}

const formSchema = z.object({
    email: z.string().email({message: "Invalid email address"}),
    password: z.string().min(8, {message: "Password must be at least 8 characters"}),
})


export default function LoginView() {
    const [loading, setLoading] = useState(false)
    const navigate = useNavigate()
    const {user} = useAuth()
    const {toast} = useToast()


    console.log("import.meta.env", import.meta.env)
    console.log('Redirect URL:', import.meta.env.PROD ? 'https://confero.club' : 'http://localhost:5173');

    const form = useForm<z.infer<typeof formSchema>>({
        resolver: zodResolver(formSchema),
        defaultValues: {
            email: "",
            password: "",
        },
    })

    useEffect(() => {
        const params = new URLSearchParams(window.location.search);
        const accessToken = params.get('orcid_access_token');
        if (accessToken) {
            localStorage.setItem('orcid_access_token', accessToken);
            navigate('/');
        }
        if (user) {
            navigate('/')
        }
    }, [user, navigate])

    const handleGoogleLogin = async () => {
        try {
            setLoading(true)
            const {error} = await supabase.auth.signInWithOAuth({
                provider: 'google',
                options: {
                    redirectTo: import.meta.env.PROD ? 'https://confero.club' : 'http://localhost:5173'
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
            console.log("import.meta.env", import.meta.env)
            const {error} = await supabase.auth.signInWithOAuth({
                provider: 'linkedin_oidc',
                options: {
                    redirectTo: import.meta.env.PROD ? 'https://confero.club' : 'http://localhost:5173'
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

    const handleLogin = async (email: string, password: string) => {
        try {
            setLoading(true)
            const {error} = await supabase.auth.signInWithPassword({
                email,
                password,
            })
            if (error) throw error
            navigate('/')
        } catch (error) {
            toast({
                title: 'Error logging in',
                description: 'Invalid email or password. Please try again.',
                variant: 'destructive'
            })
        } finally {
            setLoading(false)
        }
    }

    return (
        <div className={"w-screen h-screen"}>
            <div className="text-7xl font-bold text-center text-primary cursor-pointer"
                 onClick={() => navigate("/")}>Confero
            </div>
            <div className="h-0.5 bg-gray-200 w-1/2 mx-auto mt-2 mb-5"/>
            <Card className="w-[500px] mx-auto mt-2">
                <CardHeader>
                    <CardTitle>Login</CardTitle>
                </CardHeader>
                <CardContent className="space-y-2">
                    <CardDescription>
                        Login to access your account
                    </CardDescription>
                    <Form {...form}>
                        <form
                            className='flex flex-col items-center w-full gap-3'
                            onSubmit={form.handleSubmit(({email, password}) => handleLogin(email, password))}>
                            <FormField
                                control={form.control}
                                name="email"
                                render={({field}) => (
                                    <FormItem className='w-full'>
                                        <FormLabel>Email</FormLabel>
                                        <FormControl>
                                            <Input placeholder="Enter your email" {...field} />
                                        </FormControl>
                                        <FormMessage/>
                                    </FormItem>
                                )}
                            />
                            <FormField
                                control={form.control}
                                name="password"
                                render={({field}) => (
                                    <FormItem className='w-full'>
                                        <FormLabel>Password</FormLabel>
                                        <FormControl>
                                            <Input type="password" placeholder="Enter your password" {...field} />
                                        </FormControl>
                                        <FormMessage/>
                                    </FormItem>
                                )}
                            />
                            <div className="text-sm text-primary cursor-pointer"
                                 onClick={() => navigate('/signup')}>
                                Don't have an account? Sign up
                            </div>
                            <Button type="submit" className="w-full" disabled={loading}>
                                {loading ? 'Logging in...' : 'Login'}
                            </Button>
                        </form>
                    </Form>
                    <div className="flex flex-col items-center gap-3">
                        <div className="text-sm text-gray-500">Or</div>
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
                    </div>
                </CardContent>
            </Card>
            <Footer/>
        </div>
    )
}