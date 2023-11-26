package treeOfUsages.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;
import treeOfUsages.Plugin;
import treeOfUsages.ProjectIcons;

public class ShowKeyAction extends EnableableAction
{
    public Plugin plugin;

    @SuppressWarnings("unused")
    public ShowKeyAction()
    {
    }

    public ShowKeyAction(Plugin plugin)
    {
        super(
            "Show Key",
            "Show a key of icons used in Tree of Usages",
            ProjectIcons.ShowKey
        );
        this.plugin = plugin;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e)
    {
        plugin.showKey();
    }

    @Override
    public boolean isFirstInGroup()
    {
        return true;
    }
}