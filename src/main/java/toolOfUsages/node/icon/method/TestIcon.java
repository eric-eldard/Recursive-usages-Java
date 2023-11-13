package toolOfUsages.node.icon.method;

import javax.swing.Icon;

import com.intellij.icons.AllIcons;
import toolOfUsages.node.icon.HasIcon;

public class TestIcon implements HasIcon
{
    public TestIcon()
    {
    }

    @Override
    public Icon getIcon()
    {
        return AllIcons.Actions.StartDebugger;
    }
}