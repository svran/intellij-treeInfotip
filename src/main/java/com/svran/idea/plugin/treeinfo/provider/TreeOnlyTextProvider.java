package com.svran.idea.plugin.treeinfo.provider;

import com.intellij.ide.projectView.TreeStructureProvider;
import com.intellij.ide.projectView.ViewSettings;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.svran.idea.plugin.treeinfo.FileDirectory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * Ŀ¼����ʾ��ע .
 *
 * @author LK
 * @date 2018-04-07 1:18
 */
public class TreeOnlyTextProvider implements TreeStructureProvider {

    @NotNull
    @Override
    public Collection<AbstractTreeNode<?>> modify(@NotNull AbstractTreeNode<?> abstractTreeNode, @NotNull Collection<AbstractTreeNode<?>> collection, ViewSettings viewSettings) {
        psiDirectoryNode(abstractTreeNode);
        return collection;
    }

    @Nullable
    @Override
    public Object getData(@NotNull Collection<? extends AbstractTreeNode<?>> selected, @NotNull String dataId) {
        for (AbstractTreeNode<?> abstractTreeNode : selected) {
            psiDirectoryNode(abstractTreeNode);
        }
        return null;
    }

    /**
     * ��ȡ����Ŀ¼
     *
     * @param abstractTreeNode ����
     */
    private void psiDirectoryNode(AbstractTreeNode<?> abstractTreeNode) {
        FileDirectory.setLocationString(abstractTreeNode);
    }

}