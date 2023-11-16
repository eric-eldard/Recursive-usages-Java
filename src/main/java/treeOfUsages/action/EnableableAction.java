package treeOfUsages.action;

import javax.swing.Icon;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.util.NlsActions;
import org.jetbrains.annotations.Nullable;

public abstract class EnableableAction extends AnAction
{
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

    public abstract boolean isEnabled();

    public abstract void setEnabled(boolean enabled);
}