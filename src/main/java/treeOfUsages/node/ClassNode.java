package treeOfUsages.node;

import javax.swing.Icon;

import com.intellij.psi.NavigatablePsiElement;
import com.intellij.util.PlatformIcons;

public class ClassNode implements UsageNode
{
    private final NavigatablePsiElement element;

    public ClassNode(NavigatablePsiElement element)
    {
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
        return PlatformIcons.CLASS_ICON;
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