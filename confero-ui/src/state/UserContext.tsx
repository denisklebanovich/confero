import React, { createContext, ReactNode} from 'react';
import {ProfileResponse} from "@/generated";
import {useApi} from "@/api/useApi.ts";

const UserContext = createContext<{profileData: ProfileResponse , isLoading: boolean} | undefined>(undefined);

const UserContextProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
    const {apiClient, useApiQuery} = useApi();
    const {data: profileData, isLoading} = useApiQuery<ProfileResponse>(
        ["profile"],
        () => apiClient.profile.getUserProfile()
    );
    return (
        <UserContext.Provider value={{ profileData, isLoading }}>
            {children}
        </UserContext.Provider>
    );
};

const useUser = () => {
    const context = React.useContext(UserContext);
    if (context === undefined) {
        throw new Error('useUserContext must be used within a UserContextProvider');
    }
    return context;
};

export { UserContextProvider, useUser };
