package com.svran.idea.plugin.treeinfo;

import com.intellij.icons.AllIcons;
import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.projectView.ProjectViewNode;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.search.PsiShortNamesCache;
import com.intellij.psi.xml.XmlFile;
import com.svran.idea.plugin.treeinfo.ui.Icons;
import com.svran.idea.plugin.treeinfo.xml.XmlEntity;
import com.svran.idea.plugin.treeinfo.xml.XmlParsing;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.idea.projectView.KtClassOrObjectTreeNode;
import org.jetbrains.kotlin.psi.KtClassOrObject;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import static com.intellij.openapi.actionSystem.CommonDataKeys.VIRTUAL_FILE;

/**
 * A <code>FileDirectory</code> Class
 *
 * @author lk
 * @version 1.0
 * @date 2021/6/7 16:48
 */
public class FileDirectory {

    private static XmlFile xmlFile;

    public interface Callback {
        /**
         * �޸�·��
         *
         * @param asbbasePath      ����·��
         * @param x                ����
         * @param fileDirectoryXml ����
         * @param project          ����
         * @param extension        ����
         */
        void onModifyPath(String asbbasePath, XmlEntity x, XmlFile fileDirectoryXml, Project project, String extension);

        /**
         * ����·��
         *
         * @param asbbasePath      ����·��
         * @param fileDirectoryXml ����
         * @param project          ����
         * @param extension        ����
         */
        void onCreatePath(String asbbasePath, XmlFile fileDirectoryXml, Project project, String extension);
    }

    /**
     * ��ȡ����·��
     *
     * @param anActionEvent ����
     * @param callback      �ص�
     */
    public static void getBasePath(AnActionEvent anActionEvent, Callback callback) {
        final Project project = anActionEvent.getProject();
        if (null == project) {
            return;
        }
        //��ȡ�ļ� �ļ��еȶ���
        VirtualFile file = VIRTUAL_FILE.getData(anActionEvent.getDataContext());
        if (null != file) {
            XmlFile fileDirectoryXml = FileDirectory.getFileDirectoryXml(project, true);
            List<XmlEntity> refreshXml = XmlParsing.getRefreshXml(project, fileDirectoryXml);
            //ʹ�����·��
            String basePath = project.getPresentableUrl();
            if (null != basePath && !basePath.isEmpty()) {
                //��Ϊ������ȥ��.
                //�˴��Ľ�
                String presentableUrl = file.getCanonicalPath();
                if (presentableUrl != null && presentableUrl.length() < basePath.length()) {
                    Messages.showMessageDialog(project, "Unable to get the root path of the file", "Can't Get Path", AllIcons.Actions.MenuPaste);
                    return;
                }
                String asbbasePath = presentableUrl != null ? presentableUrl.substring(basePath.length()) : "";
                String extension = file.getExtension();
                boolean notfind = false;
                for (XmlEntity x : refreshXml) {
                    //Messages.showMessageDialog(project, presentableUrl + ":" + x.getPath(), "Can't Get Path", AllIcons.Actions.Menu_paste);
                    if (asbbasePath.equals(x.getPath())) {
                        callback.onModifyPath(asbbasePath, x, fileDirectoryXml, project, extension);
                        notfind = true;
                        break;
                    }
                }
                if (!notfind) {
                    callback.onCreatePath(asbbasePath, fileDirectoryXml, project, extension);
                }
            }
        } else {
            Messages.showMessageDialog(project, "Unable to get the root path of the project", "Can't Get Path", AllIcons.Actions.MenuPaste);
        }
    }

