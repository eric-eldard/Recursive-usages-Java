package treeOfUsages.action;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;
import treeOfUsages.Plugin;

public class ResetAction extends EnableableAction
{
    public Plugin plugin;

    @SuppressWarnings("unused")
    public ResetAction()
    {
    }

    public ResetAction(Plugin plugin)
    {
        super(
            "Reset Tree and History", 
            "Reset Tree of Usages UI and clear all trees in its history",
            AllIcons.General.Reset
        );
        this.plugin = plugin;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e)
    {
        plugin.reset();
    }
}