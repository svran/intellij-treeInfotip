package com.svran.idea.plugin.treeinfo;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.ProjectActivity;
import com.intellij.psi.xml.XmlFile;
import com.svran.idea.plugin.treeinfo.xml.XmlParsing;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 项目启动的时候执行.
 *
 * @author LK
 * @date 2018-04-07 1:18
 */
public class PluginStartupActivity implements ProjectActivity {

    public void runActivity(@NotNull Project project) {
        System.out.println("项目启动1");
        redConfigFile(project);
    }

    @Nullable
    public Object execute(@NotNull Project project, @NotNull Continuation<? super Unit> continuation) {
        System.out.println("项目启动2");
        redConfigFile(project);
        return null;
    }

    private void redConfigFile(@NotNull Project project) {
        ApplicationManager.getApplication().runReadAction(new Runnable() {
            @Override
            public void run() {
                XmlFile fileDirectoryXml = FileDirectory.getFileDirectoryXml(project, false);
                if (null != fileDirectoryXml) {
                    XmlParsing.parsing(project, fileDirectoryXml);
                }
                FileDirectory.treeChangeListener(project);
            }
        });
    }

}