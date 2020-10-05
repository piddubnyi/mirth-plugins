package net.christopherschultz.mirth.plugins.auth.ldap.client;

import com.mirth.connect.client.ui.AbstractSettingsPanel;
import com.mirth.connect.client.ui.Frame;
import com.mirth.connect.client.ui.PlatformUI;
import com.mirth.connect.client.ui.UIConstants;
import com.mirth.connect.client.ui.components.MirthRadioButton;
import com.mirth.connect.client.ui.components.MirthTextField;
import com.mirth.connect.plugins.SettingsPanelPlugin;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.lang3.math.NumberUtils;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Properties;

import static net.christopherschultz.mirth.plugins.auth.ldap.Constants.*;

public class LDAPPluginPanel extends AbstractSettingsPanel {

    private final SettingsPanelPlugin plugin;
    private final Frame parent;

    public LDAPPluginPanel(String tabName, SettingsPanelPlugin plugin) {
        super(tabName);
        this.plugin = plugin;
        this.parent = PlatformUI.MIRTH_FRAME;

        initComponents();
    }

    @Override
    public void doRefresh() {
        if (PlatformUI.MIRTH_FRAME.alertRefresh()) {
            return;
        }
        final String workingId = getFrame().startWorking("Loading " + getTabName() + " properties...");
        final Properties serverProperties = new Properties();

        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

            public Void doInBackground() {
                try {
                    Properties propertiesFromServer = plugin.getPropertiesFromServer();

                    if (propertiesFromServer != null) {
                        serverProperties.putAll(propertiesFromServer);
                    }
                } catch (Exception e) {
                    getFrame().alertThrowable(LDAPPluginPanel.this, e);
                }
                return null;
            }

            @Override
            public void done() {
                setProperties(serverProperties);
                getFrame().stopWorking(workingId);
            }
        };

