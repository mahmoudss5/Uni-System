import Welcome from "../components/Home/Welcome";
import Indicator from "../components/Home/Indicator";
import PopularCourses from "../components/Home/PopularCourses";
import Departments from "../components/Home/Departments";
import FeedBacks from "../components/Home/FeedBacks";
import FinalSecion from "../components/Home/FinalSecion";
import {useRef} from "react";
export default function Home() {

    const scrollRef = useRef<HTMLDivElement | null>(null);

    const executeScroll = () => {
        scrollRef.current?.scrollIntoView({ behavior: 'smooth' });
    };
    return (
        <>
            <Welcome scrollToSection={executeScroll} />
            <Indicator />
            <PopularCourses ref={scrollRef} />
            <Departments />
            <FeedBacks />
            <FinalSecion />
        </>
    );
}
