package org.dre0065.Controller;

import jakarta.servlet.http.*;
import org.springframework.web.bind.annotation.*;
import org.dre0065.Controller.AuthController.*;

@ControllerAdvice
public class GlobalControllerAdvice
{
    @ModelAttribute("user")
    public UserResponse addUserToModel(HttpSession session)
    {
        UserResponse user = (UserResponse) session.getAttribute("user");
        System.out.println("Session user: " + user);
        if(user == null) System.out.println("User is not set in session. Session ID: " + session.getId());
        return user;
    }

    @ModelAttribute("userRole")
    public String addUserRoleToModel(HttpSession session)
    {
        UserResponse userObj = (UserResponse) session.getAttribute("user");
        if(userObj != null)
        {
            String role = userObj.getRole();
            System.out.println("addUserRoleToModel called: " + role);
            return role;
        }
        System.out.println("Session user: " + userObj + " - Session ID: " + session.getId());
        return null;
    }
}