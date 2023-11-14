package treeOfUsages.node;

import javax.swing.Icon;

import com.intellij.icons.AllIcons;
import com.intellij.psi.NavigatablePsiElement;
import com.intellij.ui.RowIcon;
import treeOfUsages.node.icon.HasIcon;

public abstract class UsageNode implements HasIcon
{
    private final HasIcon iconContainer;

    private boolean isCyclic;

    public UsageNode(HasIcon iconContainer)
    {
        this.iconContainer = iconContainer;
    }

    public abstract NavigatablePsiElement getElement();

    public abstract String getMainText();

    public abstract String getAdditionalText();

    public Icon getIcon()
    {
        if (isCyclic)
        {
            return new RowIcon(iconContainer.getIcon(), AllIcons.Gutter.RecursiveMethod);
        }
        else
        {
            return iconContainer.getIcon();
        }
    }

    public void setIsCyclic()
    {
        isCyclic = true;
    }
}