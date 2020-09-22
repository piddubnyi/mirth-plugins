package net.christopherschultz.mirth.plugins.auth.ldap.client;

import com.mirth.connect.client.ui.AbstractSettingsPanel;
import com.mirth.connect.plugins.SettingsPanelPlugin;

public class LDAPAuthenticatorConfigurationClientPlugin extends SettingsPanelPlugin {

    private AbstractSettingsPanel settingsPanel = null;

    public LDAPAuthenticatorConfigurationClientPlugin(String name) {
        super(name);

        settingsPanel = new LDAPPluginPanel("LDAP", this);
    }

    @Override
    public AbstractSettingsPanel getSettingsPanel() {
        return settingsPanel;
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
    }

    @Override
    public void reset() {
    }

    @Override
    public String getPluginPointName() {
        return "LDAP";
    }
}