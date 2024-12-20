import {Link, NavLink, useLocation, useNavigate} from "react-router-dom";
import {Button} from "@/components/ui/button.tsx";
import {useAuth} from "@/auth/AuthProvider.tsx";
import {NavigationMenu, NavigationMenuItem, NavigationMenuList,} from "@/components/ui/navigationmenu.tsx";
import {Avatar, AvatarFallback, AvatarImage} from "@/components/ui/avatar.tsx";
import {useApi} from "@/api/useApi.ts";
import {ProfileResponse} from "@/generated";
import {useUser} from "@/state/UserContext.tsx";
import {useEffect, useState} from "react";

const Navbar = () => {
    const navigate = useNavigate();
    const location = useLocation();
    const {authorized, signOut} = useAuth();
    const {profileData, isLoading} = useUser();
    const [routes, setRoutes] = useState(updateRoutes())

    useEffect(() => {
        setRoutes(updateRoutes())
    }, [authorized, isLoading, profileData]);

    function updateRoutes(){
        if(!authorized || isLoading){
            return []
        }
        const isAdmin = profileData?.isAdmin;
        const isInvitee = profileData?.isInvitee;
        const authorizedRoutes = [
            {
                name: "Sessions",
                to: "/",
            },
            {
                name: "Applications",
                to: "/applications",
            },
            {
                name: "Profile",
                to: "/profile",
            }
        ]
        const invitee = [
            {
                name: "Sessions",
                to: "/",
            },
            {
                name: "Applications",
                to: "/applications",
            },
            {
                name: "My Calendar",
                to: "/my-calendar",
            },
            {
                name: "Organizers",
                to: "/organizers",
            },
            {
                name: "Profile",
                to: "/profile",
            }
        ]
        const admin = [
            {
                name: "Sessions",
                to: "/",
            },
            {
                name: "Applications",
                to: "/applications",
            },
            {
                name: "Conference Editions",
                to: "/conference-editions",
            },
            {
                name: "Profile",
                to: "/profile",
            }
        ]

        if(isAdmin){
            return admin;
        }
        if(isInvitee){
            return invitee;
        }
        if(authorized){
            return authorizedRoutes;
        }
    }


    return (
        <div className="navbar bg-white fixed top-0 w-full z-50">
            <div className="container mx-auto my-1 flex items-center justify-between">
                <a
                    href="#"
                    className="pl-3 text-5xl font-bold text-primary"
                    onClick={() => navigate("/")}
                >
                    Confero
                </a>
                {authorized && !isLoading && profileData && (
                    <>
                        <NavigationMenu>
                            <NavigationMenuList className="flex items-center gap-4 justify-start w-full">
                                {routes.map((route, index) => (
                                    <NavigationMenuItem
                                        key={index}
                                    >
                                        <NavLink to={route.to}
                                                 className={({isActive}) => isActive ?
                                                     "px-3 py-2 rounded-lg bg-gray-100 text-black"
                                                     : "px-3 py-2 rounded-lg text-muted-foreground"}>
                                            {route.name}
                                        </NavLink>
                                    </NavigationMenuItem>
                                ))}
                            </NavigationMenuList>
                        </NavigationMenu>

                    </>
                )}
                <div className="flex gap-3 ml-16">
                    {!isLoading && profileData && (
                    <Avatar>
                        <AvatarImage src={profileData?.avatarUrl}/>
                    </Avatar>
                    )}

                    {authorized ? (
                        <Button onClick={signOut} className={"mr-3"} variant={"secondary_grey"}>Logout</Button>
                    ) : (
                        <Button className={"mr-3"}>
                            <Link to="/login">Login</Link>
                        </Button>
                    )}
                </div>

            </div>

        </div>
    );
};

export default Navbar;
