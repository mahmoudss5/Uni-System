import Auth from "./pages/Auth";
import { RouterProvider } from "react-router-dom";
import { createBrowserRouter } from "react-router-dom";
import LoginForm from "./components/Auth/LoginForm";
import RegisterFrom from "./components/Auth/RegisterFrom";
import ErrorPage from"./components/common/ErrorPage";
import RootLayOut from "./pages/RootLayOut";
import Home from "./pages/Home";
const router = createBrowserRouter([
  {
    path: "/",
    element: <RootLayOut />,
    errorElement:<ErrorPage />,
    children: [
      {
        index: true,
        element: <Home />,
      },
      {
        path: "home",
        element: <Home />,
      }
    
    ],
  },

  {
    path: "/auth",
    element: <Auth />,
    errorElement:<ErrorPage />,
    children: [
      {
        index: true,
        element: <LoginForm />,
      },
      {
        path: "login",
        element: <LoginForm />,
      },
      {
        path: "register",
        element: <RegisterFrom />,
      },
    ],
  },
]);

function App() {
  return (
    <RouterProvider router={router} />
  );
}

export default App;
