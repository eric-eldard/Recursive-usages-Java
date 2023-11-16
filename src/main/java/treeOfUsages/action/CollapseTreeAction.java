package treeOfUsages.action;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;
import treeOfUsages.Plugin;

public class CollapseTreeAction extends EnableableAction
{
    public Plugin mtw;

    private boolean enabled = true;

    @SuppressWarnings("unused")
    public CollapseTreeAction()
    {
    }

    public CollapseTreeAction(Plugin tw)
    {
        super("Collapse All", "Collapse all", AllIcons.Actions.Collapseall);
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
            for (int i = mtw.tree.getRowCount() - 1; i >= 0; i--)
            {
                mtw.tree.collapseRow(i);
            }
        }
    }
}