package net.christopherschultz.mirth.plugins.auth.ldap.client;

import com.mirth.connect.client.ui.AbstractSettingsPanel;
import com.mirth.connect.client.ui.Frame;
import com.mirth.connect.client.ui.PlatformUI;
import com.mirth.connect.client.ui.UIConstants;

import javax.swing.*;
import java.awt.*;

public class LDAPPluginPanel extends AbstractSettingsPanel {

    private Frame parent;

    public LDAPPluginPanel(String tabName, LDAPAuthenticatorConfigurationClientPlugin plugin) {
        super(tabName);
        this.parent = PlatformUI.MIRTH_FRAME;

        initComponents();
    }

    @Override
    public void doRefresh() {
    }

    @Override
    public boolean doSave() {
        return false;
    }

    private void initComponents() {
        setBackground(UIConstants.BACKGROUND_COLOR);

        mainPanel = new JPanel();
        mainPanel.setBackground(UIConstants.BACKGROUND_COLOR);
        mainPanel.setLayout(new GridLayout(0,2));

        mainPanel.add(new JLabel("LDAP URL:"));
        urlTextField = new JTextField();
        mainPanel.add(urlTextField);

    }

    private JPanel mainPanel;
    private JTextField urlTextField;
}
