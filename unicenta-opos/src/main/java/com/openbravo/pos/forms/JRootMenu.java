//    KrOS POS  - Open Source Point Of Sale
//    Copyright (c) 2009-2018 uniCenta & previous Openbravo POS works
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with this program.  If not, see <http://www.gnu.org/licenses/>.
package com.openbravo.pos.forms;

import com.openbravo.basic.BasicException;
import com.openbravo.data.gui.JMessageDialog;
import com.openbravo.data.gui.MessageInf;
import com.openbravo.pos.scripting.ScriptEngine;
import com.openbravo.pos.scripting.ScriptException;
import com.openbravo.pos.scripting.ScriptFactory;
import com.openbravo.pos.util.Hashcypher;
import com.openbravo.pos.util.StringUtils;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;
import org.jdesktop.swingx.painter.MattePainter;
// import org.jdesktop.swingx.painter.MattePainter;

/**
 *
 * @author poolborges
 */
public class JRootMenu {
    
    private static final Logger LOGGER = Logger.getLogger("com.openbravo.pos.forms.JRootMenu");
    
    private Map<String, JPanelView> m_aPreparedViews; // Prepared views   
    private Map<String, JPanelView> m_aCreatedViews;
    
    private JPanelView m_jLastView;
    private Action m_actionfirst;
    
        private final Component parent;
        private final AppUserView appview;

    public JRootMenu(Component _parent, AppUserView _appview){
        m_aPreparedViews = new HashMap<>();
        m_aCreatedViews = new HashMap<>();
        m_actionfirst = null;
        m_jLastView = null;
        
        parent = _parent;
        appview = _appview;
    }
    
    public JPanelView getLastView(){
        return m_jLastView;
    }
    public Action getActionfirst(){
        return m_actionfirst;
    }
    public void resetActionfirst(){
        if(m_actionfirst != null){
            m_actionfirst.actionPerformed(null);
            m_actionfirst = null;
        }
    }
    
    public boolean deactivateLastView(){
        if (m_jLastView == null) {
            return true;
        } else if (m_jLastView.deactivate()) {
            m_jLastView = null;
            return true;
        } else {
            return false;
        }
    }
    
    public boolean checkLastView(JPanelView m_jMyView){
        if (m_jLastView == null || (m_jMyView != m_jLastView && m_jLastView.deactivate())) {
            return true;
        }else {
            return false;
        }
    }
    
    public void setLastView(JPanelView m_jMyView){
        m_jLastView = m_jMyView;
    }

    public Map<String, JPanelView> getPreparedViews(){
        return m_aPreparedViews;
    }
    
    public Map<String, JPanelView> getCreatedViews(){
        return m_aCreatedViews;
    }
    
    public void setRootMenu(JScrollPane m_jPanelLeft, DataLogicSystem m_dlSystem) {
        try {
            
            String menuScrip = m_dlSystem.getResourceAsText("Menu.Root");
            Component menuComponent = getScriptMenu(menuScrip);

            if (menuComponent != null) {
                m_jPanelLeft.setViewportView(menuComponent);
            } else {
                String pah = "/com/openbravo/pos/templates/Menu.Root.txt";
                LOGGER.log(Level.FINE, "Root.Men lookup classpath: "+pah);
                menuScrip = StringUtils.readResource(pah);
                menuComponent = getScriptMenu(menuScrip);
                if(menuComponent != null){
                    m_jPanelLeft.setViewportView(menuComponent);
                }else{
                    LOGGER.log(Level.SEVERE, "Failed on build Root.Menu from class path: "+pah);
                }
            }

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Exception on setup Root.Menu", e);
        }
    }

    private Component getScriptMenu(String menutext) {

        Component menuComponent = null;
        
        if(menutext == null || menutext.isBlank()){
            LOGGER.log(Level.SEVERE, "Script content is blank/emp");
            return menuComponent;
        }
        
        try {
            ScriptMenu menu = new ScriptMenu(parent, appview);

            ScriptEngine eng = ScriptFactory.getScriptEngine(ScriptFactory.BEANSHELL);
            eng.put("menu", menu);
            eng.eval(menutext);
            menuComponent = menu.getTaskPane();
        } catch (ScriptException ex) {
            LOGGER.log(Level.SEVERE, "Exception on eval Menu.Root ", ex);
            LOGGER.log(Level.WARNING, "Exception on eval Menu.Root ", menutext);
        }

        return menuComponent;
    }

    public class ScriptMenu {

        private final JXTaskPaneContainer taskPane;
        private final Component parent;
        private final AppUserView appview;

        public ScriptMenu(Component _parent, AppUserView _appview) {
            parent = _parent;
            appview = _appview;
            taskPane = new JXTaskPaneContainer();
            taskPane.applyComponentOrientation(parent.getComponentOrientation());
        }

        public ScriptGroup addGroup(String key) {
            ScriptGroup group = new ScriptGroup(key, parent, appview);
            taskPane.add(group.getTaskGroup());
            return group;
        }

        public JXTaskPaneContainer getTaskPane() {
            return taskPane;
        }
    }

    public class ScriptGroup {

        private final JXTaskPane taskGroup;
        private final AppUserView m_appview;
        private final Component parent;

