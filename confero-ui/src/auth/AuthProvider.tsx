import React, {createContext, useContext, useEffect, useState} from 'react'
import {Session, User} from '@supabase/supabase-js'
import {useNavigate} from "react-router-dom"
import {supabase} from "@/auth/supabaseClient.ts";

interface AuthContextType {
    user: User | null
    session: Session | null
    signOut: () => Promise<void>
}

const AuthContext = createContext<AuthContextType | undefined>(undefined)

export function AuthProvider({children}: { children: React.ReactNode }) {
    const [user, setUser] = useState<User | null>(null)
    const [session, setSession] = useState<Session | null>(null)
    const [loading, setLoading] = useState(true)
    const navigate = useNavigate()

    useEffect(() => {
        const setData = async () => {
            const {data: {session}, error} = await supabase.auth.getSession()
            if (error) {
                console.error(error)
                return
            }
            setSession(session)
            setUser(session?.user ?? null)
            setLoading(false)
        }

        const {data: {subscription}} = supabase.auth.onAuthStateChange((_event, session) => {
            setSession(session)
            setUser(session?.user ?? null)
            setLoading(false)
        })

        setData()

        return () => {
            subscription.unsubscribe()
        }
    }, [])

    const signOut = async () => {
        const {error} = await supabase.auth.signOut()
        if (error) throw error
        navigate('/')
    }

    const value = {
        user,
        session,
        signOut
    }

    return (
        <AuthContext.Provider value={value}>
            {!loading && children}
        </AuthContext.Provider>
    )
}

export function useAuth() {
    const context = useContext(AuthContext)
    if (context === undefined) {
        throw new Error('useAuth must be used within an AuthProvider')
    }
    return context
}