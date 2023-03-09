package com.svran.idea.plugin.treeinfo.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.psi.xml.XmlFile;
import com.svran.idea.plugin.treeinfo.FileDirectory;
import com.svran.idea.plugin.treeinfo.xml.XmlEntity;
import com.svran.idea.plugin.treeinfo.xml.XmlParsing;
import com.svran.idea.plugin.treeinfo.ui.Icons;
import com.svran.idea.plugin.treeinfo.ui.IconsList;
import org.jetbrains.annotations.NotNull;

import java.awt.*;


/**
 * A <code>ActionDescription</code> Class
 * 右键菜单
 *
 * @author lk
 * @version 1.0
 * @date 2021/6/7 14:13
 */
public class ActionDescriptionIcon extends AnAction {


    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        final IconsList dialog = new IconsList();
        dialog.pack();
        dialog.setTitle("Select Icons");
        dialog.setSize(288, 120);
        dialog.setLocationRelativeTo(null);
        dialog.setResizable(false);
        Dimension dimension = new Dimension();
        dimension.setSize(288, 120);
        dialog.setMaximumSize(dimension);
        dialog.setModal(true);
        FileDirectory.getBasePath(anActionEvent, new FileDirectory.Callback() {
            @Override
            public void onModifyPath(String asbbasePath, XmlEntity x, XmlFile fileDirectoryXml, Project project, String extension) {
                dialog.setIcons(x.getIcon());
                dialog.setVisible(true);
                Icons icons = dialog.getIcons();
                XmlParsing.modifyPath(x.getTag(), icons, fileDirectoryXml, project);
            }

            @Override
            public void onCreatePath(String asbbasePath, XmlFile fileDirectoryXml, Project project, String extension) {
                dialog.setVisible(true);
                Icons icons = dialog.getIcons();
                XmlParsing.createPath(fileDirectoryXml, project, asbbasePath, null, icons, extension);
            }
        });
    }


    @Override
    public void update(@NotNull AnActionEvent event) {
        //在Action显示之前,根据选中文件扩展名判定是否显示此Action
        //this.getTemplatePresentation().setIcon(AllIcons.Actions.MenuPaste);
    }


}
