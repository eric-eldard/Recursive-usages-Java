package treeOfUsages.node.icon.file;

import javax.swing.Icon;

import com.intellij.util.PlatformIcons;
import treeOfUsages.node.icon.HasIcon;

public class JavaFileIcon implements HasIcon
{
    public JavaFileIcon()
    {
    }

    @Override
    public Icon getIcon()
    {
        return PlatformIcons.CLASS_ICON;
    }
}