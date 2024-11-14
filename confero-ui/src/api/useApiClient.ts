import {AppClient} from "@/generated";
import {useAuth} from "@/auth/AuthProvider.tsx";
import {useMemo} from "react";

export function useApiClient() {
    const {session, orcidAccessToken} = useAuth()
    const headers = {} as Record<string, string>
    if (process.env.NODE_ENV === "development") {
        headers["Authorization"] = 'denis.klebanovich@gmail.com'
    }
    if (session?.access_token) {
        headers["Authorization"] = `Bearer ${session.access_token}`
    }
    if (orcidAccessToken) {
        headers["Orcid-Access-Token"] = orcidAccessToken
    }

    return useMemo(() => new AppClient({
        HEADERS: headers,
        WITH_CREDENTIALS: true,
        BASE: "/api",
    }), [headers]);
}
