import {useApiClient} from "@/api/api-instance.ts";
import {useMutation, UseMutationOptions, useQuery, UseQueryOptions} from "@tanstack/react-query";

export function useApi() {
    const apiClient = useApiClient()

    function useApiQuery<T>(
        key: string[],
        method: () => Promise<T>,
        options?: UseQueryOptions<T>
    ) {
        return useQuery({
            queryKey: key,
            queryFn: method,
            ...options,
        })
    }

    function useApiMutation<T, V>(
        method: (variables: V) => Promise<T>,
        options?: UseMutationOptions<T, Error, V>
    ) {
        return useMutation({
            mutationFn: method,
            ...options,
        })
    }

    return {
        useApiQuery,
        useApiMutation,
        apiClient,
    }
}