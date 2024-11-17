import React, {createContext, useContext, useEffect, useState} from 'react'
import {Session, User} from '@supabase/supabase-js'
import {useNavigate} from "react-router-dom"
import {supabase} from "@/auth/supabaseClient.ts";
import {useApi} from "@/api/useApi.ts";

interface AuthContextType {
    user: User | null
    session: Session | null,
    orcidAccessToken: string | null,
    authorized: boolean
    signOut: () => Promise<void>
}

const AuthContext = createContext<AuthContextType | undefined>(undefined)

export function AuthProvider({children}: { children: React.ReactNode }) {
    const [user, setUser] = useState<User | null>(null)
    const [session, setSession] = useState<Session | null>(null)
    const [orcidAccessToken, setOrcidAccessToken] = useState<string | null>(null)
    const [loading, setLoading] = useState(true)
    const [authorized, setAuthorized] = useState(false)
    const navigate = useNavigate()

    useEffect(() => {
        const setData = async () => {
            try{
                const {data: {session}, error} = await supabase.auth.getSession()
                if (error) {
                    console.error(error)
                    return
                }
                setSession(session)
                setUser(session?.user ?? null)
                const token = localStorage.getItem('orcid_access_token')
                setOrcidAccessToken(token);
                setAuthorized(!!session?.user || !!token)
            }
            catch (error) {
                console.error(error)
            }
            finally {
                setLoading(false)
            }
        }

        const {data: {subscription}} = supabase.auth.onAuthStateChange((_event, session) => {
            setSession(session)
            setUser(session?.user ?? null)
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
        localStorage.removeItem('orcid_access_token')
        setAuthorized(false)
        navigate('/login')
    }

    const value = {
        user,
        session,
        orcidAccessToken,
        authorized,
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