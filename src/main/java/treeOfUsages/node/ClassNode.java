package treeOfUsages.node;

import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.impl.source.PsiMethodImpl;
import treeOfUsages.node.icon.HasIcon;

public class ClassNode extends UsageNode
{
    private final PsiMethodImpl element;

    /**
     * The number of times the parent is invoked directly from this node
     */
    private final int count;

    public ClassNode(HasIcon iconContainer, PsiMethodImpl element, int count)
    {
        super(iconContainer);
        this.element = element;
        this.count = count;
    }

    @Override
    public NavigatablePsiElement getElement()
    {
        return this.element;
    }

    @Override
    public String getMainText()
    {
        return getElement().getName();
    }

    @Override
    public String getAdditionalText()
    {
        return " ← " + getElement().getOriginalElement().getContainingFile().getName() + makeMultiplicityLabel();
    }

    /**
     * @return a multiplicity label for counts greater than 1: " (x2)"
     */
    private String makeMultiplicityLabel()
    {
        return count > 1 ? String.format(" (x%d)", count) : "";
    }
}