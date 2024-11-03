import React, {createContext, useContext, useEffect, useState} from 'react'
import {Session, User} from '@supabase/supabase-js'
import {useNavigate} from "react-router-dom"
import {supabase} from "@/auth/supabaseClient.ts";

interface AuthContextType {
    user: User | null
    session: Session | null,
    orcidAccessToken: string | null,
    signOut: () => Promise<void>
}

function getCookie(name: string) {
    const value = `; ${document.cookie}`;
    const parts = value.split(`; ${name}=`);
    if (parts.length === 2) return parts.pop()?.split(';').shift() ?? null;
    return null;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined)

export function AuthProvider({children}: { children: React.ReactNode }) {
    const [user, setUser] = useState<User | null>(null)
    const [session, setSession] = useState<Session | null>(null)
    const [orcidAccessToken, setOrcidAccessToken] = useState<string | null>(null)
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

            const token = getCookie('orcid_access_token');
            setOrcidAccessToken(token);

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
        setOrcidAccessToken(null);
        navigate('/login')
    }

    const value = {
        user,
        session,
        orcidAccessToken,
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