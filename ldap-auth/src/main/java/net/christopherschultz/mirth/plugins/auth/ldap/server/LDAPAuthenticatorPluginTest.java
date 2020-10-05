package net.christopherschultz.mirth.plugins.auth.ldap.server;


import com.mirth.connect.client.core.ControllerException;
import com.mirth.connect.model.LoginStatus;

import java.util.Properties;

class LDAPAuthenticatorPluginTest {

    public static void main(String[] args) throws ControllerException {
        LDAPAuthenticatorPlugin plugin = new LDAPAuthenticatorPlugin();
        plugin.init(new Properties());
        LoginStatus loginStatus = plugin.authorizeUser("igor", "test");
        System.out.println(loginStatus.getStatus());
        System.out.println(loginStatus.getMessage());
    }

}