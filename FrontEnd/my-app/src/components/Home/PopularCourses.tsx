import { motion } from "framer-motion";
import { courses as dummyCourses } from "../common/dummyData.js";
import CourseCard from "./CourseCard";
import { forwardRef } from "react";

const PopularCourses = forwardRef<HTMLDivElement>((_props, ref) => {
    return (
        <>
            <section className="w-full bg-gray-100 py-20 min-h-[calc(100vh-5rem)] overflow-hidden ">
                <div className="container mx-auto px-4 sm:px-6 lg:px-8 h-full overflow-hidden  ">
                    <motion.div
                        className="text-center mb-12">
                        <h2 className="text-4xl font-bold text-gray-800 mb-4">Popular Courses</h2>
                        <p className="text-gray-600 text-lg">Explore our most popular courses and start learning today</p>
                    </motion.div>

                    <motion.div
                    ref={ref}
                        className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-8 py-10">
                        {dummyCourses.map((course) => (
                            <CourseCard key={course.id} course={course} />
                        ))}
                    </motion.div>
                </div>
            </section>
        </>
    )
});

PopularCourses.displayName = "PopularCourses";

export default PopularCourses;
