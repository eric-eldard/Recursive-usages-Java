package treeOfUsages.action;

import javax.swing.Icon;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.util.NlsActions;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class EnableableAction extends AnAction
{
    @Getter @Setter
    private boolean enabled = true;

    @SuppressWarnings("unused")
    public EnableableAction()
    {
    }

    public EnableableAction(
        @Nullable @NlsActions.ActionText String text,
        @Nullable @NlsActions.ActionDescription String description,
        @Nullable Icon icon)
    {
        super(text, description, icon);
    }

    @Override
    public void update(@NotNull AnActionEvent e)
    {
        e.getPresentation().setEnabled(enabled);
        super.update(e);
    }

    public boolean isFirstInGroup()
    {
        return false;
    }
}