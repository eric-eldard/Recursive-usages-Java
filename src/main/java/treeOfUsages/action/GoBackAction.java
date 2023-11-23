package treeOfUsages.action;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;
import treeOfUsages.Plugin;

public class GoBackAction extends EnableableAction
{
    public Plugin plugin;

    @SuppressWarnings("unused")
    public GoBackAction()
    {
    }

    public GoBackAction(Plugin plugin)
    {
        super(
            "Back to Previous Tree",
            "Go back to the previous tree in the history",
            AllIcons.Actions.Back
        );
        this.plugin = plugin;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e)
    {
        plugin.goBack();
    }
}