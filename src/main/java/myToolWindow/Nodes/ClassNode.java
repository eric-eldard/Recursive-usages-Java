package myToolWindow.Nodes;

import javax.swing.Icon;

import com.intellij.icons.AllIcons;
import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.impl.source.PsiMethodImpl;
import com.intellij.ui.RowIcon;
import myToolWindow.Nodes.Icons.HasIcon;

public class ClassNode implements UsageNode
{
    private final HasIcon iconContainer;

    private final PsiMethodImpl element;

    private final int count;

    private boolean isCyclic = false;

    public ClassNode(HasIcon iconContainer, PsiMethodImpl e, int count)
    {
        this.iconContainer = iconContainer;
        this.element = e;
        this.count = count;
    }

    @Override
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