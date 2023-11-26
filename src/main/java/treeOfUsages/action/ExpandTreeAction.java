package treeOfUsages.action;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;
import treeOfUsages.Plugin;

public class ExpandTreeAction extends EnableableAction
{
    public Plugin plugin;

    @SuppressWarnings("unused")
    public ExpandTreeAction()
    {
    }

    public ExpandTreeAction(Plugin plugin)
    {
        super(
            "Expand All",
            "Expand all nodes in the current tree",
            AllIcons.Actions.Expandall
        );
        this.plugin = plugin;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e)
    {
        plugin.expandAll();
    }

    @Override
    public boolean isFirstInGroup()
    {
        return true;
    }
}