    /**
     * ��ȡ��ָ�����ļ�
     *
     * @param project ��Ŀ
     * @param create  �Ƿ񴴽��ļ�
     */
    public static synchronized XmlFile getFileDirectoryXml(final Project project, boolean create) {
        if (project == null) {
            return null;
        }
        //��ѯ����ļ�����Ŀ¼�޹�
        try {
            //git֧�ֲ��ѺõĽ��
            PsiFile[] pfs = PsiShortNamesCache.getInstance(project).getFilesByName(getFileName());
//            PsiFile[] pfs = FilenameIndex.getFilesByName(project, getFileName(), GlobalSearchScope.allScope(project));
//            PsiFile[] pfs = FilenameIndex.getFilesByName(project, getFileName(), GlobalSearchScope.allScope(project));
            if (pfs.length == 1) {
                //��ȡһ���ļ���������ڶ����ͬ���ļ���ȡ��ѯ����һ��.
                PsiFile pf = pfs[0];
                if (pf instanceof XmlFile) {
                    xmlFile = (XmlFile) pf;
                } else {
                    Messages.showMessageDialog(project, "There are multiple identical files in the root directory", "Can't Get Path", AllIcons.Actions.MenuPaste);
                }
            } else if (pfs.length == 0) {
                if (create) {
                    xmlFile = createFileDirectoryXml(project);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return xmlFile;
    }

    /**
     * ��ȡ�ļ�����
     *
     * @return String
     */
    private static String getFileName() {
//        Map<String, String> map = System.getenv();
//        String userName = map.get("USERNAME");// ��ȡ�û���
//        String computerName = map.get("COMPUTERNAME");// ��ȡ�������
//        String userDomain = map.get("USERDOMAIN");// ��ȡ���������
//        System.out.println("userName:" + userName);
//        System.out.println("computerName:" + computerName);
//        System.out.println("userDomain:" + userDomain);
//        return "treeInfo/DirectoryV2" + getMD5Str(userName + computerName + userDomain) + ".xml";
        return "DirectoryV2.xml";
    }

    private static String getMD5Str(String str) {
        byte[] digest = null;
        try {
            MessageDigest md5 = MessageDigest.getInstance("md5");
            digest = md5.digest(str.getBytes(StandardCharsets.UTF_8));
            //16�Ǳ�ʾת��Ϊ16������
            return new BigInteger(1, digest).toString(16);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * �����ļ�
     *
     * @param project ��Ŀ
     */
    private static XmlFile createFileDirectoryXml(Project project) {
        if (project == null) {
            return null;
        }
        LanguageFileType xml = (LanguageFileType) FileTypeManager.getInstance().getStdFileType("XML");
        PsiFile pf = PsiFileFactory.getInstance(project).createFileFromText(getFileName(), xml, "<?xml version=\"1.0\" encoding=\"UTF-8\"?> \r\n <trees/>");
        saveFileDirectoryXml(project, pf.getText());
        return xmlFile;
    }

    /**
     * �����ļ�
     *
     * @param project ��Ŀ
     */
    public static synchronized void saveFileDirectoryXml(Project project, String text) {
        File f = new File(project.getBasePath() + File.separator + getFileName());
        if (!f.exists()) {
            try {
                boolean b = f.getParentFile().mkdirs();
                boolean newFile = f.createNewFile();
                if (newFile) {
                    BufferedWriter writer = null;
                    try {
                        writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f, false), StandardCharsets.UTF_8));
                        writer.write(text);
                    } catch (IOException e) {
                        e.printStackTrace();
                        return;
                    } finally {
                        try {
                            if (writer != null) {
                                writer.close();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
        VirtualFile virtualFile = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(f);
        if (null != virtualFile) {
            virtualFile.refresh(false, true);
            PsiFile file = PsiManager.getInstance(project).findFile(virtualFile);
            if (file instanceof XmlFile) {
                xmlFile = (XmlFile) file;
                XmlParsing.parsing(project, xmlFile);
            }
        }

    }

    /**
     * �ļ�����
     *
     * @param project ��Ŀ
     */
    public static void treeChangeListener(Project project) {
        PsiManager.getInstance(project).addPsiTreeChangeListener(
                new PsiTreeChangeListener() {
                    @Override
                    public void beforeChildAddition(@NotNull PsiTreeChangeEvent psiTreeChangeEvent) {
                    }

                    @Override
                    public void beforeChildRemoval(@NotNull PsiTreeChangeEvent psiTreeChangeEvent) {
                    }

                    @Override
                    public void beforeChildReplacement(@NotNull PsiTreeChangeEvent psiTreeChangeEvent) {
                    }

                    @Override
                    public void beforeChildMovement(@NotNull PsiTreeChangeEvent psiTreeChangeEvent) {
                    }

                    //����ǰ
                    @Override
                    public void beforeChildrenChange(@NotNull PsiTreeChangeEvent psiTreeChangeEvent) {
                        if (isFileName(psiTreeChangeEvent)) {
                            final PsiFile file = psiTreeChangeEvent.getFile();
                            if (file instanceof XmlFile) {
                                XmlParsing.parsing(project, (XmlFile) file);
                            }
                        }
                    }

                    @Override
                    public void beforePropertyChange(@NotNull PsiTreeChangeEvent psiTreeChangeEvent) {
                    }

                    @Override
                    public void childAdded(@NotNull PsiTreeChangeEvent psiTreeChangeEvent) {
                    }

                    @Override
                    public void childRemoved(@NotNull PsiTreeChangeEvent psiTreeChangeEvent) {
                        PsiElement child = psiTreeChangeEvent.getChild();
                        if (child instanceof XmlFile) {
                            if (getFileName().equals(((XmlFile) child).getName())) {
                                XmlParsing.clear(project);
                            }
                        }
                    }

                    @Override
                    public void childReplaced(@NotNull PsiTreeChangeEvent psiTreeChangeEvent) {
                    }

                    @Override
                    public void childrenChanged(@NotNull PsiTreeChangeEvent psiTreeChangeEvent) {
                        if (isFileName(psiTreeChangeEvent)) {
                            final PsiFile file = psiTreeChangeEvent.getFile();
                            if (file instanceof XmlFile) {
                                XmlParsing.parsing(project, (XmlFile) file);
                            }
                        }
                    }

                    @Override
                    public void childMoved(@NotNull PsiTreeChangeEvent psiTreeChangeEvent) {
                    }

                    @Override
                    public void propertyChanged(@NotNull PsiTreeChangeEvent psiTreeChangeEvent) {
                    }
                },
                new Disposable() {
                    @Override
                    public void dispose() {
                    }
                });
    }

    /**
     * �Ƿ�Ϊָ�����ļ�
     *
     * @param psiTreeChangeEvent ����
     * @return boolean
     */
    private static boolean isFileName(PsiTreeChangeEvent psiTreeChangeEvent) {
        final PsiFile file = psiTreeChangeEvent.getFile();
        if (null != file) {
            final VirtualFile virtualFile = file.getVirtualFile();
            if (null != virtualFile) {
                return virtualFile.getName().contains(getFileName());
            }
        }
        return false;
    }

    /**
     * ���ýڵ㱸ע
     *
     * @param abstractTreeNode ����
     */
    public static void setLocationString(AbstractTreeNode<?> abstractTreeNode) {
        if (null != abstractTreeNode) {
            Method[] methods1 = abstractTreeNode.getClass().getMethods();
            Object value = abstractTreeNode.getValue();
            if (null != value) {
                Method[] methods2 = value.getClass().getMethods();
                VirtualFile virtualFile2 = getVirtualFile(methods2, value);
                if (null != virtualFile2) {
                    setXmlToLocationString(virtualFile2, abstractTreeNode);
                    return;
                }
            }

            VirtualFile virtualFile1 = getVirtualFile(methods1, abstractTreeNode);
            if (null != virtualFile1) {
                setXmlToLocationString(virtualFile1, abstractTreeNode);
            }
        }
    }

    /**
     * ���ýڵ㱸ע
     *
     * @param node ����
     * @param data ����
     */
    public static void setLocationString(ProjectViewNode node, PresentationData data) {
        if (null != node) {
            Method[] methods1 = node.getClass().getMethods();
            Object value = node.getValue();
            if (null != value) {
                Method[] methods2 = value.getClass().getMethods();
                VirtualFile virtualFile2 = getVirtualFile(methods2, value);
                if (null != virtualFile2) {
                    setXmlToLocationString(node.getProject(), virtualFile2, data);
                    return;
                }
            }

            VirtualFile virtualFile1 = getVirtualFile(methods1, node);
            if (null != virtualFile1) {
                setXmlToLocationString(node.getProject(), virtualFile1, data);
            }
        }
    }

    /**
     * ��ȡ�� VirtualFile
     *
     * @param methods ����
     * @param o       ����
     * @return VirtualFile
     */
    private static VirtualFile getVirtualFile(Method[] methods, Object o) {
        if (o instanceof KtClassOrObjectTreeNode) {
            KtClassOrObject kv = ((KtClassOrObjectTreeNode) o).getValue();
            VirtualFile vfKt = kv.getContainingFile().getVirtualFile();
            return vfKt;
        }
        for (Method method : methods) {
            if ("getVirtualFile".equals(method.getName())) {
                method.setAccessible(true);
                try {
                    Object invoke = method.invoke(o);
                    if (invoke instanceof VirtualFile) {
                        return (VirtualFile) invoke;
                    }
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * ���ñ�ע
     *
     * @param virtualFile      ����
     * @param abstractTreeNode ����
     */
    private static void setXmlToLocationString(VirtualFile virtualFile, AbstractTreeNode<?> abstractTreeNode) {
        XmlEntity matchPath = getMatchPath(virtualFile, abstractTreeNode.getProject());
        if (null != matchPath) {
            //���ñ�ע
            abstractTreeNode.getPresentation().setLocationString(matchPath.getTitle());
        }
    }

    /**
     * ���ñ�ע
     *
     * @param virtualFile ����
     * @param data        ����
     */
    private static void setXmlToLocationString(Project project, VirtualFile virtualFile, PresentationData data) {
        XmlEntity matchPath = getMatchPath(virtualFile, project);
        if (null != matchPath) {
            //���ñ�ע
            data.setLocationString(matchPath.getTitle());
            for (Icons allIcon : FileIcons.getAllIcons()) {
                if (allIcon.getName().equals(matchPath.getIcon())) {
                    data.setIcon(allIcon.getIcon());
                    return;
                }
            }
        }
    }

    /**
     * ƥ��·��
     *
     * @param virtualFile �ļ�����
     * @return boolean
     */
    private static XmlEntity getMatchPath(VirtualFile virtualFile, Project project) {
        List<XmlEntity> xml = XmlParsing.getXml(project);
        for (XmlEntity listTreeInfo : xml) {
            if (listTreeInfo != null) {
                String basePath = project.getPresentableUrl();
                String canonicalPath = virtualFile.getCanonicalPath();
                String asbbasePath = canonicalPath == null ? "" : canonicalPath.substring(basePath != null ? basePath.length() : 0);
                //Messages.showMessageDialog(project, presentableUrl + ":" + x.getPath(), "Can't Get Path", AllIcons.Actions.Menu_paste);
                if (asbbasePath.equals(listTreeInfo.getPath())) {
                    return listTreeInfo;
                }
            }
        }
        return null;
    }
}
