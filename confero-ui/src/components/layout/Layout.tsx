import Navbar from "@/components/layout/Navbar.tsx";
import {Outlet} from "react-router-dom";
import Footer from "@/components/layout/Footer.tsx";

const Layout = () => {
    return (
        <div>
            <Navbar/>
            <div className="px-8 py-4">
                <Outlet/>
            </div>
            <Footer/>
        </div>
    );
};

export default Layout;