import {Link} from "react-router-dom";
import {Button} from "@/components/ui/button.tsx";

const Navbar = () => {
    return (
        <div className="navbar bg-background border-b border-primary">
            <div className="container mx-auto">
                <div className="flex-1">
                    <a href="#" className="text-lg font-bold text-primary">confero</a>
                </div>
                <Button>
                    <Link to='/login'>Login</Link>
                </Button>
            </div>
        </div>
    );
};

export default Navbar;