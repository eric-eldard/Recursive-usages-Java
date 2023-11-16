package treeOfUsages.node;

import javax.swing.Icon;

import com.intellij.psi.NavigatablePsiElement;

public class ClassNode implements UsageNode
{
    private final NavigatablePsiElement element;

    private final Icon icon;

    public ClassNode(Icon icon, NavigatablePsiElement element)
    {
        this.icon = icon;
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
        return icon;
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