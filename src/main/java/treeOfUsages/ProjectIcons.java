package treeOfUsages;

import com.intellij.openapi.util.IconLoader;

import javax.swing.Icon;

public interface ProjectIcons
{
    Icon SiblingMethod = IconLoader.getIcon("/icons/siblingMethod.svg", ProjectIcons.class);
    Icon SiblingMethodGutter = IconLoader.getIcon("/icons/siblingMethodGutter.svg", ProjectIcons.class);
    Icon ShowKey = IconLoader.getIcon("/icons/ejbPrimaryKeyClass.svg", ProjectIcons.class);
}