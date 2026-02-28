package UnitSystem.demo.Security.Oauth2;

import UnitSystem.demo.BusinessLogic.InterfaceServiceLayer.UserService;
import UnitSystem.demo.DataAccessLayer.Entities.User;
import UnitSystem.demo.Security.Jwt.JwtService;
import UnitSystem.demo.Security.User.SecurityUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final UnitSystem.demo.BusinessLogic.InterfaceServiceLayer.UserService userService;
    private final JwtService jwtService;

    @Value("${app.frontend-url}")
    private String frontendUrl; // this is the frontend url to redirect after success login

    /**
     * the GitHub OAuth2 provider may not provide email or name, so we handle nulls
     * the GitHub will not provide a token for us, so we generate our own JWT token after user is created/found and redirect to frontend with that token
     * we use login as username, email as email (or fabricate one), name split into first and last name
     * we create a new user if email not found in our system to avoid duplicates and errors that may arise from missing email
     * btw that's not an ai comments :) , that's  a human comments :) iam just explaining it for my self in the future
     * github response example:
     * {
     *   "login": "AhmedAli",
     *   "id": 12345678,
     *   "node_id": "MDQ6VXNlcjEyMzQ1Njc4",
     *   "avatar_url": "https://avatars.githubusercontent.com/u/12345678?v=4",
     *   "name": "Ahmed Ali",
     *   "email": "ahmed@example.com", //  it can be null and in this case we will fabricate one
     *   "location": "Cairo, Egypt",
     *   "bio": "Java Developer",
     *   "created_at": "2020-01-01T00:00:00Z"
     * }
     *  there is no token as you can see so we generate our own JWT token and redirect to frontend with it
     *  and sense frontend is not ready for take the token from url so we need to make  a new method in frontend to handle that
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {

        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        OAuth2User oAuth2User = token.getPrincipal();


        Map<String, Object> attributes = oAuth2User.getAttributes(); // this is the map containing github user info like login, email, name, etc.
        // we received the attributes from GitHub response

        String username = (String) attributes.get("login");
        String email = (String) attributes.get("email");
        String fullName = (String) attributes.get("name");

        if (email == null) {
            email = username + "@github.com";
        }

        if (fullName == null) {
            fullName = username;
        }

        String[] names = fullName.split(" ");
        String firstName = names[0];
        String lastName = names.length > 1 ? names[names.length - 1] : "";

        String finalEmail = email;
        String finalFirstName = firstName;
        String finalLastName = lastName;
        String finalUsername = username;

        User user = userService.findByEmail(finalEmail).orElseGet(() -> {
            User newUser = new User();
            newUser.setUserName(finalUsername);
            newUser.setEmail(finalEmail);
            newUser.setPassword("");
            userService.save(newUser);
            return newUser;
        });


        String jwtToken = jwtService.generateToken(new SecurityUser(user));

        String targetUrl = frontendUrl + "/oauth2/redirect?token=" + jwtToken;
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}