package treeOfUsages.action;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;
import treeOfUsages.Plugin;

public class CollapseTreeAction extends EnableableAction
{
    public Plugin plugin;

    private boolean enabled = true;

    @SuppressWarnings("unused")
    public CollapseTreeAction()
    {
    }

    public CollapseTreeAction(Plugin plugin)
    {
        super("Collapse All", "Collapse all", AllIcons.Actions.Collapseall);
        this.plugin = plugin;
    }

    @Override
    public boolean isEnabled()
    {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    @Override
    public void update(
        @NotNull
        AnActionEvent e)
    {
        e.getPresentation().setEnabled(enabled);
        super.update(e);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e)
    {
        if (plugin.getTree() != null)
        {
            for (int i = plugin.getTree().getRowCount() - 1; i >= 0; i--)
            {
                plugin.getTree().collapseRow(i);
            }
        }
    }
}