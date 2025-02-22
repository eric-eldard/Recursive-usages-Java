package treeOfUsages.action;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;
import treeOfUsages.Plugin;

public class StopFindUsagesAction extends EnableableAction
{
    public Plugin plugin;

    @SuppressWarnings("unused")
    public StopFindUsagesAction()
    {
    }

    public StopFindUsagesAction(Plugin plugin)
    {
        super(
            "Stop Building Tree",
            "Immediately terminate building of the current tree of usages",
            AllIcons.Actions.Suspend
        );
        this.plugin = plugin;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e)
    {
        plugin.stop();
    }

    @Override
    public boolean isFirstInGroup()
    {
        return true;
    }
}