        private ScriptGroup(String key, Component _parent, AppUserView _appview) {
            m_appview = _appview;
            parent = _parent;
            taskGroup = new JXTaskPane();
            taskGroup.applyComponentOrientation(parent.getComponentOrientation());
            taskGroup.setFocusable(false);
            taskGroup.setRequestFocusEnabled(false);
            taskGroup.setTitle(AppLocal.getIntString(key));
            taskGroup.setVisible(false);
            taskGroup.setFont(new java.awt.Font("Arial", 0, 16));
        }

        public void addPanel(String icon, String key, String classname) {
            addAction(new MenuPanelAction(m_appview, icon, key, classname));
        }

        public void addExecution(String icon, String key, String classname) {
            addAction(new MenuExecAction(m_appview, icon, key, classname));
        }

        public ScriptSubmenu addSubmenu(String icon, String key, String classname) {
            ScriptSubmenu submenu = new ScriptSubmenu(parent, m_appview, key);
            m_aPreparedViews.put(classname, new JPanelMenu(submenu.getMenuDefinition()));
            addAction(new MenuPanelAction(m_appview, icon, key, classname));
            return submenu;
        }

        public void addChangePasswordAction() {
            addAction(new ChangePasswordAction(parent, m_appview,
                    "/com/openbravo/images/password.png", "Menu.ChangePassword"));
        }

        public void addExitAction() {
            addAction(new ExitAction(parent, m_appview,
                    "/com/openbravo/images/logout.png", "Menu.Exit"));
        }

        private void addAction(Action act) {

            AppUser m_appuser = m_appview.getUser();
            if (m_appuser.hasPermission((String) act.getValue(AppUserView.ACTION_TASKNAME))) {
                Component c = taskGroup.add(act);
                c.applyComponentOrientation(parent.getComponentOrientation());
                c.setFocusable(false);

                taskGroup.setVisible(true);
                /* */
                if (m_actionfirst == null) {
                    m_actionfirst = act;
                }
            }
        }

        public JXTaskPane getTaskGroup() {
            return taskGroup;
        }
    }

    public class ScriptSubmenu {

        private final MenuDefinition menudef;

        private final AppUserView m_appview;
        private final Component parent;

        private ScriptSubmenu(Component _parent, AppUserView _appview, String key) {
            parent = _parent;
            m_appview = _appview;
            menudef = new MenuDefinition(key);
        }

        public void addTitle(String key) {
            menudef.addMenuTitle(key);
        }

        public void addPanel(String icon, String key, String classname) {
            menudef.addMenuItem(new MenuPanelAction(m_appview, icon, key, classname));
        }

        public void addExecution(String icon, String key, String classname) {
            menudef.addMenuItem(new MenuExecAction(m_appview, icon, key, classname));
        }

        public ScriptSubmenu addSubmenu(String icon, String key, String classname) {
            ScriptSubmenu submenu = new ScriptSubmenu(parent, m_appview,key);
            m_aPreparedViews.put(classname, new JPanelMenu(submenu.getMenuDefinition()));
            menudef.addMenuItem(new MenuPanelAction(m_appview, icon, key, classname));
            return submenu;
        }

        public void addChangePasswordAction() {
            menudef.addMenuItem(new ChangePasswordAction(parent, m_appview,
                    "/com/openbravo/images/password.png", "Menu.ChangePassword"));
        }

        public void addExitAction() {
            menudef.addMenuItem(new ExitAction(parent, m_appview,
                    "/com/openbravo/images/logout.png", "Menu.Exit"));
        }

        public MenuDefinition getMenuDefinition() {
            return menudef;
        }
    }

    private class ChangePasswordAction extends AbstractAction {

        private final Component parent;
        private final AppUserView appview;

        private ChangePasswordAction(Component parentP, AppUserView appviewP, String icon, String keytext) {
            parent = parentP;
            appview = appviewP;
            putValue(Action.SMALL_ICON, new ImageIcon(JPrincipalApp.class.getResource(icon)));
            putValue(Action.NAME, AppLocal.getIntString(keytext));
            putValue(AppUserView.ACTION_TASKNAME, keytext);

        }

        @Override
        public void actionPerformed(ActionEvent evt) {

            try {
                AppUser m_appuser = appview.getUser();
                String sNewPassword = Hashcypher.changePassword(parent, m_appuser.getPassword());
/*
                if (sNewPassword != null) {
                    DataLogicSystem m_dlSystem = (DataLogicSystem) appview.getBean("com.openbravo.pos.forms.DataLogicSystem");
                    m_dlSystem.execChangePassword(new Object[]{sNewPassword, m_appuser.getId()});
                    m_appuser.setPassword(sNewPassword);
                }
*/
            } catch (Exception e) {
                JMessageDialog.showMessage(parent,
                        new MessageInf(MessageInf.SGN_WARNING,
                                AppLocal.getIntString("message.cannotchangepassword")));
            }
        }
    }

    private class ExitAction extends AbstractAction {

        private final AppUserView m_appview;

        public ExitAction(Component _parent, AppUserView _appview, String icon, String keytext) {
            m_appview = _appview;
            putValue(Action.SMALL_ICON, new ImageIcon(JPrincipalApp.class.getResource(icon)));
            putValue(Action.NAME, AppLocal.getIntString(keytext));
            putValue(AppUserView.ACTION_TASKNAME, keytext);
        }

        @Override
        public void actionPerformed(ActionEvent evt) {
//            m_appview.closeAppView();
        }
    }

}