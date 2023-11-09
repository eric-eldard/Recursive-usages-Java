package myToolWindow.Nodes.Icons.ClassNodes;

import javax.swing.Icon;

import com.intellij.icons.AllIcons;
import myToolWindow.Nodes.Icons.HasIcon;

public class TestNode implements HasIcon
{
    public TestNode()
    {
    }

    @Override
    public Icon getIcon()
    {
        return AllIcons.Actions.StartDebugger;
    }
}