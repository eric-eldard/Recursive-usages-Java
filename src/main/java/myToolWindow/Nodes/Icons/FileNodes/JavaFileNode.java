package myToolWindow.Nodes.Icons.FileNodes;

import javax.swing.Icon;

import com.intellij.util.PlatformIcons;
import myToolWindow.Nodes.Icons.HasIcon;

public class JavaFileNode implements HasIcon
{
    public JavaFileNode()
    {
    }

    @Override
    public Icon getIcon()
    {
        return PlatformIcons.CLASS_ICON;
    }
}