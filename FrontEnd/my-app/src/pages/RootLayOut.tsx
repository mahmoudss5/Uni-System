import Nav from "../components/common/Nav";
import Footer from "../components/common/Footer";
import { Outlet } from "react-router-dom";
export default function RootLayOut() {
    return (
        <div className="min-h-screen bg-gray-200 flex flex-col ">
        <Nav />
        <main >
            <Outlet />
        </main>
        <Footer />
    </div>
    )
}