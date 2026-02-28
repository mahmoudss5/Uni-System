export interface Course {
    id: number;
    name: string;
    Teacher: string;
    department: string;
    credits: number;
    maxStudents: number;
    enrolledStudents: number;
    description: string;
    image: string;
}

export const courses: Course[] = [
    {
        id: 1,
        name: "Computer Science",
        Teacher: "Dr. John Doe",
        department: "Computer Science",
        credits: 3,
        maxStudents: 100,
        enrolledStudents: 42,
        description: "Computer Science is the study of computers and how they work.",
        image: "https://via.placeholder.com/150",
    },
    {
        id: 2,
        name: "Mathematics",
        Teacher: "Dr. Jane Doe",
        department: "Mathematics",
        credits: 3,
        maxStudents: 100,
        enrolledStudents: 100,
        description: "Mathematics is the study of numbers and how they work.",
        image: "https://via.placeholder.com/150",
    },
    {
        id: 3,
        name: "Physics",
        Teacher: "Dr. John Doe",
        department: "Physics",
        credits: 3,
        maxStudents: 100,
        enrolledStudents: 40,
        description: "Physics is the study of how the universe works.",
        image: "https://via.placeholder.com/150",
    },
    {
        id: 4,
        name: "Chemistry",
        Teacher: "Dr. ahmed elmasry",
        department: "Chemistry",
        credits: 3,
        maxStudents: 100,
        enrolledStudents: 32,
        description: "Chemistry is the study of the composition of matter and the properties of substances.",
        image: "https://via.placeholder.com/150",
    }
]

export interface Department {
    name: string;
    description: string;
    numberOfCourses: number;
    id: number;
}   
export const departments: Department[] = [
    {
        id: 1,
        name: "Computer Science",
        description: "Computer Science is the study of computers and how they work.",
        numberOfCourses: 10,
    },
    {
        id: 2,
        name: "Information Systems",
        description: "Information Systems is the study of how information is used to manage and improve organizations.",
        numberOfCourses: 10,
    },
    {
        id: 3,
        name: "Software Engineering",
        description: "Artificial Intelligence is the study of how computers can learn and make decisions.",
        numberOfCourses: 10,
    },
    {
        id: 4,
        name: "Data Science",
        description: "Data Science is the study of how to collect, analyze and use data to make decisions.",
        numberOfCourses: 10,
    },
    {
        id: 5,
        name: "Cybersecurity",
        description: "Cybersecurity is the study of how to protect computers and networks from attack.",
        numberOfCourses: 10,
    },
]

export interface FeedBack {
    id: number;
    name: string;
    description: string;
    role: string;
}
export const feedBacks: FeedBack[] = [
    {
        id: 1,
        name: "John Doe",
        description: "this university is the best university in the world ,i have learned a lot of things here and i am grateful to the university for the opportunity to study here",
        role: "Professor",
    },
    {
        id: 2,
        name: "Jane Doe",
        description: "this university is the best university in the world ,i have learned a lot of things here and i am grateful to the university for the opportunity to study here",
        role: "Assistant Professor",
    },
    {
        id: 3,
        name: "John Doe",
        description: "this university is the best university in the world ,i have learned a lot of things here and i am grateful to the university for the opportunity to study here",
        role: "Dean",
    },
    {
        id: 4,
        name: "John Doe",
        description: "this university is the best university in the world ,i have learned a lot of things here and i am grateful to the university for the opportunity to study here",
        role: "Student",
    },
]