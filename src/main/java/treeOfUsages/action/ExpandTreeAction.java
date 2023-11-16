package treeOfUsages.action;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;
import treeOfUsages.Plugin;

public class ExpandTreeAction extends EnableableAction
{
    public Plugin plugin;

    private boolean enabled = true;

    @SuppressWarnings("unused")
    public ExpandTreeAction()
    {
    }

    public ExpandTreeAction(Plugin plugin)
    {
        super("Expand All", "Expand all", AllIcons.Actions.Expandall);
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
            for (int i = 0; i < plugin.getTree().getRowCount(); i++)
            {
                plugin.getTree().expandRow(i);
            }
        }
    }
}