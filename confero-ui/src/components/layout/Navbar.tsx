const Navbar = () => {
    return (
        <div className="navbar bg-background border-b border-primary">
            <div className="container mx-auto">
                <div className="flex-1">
                    <a href="#" className="text-lg font-bold text-primary">Confero</a>
                </div>
                <div className="flex-none">
                    <a href="#" className="btn btn-primary">Login</a>
                </div>
            </div>
        </div>
    );
};

export default Navbar;