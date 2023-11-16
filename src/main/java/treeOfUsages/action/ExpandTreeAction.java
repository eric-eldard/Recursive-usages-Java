package treeOfUsages.action;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;
import treeOfUsages.Plugin;

public class ExpandTreeAction extends EnableableAction
{
    public Plugin mtw;

    private boolean enabled = true;

    @SuppressWarnings("unused")
    public ExpandTreeAction()
    {
    }

    public ExpandTreeAction(Plugin tw)
    {
        super("Expand All", "Expand all", AllIcons.Actions.Expandall);
        mtw = tw;
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
        if (mtw.tree != null)
        {
            for (int i = 0; i < mtw.tree.getRowCount(); i++)
            {
                mtw.tree.expandRow(i);
            }
        }
    }
}