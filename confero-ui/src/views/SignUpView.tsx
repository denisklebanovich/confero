import {useState} from 'react'
import {useNavigate} from "react-router-dom"
import {useForm} from "react-hook-form"
import {zodResolver} from "@hookform/resolvers/zod"
import * as z from "zod"
import {Button} from "@/components/ui/button"
import {Card, CardContent, CardDescription, CardHeader, CardTitle} from "@/components/ui/card"
import {Form, FormControl, FormField, FormItem, FormLabel, FormMessage} from "@/components/ui/form"
import {Input} from "@/components/ui/input"
import Footer from "@/components/layout/Footer.tsx"
import {supabase} from "@/auth/supabaseClient.ts"
import {useToast} from "@/hooks/use-toast.ts";

const formSchema = z.object({
    email: z.string().email({message: "Invalid email address"}),
    password: z.string().min(8, {message: "Password must be at least 8 characters"}),
    confirmPassword: z.string(),
}).refine((data) => data.password === data.confirmPassword, {
    message: "Passwords don't match",
    path: ["confirmPassword"],
})

export default function SignUpView() {
    const [loading, setLoading] = useState(false)
    const navigate = useNavigate()
    const {toast} = useToast()

    const form = useForm<z.infer<typeof formSchema>>({
        resolver: zodResolver(formSchema),
        defaultValues: {
            email: "",
            password: "",
            confirmPassword: "",
        },
    })

    const onSubmit = async (values: z.infer<typeof formSchema>) => {
        try {
            setLoading(true)
            const {error} = await supabase.auth.signUp({
                email: values.email,
                password: values.password,
            })
            if (error) throw error
            toast({
                title: 'Account created',
                description: 'Please check your email for a verification link.',
                variant: 'success',
            })
            navigate('/login')
        } catch (error) {
            console.error('Error signing up:', error)
            toast({
                title: 'Error signing up',
                description: error.message,
                variant: 'error',
            })
        } finally {
            setLoading(false)
        }
    }

    return (
        <div className="w-screen h-screen">
            <div className="text-7xl font-bold text-center text-primary cursor-pointer"
                 onClick={() => navigate("/")}>Confero
            </div>
            <div className="h-0.5 bg-gray-200 w-1/2 mx-auto mt-2 mb-20"/>
            <Card className="w-[500px] mx-auto mt-32">
                <CardHeader>
                    <CardTitle>Sign Up</CardTitle>
                    <CardDescription>
                        Create a new account
                    </CardDescription>
                </CardHeader>
                <CardContent className="space-y-4">
                    <Form {...form}>
                        <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
                            <FormField
                                control={form.control}
                                name="email"
                                render={({field}) => (
                                    <FormItem>
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
                                    <FormItem>
                                        <FormLabel>Password</FormLabel>
                                        <FormControl>
                                            <Input type="password" placeholder="Create a password" {...field} />
                                        </FormControl>
                                        <FormMessage/>
                                    </FormItem>
                                )}
                            />
                            <FormField
                                control={form.control}
                                name="confirmPassword"
                                render={({field}) => (
                                    <FormItem>
                                        <FormLabel>Confirm Password</FormLabel>
                                        <FormControl>
                                            <Input type="password" placeholder="Confirm your password" {...field} />
                                        </FormControl>
                                        <FormMessage/>
                                    </FormItem>
                                )}
                            />
                            <Button type="submit" className="w-full" disabled={loading}>
                                {loading ? 'Signing up...' : 'Sign Up'}
                            </Button>
                        </form>
                    </Form>
                    <div className="text-center text-sm">
                        Already have an account?{' '}
                        <Button variant="link" className="p-0" onClick={() => navigate('/login')}>
                            Log in
                        </Button>
                    </div>
                </CardContent>
            </Card>
            <Footer/>
        </div>
    )
}