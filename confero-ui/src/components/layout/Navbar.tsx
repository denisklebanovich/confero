import {Link, useNavigate} from "react-router-dom";
import {Button} from "@/components/ui/button.tsx";
import {useAuth} from "@/auth/AuthProvider.tsx";

const Navbar = () => {
    const navigate = useNavigate();
    const {user,signOut} = useAuth();
    return (
        <div className="navbar bg-white fixed top-0">
            <div className="container my-1">
                <div className="flex-1">
                    <a href="#" className="pl-3 text-5xl font-bold text-primary"
                       onClick={() => navigate("/")}>confero</a>
                </div>
                <div className={"flex flex-row gap-2"}>
                    <Button>
                        <Link to='/admin-sessions'>Manage Sessions</Link>
                    </Button>
                    <Button>
                        <Link to='/applications'>Applications</Link>
                    </Button>
                    {user ?
                        <Button onClick={signOut}>
                            Logout
                        </Button> :
                        <Button>
                            <Link to='/login'>Login</Link>
                        </Button>
                    }
                </div>

            </div>
        </div>
    );
};

export default Navbar;