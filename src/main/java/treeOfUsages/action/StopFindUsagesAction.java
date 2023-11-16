package treeOfUsages.action;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;
import treeOfUsages.Plugin;

public class StopFindUsagesAction extends EnableableAction
{
    public Plugin mtw;

    private boolean enabled = false;

    @SuppressWarnings("unused")
    public StopFindUsagesAction()
    {
    }

    public StopFindUsagesAction(Plugin tw)
    {
        super("Stop Building a Tree of Usages", "Stop building a tree of usages", AllIcons.Actions.Suspend);
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
    public void update(@NotNull AnActionEvent e)
    {
        e.getPresentation().setEnabled(enabled);
        super.update(e);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e)
    {
        mtw.stop();
    }
}