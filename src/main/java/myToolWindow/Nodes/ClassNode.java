package myToolWindow.Nodes;

import com.intellij.icons.AllIcons;
import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.PsiInvalidElementAccessException;
import com.intellij.psi.impl.source.PsiMethodImpl;
import com.intellij.ui.RowIcon;
import myToolWindow.Nodes.Icons.HasIcon;

import javax.swing.*;

public class ClassNode implements UsageNode {
    private final HasIcon iconContainer;
    private final PsiMethodImpl element;
    private boolean isCyclic = false;

    public ClassNode(HasIcon iconContainer, PsiMethodImpl e) {
        this.iconContainer = iconContainer;
        this.element = e;
    }

    @Override
    public Icon getIcon() {
        if (isCyclic) {
            return new RowIcon(iconContainer.getIcon(), AllIcons.Gutter.RecursiveMethod);
        } else {
            return iconContainer.getIcon();
        }
    }

    public void setIsCyclic() {
        isCyclic = true;
    }

    @Override
    public NavigatablePsiElement getElement() throws NullPointerException {
        return this.element;
    }

    @Override
    public String getMainText() throws NullPointerException {
        return getElement().getName();
    }

    @Override
    public String getAdditionalText() throws NullPointerException, PsiInvalidElementAccessException {
        return " ← " + getElement().getOriginalElement().getContainingFile().getName();
    }
}
