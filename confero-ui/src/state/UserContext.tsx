import React, { createContext, ReactNode} from 'react';
import {ProfileResponse} from "@/generated";
import {useApi} from "@/api/useApi.ts";
import {useAuth} from "@/auth/AuthProvider.tsx";

const UserContext = createContext<{profileData?: ProfileResponse , isLoading: boolean} | undefined>(undefined);

export const UserContextProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
    const {apiClient, useApiQuery} = useApi();
    const {authorized} = useAuth();
    const {data: profileData, isLoading} = useApiQuery<ProfileResponse>(
        ["profile"],
        () => {if (authorized) return apiClient.profile.getUserProfile()}
    );
    return (
        <UserContext.Provider value={{ profileData, isLoading }}>
            {children}
        </UserContext.Provider>
    );
};

export const useUser = () => {
    const context = React.useContext(UserContext);
    if (context === undefined) {
        throw new Error('useUserContext must be used within a UserContextProvider');
    }
    return context;
};
