import {AppClient} from "@/generated";
import {useAuth} from "@/auth/AuthProvider.tsx";

export function useApiClient() {
    const {session, orcidAccessToken} = useAuth()
    const headers = {} as Record<string, string>
    if (session?.access_token) {
        headers["Authorization"] = `Bearer ${session.access_token}`
    }
    if (orcidAccessToken) {
        headers["Orcid-Access-Token"] = orcidAccessToken
    }

    return new AppClient({
        HEADERS: {
            "Authorization": `Bearer ${session?.access_token}`,
            "Orcid-Access-Token": orcidAccessToken ?? "",
        },
        BASE: "/api",
    });
}
