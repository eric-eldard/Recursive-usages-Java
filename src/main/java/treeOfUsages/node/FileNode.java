package treeOfUsages.node;

import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.impl.source.tree.java.PsiMethodReferenceExpressionImpl;
import treeOfUsages.node.icon.HasIcon;

public class FileNode extends UsageNode
{
    private final PsiMethodReferenceExpressionImpl element;

    public FileNode(HasIcon iconContainer, PsiMethodReferenceExpressionImpl element)
    {
        super(iconContainer);
        this.element = element;
    }

    @Override
    public NavigatablePsiElement getElement()
    {
        return this.element;
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