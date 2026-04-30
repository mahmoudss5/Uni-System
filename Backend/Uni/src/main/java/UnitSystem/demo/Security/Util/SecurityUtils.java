package UnitSystem.demo.Security.Util;
import UnitSystem.demo.Security.User.SecurityUser;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    private static final SecurityUtils INSTANCE = new SecurityUtils();

    private String userIp;

    private SecurityUtils() {}

    public static SecurityUtils getInstance() {
        return INSTANCE;
    }

    public String getUserIp() {
        return this.userIp;
    }

    public void setUserIp(String userIp) {
        this.userIp = userIp;
    }

    
    public static Long getCurrentUserId() {
      Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
      if (authentication!=null && authentication.getPrincipal() instanceof SecurityUser securityUser) {
            return securityUser.getUserId();
      }
        return null;
    }


    public static String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            return authentication.getName();
        }
        return "Anonymous";
    }
}