        worker.execute();
    }

    @Override
    public boolean doSave() {
        if (!validateFields()) {
            return false;
        }

        final String workingId = getFrame().startWorking("Saving " + getTabName() + " properties...");

        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

            public Void doInBackground() {
                try {
                    plugin.setPropertiesToServer(getProperties());
                } catch (Exception e) {
                    getFrame().alertThrowable(LDAPPluginPanel.this, e);
                }
                return null;
            }

            @Override
            public void done() {
                setSaveEnabled(false);
                getFrame().stopWorking(workingId);
            }
        };

        worker.execute();

        return true;
    }

    private boolean validateFields() {
        return NumberUtils.isDigits(retriesTextField.getText()) && NumberUtils.isDigits(retryIntervalTextField.getText());
    }

    public void setProperties(Properties properties) {
        if (Boolean.parseBoolean(properties.getProperty("ldap.fallback-to-local-authentication"))) {
            yesEnabledRadio.setSelected(true);
        } else {
            noEnabledRadio.setSelected(true);
        }

        fillPropertyOrDefault(properties, LDAP_URL, urlTextField, "ldap://localhost:8389");
        fillPropertyOrDefault(properties, LDAP_GROUP_FILTER, groupFilterTextField, "");
        fillPropertyOrDefault(properties, LDAP_BASE_DN, baseDnTextField, "dc=your-company,dc=org");
        fillPropertyOrDefault(properties, LDAP_USER_DN_TEMPLATE, userDnTemplateTextField, "uid={username},ou=people,dc=your-company,dc=org");
        fillPropertyOrDefault(properties, LDAP_RETRIES, retriesTextField, "3");
        fillPropertyOrDefault(properties, LDAP_RETRY_INTERVAL, retryIntervalTextField, "1000");

        if (properties.getProperty(LDAP_FALLBACK_TO_LOCAL) != null && !properties.getProperty(LDAP_FALLBACK_TO_LOCAL).equals("")) {
            if(Boolean.parseBoolean(properties.getProperty(LDAP_FALLBACK_TO_LOCAL))){
                yesEnabledRadio.setSelected(true);
            } else {
                noEnabledRadio.setSelected(true);
            }
        } else {
            noEnabledRadio.setSelected(true);
        }

        parent.setSaveEnabled(false);
    }

    private void fillPropertyOrDefault(Properties properties, String propertyName, JTextField view, String defaultValue) {
        if (properties.getProperty(propertyName) != null && !properties.getProperty(propertyName).equals("")) {
            view.setText(properties.getProperty(propertyName));
        } else {
            view.setText(defaultValue);
        }
    }

    public Properties getProperties() {
        Properties properties = new Properties();

        String enabled = "true";
        if (noEnabledRadio.isSelected()) {
            enabled = "false";
        }

        properties.setProperty(LDAP_URL, urlTextField.getText());
        properties.setProperty(LDAP_USER_DN_TEMPLATE, userDnTemplateTextField.getText());
        properties.setProperty(LDAP_GROUP_FILTER, groupFilterTextField.getText());
        properties.setProperty(LDAP_BASE_DN, baseDnTextField.getText());
        properties.setProperty(LDAP_RETRIES, retriesTextField.getText());
        properties.setProperty(LDAP_RETRY_INTERVAL, retryIntervalTextField.getText());
        properties.setProperty(LDAP_FALLBACK_TO_LOCAL, enabled);
        return properties;
    }

    private void initComponents() {
        setLayout(new MigLayout("hidemode 3, novisualpadding, insets 12", "[grow]"));
        mainPanel = new JPanel();
        mainPanel.setBackground(UIConstants.BACKGROUND_COLOR);
        mainPanel.setBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(204, 204, 204)),
                        "Ldap settings",
                        TitledBorder.DEFAULT_JUSTIFICATION,
                        TitledBorder.DEFAULT_POSITION,
                        new Font("Tahoma", 1, 11)
                )
        );


        mainPanel.setLayout(new MigLayout("hidemode 3, novisualpadding, insets 0", "12[right][left]"));

        mainPanel.add(new JLabel("LDAP URL:"));
        urlTextField = new MirthTextField();
        mainPanel.add(urlTextField, "w 300!, wrap");

        mainPanel.add(new JLabel("User-dn-template:"));
        userDnTemplateTextField = new MirthTextField();
        userDnTemplateTextField.addActionListener(this::enableSaving);
        mainPanel.add(userDnTemplateTextField, "w 300!, wrap");

        mainPanel.add(new JLabel("Group-filter:"));
        groupFilterTextField = new MirthTextField();
        groupFilterTextField.addActionListener(this::enableSaving);
        mainPanel.add(groupFilterTextField, "w 150!, wrap");

        mainPanel.add(new JLabel("Base-dn:"));
        baseDnTextField = new MirthTextField();
        baseDnTextField.addActionListener(this::enableSaving);
        mainPanel.add(baseDnTextField, "w 150!, wrap");

        mainPanel.add(new JLabel("Retries:"));
        retriesTextField = new MirthTextField();
        retriesTextField.addActionListener(this::enableSaving);
        mainPanel.add(retriesTextField, "w 75!, wrap");

        mainPanel.add(new JLabel("Retry-interval (ms):"));
        retryIntervalTextField = new MirthTextField();
        retryIntervalTextField.addActionListener(this::enableSaving);
        mainPanel.add(retryIntervalTextField, "w 75!, wrap");

        mainPanel.add(new JLabel("Fallback auth:"));
        yesEnabledRadio = new MirthRadioButton("Yes");
        yesEnabledRadio.setFocusable(false);
        yesEnabledRadio.setBackground(Color.white);
        yesEnabledRadio.addActionListener(this::enableSaving);

        noEnabledRadio = new MirthRadioButton("No");
        noEnabledRadio.setFocusable(false);
        noEnabledRadio.setBackground(Color.white);
        noEnabledRadio.setSelected(true);
        noEnabledRadio.addActionListener(this::enableSaving);

        ButtonGroup enabledButtonGroup = new ButtonGroup();
        enabledButtonGroup.add(yesEnabledRadio);
        enabledButtonGroup.add(noEnabledRadio);

        mainPanel.add(yesEnabledRadio,  "split, gapleft 12");
        mainPanel.add(noEnabledRadio,  "wrap");

        add(mainPanel, "grow, sx, wrap");
        repaint();

    }

    private void enableSaving(ActionEvent actionEvent) {
        setSaveEnabled(true);
    }

    private JPanel mainPanel;
    private JTextField urlTextField;
    private JTextField userDnTemplateTextField;
    private JTextField groupFilterTextField;
    private JTextField baseDnTextField;
    private JTextField retriesTextField;
    private JTextField retryIntervalTextField;
    private JRadioButton yesEnabledRadio;
    private JRadioButton noEnabledRadio;
}
