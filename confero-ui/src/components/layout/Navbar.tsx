import {Link, NavLink, useLocation, useNavigate} from "react-router-dom";
import {Button} from "@/components/ui/button.tsx";
import {useAuth} from "@/auth/AuthProvider.tsx";
import {NavigationMenu, NavigationMenuItem, NavigationMenuList,} from "@/components/ui/navigationmenu.tsx";
import {Avatar, AvatarFallback, AvatarImage} from "@/components/ui/avatar.tsx";
import {useApi} from "@/api/useApi.ts";
import {ProfileResponse} from "@/generated";
import {useUser} from "@/state/UserContext.tsx";

const Navbar = () => {
    const navigate = useNavigate();
    const location = useLocation();
    const {authorized, signOut} = useAuth();
    const {profileData: {isAdmin, isInvitee}, isLoading} = useUser();


    console.log(isAdmin, isInvitee, "vlad")



    const routes = [
        {
            name: "Sessions",
            to: "/",
        },
        {
            name: "Applications",
            to: "/applications",
        },
        {
            name: "Organizers",
            to: "/organizers",
        },
        {
            name: "Conference Editions",
            to: "/conference-editions",
        },
        {
            name: "My Calendar",
            to: "/my-calendar",
        },
        {
            name: "Profile",
            to: "/profile",
        }
    ]
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
                <NavigationMenu>
                    <NavigationMenuList className="flex items-center gap-4 justify-around">
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
                <div className="flex gap-3 ml-16">
                    { !isLoading && profileData && (
                        <Avatar>
                            <AvatarImage src={profileData.avatarUrl}/>
                        </Avatar>)
                    }
                    {authorized ? (
                        <Button onClick={signOut} variant={"secondary_grey"}>Logout</Button>
                    ) : (
                        <Button>
                            <Link to="/login">Login</Link>
                        </Button>
                    )}
                </div>
            </div>
        </div>
    );
};

export default Navbar;
