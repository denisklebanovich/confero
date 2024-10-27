import {AppClient} from "@/generated";
import {useAuth} from "@/auth/AuthProvider.tsx";

export function useApiClient() {
    const {session} = useAuth()

    return new AppClient({
        HEADERS: {
            "Authorization": `Bearer ${session?.access_token}`,
        },
        BASE: "/api",
    });
}
