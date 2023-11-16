package treeOfUsages.node;

import javax.swing.Icon;

import com.intellij.psi.NavigatablePsiElement;
import treeOfUsages.node.icon.HasIcon;

public class ClassNode implements UsageNode
{
    private final NavigatablePsiElement element;

    private final HasIcon iconContainer;

    public ClassNode(HasIcon iconContainer, NavigatablePsiElement element)
    {
        this.iconContainer = iconContainer;
        this.element = element;
    }

    @Override
    public NavigatablePsiElement getElement()
    {
        return this.element;
    }

    @Override
    public Icon getIcon()
    {
        return iconContainer.getIcon();
    }

    @Override
    public String getMainText()
    {
        return getElement().getContainingFile().getName();
    }

    @Override
    public String getAdditionalText()
    {
        return "";
    }
}