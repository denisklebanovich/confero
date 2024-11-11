import { Link, useLocation, useNavigate } from "react-router-dom";
import { Button } from "@/components/ui/button.tsx";
import { useAuth } from "@/auth/AuthProvider.tsx";
import {
  NavigationMenu,
  NavigationMenuItem,
  NavigationMenuList,
} from "@/components/ui/navigationmenu.tsx";

const Navbar = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { authorized, signOut } = useAuth();
  return (
    <div className="navbar bg-white fixed top-0 w-full z-50">
      <div className="container my-1 flex items-center justify-between">
        <a
          href="#"
          className="pl-3 text-5xl font-bold text-primary"
          onClick={() => navigate("/")}
        >
          Confero
        </a>
        {location.pathname === "/" || location.pathname === "/applications" ? (
          <NavigationMenu>
            <NavigationMenuList className="flex gap-4">
              <NavigationMenuItem>
                <Link
                  to="/"
                  className={`px-4 py-2 rounded-md ${
                    location.pathname === "/"
                      ? "bg-gray-100 text-black"
                      : "text-black"
                  } transition-colors`}
                >
                  Sessions
                </Link>
              </NavigationMenuItem>
              <NavigationMenuItem>
                <Link
                  to="/applications"
                  className={`px-4 py-2 rounded-md ${
                    location.pathname === "/applications"
                      ? "bg-gray-100 text-black"
                      : "text-black"
                  } transition-colors`}
                >
                  Applications
                </Link>
              </NavigationMenuItem>
              <NavigationMenuItem>
                <Link
                    to="/organizers"
                    className={`px-4 py-2 rounded-md ${
                        location.pathname === "/organizers"
                            ? "bg-gray-100 text-black"
                            : "text-black"
                    } transition-colors`}
                >
                  Organizers
                </Link>

              </NavigationMenuItem>
              <NavigationMenuItem>
                <Link
                    to="/conference-editions"
                    className={`px-4 py-2 rounded-md ${
                        location.pathname === "/organizers"
                            ? "bg-gray-100 text-black"
                            : "text-black"
                    } transition-colors`}
                >
                  Conference Editions
                </Link>

              </NavigationMenuItem>
            </NavigationMenuList>
          </NavigationMenu>
        ) : null}

        {authorized ? (
          <Button onClick={signOut} variant={"secondary_grey"}>Logout</Button>
        ) : (
          <Button>
            <Link to="/login">Login</Link>
          </Button>
        )}
      </div>
    </div>
  );
};

export default Navbar;
