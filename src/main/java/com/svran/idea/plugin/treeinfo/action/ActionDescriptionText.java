package com.svran.idea.plugin.treeinfo.action;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.xml.XmlFile;
import com.svran.idea.plugin.treeinfo.FileDirectory;
import com.svran.idea.plugin.treeinfo.xml.XmlEntity;
import com.svran.idea.plugin.treeinfo.xml.XmlParsing;
import org.jetbrains.annotations.NotNull;

/**
 * A <code>ActionDescription</code> Class
 * 右键菜单
 *
 * @author lk
 * @version 1.0
 * @date 2021/6/7 14:13
 */
public class ActionDescriptionText extends AnAction {

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT; // super.getActionUpdateThread();
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        FileDirectory.getBasePath(anActionEvent, new FileDirectory.Callback() {
            @Override
            public void onModifyPath(String asbbasePath, XmlEntity x, XmlFile fileDirectoryXml, Project project, String extension) {
                String txt = Messages.showInputDialog(project, "Input Your " + asbbasePath + "  Description",
                        "What Needs To Be Description?", AllIcons.Actions.MenuPaste, x.getTitle(), null);
                if (null != txt) {
                    XmlParsing.modifyPath(x.getTag(), txt, null, fileDirectoryXml, project);
                }
            }

            @Override
            public void onCreatePath(String asbbasePath, XmlFile fileDirectoryXml, Project project, String extension) {
                String txt = Messages.showInputDialog(project, "Input Your " + asbbasePath + "  Description",
                        "What Needs To Be Description?", AllIcons.Actions.MenuPaste, "", null);
                if (null != txt) {
                    XmlParsing.createPath(fileDirectoryXml, project, asbbasePath, txt, null, extension);
                }
            }
        });
    }


    @Override
    public void update(@NotNull AnActionEvent event) {
        //在Action显示之前,根据选中文件扩展名判定是否显示此Action
        //this.getTemplatePresentation().setIcon(AllIcons.Actions.MenuPaste);
    }


}
