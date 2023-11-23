package treeOfUsages.action;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;
import treeOfUsages.Plugin;

public class GoForwardAction extends EnableableAction
{
    public Plugin plugin;

    @SuppressWarnings("unused")
    public GoForwardAction()
    {
    }

    public GoForwardAction(Plugin plugin)
    {
        super(
            "Forward to Next Tree",
            "Go forward to the next tree in the history",
            AllIcons.Actions.Forward
        );
        this.plugin = plugin;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e)
    {
        plugin.goForward();
    }
}