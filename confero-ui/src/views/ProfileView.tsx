import React, {useEffect, useState} from 'react';
import {Button} from "@/components/ui/button.tsx";
import {Input} from "@/components/ui/input.tsx";
import {Avatar, AvatarFallback, AvatarImage} from "@/components/ui/avatar"
import {useProfile} from "@/hooks/useProfile.ts";
import {Spinner} from "@/components/ui/spiner.tsx";
import {handleOrcidLogin} from "@/views/LoginView.tsx";
import OrcidIcon from "@/assets/orcid.svg?react"
import {useApi} from "@/api/useApi.ts";
import {ProfileResponse, UpdateAvatarRequest, UpdateEmailRequest, type UpdateProfileInfoRequest} from "@/generated";
import {useToast} from "@/hooks/use-toast.ts";
import {useQueryClient} from "@tanstack/react-query";
import {Label} from "@/components/ui/label.tsx";
import {useSearchParams} from "react-router-dom";
import {AvatarIcon} from "@radix-ui/react-icons";


const ProfileView = () => {
    let [params, setSearchParams] = useSearchParams();
    const {profile, isLoading} = useProfile();
    const [email, setEmail] = useState<string | undefined>();
    const {apiClient, useApiMutation} = useApi();
    const queryClient = useQueryClient();
    const {toast} = useToast();

    const {mutate: addEmail} = useApiMutation<ProfileResponse, UpdateEmailRequest>(
        (request) => apiClient.profile.updateUserEmail(request),
        {
            onSuccess: (data) => {
                setEmail('');
                toast({
                    title: "Email added",
                    description: "Check your inbox for the verification email",
                    variant: "success",
                })
                queryClient.setQueryData<ProfileResponse>(["profile"], data);
            },
            onError: (error) => {
                toast({
                    title: "Error adding email",
                    description: error.message,
                    variant: "error",
                })
            }
        }
    );

    const {mutate: updateProfilePicture} = useApiMutation<ProfileResponse, UpdateAvatarRequest>(
        (request) => apiClient.profile.updateAvatar(request),
        {
            onSuccess: (data) => {
                toast({
                    title: "Profile picture updated",
                    description: "Your profile picture has been updated",
                    variant: "success",
                })
                queryClient.setQueryData<ProfileResponse>(["profile"], data);
            },
            onError: (error) => {
                toast({
                    title: "Error updating profile picture",
                    description: error.message,
                    variant: "error",
                })
            }
        }
    );

    const {mutate: updateProfile} = useApiMutation<ProfileResponse, UpdateProfileInfoRequest>(
        (request) => apiClient.profile.updateUserInfo(request),
        {
            onSuccess: (data) => {
                toast({
                    title: "Profile updated",
                    description: "Your profile has been updated",
                    variant: "success",
                })
                queryClient.setQueryData<ProfileResponse>(["profile"], data);
            },
            onError: (error) => {
                toast({
                    title: "Error updating profile",
                    description: error.message,
                    variant: "error",
                })
            }
        }
    );

    const {mutate: verifyOrcid} = useApiMutation<ProfileResponse, { orcid: string, accessToken: string }>(
        ({orcid, accessToken}) => apiClient.profile.verifyOrcid(orcid, accessToken),
        {
            onSuccess: (data) => {
                toast({
                    title: "ORCID verified",
                    description: "Your ORCID has been verified",
                    variant: "success",
                })
                queryClient.setQueryData<ProfileResponse>(["profile"], data);
            },
            onError: (error) => {
                toast({
                    title: "Error verifying ORCID",
                    description: error.message,
                    variant: "error",
                })
            }
        }
    );


    useEffect(() => {
        console.log("params", params);
        if (params.has("orcid") && params.has("accessToken")) {
            const orcid = params.get("orcid");
            const accessToken = params.get("accessToken");
            if (orcid && accessToken) {
                verifyOrcid({orcid, accessToken});
                setSearchParams({});
            }
        }
    }, []);


    return (
        isLoading ?
            <div className="flex items-center justify-center h-96">
                <Spinner/>
            </div>
            :
            <main className="flex-1 container mx-auto px-4 py-8">
                <div className="max-w-2xl mx-auto space-y-8">
                    <h1 className="text-2xl font-bold mb-8">My Profile:</h1>
                    <div className="flex flex-col items-center space-y-4">
                        <Avatar className="w-56 h-56 bg-[#8BA663]">
                            <AvatarImage alt="Profile picture" src={profile?.avatarUrl}/>
                            <AvatarFallback>
                                <AvatarIcon className="w-full h-full"/>
                            </AvatarFallback>
                        </Avatar>
                        <Button variant="link" className="text-gray-600 relative">
                            <Input type="file" accept="image/*"
                                   onChange={(e) => {
                                       const file = e.target.files?.[0];
                                       if (file) {
                                           updateProfilePicture({avatarFile: file});
                                       }
                                   }}
                                   className="absolute top-0 left-0 w-full h-full opacity-0 cursor-pointer"
                            />
                            Change profile picture
                        </Button>
                    </div>
                    <div className="space-y-2">
                        <label htmlFor="orcid" className="text-sm font-medium">
                            ORCID
                        </label>
                        {profile?.orcid ?
                            <div className="flex items-center space-x-2">
                                <span className="p-2 bg-green-100 rounded-lg">{profile.orcid}</span>
                            </div>
                            :
                            <>
                                <Button
                                    variant='secondary'
                                    className="w-full"
                                    onClick={() => handleOrcidLogin(true)}
                                >
                                    <OrcidIcon/> Verify ORCID
                                </Button>
                                <p className="text-sm text-muted-foreground">Login with ORCID to verify it</p>
                            </>}
                    </div>
                    <div className="space-y-2">
                        <Label htmlFor="email" className="text-sm font-medium">
                            Email
                        </Label>
                        <div className="flex space-x-2">
                            <Input
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
                                id="email" type="email"/>
                            <Button
                                variant="outline"
                                onClick={() => addEmail({email})}
                            >
                                Add
                            </Button>
                        </div>
                    </div>
                    <div className="grid grid-cols-2 gap-3">
                        {profile?.emails?.map((email) => (
                            <div key={email.email}
                                 className={`flex items-center w-fit space-x-2 p-2 border rounded-lg ${email.confirmed ? "bg-green-100" : "bg-red-100"}`}>
                                <span>{email.confirmed ? "✅" : "❌"}</span>
                                <span>{email.email}</span>
                            </div>
                        ))}
                    </div>
                </div>
            </main>
    )
        ;
};

export default ProfileView;